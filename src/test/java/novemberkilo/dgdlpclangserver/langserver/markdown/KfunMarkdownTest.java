package novemberkilo.dgdlpclangserver.langserver.markdown;

import novemberkilo.dgdlpclangserver.dgdlpc.json.records.Kfun;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.KfunParameter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class KfunMarkdownTest {
    private static final Path TEST_RESOURCES = Path.of("src/test/resources/markdown/kfun");
    private final String uri = "file:///example.lpc";

    @Test
    public void testCreate_withAllFieldsPopulated() throws IOException {
        Kfun kfun = new Kfun(
                "exampleFunction",
                "This is a sample function description.",
                "String",
                "string exampleFunction(int arg)",
                List.of(
                        new KfunParameter("arg", "An integer argument.", false)
                ),
                "Markdown documentation",
                List.of("relatedFunction1", "relatedFunction2")
        );

        String result = KfunMarkdown.create(kfun, uri);

        assertMatchesFile(result, "all_fields.md");
    }

    @Test
    public void testCreate_withMissingParametersAndSeeAlso() throws IOException {
        Kfun kfun = new Kfun(
                "exampleFunction",
                "Another function description.",
                "Integer",
                "int exampleFunction()",
                List.of(),
                "Markdown documentation",
                null
        );

        String result = KfunMarkdown.create(kfun, uri);

        assertMatchesFile(result, "missing_parameters_see_also.md");
    }

    @Test
    public void testCreate_withOptionalParameters() throws IOException {
        Kfun kfun = new Kfun(
                "optionalArgFunction",
                "Function with optional parameters.",
                "Boolean",
                "bool optionalArgFunction(int? optionalArg)",
                List.of(
                        new KfunParameter("optionalArg", "An optional integer argument.", true)
                ),
                "Markdown documentation",
                null
        );

        String result = KfunMarkdown.create(kfun, uri);

        assertMatchesFile(result, "with_parameters.md");
    }

    @Test
    public void testCreate_withEmptyDescriptionAndReturnType() throws IOException {
        Kfun kfun = new Kfun(
                "noDescriptionFunction",
                "",
                null,
                "public void  noDescriptionFunction()",
                List.of(),
                "Markdown documentation",
                List.of()
        );

        String result = KfunMarkdown.create(kfun, uri);

        assertMatchesFile(result, "no_description_no_return_type.md");
    }

    @Test
    public void testCreate_withMultipleSeeAlsoLinks() throws IOException {
        Kfun kfun = new Kfun(
                "multipleSeeAlsoFunction",
                "Function with multiple see also links.",
                "Void",
                "public void  multipleSeeAlsoFunction()",
                List.of(),
                "Markdown documentation",
                List.of("link1", "link2", "link3")
        );

        String result = KfunMarkdown.create(kfun, uri);

        assertMatchesFile(result, "multiple_see_also.md");
    }

    private void assertMatchesFile(String actual, String FileName) throws IOException {
        Path File = TEST_RESOURCES.resolve(FileName);

        if (!Files.exists(File)) {
            Files.createDirectories(File.getParent());
            Files.writeString(File, actual);
            fail(" file created at " + File + ". Please verify its contents and run the test again.");
        }

        String expected = Files.readString(File);
        assertThat(actual).isEqualToNormalizingNewlines(expected);
    }
}
