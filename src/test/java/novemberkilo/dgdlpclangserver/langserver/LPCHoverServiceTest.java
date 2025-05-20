package novemberkilo.dgdlpclangserver.langserver;

import novemberkilo.dgdlpclangserver.dgdlpc.json.JsonDocLoader;
import novemberkilo.dgdlpclangserver.dgdlpc.json.KfunsDocLoader;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.LPCKeywords;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class LPCHoverServiceTest {
    private final KfunsDocLoader kfunsDocLoader = new KfunsDocLoader();
    private final JsonDocLoader<LPCKeywords> keywordsDocLoader = new JsonDocLoader<>(LPCKeywords.class, "keywords.json");
    private final LPCTextDocumentService textDocumentService = Mockito.mock(LPCTextDocumentService.class);
    private final TextDocumentIdentifier identifier = new TextDocumentIdentifier("file:///test/file.lpc");
    private final Position position = new Position(0, 0);
    private final HoverParams hoverParams = new HoverParams(identifier, position);
    private final String documentUri = "file:///test/file.lpc";
    private LPCHoverService lpcHoverService;

    @BeforeEach
    public void setup() {
        lpcHoverService = new LPCHoverService(
                kfunsDocLoader.load().kfuns(),
                keywordsDocLoader.load().keywords()

        );
    }

    @Test
    public void hoverKfun() throws ExecutionException, InterruptedException {
        Mockito.when(textDocumentService.getWordAtPosition(documentUri, position)).thenReturn(Optional.of("acos"));

        var hover = lpcHoverService.hover(textDocumentService, hoverParams);
        hover.join();

        assertThat(hover).isNotNull();
        assertThat(hover.get().getContents().getLeft().toString()).contains("acos");
    }

    @Test
    public void hoverKeyword() throws ExecutionException, InterruptedException {
        Mockito.when(textDocumentService.getWordAtPosition(documentUri, position)).thenReturn(Optional.of("atomic"));

        var hover = lpcHoverService.hover(textDocumentService, hoverParams);
        hover.join();

        assertThat(hover).isNotNull();
        assertThat(hover.get().getContents().getLeft().toString()).contains("atomic");
    }

    @Test
    public void hoverUnknownWord() throws ExecutionException, InterruptedException {
        Mockito.when(textDocumentService.getWordAtPosition(documentUri, position)).thenReturn(Optional.of("foobar"));

        var hover = lpcHoverService.hover(textDocumentService, hoverParams);
        hover.join();

        assertThat(hover).isNotNull();
        assertThat(hover.get()).isNull();
    }
}
