package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.variable;

import novemberkilo.dgdlpclangserver.LPCBaseVisitor;
import novemberkilo.dgdlpclangserver.LPCParser;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.FunctionDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.ParameterDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.variable.VariableDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.variable.VariableModifiers;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.variable.VariableType;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.PositionDetailsParser;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.function.FormalParametersVisitor;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.function.FunctionVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VariableVisitor extends LPCBaseVisitor<Void> {
    private final VariableScope globalVariableScope;
    private VariableScope currentVariableScope;

    public VariableVisitor() {
        this.globalVariableScope = new VariableScope(null, null); // Global scope has no position or parent
        this.currentVariableScope = globalVariableScope;
    }

    private void enterVariableScope(ParserRuleContext context) {
        currentVariableScope = new VariableScope(PositionDetailsParser.fromContext(context), currentVariableScope);
    }

    private void exitVariableScope() {
        currentVariableScope = currentVariableScope.getParent();
    }

    @Override
    public Void visitProgram(LPCParser.ProgramContext context) {
        enterVariableScope(context);
        super.visitProgram(context);
        exitVariableScope();
        return null;
    }

    @Override
    public Void visitBlock(LPCParser.BlockContext context) {
        enterVariableScope(context);
        super.visitBlock(context);
        exitVariableScope();
        return null;
    }

    @Override
    public Void visitSelectionStatement(LPCParser.SelectionStatementContext context) {
        enterVariableScope(context);
        super.visitSelectionStatement(context);
        exitVariableScope();
        return null;
    }

    @Override
    public Void visitIterationStatement(LPCParser.IterationStatementContext context) {
        enterVariableScope(context);
        super.visitIterationStatement(context);
        exitVariableScope();
        return null;
    }

    @Override
    public Void visitFunctionDeclaration(LPCParser.FunctionDeclarationContext context) {
        enterVariableScope(context);
        FunctionDefinition functionDef = FunctionVisitor.createFunctionDefinition(context, new FormalParametersVisitor());
        addParametersToCurrentScope(functionDef.parameters());
        super.visitFunctionDeclaration(context);
        exitVariableScope();
        return null;
    }

    @Override
    public Void visitVariableDeclaration(LPCParser.VariableDeclarationContext context) {
        VariableDefinition varDef = createVariableDefinition(context);
        currentVariableScope.addVariable(varDef);

        return null;
    }

    private @NotNull VariableDefinition createVariableDefinition(LPCParser.@NotNull VariableDeclarationContext context) {
        VariableModifiers modifiers = VariableModifiersParser.fromContext(context.modifiers());
        VariableType type = VariableTypeParser.fromContext(context.typeSpecifier());

        // TODO: need to modify this when handling multiple declarations
        String name = context.variableDeclarators().IDENTIFIER(0).getText();

        return new VariableDefinition(
                false,
                false,
                false,
                modifiers.isPrivate(),
                modifiers.isStatic(),
                type.type(),
                type.arrayDimensions(),
                name,
                PositionDetailsParser.fromContext(context),
                currentVariableScope.getPosition()
        );
    }

    public List<VariableDefinition> findVisibleVariables(PositionDetails position) {
        List<VariableDefinition> visibleVars = new ArrayList<>();
        VariableScope targetVariableScope = findDeepestContainingVariableScope(globalVariableScope, position);

        while (targetVariableScope != null) {
            visibleVars.addAll(targetVariableScope.getVariables());
            targetVariableScope = targetVariableScope.getParent();
        }

        return visibleVars;
    }

    private void addParametersToCurrentScope(@NotNull List<ParameterDefinition> parameters) {
        for (ParameterDefinition param : parameters) {
            VariableDefinition varDef = convertParameterToVariable(param);
            currentVariableScope.addVariable(varDef);
        }
    }

    @Contract("_ -> new")
    private @NotNull VariableDefinition convertParameterToVariable(@NotNull ParameterDefinition param) {
        return new VariableDefinition(
                true,
                param.isOptional(),
                param.isVariadic(),
                false,
                false,
                param.type(),
                param.arrayDimensions(),
                param.name(),
                param.position(),
                currentVariableScope.getPosition()
        );
    }


    private @Nullable VariableScope findDeepestContainingVariableScope(@NotNull VariableScope scope, PositionDetails position) {
        if (!scope.contains(position)) {
            return null;
        }

        for (VariableScope child : scope.getChildren()) {
            VariableScope deeperVariableScope = findDeepestContainingVariableScope(child, position);
            if (deeperVariableScope != null) {
                return deeperVariableScope;
            }
        }

        return scope;
    }

    public String getScopeTree() {
        return globalVariableScope.toTreeString();
    }
}
