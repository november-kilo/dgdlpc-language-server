package novemberkilo.dgdlpclangserver.dgdlpc.parser;

import novemberkilo.dgdlpclangserver.LPCLexer;
import novemberkilo.dgdlpclangserver.LPCParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public class LPCParserService {
    private final LPCErrorListener parserErrorListener;

    public LPCParserService(LPCErrorListener parserErrorListener) {
        this.parserErrorListener = parserErrorListener;
    }

    public ParseTree parse(String content) {
        LPCLexer lexer = new LPCLexer(CharStreams.fromString(content));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LPCParser parser = new LPCParser(tokens);

        List.of(lexer, parser).forEach(object -> {
            object.removeErrorListeners();
            object.addErrorListener(parserErrorListener);
        });

        return parser.program();
    }
}
