package novemberkilo.dgdlpclangserver.langserver.action.inherit;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import novemberkilo.dgdlpclangserver.langserver.LPCTextDocumentService;
import novemberkilo.dgdlpclangserver.langserver.action.CodeActionFactory;
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
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class InheritCodeActionHandlerTest {
    private final LPCTextDocumentService textDocumentService = Mockito.mock(LPCTextDocumentService.class);
    private final CodeActionFactory codeActionFactory = Mockito.mock(CodeActionFactory.class);

    private CodeActionParams params;
    private InheritCodeActionHandler handler;

    @BeforeEach
    void setUp() {
        TextDocumentIdentifier textDocument = new TextDocumentIdentifier("file:///test.lpc");
        params = new CodeActionParams(textDocument, new Range(
                new Position(0, 0),
                new Position(0, 10)
        ), new CodeActionContext(List.of()));

        handler = new InheritCodeActionHandler(textDocumentService, params, codeActionFactory);
    }

    @Test
    void canHandle_shouldReturnTrueForInheritDiagnostic() {
        assertThat(handler.canHandleCodeAction("misplaced-inherit-statement")).isTrue();
    }

    @Test
    void canHandle_shouldReturnFalseForOtherDiagnostics() {
        assertThat(handler.canHandleCodeAction("some-other-diagnostic")).isFalse();
    }

    @Test
    void handle_shouldProcessValidDiagnostic() {
        String content = "some content";
        Mockito.when(textDocumentService.getDocumentContent(any())).thenReturn(content);

        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setRange(new Range(new Position(0, 0), new Position(0, 10)));
        diagnostic.setCode("misplaced-inherit-statement");
        diagnostic.setMessage("misplaced inherit statement");

        params.getContext().setDiagnostics(List.of(diagnostic));

        CodeAction expectedAction = new CodeAction("test");
        Mockito.when(codeActionFactory.createMoveInheritAction(
                eq(params.getTextDocument().getUri()),
                eq(content),
                eq(diagnostic),
                any(PositionDetails.class)
        )).thenReturn(expectedAction);

        List<Either<Command, CodeAction>> result = handler.handleCodeAction();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getFirst().getRight()).isEqualTo(expectedAction);
    }
}
