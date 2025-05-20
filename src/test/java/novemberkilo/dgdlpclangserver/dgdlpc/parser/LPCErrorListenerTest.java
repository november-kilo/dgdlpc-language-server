package novemberkilo.dgdlpclangserver.dgdlpc.parser;

import novemberkilo.dgdlpclangserver.LPCParser;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.eclipse.lsp4j.Diagnostic;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LPCErrorListenerTest {
    @Test
    void testSyntaxErrorWithInputMismatchException() {
        LPCErrorListener errorListener = new LPCErrorListener();
        Recognizer<?, ?> recognizer = new LPCParser(null);
        int line = 3;
        int charPositionInLine = 5;
        String msg = "Mismatched input error";
        RecognitionException exception = Mockito.mock(InputMismatchException.class);

        errorListener.syntaxError(recognizer, null, line, charPositionInLine, msg, exception);

        List<Diagnostic> diagnostics = errorListener.getDiagnostics();
        assertThat(diagnostics).hasSize(0);
    }

    @Test
    void testSyntaxErrorWithNullException() {
        LPCErrorListener errorListener = new LPCErrorListener();
        Recognizer<?, ?> recognizerMock = mock(Recognizer.class);
        int line = 2;
        int charPositionInLine = 10;
        String msg = "Generic syntax error";

        errorListener.syntaxError(recognizerMock, null, line, charPositionInLine, msg, null);

        List<Diagnostic> diagnostics = errorListener.getDiagnostics();
        assertThat(diagnostics).hasSize(0);
    }
}
