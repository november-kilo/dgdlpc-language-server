package novemberkilo.dgdlpclangserver.dgdlpc.definition.variable;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.LPCDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;

public record VariableDefinition(
        boolean isFunctionParameter,
        boolean isOptional,
        boolean isVariadic,
        boolean isPrivate,
        boolean isStatic,
        String type,
        int arrayDimensions,
        String name,
        PositionDetails position,
        PositionDetails scopePosition
) implements LPCDefinition {
}
