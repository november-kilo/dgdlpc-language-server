package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.function;

import novemberkilo.dgdlpclangserver.LPCBaseVisitor;
import novemberkilo.dgdlpclangserver.LPCParser;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.ParameterDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.PositionDetailsParser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FormalParametersVisitor extends LPCBaseVisitor<List<ParameterDefinition>> {
    @Override
    public List<ParameterDefinition> visitFormalParameters(LPCParser.FormalParametersContext context) {
        return createFormalParameters(context);
    }

    private List<ParameterDefinition> createFormalParameters(LPCParser.FormalParametersContext context) {
        return Optional.of(context)
                .filter(ctx -> ctx.VOID() == null)
                .map(LPCParser.FormalParametersContext::parameterList)
                .map(this::processParameterList)
                .orElse(List.of());
    }

    private @NotNull List<ParameterDefinition> processParameterList(LPCParser.@NotNull ParameterListContext parameterList) {
        List<ParameterDefinition> parameters = new ArrayList<>();
        boolean hasSeenVarargs = false;

        for (LPCParser.ParameterDeclarationContext paramDeclaration : parameterList.parameterDeclaration()) {
            if (!hasSeenVarargs && paramDeclaration.VARARGS() != null) {
                hasSeenVarargs = true;
            }

            boolean isVariadic = paramDeclaration.getChild(paramDeclaration.getChildCount() - 1).getText().equals("...");
            ParameterDefinition parameter = createParameter(paramDeclaration, hasSeenVarargs, isVariadic, PositionDetailsParser.fromContext(paramDeclaration));
            parameters.add(parameter);
        }

        return parameters;
    }

    private @NotNull ParameterDefinition createParameter(LPCParser.@NotNull ParameterDeclarationContext paramDeclaration, boolean isOptional, boolean isVariadic, PositionDetails position) {
        String parameterType = paramDeclaration.typeSpecifier().getText();
        String parameterName = paramDeclaration.IDENTIFIER().getText();
        int arrayDimensions = Optional.ofNullable(paramDeclaration.typeSpecifier().arraySpecifier())
                .map(arr -> arr.getText().length())
                .orElse(0);
        return new ParameterDefinition(parameterType, arrayDimensions, isOptional, isVariadic, parameterName, position);
    }
}
