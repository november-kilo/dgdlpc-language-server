package novemberkilo.dgdlpclangserver.dgdlpc.definition.inherit;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.LPCDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;

public record InheritDefinition(
        boolean isPrivate,
        String label,
        String file,
        PositionDetails position
) implements LPCDefinition {
    public InheritDefinition {
        label = label == null ? "" : label;
    }
}
