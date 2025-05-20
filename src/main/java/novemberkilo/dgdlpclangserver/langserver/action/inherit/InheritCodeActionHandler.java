package novemberkilo.dgdlpclangserver.langserver.action.inherit;

import novemberkilo.dgdlpclangserver.dgdlpc.parser.LPCErrorListener;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.LPCParserService;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.inherit.InheritVisitor;
import novemberkilo.dgdlpclangserver.langserver.LPCTextDocumentService;
import novemberkilo.dgdlpclangserver.langserver.action.AbstractCodeActionHandler;
import novemberkilo.dgdlpclangserver.langserver.action.CodeActionFactory;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

public class InheritCodeActionHandler extends AbstractCodeActionHandler {
    private static final String DIAGNOSTIC_CODE = "misplaced-inherit-statement";

    public InheritCodeActionHandler(
            LPCTextDocumentService textDocumentService,
            CodeActionParams params,
            CodeActionFactory codeActionFactory
    ) {
        super(textDocumentService, params, codeActionFactory);
    }

    @Override
    public boolean canHandleCodeAction(String diagnosticCode) {
        return DIAGNOSTIC_CODE.equals(diagnosticCode);
    }

    @Override
    protected Either<Command, CodeAction> handleDiagnostic(String content, Diagnostic diagnostic) {
        InheritVisitor visitor = processInherits(content);
        return Either.forRight(codeActionFactory.createMoveInheritAction(
                params.getTextDocument().getUri(),
                content,
                diagnostic,
                visitor.getLastValidInheritPosition()
        ));
    }

    private InheritVisitor processInherits(String documentContent) {
        LPCParserService lpcParserService = new LPCParserService(new LPCErrorListener());
        ParseTree tree = lpcParserService.parse(documentContent);
        InheritVisitor inheritVisitor = new InheritVisitor();
        inheritVisitor.visit(tree);
        return inheritVisitor;
    }
}
