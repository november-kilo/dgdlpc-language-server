package novemberkilo.dgdlpclangserver.langserver.action.inherit;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InheritStatementUtilsTest {
    @Test
    void extractShouldHandleValidSingleLineRange() {
        String content = "Line 1\nLine 2\nLine 3";
        Range range = new Range(new Position(1, 0), new Position(1, 4));

        String result = InheritStatementUtils.extract(content, range);

        assertEquals("Line", result);
    }

    @Test
    void extractShouldHandleValidMultiLineRange() {
        String content = "Line 1\nLine 2\nLine 3";
        Range range = new Range(new Position(0, 5), new Position(2, 4));

        String result = InheritStatementUtils.extract(content, range);

        assertEquals("1\nLine 2\nLine", result);
    }

    @Test
    void extractShouldHandleEmptyContent() {
        String content = "";
        Range range = new Range(new Position(0, 0), new Position(0, 4));

        String result = InheritStatementUtils.extract(content, range);

        assertEquals("", result);
    }

    @Test
    void extractShouldHandleNullContent() {
        String content = null;
        Range range = new Range(new Position(0, 0), new Position(0, 4));

        String result = InheritStatementUtils.extract(content, range);

        assertEquals("", result);
    }

    @Test
    void extractShouldHandleInvalidRange_OutOfBounds() {
        String content = "Line 1\nLine 2\nLine 3";
        Range range = new Range(new Position(3, 0), new Position(3, 4));

        String result = InheritStatementUtils.extract(content, range);

        assertEquals("", result);
    }

    @Test
    void extractShouldHandleInvalidRange_StartBeforeZero() {
        String content = "Line 1\nLine 2\nLine 3";
        Range range = new Range(new Position(-1, 0), new Position(1, 4));

        String result = InheritStatementUtils.extract(content, range);

        assertEquals("", result);
    }

    @Test
    void extractShouldHandleValidSingleCharacterRange() {
        String content = "Line 1\nLine 2\nLine 3";
        Range range = new Range(new Position(1, 2), new Position(1, 3));

        String result = InheritStatementUtils.extract(content, range);

        assertEquals("n", result);
    }

    @Test
    void extractShouldHandleInvalidRange_EndBeforeZero() {
        String content = "Line 1\nLine 2\nLine 3";
        Range range = new Range(new Position(0, 0), new Position(-1, 4));

        String result = InheritStatementUtils.extract(content, range);

        assertEquals("", result);
    }

    @Test
    void extractShouldHandleInvalidRange_StartValidEndOutOfBounds() {
        String content = "Line 1\nLine 2\nLine 3";
        Range range = new Range(new Position(1, 0), new Position(3, 4));

        String result = InheritStatementUtils.extract(content, range);

        assertEquals("", result);
    }

    @Test
    void extractShouldHandleInvalidRange_StartOutOfBoundsEndValid() {
        String content = "Line 1\nLine 2\nLine 3";
        Range range = new Range(new Position(3, 0), new Position(2, 4));

        String result = InheritStatementUtils.extract(content, range);

        assertEquals("", result);
    }

    @Test
    void extractShouldHandleInvalidRange_BothNegative() {
        String content = "Line 1\nLine 2\nLine 3";
        Range range = new Range(new Position(-1, 0), new Position(-2, 4));

        String result = InheritStatementUtils.extract(content, range);

        assertEquals("", result);
    }

    @Test
    void extractShouldHandleInvalidRange_BothOutOfBounds() {
        String content = "Line 1\nLine 2\nLine 3";
        Range range = new Range(new Position(4, 0), new Position(5, 4));

        String result = InheritStatementUtils.extract(content, range);

        assertEquals("", result);
    }
}
