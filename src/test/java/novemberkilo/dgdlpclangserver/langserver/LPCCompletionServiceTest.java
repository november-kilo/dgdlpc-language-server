package novemberkilo.dgdlpclangserver.langserver;

import novemberkilo.dgdlpclangserver.langserver.completion.FunctionCompletionUtil;
import novemberkilo.dgdlpclangserver.langserver.completion.InheritLabelCompletionUtil;
import novemberkilo.dgdlpclangserver.langserver.completion.KfunCompletionUtil;
import novemberkilo.dgdlpclangserver.langserver.completion.LPCKeywordCompletionUtil;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LPCCompletionServiceTest {

    @Mock
    private KfunCompletionUtil kfunCompletionUtil;

    @Mock
    private LPCKeywordCompletionUtil keywordCompletionUtil;

    @Mock
    private LPCTextDocumentService textDocumentService;

    @Mock
    private InheritLabelCompletionUtil inheritLabelCompletionUtil;

    @Mock
    private FunctionCompletionUtil functionCompletionUtil;

    private LPCCompletionService completionService;

    @BeforeEach
    public void setUp() {
        completionService = new LPCCompletionService(
                kfunCompletionUtil,
                keywordCompletionUtil,
                inheritLabelCompletionUtil,
                functionCompletionUtil
        );
    }

    @Test
    public void completion_ShouldReturnCombinedCompletions() throws ExecutionException, InterruptedException {
        CompletionParams params = new CompletionParams();
        TextDocumentIdentifier textDocument = new TextDocumentIdentifier("file:///test.lpc");
        params.setTextDocument(textDocument);
        params.setPosition(new Position(0, 0));

        CompletionItem keywordItem = new CompletionItem("if");
        CompletionItem kfunItem = new CompletionItem("write");

        when(textDocumentService.getDocumentContentIfPresent(anyString()))
                .thenReturn(Optional.of("test content"));
        when(keywordCompletionUtil.completionsFor(anyString()))
                .thenReturn(List.of(keywordItem));
        when(kfunCompletionUtil.completionsFor(anyString()))
                .thenReturn(List.of(kfunItem));

        Either<List<CompletionItem>, CompletionList> result =
                completionService.completion(textDocumentService, params).get();

        assertThat(result.isLeft()).isTrue();
        List<CompletionItem> completionItems = result.getLeft();
        assertThat(completionItems).hasSize(2);
        assertThat(completionItems).containsExactlyInAnyOrder(keywordItem, kfunItem);

    }

    @Test
    public void completion_ShouldCallBothCompletionUtils() throws ExecutionException, InterruptedException {
        CompletionParams params = new CompletionParams();
        TextDocumentIdentifier textDocument = new TextDocumentIdentifier("file:///test.lpc");
        params.setTextDocument(textDocument);
        params.setPosition(new Position(0, 0));

        when(textDocumentService.getDocumentContentIfPresent(anyString()))
                .thenReturn(Optional.of("test content"));
        when(keywordCompletionUtil.completionsFor(anyString()))
                .thenReturn(List.of());
        when(kfunCompletionUtil.completionsFor(anyString()))
                .thenReturn(List.of());

        completionService.completion(textDocumentService, params).get();

        verify(keywordCompletionUtil, times(1)).completionsFor(anyString());
        verify(kfunCompletionUtil, times(1)).completionsFor(anyString());
    }

    @Test
    public void completion_ShouldAggregateResultsFromAllUtilities() throws ExecutionException, InterruptedException {
        CompletionParams params = new CompletionParams();
        TextDocumentIdentifier textDocument = new TextDocumentIdentifier("file:///test.lpc");
        params.setTextDocument(textDocument);
        params.setPosition(new Position(0, 0));

        CompletionItem keywordItem = new CompletionItem("if");
        CompletionItem kfunItem = new CompletionItem("write");
        CompletionItem inheritLabelItem = new CompletionItem("inherit_label");
        CompletionItem functionItem = new CompletionItem("function");

        when(textDocumentService.getDocumentContentIfPresent(anyString()))
                .thenReturn(Optional.of("test content"));
        when(keywordCompletionUtil.completionsFor(anyString()))
                .thenReturn(List.of(keywordItem));
        when(kfunCompletionUtil.completionsFor(anyString()))
                .thenReturn(List.of(kfunItem));
        when(inheritLabelCompletionUtil.completionsFor(eq(textDocumentService), eq(params), anyString()))
                .thenReturn(List.of(inheritLabelItem));
        when(functionCompletionUtil.completionsFor(eq(textDocumentService), eq(params), anyString()))
                .thenReturn(List.of(functionItem));

        Either<List<CompletionItem>, CompletionList> result =
                completionService.completion(textDocumentService, params).get();

        assertThat(result.isLeft()).isTrue();
        List<CompletionItem> completionItems = result.getLeft();
        assertThat(completionItems).hasSize(4);
        assertThat(completionItems).containsExactlyInAnyOrder(keywordItem, kfunItem, inheritLabelItem, functionItem);
    }
}
