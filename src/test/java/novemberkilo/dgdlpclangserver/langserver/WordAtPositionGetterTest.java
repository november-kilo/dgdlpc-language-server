package novemberkilo.dgdlpclangserver.langserver;

import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class WordAtPositionGetterTest {

    @Test
    public void testGetWordAtPosition_NullContent() {
        String content = null;
        Position position = new Position(0, 0);

        Optional<String> result = WordAtPositionGetter.getWordAtPosition(content, position);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void testGetWordAtPosition_InvalidPosition_LineOutOfRange() {
        String content = "Hello world\nThis is a test.";
        Position position = new Position(10, 0);

        Optional<String> result = WordAtPositionGetter.getWordAtPosition(content, position);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void testGetWordAtPosition_InvalidPosition_CharacterOutOfRange() {
        String content = "Hello world\nThis is a test.";
        Position position = new Position(0, content.length());

        Optional<String> result = WordAtPositionGetter.getWordAtPosition(content, position);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void testGetWordAtPosition_ValidWord() {
        String content = "Hello world\nThis is a test.";
        Position position = new Position(0, 6);

        Optional<String> result = WordAtPositionGetter.getWordAtPosition(content, position);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo("world");
    }

    @Test
    public void testGetWordAtPosition_PositionAtStartOfWord() {
        String content = "Hello world\nThis is a test.";
        Position position = new Position(0, 0);

        Optional<String> result = WordAtPositionGetter.getWordAtPosition(content, position);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo("Hello");
    }

    @Test
    public void testGetWordAtPosition_PositionBetweenWords() {
        String content = "Hello world\nThis is a test.";
        Position position = new Position(0, content.length());

        Optional<String> result = WordAtPositionGetter.getWordAtPosition(content, position);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void testGetWordAtPosition_EmptyContent() {
        String content = "";
        Position position = new Position(0, 0);

        Optional<String> result = WordAtPositionGetter.getWordAtPosition(content, position);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void testGetWordAtPosition_PositionAtLineEnd() {
        String content = "Hello world\nTest.";
        Position position = new Position(0, content.length());

        Optional<String> result = WordAtPositionGetter.getWordAtPosition(content, position);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void testGetWordAtPosition_ValidWordWithSpecialCharacters() {
        String content = "abc_def.ghi global_value";
        Position position = new Position(0, 4);

        Optional<String> result = WordAtPositionGetter.getWordAtPosition(content, position);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo("abc_def.ghi");
    }

    @Test
    public void testGetWordAtPosition_PositionInsideLongWord() {
        String content = "supercalifragilisticexpialidocious";
        Position position = new Position(0, 15);

        Optional<String> result = WordAtPositionGetter.getWordAtPosition(content, position);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo("supercalifragilisticexpialidocious");
    }

    @Test
    public void testGetWordAtPosition_NegativeLine() {
        String content = "Hello world\nThis is a test.";
        Position position = new Position(-1, content.length());

        Optional<String> result = WordAtPositionGetter.getWordAtPosition(content, position);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void testGetWordAtPosition_NegativeCharacter() {
        String content = "Hello world\nThis is a test.";
        Position position = new Position(0, -1);

        Optional<String> result = WordAtPositionGetter.getWordAtPosition(content, position);

        assertThat(result.isEmpty()).isTrue();
    }
}
