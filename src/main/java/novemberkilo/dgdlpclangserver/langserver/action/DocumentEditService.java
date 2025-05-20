package novemberkilo.dgdlpclangserver.langserver.action;

import novemberkilo.dgdlpclangserver.langserver.action.inherit.InheritStatementUtils;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.List;

public class DocumentEditService {
    public WorkspaceEdit createMoveInheritEdit(String uri, String content,
                                               Range sourceRange, Position targetPosition) {
        String inheritStatement = extractInheritStatement(content, sourceRange);
        List<TextEdit> edits = createLineMoveEdit(sourceRange, targetPosition, inheritStatement);
        return createWorkspaceEdit(uri, edits);
    }

    @Contract("_, _, _ -> new")
    private @NotNull @Unmodifiable List<TextEdit> createLineMoveEdit(Range sourceRange,
                                                                     Position targetPosition,
                                                                     String line) {
        return List.of(
                new TextEdit(sourceRange, ""),
                new TextEdit(
                        new Range(targetPosition, targetPosition),
                        line + "\n"
                )
        );
    }

    private @NotNull WorkspaceEdit createWorkspaceEdit(String uri, List<TextEdit> edits) {
        WorkspaceEdit workspaceEdit = new WorkspaceEdit();
        workspaceEdit.setChanges(new HashMap<>());
        workspaceEdit.getChanges().put(uri, edits);
        return workspaceEdit;
    }

    private @NotNull String extractInheritStatement(String content, Range range) {
        return InheritStatementUtils.extract(content, range);
    }
}
