package novemberkilo.dgdlpclangserver.langserver;

import novemberkilo.dgdlpclangserver.langserver.completion.InheritLabelCompletionUtil;
import novemberkilo.dgdlpclangserver.langserver.completion.KfunCompletionUtil;
import novemberkilo.dgdlpclangserver.langserver.completion.LPCKeywordCompletionUtil;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.services.LanguageClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class LPCLanguageServerTest {
    private final LPCTextDocumentService textDocumentService = Mockito.mock(LPCTextDocumentService.class);
    private final LPCWorkspaceService workspaceService = Mockito.mock(LPCWorkspaceService.class);
    private final KfunCompletionUtil kfunCompletionUtil = Mockito.mock(KfunCompletionUtil.class);
    private final LPCKeywordCompletionUtil keywordCompletionUtil = Mockito.mock(LPCKeywordCompletionUtil.class);
    private final InheritLabelCompletionUtil inheritLabelCompletionUtil = Mockito.mock(InheritLabelCompletionUtil.class);
    private LPCLanguageServer server;
    private final LPCLanguageServerContext mockContext = Mockito.mock(LPCLanguageServerContext.class);

    @BeforeEach
    void setUp() {
        Mockito.when(mockContext.getKfunCompletionUtil()).thenReturn(kfunCompletionUtil);
        Mockito.when(mockContext.getKeywordCompletionUtil()).thenReturn(keywordCompletionUtil);
        Mockito.when(mockContext.getInheritLabelCompletionUtil()).thenReturn(inheritLabelCompletionUtil);
        LPCLanguageServerContext.setInstance(mockContext);
        server = new LPCLanguageServer(textDocumentService, workspaceService);
    }

    @AfterEach
    public void tearDown() {
        LPCLanguageServerContext.setInstance(null);
        Mockito.reset(textDocumentService, workspaceService, kfunCompletionUtil, keywordCompletionUtil, inheritLabelCompletionUtil);
    }

    @Test
    void testInitialization() throws ExecutionException, InterruptedException {
        InitializeParams params = new InitializeParams();
        params.setProcessId(1234);
        params.setRootUri("file:///test/workspace");

        InitializeResult result = server.initialize(params).get();
        ServerCapabilities capabilities = result.getCapabilities();

        assertThat(capabilities).isNotNull();
        assertThat(capabilities.getCompletionProvider()).isNotNull();
        assertThat(capabilities.getHoverProvider()).isNotNull();
        assertThat(capabilities.getDiagnosticProvider()).isNull();
        assertThat(capabilities.getCodeActionProvider()).isNotNull();
        assertThat(capabilities.getWorkspace()).isNotNull();
        assertThat(capabilities.getWorkspaceSymbolProvider().getLeft()).isTrue();
    }

    @Test
    public void testShutdown() {
        CompletableFuture<Object> shutdownFuture = server.shutdown();

        assertThat(shutdownFuture).isNotNull();
    }

    @Test
    public void testGetWorkspaceService() {
        assertThat(server.getWorkspaceService()).isSameAs(workspaceService);
    }

    @Test
    void testHoverProviderIsEnabled() throws ExecutionException, InterruptedException {
        InitializeParams params = new InitializeParams();
        InitializeResult result = server.initialize(params).get();
        ServerCapabilities capabilities = result.getCapabilities();

        assertThat(capabilities.getHoverProvider().getLeft()).isEqualTo(true);
    }

    @Test
    void testTextDocumentSyncOptions() throws ExecutionException, InterruptedException {
        InitializeParams params = new InitializeParams();
        InitializeResult result = server.initialize(params).get();
        ServerCapabilities capabilities = result.getCapabilities();

        assertThat(capabilities.getTextDocumentSync()).isNotNull();
    }

    @Test
    void testCompletionTriggerCharacters() throws ExecutionException, InterruptedException {
        InitializeParams params = new InitializeParams();
        InitializeResult result = server.initialize(params).get();
        ServerCapabilities capabilities = result.getCapabilities();

        assertThat(capabilities.getCompletionProvider().getTriggerCharacters())
                .containsExactly(".", "->");
    }

    @Test
    public void testConnect() {
        LanguageClient client = Mockito.mock(LanguageClient.class);

        server.connect(client);

        Mockito.verify(textDocumentService).connect(client);
    }

    @Test
    public void testGetTextDocumentService() {
        assertThat(server.getTextDocumentService()).isSameAs(textDocumentService);
    }
}
