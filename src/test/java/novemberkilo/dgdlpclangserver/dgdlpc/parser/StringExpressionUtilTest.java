package novemberkilo.dgdlpclangserver.dgdlpc.parser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class StringExpressionUtilTest {
    private static Stream<Arguments> stringExpressionTestCases() {
        return Stream.of(
                args("Null input", new StringExpressionTestCase(
                        null,
                        null
                )),
                args("Empty string", new StringExpressionTestCase(
                        "",
                        ""
                )),

                args("Single layer parentheses", new StringExpressionTestCase(
                        "(text)",
                        "text"
                )),
                args("Multiple layer parentheses", new StringExpressionTestCase(
                        "(((text)))",
                        "text"
                )),
                args("Unmatched parentheses", new StringExpressionTestCase(
                        "(unmatched",
                        "(unmatched"
                )),

                args("Trim spaces", new StringExpressionTestCase(
                        "   (   text   )   ",
                        "text"
                )),

                args("Simple concatenation", new StringExpressionTestCase(
                        "\"Hello\" + \"World\"",
                        "HelloWorld"
                )),
                args("Concatenation with spaces", new StringExpressionTestCase(
                        "\"Hello\" + \" \" + \"World\"",
                        "Hello World"
                )),

                args("Simple quoted string", new StringExpressionTestCase(
                        "\"simpleText\"",
                        "simpleText"
                )),
                args("Starts with quote", new StringExpressionTestCase(
                        "\"starts with",
                        "\"starts with"
                )),
                args("Ends with quote", new StringExpressionTestCase(
                        "ends with\"",
                        "ends with\""
                )),
                args("Empty quoted strings", new StringExpressionTestCase(
                        "\"\" + \"\"",
                        ""
                )),

                args("Complex nested expression", new StringExpressionTestCase(
                        "(((\"Hello\" + \" World\")))",
                        "Hello World"
                )),
                args("Mixed quoted and unquoted", new StringExpressionTestCase(
                        "\"Hello\" + World + \"!\"",
                        "HelloWorld!"
                )),

                args("Pure text", new StringExpressionTestCase(
                        "pureText",
                        "pureText"
                ))
        );
    }

    private static Arguments args(String name, StringExpressionTestCase testCase) {
        return Arguments.of(name, testCase);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("stringExpressionTestCases")
    void shouldCleanStringExpression(String testName, StringExpressionTestCase testCase) {
        assertThat(StringExpressionUtil.cleanStringExpression(testCase.input()))
                .isEqualTo(testCase.expected());
    }

    private record StringExpressionTestCase(String input, String expected) {
    }
}
