package novemberkilo.dgdlpclangserver.langserver.markdown;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentLinkTest {
    @Test
    public void shouldHandleValidSourceAndValidReference() {
        String sourceUri = "https://example.com/docs";
        String reference = "section1";
        String expected = "https://example.com/docs#section1";

        String result = DocumentLink.createDocumentationUri(sourceUri, reference);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void shouldHandleValidSourceAndEmptyReference() {
        String sourceUri = "https://example.com/docs";
        String reference = "";
        String expected = "https://example.com/docs#";

        String result = DocumentLink.createDocumentationUri(sourceUri, reference);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void shouldHandleEmptySourceAndVaildReference() {
        String sourceUri = "";
        String reference = "section1";
        String expected = "#section1";

        String result = DocumentLink.createDocumentationUri(sourceUri, reference);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void shouldHandleEmptySourceAndEmptyReference() {
        String sourceUri = "";
        String reference = "";
        String expected = "#";

        String result = DocumentLink.createDocumentationUri(sourceUri, reference);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void shouldHandleSpecialCharacters() {
        String sourceUri = "https://example.com/docs?query=1";
        String reference = "section@#$%^&*";
        String expected = "https://example.com/docs?query=1#section@#$%^&*";

        String result = DocumentLink.createDocumentationUri(sourceUri, reference);

        assertThat(result).isEqualTo(expected);
    }
}
