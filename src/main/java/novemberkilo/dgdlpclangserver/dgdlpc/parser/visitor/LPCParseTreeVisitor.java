package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public interface LPCParseTreeVisitor<T> {
    Void visit(ParseTree tree);
    List<T> getAll();
}
