package novemberkilo.dgdlpclangserver.langserver;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class LPCLanguageServerLauncher {
    public static void main(String[] args) {
        try {
            LPCLanguageServerContext.getInstance();
            LPCLanguageServer server = buildLPCLanguageServer();
            log.info("Server created");

            Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(
                    server,
                    System.in,
                    System.out
            );
            log.info("Launcher created");

            LanguageClient client = launcher.getRemoteProxy();
            log.info("Client created");

            server.connect(client);
            log.info("Server connected to client");

            launcher.startListening();
            log.info("Listening for client connections");
        } catch (Exception e) {
            log.error("Error starting language server", e);
            System.exit(1);
        }
    }

    private static @NotNull LPCLanguageServer buildLPCLanguageServer() {
        LPCTextDocumentService textDocumentService;
        LPCWorkspaceService workspaceService;

        textDocumentService = new LPCTextDocumentService(
                new LPCCodeActionService(
                        LPCLanguageServerContext.getInstance().getCodeActionFactory()
                ),
                new LPCCompletionService(
                        LPCLanguageServerContext.getInstance().getKfunCompletionUtil(),
                        LPCLanguageServerContext.getInstance().getKeywordCompletionUtil(),
                        LPCLanguageServerContext.getInstance().getInheritLabelCompletionUtil(),
                        LPCLanguageServerContext.getInstance().getFunctionCompletionUtil()
                ),
                new LPCHoverService(
                        LPCLanguageServerContext.getInstance().getKfuns(),
                        LPCLanguageServerContext.getInstance().getKeywords()
                )
        );

        workspaceService = new LPCWorkspaceService();

        return new LPCLanguageServer(textDocumentService, workspaceService);
    }
}
