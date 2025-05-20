package novemberkilo.dgdlpclangserver.langserver.completion;

import lombok.experimental.UtilityClass;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.FunctionDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.LPCKeyword;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@UtilityClass
public class CodeSnippetUtil {
    public @NotNull CompletionItem createSnippet(
            @NotNull String label,
            @NotNull CompletionItemKind kind,
            String detail,
            String insertText,
            String documentation) {
        CompletionItem item = new CompletionItem(label);
        item.setKind(kind);

        Optional.ofNullable(detail).ifPresent(item::setDetail);

        Optional.ofNullable(insertText).ifPresent(text -> {
            item.setInsertText(text);
            item.setInsertTextFormat(InsertTextFormat.Snippet);
        });

        Optional.ofNullable(documentation).ifPresent(doc -> item.setDocumentation(new MarkupContent(MarkupKind.MARKDOWN, doc)));

        return item;
    }

    public @NotNull CompletionItem createKeywordCompletionItem(@NotNull String name, @NotNull LPCKeyword keyword) {
        return createSnippet(
                name,
                CompletionItemKind.Keyword,
                keyword.description(),
                null,
                null
        );
    }

    public @NotNull CompletionItem createInheritLabelCompletionItem(String label) {
        return createSnippet(
                label,
                CompletionItemKind.Reference,
                "inherit label",
                String.format("%s::${0}", label),
                String.format("Inherit label: `%s`", label)
        );
    }

    public static @NotNull CompletionItem createFunctionCompletionItem(@NotNull FunctionDefinition functionDefinition) {
        return createSnippet(
                functionDefinition.name(),
                CompletionItemKind.Function,
                "function",
                String.format("%s(${0}", functionDefinition.name()),
                String.format("Function: `%s`", functionDefinition.name())
        );
    }
}
