package novemberkilo.dgdlpclangserver.langserver;

import novemberkilo.dgdlpclangserver.dgdlpc.parser.LPCParserService;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.LPCParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractCompletionUtil<T> {
    private final LPCParserService parserService;
    private final LPCParseTreeVisitor<T> visitor;

    public AbstractCompletionUtil(LPCParserService parserService,
                                  LPCParseTreeVisitor<T> visitor) {
        this.parserService = parserService;
        this.visitor = visitor;
    }

    public List<CompletionItem> completionsFor(LPCTextDocumentService textDocumentService,
                                               CompletionParams completionParams,
                                               String prefix) {
        List<T> definitions = getAllDefinitions(textDocumentService, completionParams);

        Stream<T> stream = definitions.stream();

        Predicate<T> finalPredicate = entry ->
                prefix.isEmpty() || matchesPrefix(entry, prefix);

        Optional<Predicate<T>> additionalFilter = getAdditionalFilter();
        if (additionalFilter.isPresent()) {
            finalPredicate = finalPredicate.and(additionalFilter.get());
        }

        return stream
                .filter(finalPredicate)
                .distinct()
                .map(this::createCompletionItem)
                .toList();
    }

    public abstract CompletionItem createCompletionItem(T entry);

    public List<T> getAllDefinitions(@NotNull LPCTextDocumentService textDocumentService,
                                     @NotNull CompletionParams completionParams) {
        String documentUri = completionParams.getTextDocument().getUri();
        Optional<String> content = textDocumentService.getDocumentContentIfPresent(documentUri);
        return content.map(this::parseAndVisitDocument).orElse(List.of());
    }

    private List<T> parseAndVisitDocument(String documentContent) {
        ParseTree parseTree = parserService.parse(documentContent);
        visitor.visit(parseTree);
        return visitor.getAll();
    }

    public boolean matchesPrefix(T entry, String prefix) {
        return true;
    }

    public Optional<Predicate<T>> getAdditionalFilter() {
        return Optional.empty();
    }
}
