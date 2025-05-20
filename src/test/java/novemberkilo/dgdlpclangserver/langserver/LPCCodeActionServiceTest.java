package novemberkilo.dgdlpclangserver.langserver;

import novemberkilo.dgdlpclangserver.langserver.action.CodeActionFactory;
import novemberkilo.dgdlpclangserver.langserver.action.inherit.InheritCodeActionHandler;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LPCCodeActionServiceTest {
    @Test
    public void shouldHandleValidSetup() throws Exception {
        LPCTextDocumentService textDocumentService = Mockito.mock(LPCTextDocumentService.class);
        CodeActionParams params = Mockito.mock(CodeActionParams.class);
        InheritCodeActionHandler inheritCodeActionHandler = Mockito.mock(InheritCodeActionHandler.class);
        Supplier<CodeActionFactory> factorySupplier = createFactorySupplier();
        CodeActionFactory codeActionFactory = mock(CodeActionFactory.class);
        LPCCodeActionService service = spy(new LPCCodeActionService(codeActionFactory));

        List<Either<Command, CodeAction>> actionList = List.of(Either.forLeft(new Command()));

        when(factorySupplier.get()).thenReturn(codeActionFactory);
        doReturn(inheritCodeActionHandler).when(service).createInheritHandler(textDocumentService, params);
        when(inheritCodeActionHandler.handleCodeAction()).thenReturn(actionList);

        CompletableFuture<List<Either<Command, CodeAction>>> resultFuture = service.codeAction(textDocumentService, params);
        List<Either<Command, CodeAction>> result = resultFuture.get();

        assertThat(result).isEqualTo(actionList);
        verify(inheritCodeActionHandler).handleCodeAction();
    }

    @SuppressWarnings("unchecked")
    private Supplier<CodeActionFactory> createFactorySupplier() {
        return mock(Supplier.class);
    }

    @Test
    public void shouldHandleEmptyResponses() throws Exception {
        LPCTextDocumentService textDocumentService = Mockito.mock(LPCTextDocumentService.class);
        CodeActionParams params = Mockito.mock(CodeActionParams.class);
        InheritCodeActionHandler inheritCodeActionHandler = Mockito.mock(InheritCodeActionHandler.class);
        Supplier<CodeActionFactory> factorySupplier = createFactorySupplier();
        CodeActionFactory codeActionFactory = mock(CodeActionFactory.class);
        LPCCodeActionService service = spy(new LPCCodeActionService(codeActionFactory));

        when(factorySupplier.get()).thenReturn(codeActionFactory);
        doReturn(inheritCodeActionHandler).when(service).createInheritHandler(textDocumentService, params);
        when(inheritCodeActionHandler.handleCodeAction()).thenReturn(List.of());

        CompletableFuture<List<Either<Command, CodeAction>>> resultFuture = service.codeAction(textDocumentService, params);
        List<Either<Command, CodeAction>> result = resultFuture.get();

        assertThat(result).hasSize(0);
        verify(inheritCodeActionHandler).handleCodeAction();
    }

    @Test
    public void shouldCreateInheritHandler() {
        LPCTextDocumentService textDocumentService = Mockito.mock(LPCTextDocumentService.class);
        CodeActionParams params = Mockito.mock(CodeActionParams.class);
        CodeActionFactory codeActionFactory = mock(CodeActionFactory.class);
        LPCCodeActionService service = spy(new LPCCodeActionService(codeActionFactory));

        InheritCodeActionHandler handler = service.createInheritHandler(textDocumentService, params);

        assertThat(handler).isNotNull();
    }
}
