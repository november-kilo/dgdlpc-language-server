package novemberkilo.dgdlpclangserver.langserver.action;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CodeActionFactoryTest {

    @Mock
    private DocumentEditService documentEditService;

    private CodeActionFactory codeActionFactory;

    @BeforeEach
    public void setUp() {
        documentEditService = org.mockito.Mockito.mock(DocumentEditService.class);
        codeActionFactory = new CodeActionFactory(documentEditService);
    }

    @Test
    public void createMoveInheritActionShouldCreateValidCodeAction() {
        String uri = "file:///test.lpc";
        String content = "some content";
        Diagnostic diagnostic = createTestDiagnostic();
        PositionDetails insertPosition = new PositionDetails(0, 0, 0, 0, 0, 0);
        WorkspaceEdit expectedEdit = new WorkspaceEdit();

        when(documentEditService.createMoveInheritEdit(
                eq(uri),
                eq(content),
                any(Range.class),
                any(Position.class)
        )).thenReturn(expectedEdit);

        CodeAction result = codeActionFactory.createMoveInheritAction(
                uri,
                content,
                diagnostic,
                insertPosition
        );

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Move inherit statement to top");
        assertThat(result.getKind()).isEqualTo(CodeActionKind.QuickFix);
        assertThat(result.getDiagnostics()).hasSize(1);
        assertThat(result.getDiagnostics().getFirst()).isEqualTo(diagnostic);
        assertThat(result.getEdit()).isEqualTo(expectedEdit);
    }

    @Test
    public void createMoveInheritActionShouldUseCorrectPosition() {
        String uri = "file:///test.lpc";
        String content = "test content";
        Diagnostic diagnostic = createTestDiagnostic();
        PositionDetails insertPosition = new PositionDetails(5, 10, 0, 0, 0, 0);

        codeActionFactory.createMoveInheritAction(uri, content, diagnostic, insertPosition);

        verify(documentEditService).createMoveInheritEdit(
                eq(uri),
                eq(content),
                eq(diagnostic.getRange()),
                eq(new Position(5, 10))
        );
    }

    @Test
    public void createMoveInheritActionShouldHandleZeroPositions() {
        String uri = "file:///test.lpc";
        String content = "";
        Diagnostic diagnostic = createTestDiagnostic();
        PositionDetails insertPosition = new PositionDetails(0, 0, 0, 0, 0, 0);

        CodeAction result = codeActionFactory.createMoveInheritAction(
                uri,
                content,
                diagnostic,
                insertPosition
        );

        assertThat(result).isNotNull();
        verify(documentEditService).createMoveInheritEdit(
                eq(uri),
                eq(content),
                eq(diagnostic.getRange()),
                eq(new Position(0, 0))
        );
    }

    private Diagnostic createTestDiagnostic() {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setRange(new Range(
                new Position(1, 0),
                new Position(1, 10)
        ));
        diagnostic.setMessage("Test diagnostic");
        return diagnostic;
    }
}
