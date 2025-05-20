package novemberkilo.dgdlpclangserver.langserver.markdown;

import novemberkilo.dgdlpclangserver.dgdlpc.json.records.LPCKeyword;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KeywordMarkdownTest {
    @Test
    public void shouldHandleValidKeywordAndDescription() {
        LPCKeyword keyword = new LPCKeyword("ExampleKeyword", "This is an example keyword description.");
        String expectedMarkdown = "**ExampleKeyword**\n\nThis is an example keyword description.";

        String result = KeywordMarkdown.create(keyword, "example-uri");

        assertThat(result).isEqualTo(expectedMarkdown);
    }

    @Test
    public void shouldHandleEmptyKeywordAndDescription() {
        LPCKeyword keyword = new LPCKeyword("", "");
        String expectedMarkdown = "****\n\n";

        String result = KeywordMarkdown.create(keyword, "example-uri");

        assertThat(result).isEqualTo(expectedMarkdown);
    }
}
