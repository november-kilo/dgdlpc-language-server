package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.variable;

import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class VariableScopeTest {
    private static Stream<Arguments> containsTestCases() {
        return Stream.of(
                // Global scope (null position) cases
                args("Global scope contains any position",
                        new ContainsTestCase(
                                null,                           // scope position
                                pos(50, 60),                    // target position
                                true,                           // expected result
                                "Global scope should contain any position"
                        )
                ),

                // Regular scope cases
                args("Position fully within scope",
                        new ContainsTestCase(
                                pos(0, 100),                    // scope position
                                pos(50, 60),                    // target position
                                true,                           // expected result
                                "Scope should contain position that starts within its bounds"
                        )
                ),

                // Boundary cases
                args("Position starting at scope start",
                        new ContainsTestCase(
                                pos(10, 100),
                                pos(10, 20),
                                true,
                                "Scope should contain position starting at its start boundary"
                        )
                ),

                args("Position starting at scope end",
                        new ContainsTestCase(
                                pos(10, 100),
                                pos(100, 110),
                                true,
                                "Scope should contain position starting at its end boundary"
                        )
                ),

                // Outside scope cases
                args("Position starting before scope",
                        new ContainsTestCase(
                                pos(50, 100),
                                pos(40, 60),
                                false,
                                "Scope should not contain position starting before its bounds"
                        )
                ),

                args("Position starting after scope",
                        new ContainsTestCase(
                                pos(50, 100),
                                pos(101, 110),
                                false,
                                "Scope should not contain position starting after its bounds"
                        )
                ),

                // Edge cases
                args("Zero-length position at scope start",
                        new ContainsTestCase(
                                pos(10, 100),
                                pos(10, 10),
                                true,
                                "Scope should contain zero-length position at its start"
                        )
                ),

                args("Position overlapping scope end",
                        new ContainsTestCase(
                                pos(10, 100),
                                pos(90, 110),
                                true,
                                "Scope should contain position that starts within but extends beyond"
                        )
                )
        );
    }

    private static Arguments args(String name, ContainsTestCase testCase) {
        return Arguments.of(name, testCase);
    }

    private static PositionDetails pos(int start, int end) {
        return new PositionDetails(1, start, start, 1, end, end);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("containsTestCases")
    void shouldCheckIfPositionIsContained(String testName, ContainsTestCase testCase) {
        VariableScope scope = new VariableScope(testCase.scopePosition(), null);

        boolean result = scope.contains(testCase.targetPosition());

        assertThat(result).as(testCase.description()).isEqualTo(testCase.expected());
    }

    private record ContainsTestCase(PositionDetails scopePosition, PositionDetails targetPosition, boolean expected,
                                    String description) {
    }
}
