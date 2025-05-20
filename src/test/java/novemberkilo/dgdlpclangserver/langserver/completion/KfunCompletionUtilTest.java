package novemberkilo.dgdlpclangserver.langserver.completion;

import novemberkilo.dgdlpclangserver.dgdlpc.json.records.Kfun;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.KfunParameter;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.MarkupKind;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KfunCompletionUtilTest {
    @Test
    public void testCompletionsForWhenMultipleMatch() {
        Kfun kfun1 = mock(Kfun.class);
        Kfun kfun2 = mock(Kfun.class);

        when(kfun1.synopsis()).thenReturn("kfun1 synopsis");
        when(kfun1.returnType()).thenReturn("void");
        when(kfun1.description()).thenReturn("Description for kfun1");
        when(kfun1.parameters()).thenReturn(List.of(
                new KfunParameter("param1", "description1", false),
                new KfunParameter("param2", "description2", false)
        ));

        when(kfun2.synopsis()).thenReturn("kfun2 synopsis");
        when(kfun2.returnType()).thenReturn("int");
        when(kfun2.description()).thenReturn("Description for kfun2");
        when(kfun2.parameters()).thenReturn(List.of());

        Map<String, Kfun> kfuns = Map.of(
                "kfun1", kfun1,
                "kfun2", kfun2
        );

        KfunCompletionUtil util = new KfunCompletionUtil(kfuns);

        List<CompletionItem> result = util.completionsFor("kfun");

        assertThat(result)
                .hasSize(2)
                .extracting(
                        CompletionItem::getLabel,
                        CompletionItem::getKind,
                        CompletionItem::getDetail,
                        CompletionItem::getInsertText,
                        CompletionItem::getInsertTextFormat,
                        item -> item.getDocumentation().getRight().getKind(),
                        item -> item.getDocumentation().getRight().getValue()
                )
                .containsExactlyInAnyOrder(
                        tuple(
                                "kfun1",
                                CompletionItemKind.Function,
                                "kfun1 synopsis",
                                "kfun1(${1:param1}, ${2:param2});${0}",
                                InsertTextFormat.Snippet,
                                MarkupKind.MARKDOWN,
                                "```lpc\nvoid kfun1(param1, param2);\n```\n\nDescription for kfun1"
                        ),
                        tuple(
                                "kfun2",
                                CompletionItemKind.Function,
                                "kfun2 synopsis",
                                "kfun2();${0}",
                                InsertTextFormat.Snippet,
                                MarkupKind.MARKDOWN,
                                "```lpc\nint kfun2();\n```\n\nDescription for kfun2"
                        )
                );
    }

    @Test
    public void testCompletionsForWithMatchingPrefix() {
        Kfun kfun1 = mock(Kfun.class);
        when(kfun1.synopsis()).thenReturn("kfun1 synopsis");
        when(kfun1.returnType()).thenReturn("void");
        when(kfun1.description()).thenReturn("Description for kfun1");
        when(kfun1.parameters()).thenReturn(List.of(
                new KfunParameter("param1", "description1", false)
        ));

        Map<String, Kfun> kfuns = Map.of("kfun1", kfun1, "otherKfun", mock(Kfun.class));

        KfunCompletionUtil util = new KfunCompletionUtil(kfuns);

        List<CompletionItem> result = util.completionsFor("kf");

        assertThat(result).hasSize(1);
        CompletionItem item = result.getFirst();
        assertThat(item.getLabel()).isEqualTo("kfun1");
        assertThat(item.getInsertText()).isEqualTo("kfun1(${1:param1});${0}");
    }

    @Test
    public void testCompletionsForWithEmptyPrefix() {
        Kfun kfun1 = mock(Kfun.class);
        when(kfun1.synopsis()).thenReturn("kfun1 synopsis");
        when(kfun1.returnType()).thenReturn("void");
        when(kfun1.description()).thenReturn("Description for kfun1");
        when(kfun1.parameters()).thenReturn(List.of(
                new KfunParameter("param1", "description1", false)
        ));

        Map<String, Kfun> kfuns = Map.of("kfun1", kfun1, "otherKfun", mock(Kfun.class));

        KfunCompletionUtil util = new KfunCompletionUtil(kfuns);

        List<CompletionItem> result = util.completionsFor("");

        assertThat(result).hasSize(2);
    }

    @Test
    public void testCompletionsForWithNoMatches() {
        Kfun kfun1 = mock(Kfun.class);
        Map<String, Kfun> kfuns = Map.of("kfun1", kfun1);

        KfunCompletionUtil util = new KfunCompletionUtil(kfuns);

        List<CompletionItem> result = util.completionsFor("nonExistingPrefix");

        assertThat(result).hasSize(0);
    }

    @Test
    public void testCompletionsForEmptyKfuns() {
        KfunCompletionUtil util = new KfunCompletionUtil(Map.of());

        List<CompletionItem> result = util.completionsFor("");

        assertThat(result).hasSize(0);
    }
}
