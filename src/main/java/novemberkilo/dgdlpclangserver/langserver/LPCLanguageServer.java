package novemberkilo.dgdlpclangserver.langserver;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.CodeActionOptions;
import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.FileOperationFilter;
import org.eclipse.lsp4j.FileOperationOptions;
import org.eclipse.lsp4j.FileOperationPattern;
import org.eclipse.lsp4j.FileOperationsServerCapabilities;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.TextDocumentSyncOptions;
import org.eclipse.lsp4j.WorkspaceFoldersOptions;
import org.eclipse.lsp4j.WorkspaceServerCapabilities;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class LPCLanguageServer implements LanguageServer, LanguageClientAware {
    private final LPCTextDocumentService textDocumentService;
    private final LPCWorkspaceService workspaceService;

    public LPCLanguageServer(
            LPCTextDocumentService textDocumentService,
            LPCWorkspaceService workspaceService
    ) {
        this.textDocumentService = textDocumentService;
        this.workspaceService = workspaceService;
        log.info("Initialized LPC language server");
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        ServerCapabilities capabilities = new ServerCapabilities();

        addTextDocumentSyncCapability(capabilities);
        addCompletionCapability(capabilities);
        addHoverCapability(capabilities);
        addCodeActionCapability(capabilities);
        addWorkspaceCapability(capabilities);

        return CompletableFuture.completedFuture(new InitializeResult(capabilities));
    }

    private void addWorkspaceCapability(@NotNull ServerCapabilities capabilities) {
        capabilities.setWorkspace(createWorkspaceServerCapabilities());
        capabilities.setWorkspaceSymbolProvider(true);
    }

    private @NotNull WorkspaceServerCapabilities createWorkspaceServerCapabilities() {
        WorkspaceServerCapabilities workspaceCapabilities = new WorkspaceServerCapabilities();
        workspaceCapabilities.setWorkspaceFolders(createWorkspaceFoldersOptions());
        workspaceCapabilities.setFileOperations(createFileOperationsCapabilities());
        return workspaceCapabilities;
    }

    private @NotNull WorkspaceFoldersOptions createWorkspaceFoldersOptions() {
        WorkspaceFoldersOptions workspaceFoldersOptions = new WorkspaceFoldersOptions();
        workspaceFoldersOptions.setSupported(true);
        workspaceFoldersOptions.setChangeNotifications(true);
        return workspaceFoldersOptions;
    }

    private @NotNull FileOperationsServerCapabilities createFileOperationsCapabilities() {
        FileOperationsServerCapabilities fileOperationsServerCapabilities = new FileOperationsServerCapabilities();
        FileOperationOptions fileOperationOptions = createFileOperationOptions();

        fileOperationsServerCapabilities.setDidCreate(fileOperationOptions);
        fileOperationsServerCapabilities.setDidRename(fileOperationOptions);
        fileOperationsServerCapabilities.setDidDelete(fileOperationOptions);

        return fileOperationsServerCapabilities;
    }

    @Contract(" -> new")
    private @NotNull FileOperationOptions createFileOperationOptions() {
        FileOperationFilter fileOperationFilter = new FileOperationFilter();
        fileOperationFilter.setPattern(new FileOperationPattern("**/*.[ch]"));

        return new FileOperationOptions(Collections.singletonList(fileOperationFilter));
    }

    private void addCodeActionCapability(@NotNull ServerCapabilities capabilities) {
        capabilities.setCodeActionProvider(new CodeActionOptions(List.of(
                CodeActionKind.QuickFix
        )));
    }

    private void addHoverCapability(@NotNull ServerCapabilities capabilities) {
        capabilities.setHoverProvider(true);
    }

    private void addCompletionCapability(@NotNull ServerCapabilities capabilities) {
        CompletionOptions completionOptions = new CompletionOptions();
        completionOptions.setTriggerCharacters(Arrays.asList(".", "->"));
        capabilities.setCompletionProvider(completionOptions);
    }

    private void addTextDocumentSyncCapability(@NotNull ServerCapabilities capabilities) {
        TextDocumentSyncOptions syncOptions = new TextDocumentSyncOptions();
        syncOptions.setChange(TextDocumentSyncKind.Full);
        syncOptions.setOpenClose(true);
        capabilities.setTextDocumentSync(syncOptions);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return CompletableFuture.completedFuture(null);
    }

    @Generated
    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public void connect(LanguageClient client) {
        this.textDocumentService.connect(client);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }
}
