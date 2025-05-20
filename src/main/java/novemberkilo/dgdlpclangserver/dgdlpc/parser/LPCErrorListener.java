package novemberkilo.dgdlpclangserver.dgdlpc.parser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.eclipse.lsp4j.Diagnostic;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LPCErrorListener extends BaseErrorListener {
    @Getter
    private final List<Diagnostic> diagnostics = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException recognitionException) {
        if (recognitionException instanceof InputMismatchException) {
            // TODO: create diagnostic for ';'
            log.warn("Input mismatch at line {}:{} {}", line, charPositionInLine, msg);
        }
    }
}
