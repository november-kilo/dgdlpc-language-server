package novemberkilo.dgdlpclangserver.langserver.completion;

import lombok.RequiredArgsConstructor;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.LPCKeyword;
import org.eclipse.lsp4j.CompletionItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LPCKeywordCompletionUtil {
    private final Map<String, LPCKeyword> keywords;

    public List<CompletionItem> completionsFor(String prefix) {
        return keywords.entrySet().stream()
                .filter(entry -> shouldAddCompletion(prefix, entry.getKey()))
                .map(entry -> CodeSnippetUtil.createKeywordCompletionItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private boolean shouldAddCompletion(@NotNull String prefix, String name) {
        return prefix.isEmpty() || name.startsWith(prefix);
    }
}

