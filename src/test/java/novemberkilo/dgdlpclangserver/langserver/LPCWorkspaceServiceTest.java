package novemberkilo.dgdlpclangserver.langserver;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams;
import org.eclipse.lsp4j.FileChangeType;
import org.eclipse.lsp4j.FileEvent;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.WorkspaceFoldersChangeEvent;
import org.eclipse.lsp4j.WorkspaceSymbolParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LPCWorkspaceServiceTest {
    private LPCWorkspaceService workspaceService;
    private LPCFileVisitor lpcFileVisitor;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        lpcFileVisitor = new DefaultLPCFileVisitor();
        workspaceService = new LPCWorkspaceService(lpcFileVisitor);
    }

    @Test
    void shouldHandleConfigurationChanges() {
        var params = new DidChangeConfigurationParams();
        params.setSettings("{ \"someConfig\": \"value\" }");

        assertThatNoException()
                .isThrownBy(() -> workspaceService.didChangeConfiguration(params));
    }

    @Test
    void shouldHandleWatchedLPCSourceFileChanges_CreateUpdateDeleteLPCFile() {
        String fileUri = "file:///test.c";
        createUpdateDeleteLPCFile(fileUri);
    }

    @Test
    void shouldHandleWatchedLPCHeaderFileChanges_CreateUpdateDeleteLPCFile() {
        String fileUri = "file:///test.h";
        createUpdateDeleteLPCFile(fileUri);
    }

    private void createUpdateDeleteLPCFile(String fileUri) {
        createFile(fileUri, FileChangeType.Created);
        assertThat(workspaceService.getLPCFiles()).hasSize(1);

        createFile(fileUri, FileChangeType.Changed);
        assertThat(workspaceService.getLPCFiles()).hasSize(1);

        createFile(fileUri, FileChangeType.Deleted);
        assertThat(workspaceService.getLPCFiles()).hasSize(0);
    }

    @Test
    void shouldHandleFileCreatedEventForLPCFile () {
        var fileUri = tempDir.toUri() + "/test.c";
        createFile(fileUri, FileChangeType.Created);

        assertThat(workspaceService.getLPCFiles()).hasSize(1);
    }

    @Test
    void shouldHandleFileCreatedEventForNonLPCFile () {
        var fileUri = tempDir.toUri() + "/test.x";
        createFile(fileUri, FileChangeType.Created);

        assertThat(workspaceService.getLPCFiles()).hasSize(0);
    }

    @Test
    void shouldHandleFileDeletedEventForLPCFile () {
        var fileUri = tempDir.toUri() + "/test.c";
        createFile(fileUri, FileChangeType.Created);
        assertThat(workspaceService.getLPCFiles()).hasSize(1);
        createFile(fileUri, FileChangeType.Deleted);

        assertThat(workspaceService.getLPCFiles()).hasSize(0);
    }

    @Test
    void shouldHandleFileChangedEventForLPCFileInSet () {
        var fileUri = tempDir.toUri() + "/test.c";
        createFile(fileUri, FileChangeType.Created);
        assertThat(workspaceService.getLPCFiles()).hasSize(1);
        var fileEvent = new FileEvent(fileUri, FileChangeType.Changed);
        var params = new DidChangeWatchedFilesParams(List.of(fileEvent));

        assertThatNoException()
                .isThrownBy(() -> workspaceService.didChangeWatchedFiles(params));
    }

    @Test
    void shouldHandleFileChangedEventForLPCFileNotInSet () {
        var fileUri = tempDir.toUri() + "/test.c";
        var fileEvent = new FileEvent(fileUri, FileChangeType.Changed);
        var params = new DidChangeWatchedFilesParams(List.of(fileEvent));

        assertThatNoException()
                .isThrownBy(() -> workspaceService.didChangeWatchedFiles(params));
    }

    @Test
    void shouldHandleFileChangedEventForNonLPCFile () {
        var fileUri = tempDir.toUri() + "/test.x";
        var fileEvent = new FileEvent(fileUri, FileChangeType.Changed);
        var params = new DidChangeWatchedFilesParams(List.of(fileEvent));

        assertThatNoException()
                .isThrownBy(() -> workspaceService.didChangeWatchedFiles(params));
    }

    @Test
    void shouldAddLPCFilesWhenAddingWorkspaceFolder() throws IOException {
        Path cFile = createFile(tempDir, "test.c");
        Path hFile = createFile(tempDir, "test.h");
        Path txtFile = createFile(tempDir, "test.txt");

        WorkspaceFolder workspaceFolder = new WorkspaceFolder(tempDir.toUri().toString(), "temp");
        DidChangeWorkspaceFoldersParams params = new DidChangeWorkspaceFoldersParams(
                new WorkspaceFoldersChangeEvent(List.of(workspaceFolder), Collections.emptyList())
        );

        workspaceService.didChangeWorkspaceFolders(params);

        Set<URI> lpcFiles = workspaceService.getLPCFiles();
        assertThat(lpcFiles)
                .containsExactlyInAnyOrder(cFile.toUri(), hFile.toUri())
                .doesNotContain(txtFile.toUri());
    }

    @Test
    void shouldRemoveLPCFilesWhenRemovingWorkspaceFolder() throws IOException {
        Path subFolder = Files.createDirectory(tempDir.resolve("subfolder"));
        createFile(subFolder, "test.c");
        WorkspaceFolder workspaceFolder = new WorkspaceFolder(tempDir.toUri().toString(), "temp");
        workspaceService.didChangeWorkspaceFolders(new DidChangeWorkspaceFoldersParams(
                new WorkspaceFoldersChangeEvent(List.of(workspaceFolder), Collections.emptyList())
        ));

        workspaceService.didChangeWorkspaceFolders(new DidChangeWorkspaceFoldersParams(
                new WorkspaceFoldersChangeEvent(Collections.emptyList(), List.of(workspaceFolder))
        ));

        assertThat(workspaceService.getLPCFiles()).isEmpty();
    }

    @Test
    void shouldHandleFileChanges() throws IOException {
        Path cFile = createFile(tempDir, "test.c");
        URI fileUri = cFile.toUri();
        FileEvent createEvent = new FileEvent(fileUri.toString(), FileChangeType.Created);
        workspaceService.didChangeWatchedFiles(new DidChangeWatchedFilesParams(List.of(createEvent)));

        assertThat(workspaceService.getLPCFiles()).contains(fileUri);

        FileEvent deleteEvent = new FileEvent(fileUri.toString(), FileChangeType.Deleted);
        workspaceService.didChangeWatchedFiles(new DidChangeWatchedFilesParams(List.of(deleteEvent)));

        assertThat(workspaceService.getLPCFiles()).doesNotContain(fileUri);
    }

    @Test
    void shouldIgnoreNonLPCFiles() throws IOException {
        Path txtFile = createFile(tempDir, "test.txt");
        URI fileUri = txtFile.toUri();

        FileEvent createEvent = new FileEvent(fileUri.toString(), FileChangeType.Created);
        workspaceService.didChangeWatchedFiles(new DidChangeWatchedFilesParams(List.of(createEvent)));

        assertThat(workspaceService.getLPCFiles()).doesNotContain(fileUri);
    }

    @Test
    void shouldHandleNestedFolderStructure() throws IOException {
        Path folder1 = Files.createDirectory(tempDir.resolve("folder1"));
        Path folder2 = Files.createDirectory(folder1.resolve("folder2"));
        Path cFile1 = createFile(folder1, "test1.c");
        Path cFile2 = createFile(folder2, "test2.c");

        WorkspaceFolder workspaceFolder = new WorkspaceFolder(tempDir.toUri().toString(), "temp");
        DidChangeWorkspaceFoldersParams params = new DidChangeWorkspaceFoldersParams(
                new WorkspaceFoldersChangeEvent(List.of(workspaceFolder), Collections.emptyList())
        );

        workspaceService.didChangeWorkspaceFolders(params);

        assertThat(workspaceService.getLPCFiles())
                .containsExactlyInAnyOrder(cFile1.toUri(), cFile2.toUri());
    }

    @Test
    void shouldHandleInaccessibleFolder() {
        URI invalidUri = URI.create("file:///nonexistent/folder/");
        WorkspaceFolder workspaceFolder = new WorkspaceFolder(invalidUri.toString(), "invalid");
        DidChangeWorkspaceFoldersParams params = new DidChangeWorkspaceFoldersParams(
                new WorkspaceFoldersChangeEvent(List.of(workspaceFolder), Collections.emptyList())
        );

        assertThatThrownBy(() -> workspaceService.didChangeWorkspaceFolders(params))
                .isInstanceOf(RuntimeException.class)
                        .hasMessage("java.nio.file.NoSuchFileException: /nonexistent/folder");
    }

    @Test
    void shouldReturnEmptyResultsForSymbolRequest() {
        var params = new WorkspaceSymbolParams("testQuery");

        var result = workspaceService.symbol(params);

        assertThat(result)
                .succeedsWithin(1, java.util.concurrent.TimeUnit.SECONDS)
                .satisfies(either -> assertThat(either.isRight() && either.getRight().isEmpty()).isTrue());
    }

    private Path createFile(Path directory, String fileName) throws IOException {
        Path file = directory.resolve(fileName);
        Files.writeString(file, "test content");
        return file;
    }

    private void createFile(String fileUri, FileChangeType created) {
        var createdEvent = new FileEvent(fileUri, created);
        var createParams = new DidChangeWatchedFilesParams(List.of(createdEvent));
        workspaceService.didChangeWatchedFiles(createParams);
    }
}
