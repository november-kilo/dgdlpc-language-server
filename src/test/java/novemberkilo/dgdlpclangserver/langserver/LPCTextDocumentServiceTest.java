package novemberkilo.dgdlpclangserver.langserver;

import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.eclipse.lsp4j.services.LanguageClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LPCTextDocumentServiceTest {
    private final LPCCodeActionService codeActionService = mock(LPCCodeActionService.class);
    private final LPCHoverService hoverService = mock(LPCHoverService.class);
    private final LPCCompletionService completionService = mock(LPCCompletionService.class);
    private final LPCLanguageServer server = mock(LPCLanguageServer.class);
    private final LanguageClient mockClient = mock(LanguageClient.class);
    private LPCTextDocumentService textDocumentService;

    @BeforeEach
    public void setup() {
        textDocumentService = Mockito.spy(new LPCTextDocumentService(
                codeActionService,
                completionService,
                hoverService
        ));
        textDocumentService.connect(mockClient);
    }

    @Test
    public void didOpenShouldHandleEmptyDocument() {
        TextDocumentItem emptyDocument = new TextDocumentItem("file://empty", "lpc", 1, "");
        DidOpenTextDocumentParams params = new DidOpenTextDocumentParams(emptyDocument);

        textDocumentService.didOpen(params);

        assertThat(textDocumentService.getDocumentContent("file://empty")).isEqualTo("");
    }

    @Test
    public void didOpenShouldOverwriteExistingDocumentContent() {
        TextDocumentItem initialDocument = new TextDocumentItem("file://example3", "lpc", 1, "initial content");
        DidOpenTextDocumentParams initialParams = new DidOpenTextDocumentParams(initialDocument);
        textDocumentService.didOpen(initialParams);

        TextDocumentItem updatedDocument = new TextDocumentItem("file://example3", "lpc", 1, "updated content");
        DidOpenTextDocumentParams updatedParams = new DidOpenTextDocumentParams(updatedDocument);
        textDocumentService.didOpen(updatedParams);

        assertThat(textDocumentService.getDocumentContent("file://example3")).isEqualTo("updated content");
    }

    @Test
    public void didOpenShouldPublishDiagnostics() {
        TextDocumentItem textDocument = new TextDocumentItem("file://example4", "lpc", 1, "diagnostic test content");
        DidOpenTextDocumentParams params = new DidOpenTextDocumentParams(textDocument);

        textDocumentService.didOpen(params);

        verify(mockClient, times(1)).publishDiagnostics(any(PublishDiagnosticsParams.class));
    }

    @Test
    public void didOpenShouldStoreDocumentInMemory() {
        TextDocumentItem textDocument = new TextDocumentItem("file://example1", "lpc", 1, "example content");
        DidOpenTextDocumentParams params = new DidOpenTextDocumentParams(textDocument);

        textDocumentService.didOpen(params);

        assertThat(textDocumentService.getDocumentContent("file://example1")).isEqualTo("example content");
    }

    @Test
    public void didOpenShouldValidateDocument() {
        TextDocumentItem textDocument = new TextDocumentItem("file://example2", "lpc", 1, "content to validate");
        DidOpenTextDocumentParams params = new DidOpenTextDocumentParams(textDocument);

        doNothing().when(textDocumentService).validateDocument(any(TextDocumentIdentifier.class));
        textDocumentService.didOpen(params);

        verify(textDocumentService, times(1)).validateDocument(any(TextDocumentIdentifier.class));
    }

    @Test
    public void didChangeShouldUpdateDocumentContent() {
        TextDocumentItem initialDocument = new TextDocumentItem("file://test", "lpc", 1, "initial content");
        textDocumentService.didOpen(new DidOpenTextDocumentParams(initialDocument));

        VersionedTextDocumentIdentifier identifier = new VersionedTextDocumentIdentifier("file://test", 2);
        TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent("changed content");
        DidChangeTextDocumentParams changeParams = new DidChangeTextDocumentParams(identifier, List.of(changeEvent));

        textDocumentService.didChange(changeParams);

        assertThat(textDocumentService.getDocumentContent("file://test")).isEqualTo("changed content");
    }

    @Test
    public void didChangeShouldValidateDocument() {
        TextDocumentItem initialDocument = new TextDocumentItem("file://test", "lpc", 1, "initial content");
        textDocumentService.didOpen(new DidOpenTextDocumentParams(initialDocument));

        VersionedTextDocumentIdentifier identifier = new VersionedTextDocumentIdentifier("file://test", 2);
        TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent("changed content");
        DidChangeTextDocumentParams changeParams = new DidChangeTextDocumentParams(identifier, List.of(changeEvent));

        doNothing().when(textDocumentService).validateDocument(any(TextDocumentIdentifier.class));
        textDocumentService.didChange(changeParams);

        verify(textDocumentService, times(2)).validateDocument(any(TextDocumentIdentifier.class));
    }

    @Test
    public void didCloseShouldRemoveDocument() {
        TextDocumentItem document = new TextDocumentItem("file://test", "lpc", 1, "content");
        textDocumentService.didOpen(new DidOpenTextDocumentParams(document));

        DidCloseTextDocumentParams closeParams = new DidCloseTextDocumentParams();
        closeParams.setTextDocument(new TextDocumentIdentifier("file://test"));
        textDocumentService.didClose(closeParams);

        assertThat(textDocumentService.getDocumentContent("file://test")).isNull();
    }

    @Test
    public void didCloseShouldClearDiagnostics() {
        TextDocumentItem document = new TextDocumentItem("file://test", "lpc", 1, "content");
        textDocumentService.didOpen(new DidOpenTextDocumentParams(document));

        DidCloseTextDocumentParams closeParams = new DidCloseTextDocumentParams();
        closeParams.setTextDocument(new TextDocumentIdentifier("file://test"));
        textDocumentService.didClose(closeParams);

        verify(mockClient, times(2)).publishDiagnostics(argThat(params ->
                params.getUri().equals("file://test") && params.getDiagnostics().isEmpty()
        ));
    }

    @Test
    public void didSaveShouldValidateDocument() {
        TextDocumentItem document = new TextDocumentItem("file://test", "lpc", 1, "content");
        textDocumentService.didOpen(new DidOpenTextDocumentParams(document));

        DidSaveTextDocumentParams saveParams = new DidSaveTextDocumentParams();
        saveParams.setTextDocument(new TextDocumentIdentifier("file://test"));

        doNothing().when(textDocumentService).validateDocument(any(TextDocumentIdentifier.class));
        textDocumentService.didSave(saveParams);

        verify(textDocumentService, times(2)).validateDocument(any(TextDocumentIdentifier.class));
    }

    @Test
    public void codeActionShouldDelegateToCodeActionService() {
        CodeActionParams params = new CodeActionParams();

        textDocumentService.codeAction(params);

        verify(codeActionService).codeAction(any(LPCTextDocumentService.class), argThat(p -> p.equals(params)));
    }

    @Test
    public void hoverShouldDelegateToHoverService() {
        HoverParams params = new HoverParams();

        textDocumentService.hover(params);

        verify(hoverService).hover(textDocumentService, params);
    }

    @Test
    public void completionShouldDelegateToCompletionService() {
        CompletionParams params = new CompletionParams();

        textDocumentService.completion(params);

        verify(completionService).completion(textDocumentService, params);
    }

    @Test
    void getWordAtPositionIsEmptyWhenDocumentNotOpen() {
        Position position = new Position(0, 0);

        Optional<String> result = textDocumentService.getWordAtPosition("uri", position);

        assertThat(result).isEmpty();
    }

    @Test
    void getWordAtPositionGetsCorrectWord() {
        Position position = new Position(0, 2);
        TextDocumentItem document = new TextDocumentItem("file://test", "lpc", 1, "foo bar");
        textDocumentService.didOpen(new DidOpenTextDocumentParams(document));

        Optional<String> result = textDocumentService.getWordAtPosition("file://test", position);

        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo("foo");
    }

    @Test
    void getDocumentContentIfPresent_WithExistingUri_ShouldReturnContent() {
        String uri = "file:///test.lpc";
        String content = "test content";
        DidOpenTextDocumentParams params = new DidOpenTextDocumentParams(new TextDocumentItem(uri, "lpc", 1, content));
        textDocumentService.didOpen(params);  // Assuming there's a method to add documents

        Optional<String> result = textDocumentService.getDocumentContentIfPresent(uri);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(content);
    }

    @Test
    void getDocumentContentIfPresent_WithNonExistentUri_ShouldReturnEmptyOptional() {
        String uri = "file:///nonexistent.lpc";

        Optional<String> result = textDocumentService.getDocumentContentIfPresent(uri);

        assertThat(result.isPresent()).isFalse();
    }

    @Test
    void getDocumentContentIfPresent_AfterDocumentRemoval_ShouldReturnEmptyOptional() {
        String uri = "file:///test.lpc";
        String content = "test content";
        DidOpenTextDocumentParams params = new DidOpenTextDocumentParams(new TextDocumentItem(uri, "lpc", 1, content));
        textDocumentService.didOpen(params);
        DidCloseTextDocumentParams closeParams = new DidCloseTextDocumentParams(new TextDocumentIdentifier(uri));
        textDocumentService.didClose(closeParams);

        Optional<String> result = textDocumentService.getDocumentContentIfPresent(uri);

        assertThat(result.isPresent()).isFalse();
    }
}
