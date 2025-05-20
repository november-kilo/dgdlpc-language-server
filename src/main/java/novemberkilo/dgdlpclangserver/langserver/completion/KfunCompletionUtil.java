package novemberkilo.dgdlpclangserver.langserver.completion;

import lombok.RequiredArgsConstructor;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.Kfun;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.KfunParameter;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public final class KfunCompletionUtil {
    private static final String MARKDOWN_CODE_BLOCK_FORMAT = "```lpc\n%s %s\n```\n\n%s";
    private static final String PARAMETER_SEPARATOR = ", ";

    private final Map<String, Kfun> kfuns;

    private static @NotNull String createParameterSnippet(@NotNull KfunParameter parameter, int index) {
        return String.format("${%d:%s}", index, parameter.label());
    }

    private static @NotNull List<String> buildDocumentationParameters(@NotNull Kfun kfun) {
        return kfun.parameters().stream()
                .map(KfunParameter::label)
                .toList();
    }

    public List<CompletionItem> completionsFor(String prefix) {
        return kfuns.entrySet().stream()
                .filter(entry -> shouldAddCompletion(prefix, entry.getKey()))
                .map(entry -> createKfunCompletionItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private boolean shouldAddCompletion(@NotNull String prefix, String name) {
        return prefix.isEmpty() || name.startsWith(prefix);
    }

    private @NotNull CompletionItem createKfunCompletionItem(String name, @NotNull Kfun kfun) {
        CompletionItem item = new CompletionItem(name);
        item.setKind(CompletionItemKind.Function);
        item.setDetail(kfun.synopsis());

        String insertText = createInsertText(name, kfun);
        item.setInsertText(insertText);
        item.setInsertTextFormat(InsertTextFormat.Snippet);

        String documentation = createDocumentation(name, kfun);
        item.setDocumentation(new MarkupContent(MarkupKind.MARKDOWN, documentation));

        return item;
    }

    private @NotNull String createInsertText(String name, Kfun kfun) {
        List<String> parameterSnippets = buildParameterSnippets(kfun);
        return String.format("%s(%s);${0}",
                name,
                String.join(PARAMETER_SEPARATOR, parameterSnippets));
    }

    private @NotNull String createDocumentation(String name, Kfun kfun) {
        List<String> paramLabels = buildDocumentationParameters(kfun);
        String signature = String.format("%s(%s);",
                name,
                String.join(PARAMETER_SEPARATOR, paramLabels));

        return String.format(MARKDOWN_CODE_BLOCK_FORMAT,
                kfun.returnType(),
                signature,
                kfun.description());
    }

    private @NotNull List<String> buildParameterSnippets(@NotNull Kfun kfun) {
        var parameters = kfun.parameters();
        return parameters.isEmpty()
                ? new ArrayList<>()
                : IntStream.range(0, parameters.size())
                .mapToObj(i -> createParameterSnippet(parameters.get(i), i + 1))
                .collect(Collectors.toList());
    }
}
