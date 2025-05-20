package novemberkilo.dgdlpclangserver.langserver.completion;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.FunctionDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.LPCParserService;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.function.FunctionVisitor;
import novemberkilo.dgdlpclangserver.langserver.AbstractCompletionUtil;
import org.eclipse.lsp4j.CompletionItem;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

public class FunctionCompletionUtil extends AbstractCompletionUtil<FunctionDefinition> {
    public FunctionCompletionUtil(LPCParserService parserService, FunctionVisitor functionVisitor) {
        super(parserService, functionVisitor);
    }

    @Override
    public CompletionItem createCompletionItem(@NotNull FunctionDefinition entry) {
        return CodeSnippetUtil.createFunctionCompletionItem(entry);
    }

    @Override
    public boolean matchesPrefix(@NotNull FunctionDefinition entry, String prefix) {
        return entry.name().startsWith(prefix);
    }

    @Override
    public Optional<Predicate<FunctionDefinition>> getAdditionalFilter() {
        return Optional.of(entry -> !entry.isOperator());
    }
}
