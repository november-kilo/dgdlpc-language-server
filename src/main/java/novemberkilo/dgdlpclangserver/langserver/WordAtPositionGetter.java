package novemberkilo.dgdlpclangserver.langserver;

import lombok.experimental.UtilityClass;
import org.eclipse.lsp4j.Position;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@UtilityClass
public final class WordAtPositionGetter {
    public Optional<String> getWordAtPosition(String content, Position position) {
        if (content == null) {
            return Optional.empty();
        }

        int offset = positionToOffset(content, position);
        if (offset == -1) {
            return Optional.empty();
        }

        int start = findWordStart(content, offset);
        int end = findWordEnd(content, offset);

        return Optional.of(content.substring(start, end));
    }

    private int positionToOffset(String content, @NotNull Position position) {
        int line = 0;
        int offset = 0;

        while (line < position.getLine() && offset < content.length()) {
            if (content.charAt(offset) == '\n') {
                line++;
            }
            offset++;
        }

        if (line != position.getLine()) {
            return -1;
        }

        offset += position.getCharacter();
        return offset < content.length() ? offset : -1;
    }

    private int findWordStart(String content, int offset) {
        while (offset > 0) {
            char ch = content.charAt(offset - 1);
            if (isWordChar(ch)) {
                break;
            }
            offset--;
        }
        return offset;
    }

    private int findWordEnd(@NotNull String content, int offset) {
        while (offset < content.length()) {
            if (isWordChar(content.charAt(offset))) {
                break;
            }
            offset++;
        }
        return offset;
    }

    private boolean isWordChar(char ch) {
        return !Character.isLetterOrDigit(ch) && ch != '_' && ch != '.';
    }
}
