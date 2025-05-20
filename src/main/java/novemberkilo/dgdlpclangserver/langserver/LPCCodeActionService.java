package novemberkilo.dgdlpclangserver.langserver;

import lombok.RequiredArgsConstructor;
import novemberkilo.dgdlpclangserver.langserver.action.CodeActionFactory;
import novemberkilo.dgdlpclangserver.langserver.action.inherit.InheritCodeActionHandler;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class LPCCodeActionService {
    private final CodeActionFactory codeActionFactory;

    public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(
            LPCTextDocumentService lpcTextDocumentService,
            CodeActionParams codeActionParams
    ) {
        return CompletableFuture.supplyAsync(() ->
                createInheritHandler(lpcTextDocumentService, codeActionParams).handleCodeAction()
        );
    }

    public InheritCodeActionHandler createInheritHandler(
            LPCTextDocumentService textDocumentService,
            CodeActionParams params
    ) {
        return new InheritCodeActionHandler(textDocumentService, params, codeActionFactory);
    }
}

