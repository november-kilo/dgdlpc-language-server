package novemberkilo.dgdlpclangserver.langserver.completion;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.inherit.InheritDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.LPCParserService;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.inherit.InheritVisitor;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class InheritLabelCompletionUtilTest {
    private final LPCParserService parserService = Mockito.mock(LPCParserService.class);
    private final InheritVisitor inheritVisitor = Mockito.mock(InheritVisitor.class);
    private InheritLabelCompletionUtil completionUtil;

    @BeforeEach
    void setUp() {
        completionUtil = new InheritLabelCompletionUtil(parserService, inheritVisitor);
        CompletionParams completionParams = new CompletionParams();
        completionParams.setTextDocument(new TextDocumentIdentifier("file:///test.lpc"));
    }

    private InheritDefinition createInheritDefinition(String label) {
        return new InheritDefinition(
                false,
                label,
                "testPath",
                new PositionDetails(0, 0, 0, 0, 0, 0)
        );
    }

    @Test
    public void shouldCreateValidCompletionItem() {
        InheritDefinition inheritDefinition = createInheritDefinition("testLabel");

        CompletionItem result = completionUtil.createCompletionItem(inheritDefinition);

        assertThat(result).isNotNull();
        assertThat(result.getLabel()).isEqualTo("testLabel");
    }

    @Test
    public void shouldMatchPrefix() {
        InheritDefinition inheritDefinition = createInheritDefinition("testLabel");

        assertThat(completionUtil.matchesPrefix(inheritDefinition, "testL")).isTrue();
        assertThat(completionUtil.matchesPrefix(inheritDefinition, "testP")).isFalse();
    }

    @Test
    public void shouldHandleNullLabel() {
        InheritDefinition inheritDefinition = createInheritDefinition(null);

        assertThat(completionUtil.matchesPrefix(inheritDefinition, "testL")).isFalse();
    }

    @Test
    public void shouldNotFilterLabeledInherit() {
        InheritDefinition inheritDefinition = createInheritDefinition("testLabel");

        Optional<Predicate<InheritDefinition>> filter = completionUtil.getAdditionalFilter();

        assertThat(filter).isPresent();
        assertThat(filter.get().test(inheritDefinition)).isTrue();
    }

    @Test
    public void shouldFilterNotLabeledInherit() {
        InheritDefinition inheritDefinition = createInheritDefinition(null);

        Optional<Predicate<InheritDefinition>> filter = completionUtil.getAdditionalFilter();

        assertThat(filter).isPresent();
        assertThat(filter.get().test(inheritDefinition)).isFalse();
    }
}
