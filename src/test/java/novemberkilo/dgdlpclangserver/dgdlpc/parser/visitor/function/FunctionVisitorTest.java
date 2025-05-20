package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.function;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.FunctionDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.function.ParameterDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.variable.VariableVisitorTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FunctionVisitor Tests")
class FunctionVisitorTest {
    private final FormalParametersVisitor paramVisitor = new FormalParametersVisitor();
    private final FunctionVisitor visitor = new FunctionVisitor(paramVisitor);

    private static Stream<FunctionTestCase> functionTestCases() {
        return Stream.of(
                new FunctionTestCase(
                        """
                                private static nomask void test_function(int x, string y) {
                                    // function body
                                }
                                """,
                        List.of(new ExpectedFunction(
                                false,          // isDeclarationOnly
                                false,          // isAtomic
                                true,           // isPrivate
                                true,           // isStatic
                                true,           // isNomask
                                false,          // isOperator
                                null,           // operatorSymbol
                                "void",         // returnType
                                "test_function", // name
                                List.of("x", "y") // parameters
                        ))
                ),
                new FunctionTestCase(
                        """
                                mixed operator + (int x);
                                """,
                        List.of(new ExpectedFunction(
                                true,           // isDeclarationOnly
                                false,          // isAtomic
                                false,          // isPrivate
                                false,          // isStatic
                                false,          // isNomask
                                true,           // isOperator
                                "+",            // operatorSymbol
                                "mixed",        // returnType
                                "operator+",   // name
                                List.of("x")    // parameters
                        ))
                ),
                new FunctionTestCase(
                        """
                                void func1();
                                static string func2(int x) {
                                    // body
                                }
                                """,
                        List.of(
                                new ExpectedFunction(
                                        true, false, false, false, false, false,
                                        null, "void", "func1", List.of()
                                ),
                                new ExpectedFunction(
                                        false, false, false, true, false, false,
                                        null, "string", "func2", List.of("x")
                                )
                        )
                ),
                new FunctionTestCase(
                        """
                                void func1(void) {
                                    // body
                                }
                                void func2(void);
                                """,
                        List.of(
                                new ExpectedFunction(
                                        false, false, false, false, false, false,
                                        null, "void", "func1", List.of()
                                ),
                                new ExpectedFunction(
                                        true, false, false, false, false, false,
                                        null, "void", "func2", List.of()
                                )
                        )
                ),
                new FunctionTestCase(
                        """
                                atomic void func1(void) {
                                    // body
                                }
                                """,
                        List.of(
                                new ExpectedFunction(
                                        false, true, false, false, false, false,
                                        null, "void", "func1", List.of()
                                )
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("functionTestCases")
    void shouldProcessFunctions(FunctionTestCase testCase) {
        visitor.visit(VariableVisitorTest.createParser(testCase.code).program());
        List<FunctionDefinition> functions = visitor.getAll();

        assertThat(functions).hasSize(testCase.expectedFunctions().size());

        for (int i = 0; i < functions.size(); i++) {
            FunctionDefinition actual = functions.get(i);
            ExpectedFunction expected = testCase.expectedFunctions().get(i);

            assertThat(actual)
                    .satisfies(f -> {
                        assertThat(f.isDeclaration()).isEqualTo(expected.isDeclarationOnly);
                        assertThat(f.isAtomic()).isEqualTo(expected.isAtomic);
                        assertThat(f.isPrivate()).isEqualTo(expected.isPrivate);
                        assertThat(f.isStatic()).isEqualTo(expected.isStatic);
                        assertThat(f.isNomask()).isEqualTo(expected.isNomask);
                        assertThat(f.isOperator()).isEqualTo(expected.isOperator);
                        assertThat(f.operatorSymbol()).isEqualTo(expected.operatorSymbol);
                        assertThat(f.returnType()).isEqualTo(expected.returnType);
                        assertThat(f.name()).isEqualTo(expected.name);

                        if (expected.parameters() != null) {
                            assertThat(f.parameters())
                                    .extracting(ParameterDefinition::name)
                                    .containsExactlyElementsOf(expected.parameters());
                        }
                    });
        }
    }

    private record FunctionTestCase(String code, List<ExpectedFunction> expectedFunctions) {
    }

    private record ExpectedFunction(boolean isDeclarationOnly, boolean isAtomic,
                                    boolean isPrivate, boolean isStatic, boolean isNomask,
                                    boolean isOperator, String operatorSymbol, String returnType, String name,
                                    List<String> parameters) {
    }
}
