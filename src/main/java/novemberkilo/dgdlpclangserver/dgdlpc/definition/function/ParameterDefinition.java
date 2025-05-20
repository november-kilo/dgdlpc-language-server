package novemberkilo.dgdlpclangserver.dgdlpc.definition.function;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.LPCDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;

public record ParameterDefinition(
        String type,
        int arrayDimensions,
        boolean isOptional,
        boolean isVariadic,
        String name,
        PositionDetails position
) implements LPCDefinition {
}
