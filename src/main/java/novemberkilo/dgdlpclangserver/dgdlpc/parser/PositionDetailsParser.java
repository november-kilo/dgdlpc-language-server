package novemberkilo.dgdlpclangserver.dgdlpc.parser;

import lombok.experimental.UtilityClass;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class PositionDetailsParser {
    @Contract("_ -> new")
    public @NotNull PositionDetails fromContext(@NotNull ParserRuleContext context) {
        Token start = context.start;
        Token stop = context.stop;

        return new PositionDetails(
                getLineZeroBased(start.getLine()),
                getCharPositionInLine(start),
                start.getStartIndex(),
                getLineZeroBased(stop.getLine()),
                getCharPositionInLine(stop),
                stop.getStopIndex()
        );
    }

    private int getLineZeroBased(int line) {
        return line - 1;
    }

    private int getCharPositionInLine(@NotNull Token token) {
        return token.getCharPositionInLine();
    }
}
