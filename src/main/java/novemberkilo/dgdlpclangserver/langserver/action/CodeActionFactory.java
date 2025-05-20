package novemberkilo.dgdlpclangserver.langserver.action;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CodeActionFactory {
    private final DocumentEditService documentEditService;

    public CodeActionFactory(DocumentEditService documentEditService) {
        this.documentEditService = documentEditService;
    }

    public CodeAction createMoveInheritAction(String uri, String content, Diagnostic diagnostic, @NotNull PositionDetails insertPosition) {
        CodeAction action = createQuickFix("Move inherit statement to top", diagnostic);
        Position position = new Position(insertPosition.startLine(), insertPosition.startColumn());
        action.setEdit(documentEditService.createMoveInheritEdit(
                uri,
                content,
                diagnostic.getRange(),
                position
        ));

        return action;
    }

    private @NotNull CodeAction createQuickFix(String title, Diagnostic diagnostic) {
        CodeAction action = new CodeAction(title);
        action.setKind(CodeActionKind.QuickFix);
        action.setDiagnostics(List.of(diagnostic));
        return action;
    }
}
