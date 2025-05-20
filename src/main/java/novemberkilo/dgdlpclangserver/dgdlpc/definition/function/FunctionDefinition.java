package novemberkilo.dgdlpclangserver.dgdlpc.definition.function;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.LPCDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;

import java.util.List;

public record FunctionDefinition(
        boolean isDeclaration,
        boolean isAtomic,
        boolean isPrivate,
        boolean isStatic,
        boolean isNomask,
        boolean isOperator,
        String operatorSymbol,
        String returnType,
        String name,
        List<ParameterDefinition> parameters,
        PositionDetails position
) implements LPCDefinition {
}
