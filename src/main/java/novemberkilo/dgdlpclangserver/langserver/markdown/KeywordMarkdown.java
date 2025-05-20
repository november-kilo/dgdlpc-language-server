package novemberkilo.dgdlpclangserver.langserver.markdown;

import lombok.experimental.UtilityClass;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.LPCKeyword;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class KeywordMarkdown {
    public @NotNull String create(@NotNull LPCKeyword keyword, String uri) {
        return String.format("**%s**\n\n%s",
                keyword.name(),
                keyword.description()
        );
    }
}
