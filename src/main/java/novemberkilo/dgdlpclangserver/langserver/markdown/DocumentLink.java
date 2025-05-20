package novemberkilo.dgdlpclangserver.langserver.markdown;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class DocumentLink {
    @NotNull
    @Contract(pure = true)
    public String createDocumentationUri(String sourceUri, String reference) {
        // TODO: Implement proper URI construction for documentation links
        return sourceUri + "#" + reference;
    }
}
