package novemberkilo.dgdlpclangserver.langserver.hover;

import lombok.RequiredArgsConstructor;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.Kfun;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.LPCKeyword;
import novemberkilo.dgdlpclangserver.langserver.LPCLanguageServerContext;
import novemberkilo.dgdlpclangserver.langserver.LPCTextDocumentService;
import novemberkilo.dgdlpclangserver.langserver.markdown.KeywordMarkdown;
import novemberkilo.dgdlpclangserver.langserver.markdown.KfunMarkdown;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class WordHoverSupplier implements Supplier<Hover> {
    private final LPCTextDocumentService textDocumentService;
    private final HoverParams params;
    private final Map<String, Kfun> kfuns;
    private final Map<String, LPCKeyword> keywords;

    public WordHoverSupplier(LPCTextDocumentService textDocumentService, HoverParams params) {
        this(
                textDocumentService,
                params,
                LPCLanguageServerContext.getInstance().getKfuns(),
                LPCLanguageServerContext.getInstance().getKeywords()
        );
    }

    @Override
    public Hover get() {
        return textDocumentService.getWordAtPosition(
                        params.getTextDocument().getUri(),
                        params.getPosition()
                )
                .filter(word -> !word.isEmpty())
                .flatMap(this::generateMarkdownContent)
                .map(content -> new Hover(List.of(Either.forLeft(content))))
                .orElse(null);
    }

    private Optional<String> generateMarkdownContent(String word) {
        String uri = params.getTextDocument().getUri();

        if (kfuns.containsKey(word)) {
            return Optional.of(KfunMarkdown.create(kfuns.get(word), uri));
        }

        if (keywords.containsKey(word)) {
            return Optional.of(KeywordMarkdown.create(keywords.get(word), uri));
        }

        return Optional.empty();
    }
}
