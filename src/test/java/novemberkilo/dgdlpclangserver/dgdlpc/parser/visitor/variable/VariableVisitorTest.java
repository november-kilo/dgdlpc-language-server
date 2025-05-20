package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.variable;

import novemberkilo.dgdlpclangserver.LPCLexer;
import novemberkilo.dgdlpclangserver.LPCParser;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class VariableVisitorTest {
    private static Stream<VariableTestCase> variableDeclarationTestCases() {
        return Stream.of(
                new VariableTestCase(
                        """
                                private int globalVar;
                                static object some_object;
                                
                                void some_function(int param1, string*** param2) {
                                    int localVar;
                                    if (true) {
                                        float nestedVar;
                                        while (1) {
                                            float **nestedFloatArray;
                                        }
                                    }
                                }
                                """,
                        List.of(
                                "Variable: int globalVar (global) (private)",
                                "Variable: object some_object (global) (static)",
                                "Variable: int param1 (parameter)",
                                "Variable: string*** param2 (parameter)",
                                "Variable: int localVar",
                                "Variable: float nestedVar",
                                "Variable: float **nestedFloatArray"
                        )
                ),
                new VariableTestCase(
                        """
                                void nested_scopes() {
                                    int a;
                                    {
                                        int b;
                                        {
                                            int c;
                                        }
                                    }
                                }
                                """,
                        List.of(
                                "Variable: int a",
                                "Variable: int b",
                                "Variable: int c"
                        )
                )
        );
    }

    public static LPCParser createParser(String code) {
        LPCLexer lexer = new LPCLexer(CharStreams.fromString(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        return new LPCParser(tokens);
    }

    @ParameterizedTest
    @MethodSource("variableDeclarationTestCases")
    void shouldProcessVariableDeclarations(VariableTestCase testCase) {
        VariableVisitor visitor = new VariableVisitor();
        visitor.visit(createParser(testCase.code).program());

        String scopeTree = visitor.getScopeTree();

        assertThat(scopeTree)
                .contains("Global Scope")
                .contains(testCase.expectedVariables);
    }

    @Test
    void shouldHandleVariableVisibility() {
        // Given
        var testCase = new VisibilityTestCase(
                """
                        private int x;
                        void test() {
                            int y;
                            if (true) {
                                int z;
                            }
                        }
                        """,
                new PositionDetails(0, 0, 50, 0, 0, 55),
                List.of("x", "y")
        );

        VariableVisitor visitor = new VariableVisitor();
        visitor.visit(createParser(testCase.code).program());
        var visibleVars = visitor.findVisibleVariables(testCase.position);

        assertThat(visibleVars)
                .hasSize(testCase.expectedVisibleVars.size())
                .extracting("name")
                .containsExactlyInAnyOrderElementsOf(testCase.expectedVisibleVars);
    }

    private record VariableTestCase(String code, List<String> expectedVariables) {
    }

    private record VisibilityTestCase(String code, PositionDetails position, List<String> expectedVisibleVars) {
    }
}
