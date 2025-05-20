package novemberkilo.dgdlpclangserver.langserver;

import novemberkilo.dgdlpclangserver.dgdlpc.parser.LPCErrorListener;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.LPCParserService;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.inherit.InheritVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class LPCTextDocumentService implements TextDocumentService, LanguageClientAware {
    private final Map<String, String> documents;
    private final LPCCodeActionService lpcCodeActionService;
    private final LPCCompletionService lpcCompletionService;
    private final LPCHoverService lpcHoverService;
    private LanguageClient client;

    public LPCTextDocumentService(
            LPCCodeActionService lpcCodeActionService,
            LPCCompletionService lpcCompletionService,
            LPCHoverService lpcHoverService
    ) {
        this.lpcCodeActionService = lpcCodeActionService;
        this.lpcCompletionService = lpcCompletionService;
        this.lpcHoverService = lpcHoverService;
        this.documents = new ConcurrentHashMap<>();
    }

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
    }

    @Override
    public void didOpen(@NotNull DidOpenTextDocumentParams params) {
        TextDocumentItem document = params.getTextDocument();
        documents.put(document.getUri(), document.getText());
        TextDocumentIdentifier identifier = new TextDocumentIdentifier(document.getUri());
        validateDocument(identifier);
    }

    @Override
    public void didChange(@NotNull DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getContentChanges().getFirst().getText();
        documents.put(uri, text);
        validateDocument(params.getTextDocument());
    }

    @Override
    public void didClose(@NotNull DidCloseTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        documents.remove(uri);
        client.publishDiagnostics(new PublishDiagnosticsParams(uri, List.of()));
    }

    @Override
    public void didSave(@NotNull DidSaveTextDocumentParams params) {
        validateDocument(params.getTextDocument());
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {
        return lpcCompletionService.completion(this, params);

    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        return lpcHoverService.hover(this, params);
    }

    @Override
    public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(CodeActionParams codeActionParams) {
        return lpcCodeActionService.codeAction(this, codeActionParams);
    }

    public String getDocumentContent(String uri) {
        return documents.get(uri);
    }

    public Optional<String> getDocumentContentIfPresent(String uri) {
        return Optional.ofNullable(documents.get(uri));
    }

    void validateDocument(@NotNull TextDocumentIdentifier document) {
        String text = documents.get(document.getUri());

        LPCErrorListener lpcErrorListener = new LPCErrorListener();
        LPCParserService lpcParserService = new LPCParserService(lpcErrorListener);
        ParseTree tree = lpcParserService.parse(text);

        List<Diagnostic> allDiagnostics = new ArrayList<>(lpcErrorListener.getDiagnostics());
        List<Diagnostic> visitorDiagnostics = visitParseTree(tree);
        allDiagnostics.addAll(visitorDiagnostics);

        publishAllDiagnostics(document.getUri(), allDiagnostics);
    }

    private @NotNull List<Diagnostic> visitParseTree(ParseTree tree) {
        List<Diagnostic> diagnostics = new ArrayList<>();
        List.of(
                new InheritVisitor()
        ).forEach(visitor -> {
            visitor.visit(tree);
            diagnostics.addAll(visitor.getDiagnostics());
        });
        return diagnostics;
    }

    private void publishAllDiagnostics(String documentUri, List<Diagnostic> diagnostics) {
        PublishDiagnosticsParams publishDiagnosticsParams = new PublishDiagnosticsParams(
                documentUri,
                diagnostics
        );

        client.publishDiagnostics(publishDiagnosticsParams);
    }

    public Optional<String> getWordAtPosition(String documentUri, Position position) {
        return Optional.ofNullable(documents.get(documentUri))
                .flatMap(content -> WordAtPositionGetter.getWordAtPosition(content, position));
    }
}
