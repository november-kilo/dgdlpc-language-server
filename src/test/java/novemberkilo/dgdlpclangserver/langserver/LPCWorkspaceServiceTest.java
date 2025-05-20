package novemberkilo.dgdlpclangserver.langserver;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.FileEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;

public class LPCWorkspaceServiceTest {
    private LPCWorkspaceService workspaceService;

    @BeforeEach
    void setUp() {
        workspaceService = new LPCWorkspaceService();
    }

    @Test
    void shouldHandleConfigurationChanges() {
        var params = new DidChangeConfigurationParams();
        params.setSettings("{ \"someConfig\": \"value\" }");

        assertThatNoException()
                .isThrownBy(() -> workspaceService.didChangeConfiguration(params));
    }

    @Test
    void shouldHandleWatchedFileChanges() {
        var params = new DidChangeWatchedFilesParams(List.of(new FileEvent()));

        assertThatNoException()
                .isThrownBy(() -> workspaceService.didChangeWatchedFiles(params));
    }

}
