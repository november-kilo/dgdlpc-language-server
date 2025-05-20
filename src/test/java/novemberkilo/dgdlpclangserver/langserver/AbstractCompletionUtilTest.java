package novemberkilo.dgdlpclangserver.langserver;

import novemberkilo.dgdlpclangserver.dgdlpc.parser.LPCParserService;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.LPCParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AbstractCompletionUtilTest {

    @Mock
    private LPCParserService parserService;

    @Mock
    private LPCParseTreeVisitor<String> visitor;

    @Mock
    private LPCTextDocumentService textDocumentService;

    @Mock
    private ParseTree parseTree;

    private CompletionParams completionParams;
    private TestCompletionUtil util;

    @BeforeEach
    public void setUp() {
        completionParams = new CompletionParams();
        completionParams.setTextDocument(new TextDocumentIdentifier("test.lpc"));
        util = new TestCompletionUtil(parserService, visitor, false);
    }

    @Test
    public void shouldReturnDefaultValueWhenNoOverride() {
        TestCompletionItemNoOverrides util = new TestCompletionItemNoOverrides(parserService, visitor);

        assertThat(util.matchesPrefix("test", "test")).isTrue();
        assertThat(util.getAdditionalFilter()).isEmpty();
    }

    @Test
    public void shouldReturnAllItemsForEmptyPrefix() {
        List<String> definitions = Arrays.asList("test1", "test2", "other");
        setupMocks(definitions);

        List<CompletionItem> results = util.completionsFor(textDocumentService, completionParams, "");

        assertThat(results).hasSize(3)
                .extracting("label")
                .containsExactlyInAnyOrder("test1", "test2", "other");
    }

    @Test
    public void shouldReturnFilteredItemsForPrefix() {
        List<String> definitions = Arrays.asList("test1", "test2", "other");
        setupMocks(definitions);

        List<CompletionItem> results = util.completionsFor(textDocumentService, completionParams, "test");

        assertThat(results).hasSize(2)
                .extracting("label")
                .containsExactlyInAnyOrder("test1", "test2");
    }

    @Test
    public void shouldApplyAdditionalFilterWhenProvided() {
        util = new TestCompletionUtil(parserService, visitor, true);
        List<String> definitions = Arrays.asList("one", "two", "three");
        setupMocks(definitions);

        List<CompletionItem> results = util.completionsFor(textDocumentService, completionParams, "");

        assertThat(results).hasSize(1)
                .extracting("label")
                .containsExactly("three");
    }

    @Test
    public void shouldReturnEmptyListWhenNoDefinitionsFound() {
        when(textDocumentService.getDocumentContentIfPresent(anyString()))
                .thenReturn(Optional.empty());

        List<String> results = util.getAllDefinitions(textDocumentService, completionParams);

        assertThat(results).isEmpty();
    }

    @Test
    public void shouldReturnAllFoundDefinitions() {
        List<String> expectedDefinitions = Arrays.asList("test1", "test2");
        setupMocks(expectedDefinitions);

        List<String> results = util.getAllDefinitions(textDocumentService, completionParams);

        assertThat(results).isEqualTo(expectedDefinitions);
    }

    private void setupMocks(List<String> definitions) {
        when(textDocumentService.getDocumentContentIfPresent(anyString()))
                .thenReturn(Optional.of("content"));
        when(parserService.parse(anyString())).thenReturn(parseTree);
        when(visitor.getAll()).thenReturn(definitions);
    }

    private static class TestCompletionItemNoOverrides extends AbstractCompletionUtil<String> {
        public TestCompletionItemNoOverrides(LPCParserService parserService, LPCParseTreeVisitor<String> visitor) {
            super(parserService, visitor);
        }

        @Override
        public CompletionItem createCompletionItem(String entry) {
            return null;
        }
    }

    private static class TestCompletionUtil extends AbstractCompletionUtil<String> {
        private final boolean shouldAddFilter;

        public TestCompletionUtil(LPCParserService parserService,
                                  LPCParseTreeVisitor<String> visitor,
                                  boolean shouldAddFilter) {
            super(parserService, visitor);
            this.shouldAddFilter = shouldAddFilter;
        }

        @Override
        public CompletionItem createCompletionItem(String entry) {
            CompletionItem item = new CompletionItem();
            item.setLabel(entry);
            return item;
        }

        @Override
        public boolean matchesPrefix(String entry, String prefix) {
            return entry.startsWith(prefix);
        }

        @Override
        public Optional<Predicate<String>> getAdditionalFilter() {
            return shouldAddFilter
                    ? Optional.of(s -> s.length() > 3)
                    : Optional.empty();
        }
    }
}

