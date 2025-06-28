package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.function;

import novemberkilo.dgdlpclangserver.LPCBaseVisitor;
import novemberkilo.dgdlpclangserver.LPCParser;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.FunctionDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.FunctionDetails;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.FunctionModifiers;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.ParameterDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.PositionDetailsParser;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.LPCParseTreeVisitor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FunctionVisitor extends LPCBaseVisitor<Void>
        implements LPCParseTreeVisitor<FunctionDefinition> {
    private final FormalParametersVisitor formalParametersVisitor;

    private final List<FunctionDefinition> allFunctions = new ArrayList<>();

    public FunctionVisitor(FormalParametersVisitor formalParametersVisitor) {
        this.formalParametersVisitor = formalParametersVisitor;
    }

    public static @NotNull FunctionDefinition createFunctionDefinition(LPCParser.@NotNull FunctionDeclarationContext context, FormalParametersVisitor formalParametersVisitor) {
        FunctionModifiers modifiers = FunctionModifiersParser.fromContext(context.modifiers());
        FunctionDetails details = FunctionDetailsParser.fromContext(context);
        List<ParameterDefinition> parameters = extractParameters(context.functionDeclarator(), formalParametersVisitor);

        return new FunctionDefinition(
                context.block() == null,
                modifiers.isAtomic(),
                modifiers.isPrivate(),
                modifiers.isStatic(),
                modifiers.isNomask(),
                details.isOperator(),
                details.operatorSymbol(),
                details.returnType(),
                details.name(),
                parameters,
                PositionDetailsParser.fromContext(context)
        );
    }

    @Contract("_, _ -> !null")
    private static List<ParameterDefinition> extractParameters(LPCParser.@NotNull FunctionDeclaratorContext declaratorContext, @NotNull FormalParametersVisitor formalParametersVisitor) {
        return Optional.ofNullable(declaratorContext.formalParameters())
                .map(formalParametersVisitor::visitFormalParameters)
                .orElse(new ArrayList<>());
    }

    @Override
    public Void visitProgram(LPCParser.@NotNull ProgramContext context) {
        for (LPCParser.ProgramElementContext element : context.programElement()) {
            if (element.functionDeclaration() != null) {
                FunctionDefinition functionDef = createFunctionDefinition(element.functionDeclaration(), formalParametersVisitor);
                allFunctions.add(functionDef);
            }
        }

        return super.visitProgram(context);
    }

    @Override
    public List<FunctionDefinition> getAll() {
        return allFunctions;
    }
}
