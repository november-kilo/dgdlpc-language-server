package novemberkilo.dgdlpclangserver.langserver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.Kfun;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.LPCKeyword;
import novemberkilo.dgdlpclangserver.langserver.hover.WordHoverSupplier;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class LPCHoverService {
    private final Map<String, Kfun> kfuns;
    private final Map<String, LPCKeyword> keywords;

    public CompletableFuture<Hover> hover(LPCTextDocumentService textDocumentService, HoverParams params) {
        return CompletableFuture.supplyAsync(
                new WordHoverSupplier(textDocumentService, params, kfuns, keywords)
        );
    }
}
