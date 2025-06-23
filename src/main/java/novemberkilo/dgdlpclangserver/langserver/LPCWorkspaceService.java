package novemberkilo.dgdlpclangserver.langserver;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams;
import org.eclipse.lsp4j.FileEvent;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.WorkspaceSymbol;
import org.eclipse.lsp4j.WorkspaceSymbolParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class LPCWorkspaceService implements WorkspaceService {
    private final LPCFileVisitor lpcFileVisitor;

    public LPCWorkspaceService(LPCFileVisitor lpcFileVisitor) {
        this.lpcFileVisitor = lpcFileVisitor;
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
        // TODO: Handle configuration changes
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        for (FileEvent event : params.getChanges()) {
            URI fileUri = URI.create(event.getUri());

            switch (event.getType()) {
                case Created -> {
                    lpcFileVisitor.add(fileUri);
                    handleNewFile(fileUri);
                }

                case Deleted -> lpcFileVisitor.remove(fileUri);

                case Changed -> {
                    if (lpcFileVisitor.getFiles().contains(fileUri)) {
                        handleFileChanged(fileUri);
                    }
                }
            }
        }
    }

    private void handleFileChanged(URI fileUri) {
        // TODO: handle file changed
    }

    private void handleNewFile(URI fileUri) {
        // TODO: handle new file
    }

    @Override
    public void didChangeWorkspaceFolders(DidChangeWorkspaceFoldersParams params) {
        params.getEvent().getAdded().forEach(folder -> {
            try {
                addWorkspaceFolder(URI.create(folder.getUri()));
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
        });

        params.getEvent().getRemoved().forEach(folder -> removeWorkspaceFolder(URI.create(folder.getUri())));
    }

    public Set<URI> getLPCFiles() {
        return lpcFileVisitor.getFiles();
    }

    private void removeWorkspaceFolder(URI folderUri) {
        lpcFileVisitor.remove(folderUri);
    }

    @Override
    public CompletableFuture<Either<List<? extends SymbolInformation>, List<? extends WorkspaceSymbol>>> symbol(WorkspaceSymbolParams params) {
        return CompletableFuture.completedFuture(Either.forRight(new ArrayList<>()));
    }

    private void addWorkspaceFolder(URI folderUri) throws IOException {
        lpcFileVisitor.addWorkspaceFolder(Paths.get(folderUri));
    }
}
