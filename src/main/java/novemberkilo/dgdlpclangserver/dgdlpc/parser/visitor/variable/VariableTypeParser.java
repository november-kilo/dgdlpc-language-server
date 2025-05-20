package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.variable;

import lombok.experimental.UtilityClass;
import novemberkilo.dgdlpclangserver.LPCParser;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.variable.VariableType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@UtilityClass
public final class VariableTypeParser {
    public @NotNull VariableType fromContext(LPCParser.@NotNull TypeSpecifierContext context) {
        String baseType = context.getChild(0).getText();
        int arrayDimensions = Optional.ofNullable(context.arraySpecifier())
                .map(arraySpecifierContext -> arraySpecifierContext.getText().length())
                .orElse(0);
        return new VariableType(baseType, arrayDimensions);
    }
}
