package novemberkilo.dgdlpclangserver.langserver.hover;

import novemberkilo.dgdlpclangserver.dgdlpc.json.JsonDocLoader;
import novemberkilo.dgdlpclangserver.dgdlpc.json.KfunsDocLoader;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.Kfun;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.LPCKeyword;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.LPCKeywords;
import novemberkilo.dgdlpclangserver.langserver.LPCLanguageServerContext;
import novemberkilo.dgdlpclangserver.langserver.LPCTextDocumentService;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WordHoverSupplierTest {
    @Test
    public void testPartialConstructorUsesContext() {
        KfunsDocLoader kfunsDocLoader = new KfunsDocLoader();
        LPCLanguageServerContext mockContext = Mockito.mock(LPCLanguageServerContext.class);
        LPCTextDocumentService textDocumentService = mock(LPCTextDocumentService.class);
        HoverParams hoverParams = new HoverParams(new TextDocumentIdentifier("file:///example"), new Position(5, 5));
        Mockito.when(mockContext.getKfuns()).thenReturn(kfunsDocLoader.load().kfuns());
        Mockito.when(mockContext.getKeywords()).thenReturn(Map.of());
        when(textDocumentService.getWordAtPosition("file:///example", hoverParams.getPosition()))
                .thenReturn(Optional.of("acos"));

        LPCLanguageServerContext.setInstance(mockContext);

        try {
            WordHoverSupplier supplier = new WordHoverSupplier(textDocumentService, hoverParams);
            Hover hover = supplier.get();

            assertThat(hover.getContents().getLeft().toString()).contains("acos");
            verify(textDocumentService).getWordAtPosition("file:///example", hoverParams.getPosition());
        } finally {
            LPCLanguageServerContext.setInstance(null);
        }
    }

    @Test
    public void testGetWhenKfunFound() {
        KfunsDocLoader kfunsDocLoader = new KfunsDocLoader();
        LPCTextDocumentService textDocumentService = mock(LPCTextDocumentService.class);
        HoverParams hoverParams = new HoverParams(new TextDocumentIdentifier("file:///example"), new Position(5, 5));
        Map<String, Kfun> kfuns = kfunsDocLoader.load().kfuns();
        Map<String, LPCKeyword> keywords = Map.of();

        when(textDocumentService.getWordAtPosition("file:///example", hoverParams.getPosition()))
                .thenReturn(Optional.of("acos"));

        WordHoverSupplier supplier = new WordHoverSupplier(textDocumentService, hoverParams, kfuns, keywords);

        Hover hover = supplier.get();

        assertThat(hover.getContents().getLeft().toString()).contains("acos");
        verify(textDocumentService).getWordAtPosition("file:///example", hoverParams.getPosition());
    }

    @Test
    public void testGetWhenKeywordFound() {
        JsonDocLoader<LPCKeywords> keywordsDocLoader = new JsonDocLoader<>(LPCKeywords.class, "keywords.json");
        LPCTextDocumentService textDocumentService = mock(LPCTextDocumentService.class);
        HoverParams hoverParams = new HoverParams(new TextDocumentIdentifier("file:///example"), new Position(5, 5));
        Map<String, Kfun> kfuns = Map.of();
        Map<String, LPCKeyword> keywords = keywordsDocLoader.load().keywords();

        when(textDocumentService.getWordAtPosition("file:///example", hoverParams.getPosition()))
                .thenReturn(Optional.of("atomic"));

        WordHoverSupplier supplier = new WordHoverSupplier(textDocumentService, hoverParams, kfuns, keywords);

        Hover hover = supplier.get();

        assertThat(hover.getContents().getLeft().toString()).contains("atomic");
        verify(textDocumentService).getWordAtPosition("file:///example", hoverParams.getPosition());
    }

    @Test
    public void testGetWhenNoMatchFound() {
        LPCTextDocumentService textDocumentService = mock(LPCTextDocumentService.class);
        HoverParams hoverParams = new HoverParams(new TextDocumentIdentifier("file:///example"), new Position(5, 5));
        Map<String, Kfun> kfuns = Map.of();
        Map<String, LPCKeyword> keywords = Map.of();

        when(textDocumentService.getWordAtPosition("file:///example", hoverParams.getPosition()))
                .thenReturn(Optional.of("nonexistentWord"));

        WordHoverSupplier supplier = new WordHoverSupplier(textDocumentService, hoverParams, kfuns, keywords);

        Hover hover = supplier.get();

        assertThat(hover).isNull();
        verify(textDocumentService).getWordAtPosition("file:///example", hoverParams.getPosition());
    }

    @Test
    public void testGetWhenWordNotFound() {
        LPCTextDocumentService textDocumentService = mock(LPCTextDocumentService.class);
        HoverParams hoverParams = new HoverParams(new TextDocumentIdentifier("file:///example"), new Position(5, 5));
        Map<String, Kfun> kfuns = Map.of();
        Map<String, LPCKeyword> keywords = Map.of();

        when(textDocumentService.getWordAtPosition("file:///example", hoverParams.getPosition()))
                .thenReturn(Optional.empty());

        WordHoverSupplier supplier = new WordHoverSupplier(textDocumentService, hoverParams, kfuns, keywords);

        Hover hover = supplier.get();

        assertThat(hover).isNull();
        verify(textDocumentService).getWordAtPosition("file:///example", hoverParams.getPosition());
    }

    @Test
    public void testGetWhenWordIsEmpty() {
        LPCTextDocumentService textDocumentService = mock(LPCTextDocumentService.class);
        HoverParams hoverParams = new HoverParams(new TextDocumentIdentifier("file:///example"), new Position(5, 5));
        Map<String, Kfun> kfuns = Map.of();
        Map<String, LPCKeyword> keywords = Map.of();

        when(textDocumentService.getWordAtPosition("file:///example", hoverParams.getPosition()))
                .thenReturn(Optional.of(""));

        WordHoverSupplier supplier = new WordHoverSupplier(textDocumentService, hoverParams, kfuns, keywords);

        Hover hover = supplier.get();

        assertThat(hover).isNull();
        verify(textDocumentService).getWordAtPosition("file:///example", hoverParams.getPosition());
    }
}
