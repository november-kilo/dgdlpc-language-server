package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.function;

import lombok.experimental.UtilityClass;
import novemberkilo.dgdlpclangserver.LPCParser;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.FunctionModifiers;

import java.util.Optional;

@UtilityClass
public final class FunctionModifiersParser {
    public FunctionModifiers fromContext(LPCParser.ModifiersContext modifiersContext) {
        return Optional.ofNullable(modifiersContext)
                .map(LPCParser.ModifiersContext::modifier)
                .map(modifiers -> new FunctionModifiers(
                        modifiers.stream().anyMatch(modifier -> modifier.PRIVATE() != null),
                        modifiers.stream().anyMatch(modifier -> modifier.STATIC() != null),
                        modifiers.stream().anyMatch(modifier -> modifier.NOMASK() != null),
                        modifiers.stream().anyMatch(modifier -> modifier.ATOMIC() != null)
                ))
                .orElseGet(() -> new FunctionModifiers(false, false, false, false));
    }
}
