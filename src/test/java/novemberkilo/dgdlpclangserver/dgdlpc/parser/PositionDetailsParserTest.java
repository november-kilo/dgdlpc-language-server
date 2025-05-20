package novemberkilo.dgdlpclangserver.dgdlpc.parser;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class PositionDetailsParserTest {
    @Test
    void shouldParsePositionDetailsFromContext() {
        Token startToken = Mockito.mock(Token.class);
        Token stopToken = Mockito.mock(Token.class);

        Mockito.when(startToken.getLine()).thenReturn(2);
        Mockito.when(startToken.getCharPositionInLine()).thenReturn(4);
        Mockito.when(startToken.getStartIndex()).thenReturn(10);

        Mockito.when(stopToken.getLine()).thenReturn(4);
        Mockito.when(stopToken.getCharPositionInLine()).thenReturn(16);
        Mockito.when(stopToken.getStopIndex()).thenReturn(42);

        ParserRuleContext context = new MockParserRuleContext(startToken, stopToken);

        PositionDetails result = PositionDetailsParser.fromContext(context);

        assertThat(result.startLine()).isEqualTo(1);
        assertThat(result.startColumn()).isEqualTo(4);
        assertThat(result.startIndex()).isEqualTo(10);
        assertThat(result.endLine()).isEqualTo(3);
        assertThat(result.endColumn()).isEqualTo(16);
        assertThat(result.endIndex()).isEqualTo(42);
    }

    static class MockParserRuleContext extends ParserRuleContext {
        public MockParserRuleContext(Token start, Token stop) {
            this.start = start;
            this.stop = stop;
        }
    }
}
