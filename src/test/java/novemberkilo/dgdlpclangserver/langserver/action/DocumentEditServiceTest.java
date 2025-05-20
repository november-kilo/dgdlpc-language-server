package novemberkilo.dgdlpclangserver.langserver.action;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentEditServiceTest {
    @Test
    public void createMoveInheritEditReturnsExpectedResult() {
        String uri = "file:///example/file.c";
        String content = "inherit \"/path/to/lib.c\";\n";
        Range sourceRange = new Range(new Position(0, 0), new Position(0, content.length() - 1));
        Position targetPosition = new Position(1, 0);
        DocumentEditService service = new DocumentEditService();

        WorkspaceEdit edit = service.createMoveInheritEdit(uri, content, sourceRange, targetPosition);

        assertThat(edit).isNotNull();
        assertThat(edit.getChanges()).isNotNull().hasSize(1);
        assertThat(edit.getChanges().get(uri).get(0).getNewText()).isEqualTo("");
        assertThat(edit.getChanges().get(uri).get(1).getNewText()).isEqualTo(content);
    }
}
