package novemberkilo.dgdlpclangserver.langserver.completion;

import novemberkilo.dgdlpclangserver.dgdlpc.json.records.LPCKeyword;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LPCKeywordCompletionUtilTest {
    @Test
    public void testCompletionsFor_withMatchingPrefix() {
        Map<String, LPCKeyword> keywords = Map.of(
                "function", new LPCKeyword("function", "Defines a function"),
                "for", new LPCKeyword("for", "Defines a for loop"),
                "foreach", new LPCKeyword("foreach", "Defines a foreach loop")
        );

        LPCKeywordCompletionUtil completionUtil = new LPCKeywordCompletionUtil(keywords);

        List<CompletionItem> completions = completionUtil.completionsFor("for");

        assertThat(completions).hasSize(2);
        assertThat(
                completions.stream().anyMatch(item -> item.getLabel().equals("for") &&
                        item.getKind() == CompletionItemKind.Keyword &&
                        "Defines a for loop".equals(item.getDetail()))
        ).isTrue();
        assertThat(
                completions.stream().anyMatch(item -> item.getLabel().equals("foreach") &&
                        item.getKind() == CompletionItemKind.Keyword &&
                        "Defines a foreach loop".equals(item.getDetail()))
        ).isTrue();
    }

    @Test
    public void testCompletionsFor_withEmptyPrefix() {
        Map<String, LPCKeyword> keywords = Map.of(
                "function", new LPCKeyword("function", "Defines a function"),
                "if", new LPCKeyword("if", "Defines an if statement")
        );

        LPCKeywordCompletionUtil completionUtil = new LPCKeywordCompletionUtil(keywords);

        List<CompletionItem> completions = completionUtil.completionsFor("");

        assertThat(completions).hasSize(2);
        assertThat(
                completions.stream().anyMatch(item -> item.getLabel().equals("function") &&
                        item.getKind() == CompletionItemKind.Keyword &&
                        "Defines a function".equals(item.getDetail()))
        ).isTrue();
        assertThat(
                completions.stream().anyMatch(item -> item.getLabel().equals("if") &&
                        item.getKind() == CompletionItemKind.Keyword &&
                        "Defines an if statement".equals(item.getDetail()))
        ).isTrue();
    }

    @Test
    public void testCompletionsFor_withNonMatchingPrefix() {
        Map<String, LPCKeyword> keywords = Map.of(
                "function", new LPCKeyword("function", "Defines a function"),
                "struct", new LPCKeyword("struct", "Defines a struct")
        );

        LPCKeywordCompletionUtil completionUtil = new LPCKeywordCompletionUtil(keywords);

        List<CompletionItem> completions = completionUtil.completionsFor("foo");

        assertThat(completions).hasSize(0);
    }

    @Test
    public void testCompletionsFor_withPartialMatch() {
        Map<String, LPCKeyword> keywords = Map.of(
                "continue", new LPCKeyword("continue", "Defines a continue statement"),
                "constructor", new LPCKeyword("constructor", "Defines a constructor")
        );

        LPCKeywordCompletionUtil completionUtil = new LPCKeywordCompletionUtil(keywords);

        List<CompletionItem> completions = completionUtil.completionsFor("con");

        assertThat(completions).hasSize(2);
        assertThat(
                completions.stream().anyMatch(item -> item.getLabel().equals("continue") &&
                        item.getKind() == CompletionItemKind.Keyword &&
                        "Defines a continue statement".equals(item.getDetail()))
        ).isTrue();
        assertThat(
                completions.stream().anyMatch(item -> item.getLabel().equals("constructor") &&
                        item.getKind() == CompletionItemKind.Keyword &&
                        "Defines a constructor".equals(item.getDetail()))
        ).isTrue();
    }

    @Test
    public void testCompletionsFor_withExactMatch() {
        Map<String, LPCKeyword> keywords = Map.of(
                "switch", new LPCKeyword("switch", "Defines a switch statement"),
                "case", new LPCKeyword("case", "Defines a case within a switch")
        );

        LPCKeywordCompletionUtil completionUtil = new LPCKeywordCompletionUtil(keywords);

        List<CompletionItem> completions = completionUtil.completionsFor("switch");

        assertThat(completions).hasSize(1);
        assertThat(completions.getFirst().getLabel()).isEqualTo("switch");
        assertThat(completions.getFirst().getKind()).isEqualTo(CompletionItemKind.Keyword);
        assertThat(completions.getFirst().getDetail()).isEqualTo("Defines a switch statement");
    }
}
