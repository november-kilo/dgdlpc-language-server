package novemberkilo.dgdlpclangserver.langserver;

import novemberkilo.dgdlpclangserver.dgdlpc.json.records.Kfun;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.LPCKeyword;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.LPCErrorListener;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.LPCParserService;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.inherit.InheritVisitor;
import novemberkilo.dgdlpclangserver.langserver.completion.InheritLabelCompletionUtil;
import novemberkilo.dgdlpclangserver.langserver.completion.KfunCompletionUtil;
import novemberkilo.dgdlpclangserver.langserver.completion.LPCKeywordCompletionUtil;

import java.util.Map;

public interface LPCLanguageServerContextInterface {
    KfunCompletionUtil getKfunCompletionUtil();

    LPCKeywordCompletionUtil getKeywordCompletionUtil();

    InheritLabelCompletionUtil getInheritLabelCompletionUtil();

    Map<String, Kfun> getKfuns();

    Map<String, LPCKeyword> getKeywords();

    LPCErrorListener getParserErrorService();

    LPCParserService getParserService();

    InheritVisitor getInheritVisitor();
}
