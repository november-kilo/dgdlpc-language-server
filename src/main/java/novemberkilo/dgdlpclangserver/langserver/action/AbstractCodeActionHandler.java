package novemberkilo.dgdlpclangserver.langserver.action;

import novemberkilo.dgdlpclangserver.langserver.LPCTextDocumentService;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCodeActionHandler implements CodeActionHandler {
    protected final LPCTextDocumentService textDocumentService;
    protected final CodeActionParams params;
    protected final CodeActionFactory codeActionFactory;

    protected AbstractCodeActionHandler(
            LPCTextDocumentService textDocumentService,
            CodeActionParams params,
            CodeActionFactory codeActionFactory
    ) {
        this.textDocumentService = textDocumentService;
        this.params = params;
        this.codeActionFactory = codeActionFactory;
    }

    @Override
    public List<Either<Command, CodeAction>> handleCodeAction() {
        String documentContent = textDocumentService.getDocumentContent(params.getTextDocument().getUri());

        return params.getContext().getDiagnostics().stream()
                .filter(this::isRelevantDiagnostic)
                .map(diagnostic -> handleDiagnostic(documentContent, diagnostic))
                .collect(Collectors.toList());
    }

    protected abstract Either<Command, CodeAction> handleDiagnostic(String content, Diagnostic diagnostic);

    protected boolean isRelevantDiagnostic(Diagnostic diagnostic) {
        return diagnostic != null
                && canHandleCodeAction(diagnostic.getCode().getLeft())
                && isWithinRange(diagnostic.getRange(), params.getRange());
    }

    protected boolean isWithinRange(Range diagnosticRange, Range requestedRange) {
        if (requestedRange == null) {
            return true;
        }

        Position start = requestedRange.getStart();
        Position end = requestedRange.getEnd();
        Position diagStart = diagnosticRange.getStart();
        Position diagEnd = diagnosticRange.getEnd();

        return (comparePositions(diagStart, start) >= 0 && comparePositions(diagEnd, end) <= 0);
    }

    private int comparePositions(@NotNull Position pos1, @NotNull Position pos2) {
        if (pos1.getLine() != pos2.getLine()) {
            return Integer.compare(pos1.getLine(), pos2.getLine());
        }
        return Integer.compare(pos1.getCharacter(), pos2.getCharacter());
    }
}
