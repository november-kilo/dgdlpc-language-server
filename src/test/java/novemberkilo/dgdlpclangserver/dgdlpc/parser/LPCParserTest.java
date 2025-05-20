package novemberkilo.dgdlpclangserver.dgdlpc.parser;

import lombok.extern.slf4j.Slf4j;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.variable.VariableDefinition;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.function.FormalParametersVisitor;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.function.FunctionVisitor;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.inherit.InheritVisitor;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.variable.VariableVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class LPCParserTest {
    private final LPCParserService lpcParserService = new LPCParserService(new LPCErrorListener());
    private final InheritVisitor inheritVisitor = new InheritVisitor();
    private final FormalParametersVisitor formalParametersVisitor = new FormalParametersVisitor();
    private final FunctionVisitor functionVisitor = new FunctionVisitor(formalParametersVisitor);
    private final VariableVisitor variableVisitor = new VariableVisitor();

    private ParseTree tree;

    @Test
    void testCode() {
        String input = """
                inherit "foo/ba" + "r";
                inherit format "/lib/fmt";
                
                int a;
                
                static int operator<= (mixed str, object xyz) {
                    int**** xxx = 42;
                
                    if (1) {
                        int b;
                        if (2) {
                            int c;
                        }
                    }
                
                    return (compare(str) <= 0);
                }
                
                static nomask int some_fn(string str, int a, int b);
                
                static nomask int some_fn(string str, varargs int aa, int b, mixed foo...) {
                    return c;
                }
                """;

        tree = lpcParserService.parse(input);
        inheritVisitor.visit(tree);
        functionVisitor.visit(tree);
        variableVisitor.visit(tree);

        log.info("fn: {}", functionVisitor.getAll().size());
        log.info("inherit: {}", inheritVisitor.getAll().size());
        log.info(variableVisitor.getScopeTree());

        PositionDetails positionDetails = new PositionDetails(
                0, 0, 150, 0, 0, 151
        );
        List<VariableDefinition> variables = variableVisitor.findVisibleVariables(positionDetails);
        variables.forEach(variable -> log.info("Found variable: {}", variable));

        assertThat(tree).isNotNull();
        assertThat(tree.getChildCount()).isGreaterThan(0);
    }
}
