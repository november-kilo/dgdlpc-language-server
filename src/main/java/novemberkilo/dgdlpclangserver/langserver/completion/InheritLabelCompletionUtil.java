package novemberkilo.dgdlpclangserver.langserver.completion;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.inherit.InheritDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.LPCParserService;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.inherit.InheritVisitor;
import novemberkilo.dgdlpclangserver.langserver.AbstractCompletionUtil;
import org.eclipse.lsp4j.CompletionItem;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

public class InheritLabelCompletionUtil extends AbstractCompletionUtil<InheritDefinition> {
    public InheritLabelCompletionUtil(LPCParserService parserService, InheritVisitor inheritVisitor) {
        super(parserService, inheritVisitor);
    }

    @Override
    public CompletionItem createCompletionItem(@NotNull InheritDefinition entry) {
        return CodeSnippetUtil.createInheritLabelCompletionItem(entry.label());
    }

    @Override
    public boolean matchesPrefix(@NotNull InheritDefinition entry, String prefix) {
        if (entry.label().isEmpty()) {
            return false;
        }

        return entry.label().startsWith(prefix);
    }

    @Override
    public Optional<Predicate<InheritDefinition>> getAdditionalFilter() {
        return Optional.of(inheritDefinition -> !inheritDefinition.label().isEmpty());
    }
}
