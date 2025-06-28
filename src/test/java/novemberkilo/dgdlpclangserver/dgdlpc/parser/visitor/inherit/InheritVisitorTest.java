package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.inherit;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.inherit.InheritDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.variable.VariableVisitorTest;
import org.eclipse.lsp4j.Diagnostic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class InheritVisitorTest {
    @ParameterizedTest
    @MethodSource("provideInheritTestCases")
    void shouldCorrectlyProcessInheritStatements(List<InheritTestCase> inheritData, boolean hasLabels) {
        String testCode = buildTestCode(inheritData);
        InheritVisitor visitor = new InheritVisitor();

        visitor.visit(VariableVisitorTest.createParser(testCode).program());
        List<InheritDefinition> inherits = visitor.getAll();

        assertThat(inherits).hasSize(inheritData.size());

        for (int i = 0; i < inherits.size(); i++) {
            var expected = inheritData.get(i);
            var actual = inherits.get(i);

            assertThat(actual.file()).isEqualTo(expected.file);
            assertThat(actual.isPrivate()).isEqualTo(expected.isPrivate());
            if (hasLabels) {
                assertThat(actual.label()).isEqualTo(expected.label);
            } else {
                assertThat(actual.label()).isEmpty();
            }
        }
    }

    private static Stream<Arguments> provideInheritTestCases() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                new InheritTestCase("/path/to/file3.c", false, "lib"),
                                new InheritTestCase("/path/to/file4.c", true, "lib")
                        ),
                        true  // has labels
                ),
                Arguments.of(
                        List.of(
                                new InheritTestCase("/path/to/file.c", false, null),
                                new InheritTestCase("/path/to/file2.c", true, null)
                        ),
                        false  // has no labels
                )
        );
    }

    @Test
    void shouldGenerateDiagnosticForMisplacedInherits() {
        String testCode = """
                inherit "/path/to/file.c";
                function test() {}
                inherit "/path/to/file2.c";
                """;
        InheritVisitor visitor = new InheritVisitor();

        visitor.visit(VariableVisitorTest.createParser(testCode).program());
        List<Diagnostic> diagnostics = visitor.getDiagnostics();

        assertThat(diagnostics)
                .hasSize(1)
                .allMatch(d -> d.getMessage().equals("misplaced inherit statement"));
    }

    @Test
    void shouldGenerateDiagnosticForMisplacedInheritsWhenPreprocessorDirectivesArePresent() {
        String testCode = """
                inherit "/path/to/file.c";
                #define TRUE 1
                function test() {}
                inherit "/path/to/file2.c";
                """;
        InheritVisitor visitor = new InheritVisitor();

        visitor.visit(VariableVisitorTest.createParser(testCode).program());
        List<Diagnostic> diagnostics = visitor.getDiagnostics();

        assertThat(diagnostics)
                .hasSize(1)
                .allMatch(d -> d.getMessage().equals("misplaced inherit statement"));
    }

    @Test
    public void shouldGenerateDiagnosticFromMisplacedInheritsWhenOtherStatementsAndPreprocessorDirectivesArePresent() {
        String testCode = """
                string name;
                #define FOO bar
                inherit "/path/to/file1.c";
                """;
        InheritVisitor visitor = new InheritVisitor();

        visitor.visit(VariableVisitorTest.createParser(testCode).program());
        List<Diagnostic> diagnostics = visitor.getDiagnostics();

        assertThat(diagnostics)
                .hasSize(1)
                .allMatch(d -> d.getMessage().equals("misplaced inherit statement"));

    }

    @Test
    void shouldNotGenerateDiagnosticForProperlyPlacedInherits() {
        String testCode = """
                inherit "/path/to/file1.c";
                inherit "/path/to/file2.c";
                function test() {}
                """;
        InheritVisitor visitor = new InheritVisitor();

        visitor.visit(VariableVisitorTest.createParser(testCode).program());
        List<Diagnostic> diagnostics = visitor.getDiagnostics();

        assertThat(diagnostics).isEmpty();
    }

    @Test
    void shouldNotGenerateDiagnosticForProperlyPlacedInheritsWhenPreprocessorDirectivesArePresent() {
        String testCode = """
                inherit "/path/to/file1.c";
                #define TRUE 1
                inherit "/path/to/file2.c";
                function test() {}
                """;
        InheritVisitor visitor = new InheritVisitor();

        visitor.visit(VariableVisitorTest.createParser(testCode).program());
        List<Diagnostic> diagnostics = visitor.getDiagnostics();

        assertThat(diagnostics).isEmpty();
    }

    @Test
    void shouldNotGenerateDiagnosticForProperlyPlacedInheritsWhenIncomplete() {
        String testCode = """
                string name;
                
                inherit "/path/to/file1.c"
                """;
        InheritVisitor visitor = new InheritVisitor();

        visitor.visit(VariableVisitorTest.createParser(testCode).program());
        List<Diagnostic> diagnostics = visitor.getDiagnostics();

        assertThat(diagnostics).isEmpty();
    }

    @Test
    void shouldNotGenerateDiagnosticWhenNoInheritStatementsPresent() {
        String testCode = """
                string name;
                """;
        InheritVisitor visitor = new InheritVisitor();

        visitor.visit(VariableVisitorTest.createParser(testCode).program());
        List<Diagnostic> diagnostics = visitor.getDiagnostics();

        assertThat(diagnostics).isEmpty();
    }

    @Test
    void shouldNotGenerateDiagnosticWhenDocumentIsEmpty() {
        String testCode = "";
        InheritVisitor visitor = new InheritVisitor();

        visitor.visit(VariableVisitorTest.createParser(testCode).program());
        List<Diagnostic> diagnostics = visitor.getDiagnostics();

        assertThat(diagnostics).isEmpty();
    }

    @Test
    void shouldGenerateDiagnosticWithCorrectInsertPositionWhenNoneHasBeenCalculated() {
        String testCode = """
                #define TRUE 1
                
                string name;
                
                inherit "foo/bar";
                inherit "foo/bar/qux";
                """;
        InheritVisitor visitor = new InheritVisitor();

        visitor.visit(VariableVisitorTest.createParser(testCode).program());
        List<Diagnostic> diagnostics = visitor.getDiagnostics();

        assertThat(diagnostics)
                .hasSize(2)
                .allMatch(d -> d.getMessage().equals("misplaced inherit statement"));
    }

    @Test
    void shouldProvideDefaultPositionWhenNoInheritsHaveBeenProcessed() {
        String testCode = """
                string name;
                """;
        InheritVisitor visitor = new InheritVisitor();

        visitor.visit(VariableVisitorTest.createParser(testCode).program());
        var position = visitor.getLastValidInheritPosition();

        assertThat(position)
                .isNotNull()
                .satisfies(p -> {
                    assertThat(p.startLine()).isZero();
                    assertThat(p.startColumn()).isZero();
                    assertThat(p.startIndex()).isZero();
                    assertThat(p.endLine()).isZero();
                    assertThat(p.endColumn()).isZero();
                    assertThat(p.endIndex()).isZero();
                });
    }

    private String buildTestCode(List<InheritTestCase> inheritData) {
        return inheritData.stream()
                .map(data -> String.format("%sinherit%s \"%s\";",
                        data.isPrivate() ? "private " : "",
                        data.label != null ? " " + data.label : "",
                        data.file))
                .collect(Collectors.joining("\n"));
    }

    private record InheritTestCase(String file, boolean isPrivate, String label) {
    }
}
