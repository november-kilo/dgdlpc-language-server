package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.function;

import novemberkilo.dgdlpclangserver.LPCParser;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.FunctionDetails;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class FunctionDetailsParser {
    private FunctionDetailsParser() {
    }

    @Contract("_ -> new")
    public static @NotNull FunctionDetails fromContext(LPCParser.@NotNull FunctionDeclarationContext context) {
        String returnType = context.typeSpecifier().getText();
        var declarator = context.functionDeclarator();

        if (declarator.operatorName() != null) {
            String operatorSymbol = declarator.operatorName().getChild(1).getText();
            return new FunctionDetails(returnType, "operator" + operatorSymbol, true, operatorSymbol);
        } else {
            String name = declarator.IDENTIFIER().getText();
            return new FunctionDetails(returnType, name, false, null);
        }
    }
}
