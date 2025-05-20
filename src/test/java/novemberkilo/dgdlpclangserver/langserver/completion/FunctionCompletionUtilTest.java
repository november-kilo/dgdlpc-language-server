package novemberkilo.dgdlpclangserver.langserver.completion;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.FunctionDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.LPCParserService;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.function.FunctionVisitor;
import org.eclipse.lsp4j.CompletionItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class FunctionCompletionUtilTest {
    @Test
    public void testCreateCompletionItem() {
        LPCParserService mockParserService = mock(LPCParserService.class);
        FunctionVisitor mockFunctionVisitor = mock(FunctionVisitor.class);
        FunctionCompletionUtil functionCompletionUtil = new FunctionCompletionUtil(mockParserService, mockFunctionVisitor);
        FunctionDefinition functionDefinition = createFunctionDefinition("testFunction");

        CompletionItem result = functionCompletionUtil.createCompletionItem(functionDefinition);

        assertThat(result.getLabel()).isEqualTo("testFunction");
    }

    @Test
    public void shouldMatchPrefixWhenPrefixMatches() {
        LPCParserService mockParserService = mock(LPCParserService.class);
        FunctionVisitor mockFunctionVisitor = mock(FunctionVisitor.class);
        FunctionCompletionUtil functionCompletionUtil = new FunctionCompletionUtil(mockParserService, mockFunctionVisitor);
        FunctionDefinition functionDefinition = createFunctionDefinition("prefixMatches");
        String prefix = "prefix";

        boolean matches = functionCompletionUtil.matchesPrefix(functionDefinition, prefix);

        assertThat(matches).isTrue();
    }

    @Test
    public void shouldMatchPrefixWhenPrefixDoesNotMatch() {
        LPCParserService mockParserService = mock(LPCParserService.class);
        FunctionVisitor mockFunctionVisitor = mock(FunctionVisitor.class);
        FunctionCompletionUtil functionCompletionUtil = new FunctionCompletionUtil(mockParserService, mockFunctionVisitor);
        FunctionDefinition functionDefinition = createFunctionDefinition("prefixDoesNotMatch");
        String prefix = "notPrefix";

        boolean matches = functionCompletionUtil.matchesPrefix(functionDefinition, prefix);

        assertThat(matches).isFalse();
    }

    @Test
    public void getAdditionalFilterShouldExcludeOperators() {
        LPCParserService mockParserService = mock(LPCParserService.class);
        FunctionVisitor mockFunctionVisitor = mock(FunctionVisitor.class);
        FunctionCompletionUtil functionCompletionUtil = new FunctionCompletionUtil(mockParserService, mockFunctionVisitor);
        FunctionDefinition operatorFunction = createFunctionDefinition("operatorFunction", true);
        FunctionDefinition nonOperatorFunction = createFunctionDefinition("nonOperatorFunction", false);

        boolean operatorIncluded = functionCompletionUtil.getAdditionalFilter()
                .orElse(entry -> true)
                .test(operatorFunction);

        boolean nonOperatorIncluded = functionCompletionUtil.getAdditionalFilter()
                .orElse(entry -> true)
                .test(nonOperatorFunction);

        assertThat(operatorIncluded).isFalse();
        assertThat(nonOperatorIncluded).isTrue();
    }

    private FunctionDefinition createFunctionDefinition(String name) {
        return new FunctionDefinition(
                false, false, false, false, false, false, null, "void", name, List.of(), null
        );
    }

    private FunctionDefinition createFunctionDefinition(String name, boolean isOperator) {
        return new FunctionDefinition(
                false, false, false, false, false, isOperator, null, "void", name, List.of(), null
        );
    }
}
