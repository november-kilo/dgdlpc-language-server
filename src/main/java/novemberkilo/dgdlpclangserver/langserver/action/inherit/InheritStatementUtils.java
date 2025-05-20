package novemberkilo.dgdlpclangserver.langserver.action.inherit;

import lombok.experimental.UtilityClass;
import org.eclipse.lsp4j.Range;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class InheritStatementUtils {
    public String extract(String content, Range range) {
        if (!isValidContent(content)) {
            return "";
        }

        String[] lines = content.split("\n", -1);
        if (!isValidRange(lines, range)) {
            return "";
        }

        return buildStatement(lines, range);
    }

    private boolean isValidContent(String content) {
        return content != null && !content.isEmpty();
    }

    private boolean isValidRange(String[] lines, @NotNull Range range) {
        int startLine = range.getStart().getLine();
        int endLine = range.getEnd().getLine();
        return startLine >= 0 && startLine < lines.length &&
                endLine >= 0 && endLine < lines.length;
    }

    private @NotNull String buildStatement(String[] lines, @NotNull Range range) {
        StringBuilder statement = new StringBuilder();
        int startLine = range.getStart().getLine();
        int endLine = range.getEnd().getLine();
        int startChar = range.getStart().getCharacter();
        int endChar = range.getEnd().getCharacter();

        if (startLine == endLine) {
            appendSingleLine(statement, lines, startLine, startChar, endChar);
        } else {
            appendMultipleLines(statement, lines, startLine, endLine, startChar, endChar);
        }

        return statement.toString().replaceAll("\\s+$", "");
    }

    private void appendSingleLine(@NotNull StringBuilder statement, String @NotNull [] lines,
                                  int line, int startChar, int endChar) {
        statement.append(lines[line], startChar, endChar);
    }

    private void appendMultipleLines(@NotNull StringBuilder statement, String @NotNull [] lines,
                                     int startLine, int endLine,
                                     int startChar, int endChar) {
        statement.append(lines[startLine].substring(startChar)).append("\n");

        for (int i = startLine + 1; i < endLine; i++) {
            statement.append(lines[i]).append("\n");
        }

        statement.append(lines[endLine], 0, endChar);
    }
}
