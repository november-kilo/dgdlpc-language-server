package novemberkilo.dgdlpclangserver.dgdlpc.parser;

import lombok.experimental.UtilityClass;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import org.antlr.v4.runtime.ParserRuleContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class PositionDetailsParser {
    @Contract("_ -> new")
    public @NotNull PositionDetails fromContext(@NotNull ParserRuleContext context) {
        return new PositionDetails(
                context.start.getLine() - 1,  // Convert to 0-based
                context.start.getCharPositionInLine(),
                context.start.getStartIndex(),
                context.stop.getLine() - 1,   // Convert to 0-based
                context.stop.getCharPositionInLine(),
                context.stop.getStopIndex()
        );
    }
}
