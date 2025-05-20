package novemberkilo.dgdlpclangserver.langserver.action;

import novemberkilo.dgdlpclangserver.langserver.LPCTextDocumentService;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractCodeActionHandlerTest {
    @Mock
    private LPCTextDocumentService textDocumentService;
    @Mock
    private CodeActionFactory codeActionFactory;

    private TestCodeActionHandler handler;

    @BeforeEach
    public void setUp() {
        TextDocumentIdentifier textDocument = new TextDocumentIdentifier("file:///test.lpc");
        CodeActionParams params = new CodeActionParams(
                textDocument,
                new Range(new Position(0, 0), new Position(2, 0)),
                new CodeActionContext(List.of())
        );
        handler = new TestCodeActionHandler(textDocumentService, params, codeActionFactory, "test-code");
    }

    @Test
    public void isRelevantDiagnostic_ShouldReturnFalseForNullDiagnostic() {
        assertThat(handler.isRelevantDiagnostic(null)).isFalse();
    }

    @Test
    public void isRelevantDiagnostic_ShouldReturnFalseForNonMatchingCode() {
        Diagnostic diagnostic = createDiagnostic("wrong-code", 1, 0, 1, 5);

        assertThat(handler.isRelevantDiagnostic(diagnostic)).isFalse();
    }

    @Test
    void isRelevantDiagnostic_ShouldReturnTrueWhenAllConditionsPass() {
        Diagnostic diagnostic = createDiagnostic("test-code", 1, 0, 1, 5);

        assertThat(handler.isRelevantDiagnostic(diagnostic)).isTrue();
    }

    @Test
    void isRelevantDiagnostic_ShouldReturnFalseWhenCodeMatchesButOutOfRange() {
        Diagnostic diagnostic = createDiagnostic("test-code", 3, 0, 3, 5);

        assertThat(handler.isRelevantDiagnostic(diagnostic)).isFalse();
    }

    @Test
    public void isWithinRange_ShouldReturnTrueForNullRequestedRange() {
        Range diagnosticRange = new Range(new Position(1, 0), new Position(1, 5));

        assertThat(handler.isWithinRange(diagnosticRange, null)).isTrue();
    }

    @Test
    public void isWithinRange_ShouldReturnTrueForContainedRange() {
        Range diagnosticRange = new Range(new Position(1, 0), new Position(1, 5));
        Range requestedRange = new Range(new Position(0, 0), new Position(2, 0));

        assertThat(handler.isWithinRange(diagnosticRange, requestedRange)).isTrue();
    }

    @Test
    public void isWithinRange_ShouldReturnFalseForNonContainedRange() {
        Range diagnosticRange = new Range(new Position(3, 0), new Position(3, 5));
        Range requestedRange = new Range(new Position(0, 0), new Position(2, 0));

        assertThat(handler.isWithinRange(diagnosticRange, requestedRange)).isFalse();
    }

    @Test
    public void isWithinRange_ShouldCompareCharactersWhenLinesAreEqual() {
        Range diagnosticRange = new Range(new Position(1, 5), new Position(1, 10));
        Range requestedRange = new Range(new Position(1, 0), new Position(1, 15));

        assertThat(handler.isWithinRange(diagnosticRange, requestedRange)).isTrue();
    }

    @Test
    public void isWithinRange_ShouldReturnFalseWhenCharactersAreOutOfRange() {
        Range diagnosticRange = new Range(new Position(1, 0), new Position(1, 20));
        Range requestedRange = new Range(new Position(1, 5), new Position(1, 15));

        assertThat(handler.isWithinRange(diagnosticRange, requestedRange)).isFalse();
    }

    private Diagnostic createDiagnostic(String code, int startLine, int startChar, int endLine, int endChar) {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setRange(new Range(
                new Position(startLine, startChar),
                new Position(endLine, endChar)
        ));
        diagnostic.setCode(Either.forLeft(code));
        return diagnostic;
    }

    private static class TestCodeActionHandler extends AbstractCodeActionHandler {
        private final String acceptedCode;

        TestCodeActionHandler(
                LPCTextDocumentService service,
                CodeActionParams params,
                CodeActionFactory factory,
                String acceptedCode
        ) {
            super(service, params, factory);
            this.acceptedCode = acceptedCode;
        }

        @Override
        protected Either<Command, CodeAction> handleDiagnostic(String content, Diagnostic diagnostic) {
            return Either.forRight(new CodeAction("Test Action"));
        }

        @Override
        public boolean canHandleCodeAction(String diagnosticCode) {
            return acceptedCode.equals(diagnosticCode);
        }
    }
}
