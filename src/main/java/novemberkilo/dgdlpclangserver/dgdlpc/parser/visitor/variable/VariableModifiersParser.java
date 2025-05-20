package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.variable;

import lombok.experimental.UtilityClass;
import novemberkilo.dgdlpclangserver.LPCParser;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.variable.VariableModifiers;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class VariableModifiersParser {
    @Contract("null -> new")
    public @NotNull VariableModifiers fromContext(LPCParser.ModifiersContext context) {
        if (context == null) {
            return new VariableModifiers(false, false);
        }

        var modifiers = context.modifier();
        return new VariableModifiers(
                modifiers.stream().anyMatch(m -> m.PRIVATE() != null),
                modifiers.stream().anyMatch(m -> m.STATIC() != null)
        );
    }
}
