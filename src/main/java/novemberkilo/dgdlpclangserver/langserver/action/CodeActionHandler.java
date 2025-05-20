package novemberkilo.dgdlpclangserver.langserver.action;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.List;

public interface CodeActionHandler {
    List<Either<Command, CodeAction>> handleCodeAction();

    boolean canHandleCodeAction(String diagnosticCode);
}
