package novemberkilo.dgdlpclangserver.dgdlpc.definition;

public record PositionDetails(
        int startLine,
        int startColumn,
        int startIndex,
        int endLine,
        int endColumn,
        int endIndex
) {
}
