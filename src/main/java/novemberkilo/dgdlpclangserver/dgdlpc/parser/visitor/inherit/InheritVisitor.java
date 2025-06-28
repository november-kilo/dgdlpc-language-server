package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.inherit;

import lombok.Getter;
import novemberkilo.dgdlpclangserver.LPCBaseVisitor;
import novemberkilo.dgdlpclangserver.LPCParser;
import novemberkilo.dgdlpclangserver.dgdlpc.DiagnosticUtil;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.inherit.InheritDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.PositionDetailsParser;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.StringExpressionUtil;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.HasDiagnostics;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.LPCParseTreeVisitor;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InheritVisitor extends LPCBaseVisitor<Void>
        implements HasDiagnostics, LPCParseTreeVisitor<InheritDefinition> {
    private final List<InheritDefinition> allInherits = new ArrayList<>();

    @Getter
    private final List<Diagnostic> diagnostics = new ArrayList<>();

    private PositionDetails lastValidInheritPosition;
    private boolean hasSeenNonInherit = false;

    @Override
    public Void visitInheritDeclaration(LPCParser.@NotNull InheritDeclarationContext context) {
        boolean isCompleteStatement = context.getText().trim().endsWith(";");
        InheritDefinition inheritDefinition = createInheritDefinition(context);
        allInherits.add(inheritDefinition);

        if (!isCompleteStatement) {
            return super.visitInheritDeclaration(context);
        }

        if (hasSeenNonInherit) {
            diagnostics.add(getDiagnostic(inheritDefinition));
        } else {
            establishLastValidInheritPosition(inheritDefinition);
        }

        return super.visitInheritDeclaration(context);
    }

    @Override
    public Void visitFunctionDeclaration(LPCParser.FunctionDeclarationContext context) {
        hasSeenNonInherit = true;
        return super.visitFunctionDeclaration(context);
    }

    @Override
    public Void visitVariableDeclaration(LPCParser.VariableDeclarationContext context) {
        hasSeenNonInherit = true;
        return super.visitVariableDeclaration(context);
    }

    private void establishLastValidInheritPosition(@NotNull InheritDefinition inheritDefinition) {
        int endLine = inheritDefinition.position().endLine() + 1;
        int endIndex = inheritDefinition.position().endIndex();

        lastValidInheritPosition = new PositionDetails(
                endLine,
                0,
                endIndex,
                endLine,
                0,
                endIndex
        );
    }

    @Contract("_ -> new")
    private @NotNull Diagnostic getDiagnostic(@NotNull InheritDefinition inheritDefinition) {
        PositionDetails position = inheritDefinition.position();
        Range range = new Range(
                new Position(position.startLine(), position.startColumn()),
                new Position(position.endLine(), position.endColumn() + 1)
        );

        return DiagnosticUtil.error(range, "misplaced inherit statement");
    }

    @Contract("_ -> new")
    private @NotNull InheritDefinition createInheritDefinition(LPCParser.@NotNull InheritDeclarationContext context) {
        boolean isPrivate = context.PRIVATE() != null;
        String label = extractLabel(context);
        String inheritedFilePath = context.stringExpression().getText();
        inheritedFilePath = StringExpressionUtil.cleanStringExpression(inheritedFilePath);
        PositionDetails positionDetails = PositionDetailsParser.fromContext(context);

        return new InheritDefinition(isPrivate, label, inheritedFilePath, positionDetails);
    }

    private @Nullable String extractLabel(LPCParser.@NotNull InheritDeclarationContext context) {
        return context.IDENTIFIER() != null ? context.IDENTIFIER().getText() : null;
    }

    public PositionDetails getLastValidInheritPosition() {
        return Objects
                .requireNonNullElseGet(
                        lastValidInheritPosition,
                        () -> new PositionDetails(0, 0, 0, 0, 0, 0)
                );
    }

    @Override
    public List<InheritDefinition> getAll() {
        return allInherits;
    }
}
