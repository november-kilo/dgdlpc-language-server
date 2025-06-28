package novemberkilo.dgdlpclangserver.langserver;

import novemberkilo.dgdlpclangserver.langserver.completion.FunctionCompletionUtil;
import novemberkilo.dgdlpclangserver.langserver.completion.InheritLabelCompletionUtil;
import novemberkilo.dgdlpclangserver.langserver.completion.KfunCompletionUtil;
import novemberkilo.dgdlpclangserver.langserver.completion.LPCKeywordCompletionUtil;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LPCCompletionService {
    private final KfunCompletionUtil kfunCompletionUtil;
    private final LPCKeywordCompletionUtil keywordCompletionUtil;
    private final InheritLabelCompletionUtil inheritLabelCompletionUtil;
    private final FunctionCompletionUtil functionCompletionUtil;

    public LPCCompletionService(
            KfunCompletionUtil kfunCompletionUtil,
            LPCKeywordCompletionUtil keywordCompletionUtil,
            InheritLabelCompletionUtil inheritLabelCompletionUtil,
            FunctionCompletionUtil functionCompletionUtil
    ) {
        this.kfunCompletionUtil = kfunCompletionUtil;
        this.keywordCompletionUtil = keywordCompletionUtil;
        this.inheritLabelCompletionUtil = inheritLabelCompletionUtil;
        this.functionCompletionUtil = functionCompletionUtil;
    }

    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
            LPCTextDocumentService textDocumentService,
            CompletionParams params
    ) {
        return CompletableFuture.supplyAsync(() -> createCompletionItems(textDocumentService, params));
    }

    private @NotNull Either<List<CompletionItem>, CompletionList> createCompletionItems(
            LPCTextDocumentService textDocumentService,
            CompletionParams params
    ) {
        String prefix = getPrefix(textDocumentService, params);
        List<CompletionItem> items = new ArrayList<>();

        items.addAll(kfunCompletionUtil.completionsFor(prefix));
        items.addAll(keywordCompletionUtil.completionsFor(prefix));
        items.addAll(inheritLabelCompletionUtil.completionsFor(textDocumentService, params, prefix));
        items.addAll(functionCompletionUtil.completionsFor(textDocumentService, params, prefix));

        return Either.forLeft(items.stream().distinct().toList());
    }

    private String getPrefix(@NotNull LPCTextDocumentService textDocumentService, @NotNull CompletionParams params) {
        return textDocumentService.getDocumentContentIfPresent(params.getTextDocument().getUri())
                .flatMap(content -> WordAtPositionGetter.getWordAtPosition(content, params.getPosition()))
                .orElse("");
    }
}

