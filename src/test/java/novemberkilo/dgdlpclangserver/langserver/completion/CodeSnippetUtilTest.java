package novemberkilo.dgdlpclangserver.langserver.completion;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.FunctionDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.LPCKeyword;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeSnippetUtilTest {
    @Test
    public void createSnippet_WithAllParameters() {
        CompletionItem item = CodeSnippetUtil.createSnippet(
                "testLabel",
                CompletionItemKind.Function,
                "test detail",
                "test insert text",
                "test documentation"
        );

        assertThat(item.getLabel()).isEqualTo("testLabel");
        assertThat(item.getKind()).isEqualTo(CompletionItemKind.Function);
        assertThat(item.getDetail()).isEqualTo("test detail");
        assertThat(item.getInsertText()).isEqualTo("test insert text");
        assertThat(item.getInsertTextFormat()).isEqualTo(InsertTextFormat.Snippet);
        MarkupContent documentation = item.getDocumentation().getRight();
        assertThat(documentation.getKind()).isEqualTo(MarkupKind.MARKDOWN);
        assertThat(documentation.getValue()).isEqualTo("test documentation");
    }

    @Test
    public void createSnippet_WithNullOptionalParameters() {
        CompletionItem item = CodeSnippetUtil.createSnippet(
                "testLabel",
                CompletionItemKind.Function,
                null,
                null,
                null
        );

        assertThat(item.getLabel()).isEqualTo("testLabel");
        assertThat(item.getKind()).isEqualTo(CompletionItemKind.Function);
        assertThat(item.getDetail()).isNull();
        assertThat(item.getInsertText()).isNull();
        assertThat(item.getInsertTextFormat()).isNull();
        assertThat(item.getDocumentation()).isNull();
    }

    @Test
    public void createKeywordCompletionItem() {
        LPCKeyword keyword = new LPCKeyword("testKeyword", "test description");

        CompletionItem item = CodeSnippetUtil.createKeywordCompletionItem("testKeyword", keyword);

        assertThat(item.getLabel()).isEqualTo("testKeyword");
        assertThat(item.getKind()).isEqualTo(CompletionItemKind.Keyword);
        assertThat(item.getDetail()).isEqualTo("test description");
        assertThat(item.getInsertText()).isNull();
        assertThat(item.getInsertTextFormat()).isNull();
        assertThat(item.getDocumentation()).isNull();
    }

    @Test
    public void createInheritLabelCompletionItem() {
        CompletionItem item = CodeSnippetUtil.createInheritLabelCompletionItem("TestLabel");

        assertThat(item.getLabel()).isEqualTo("TestLabel");
        assertThat(item.getKind()).isEqualTo(CompletionItemKind.Reference);
        assertThat(item.getDetail()).isEqualTo("inherit label");
        assertThat(item.getInsertText()).isEqualTo("TestLabel::${0}");
        assertThat(item.getInsertTextFormat()).isEqualTo(InsertTextFormat.Snippet);
    }

    @Test
    public void createFunctionCompletionItem() {
        FunctionDefinition functionDefinition = new FunctionDefinition(
                false,
                false,
                false,
                false,
                false,
                false,
                null,
                "void",
                "fn",
                List.of(),
                new PositionDetails(0, 0, 0, 0, 0, 0)
        );
        CompletionItem item = CodeSnippetUtil.createFunctionCompletionItem(functionDefinition);

        assertThat(item.getLabel()).isEqualTo("fn");
        assertThat(item.getKind()).isEqualTo(CompletionItemKind.Function);
        assertThat(item.getDetail()).isEqualTo("function");
        assertThat(item.getInsertText()).isEqualTo("fn(${0}");
        assertThat(item.getInsertTextFormat()).isEqualTo(InsertTextFormat.Snippet);
        MarkupContent documentation = item.getDocumentation().getRight();
        assertThat(documentation.getKind()).isEqualTo(MarkupKind.MARKDOWN);
        assertThat(documentation.getValue()).isEqualTo("Function: `fn`");
    }
}
