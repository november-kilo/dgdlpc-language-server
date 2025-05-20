package novemberkilo.dgdlpclangserver.langserver;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import novemberkilo.dgdlpclangserver.dgdlpc.json.JsonDocLoader;
import novemberkilo.dgdlpclangserver.dgdlpc.json.KfunsDocLoader;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.Kfun;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.Kfuns;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.LPCKeyword;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.LPCKeywords;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.LPCErrorListener;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.LPCParserService;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.function.FormalParametersVisitor;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.function.FunctionVisitor;
import novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.inherit.InheritVisitor;
import novemberkilo.dgdlpclangserver.langserver.action.CodeActionFactory;
import novemberkilo.dgdlpclangserver.langserver.action.DocumentEditService;
import novemberkilo.dgdlpclangserver.langserver.completion.FunctionCompletionUtil;
import novemberkilo.dgdlpclangserver.langserver.completion.InheritLabelCompletionUtil;
import novemberkilo.dgdlpclangserver.langserver.completion.KfunCompletionUtil;
import novemberkilo.dgdlpclangserver.langserver.completion.LPCKeywordCompletionUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

@Slf4j
public class LPCLanguageServerContext implements LPCLanguageServerContextInterface {
    @Setter
    private static LPCLanguageServerContext instance;

    @Getter
    final DocumentEditService documentEditService;

    @Getter
    private final CodeActionFactory codeActionFactory;

    @Getter
    private final LPCErrorListener parserErrorService;

    @Getter
    private final LPCParserService parserService;

    @Getter
    private final InheritVisitor inheritVisitor;

    @Getter
    private final FormalParametersVisitor formalParametersVisitor;

    @Getter
    private final FunctionVisitor functionVisitor;

    @Getter
    private final KfunCompletionUtil kfunCompletionUtil;

    @Getter
    private final LPCKeywordCompletionUtil keywordCompletionUtil;

    @Getter
    private final InheritLabelCompletionUtil inheritLabelCompletionUtil;

    @Getter
    private final FunctionCompletionUtil functionCompletionUtil;

    @Getter
    private final Map<String, Kfun> kfuns;

    @Getter
    private final Map<String, LPCKeyword> keywords;

    public static LPCLanguageServerContext getInstance() {
        if (instance == null) {
            instance = new LPCLanguageServerContext();
        }
        return instance;
    }

    private LPCLanguageServerContext() {
        this.kfuns = loadKfuns(new KfunsDocLoader());
        this.keywords = loadKeywords(new JsonDocLoader<>(LPCKeywords.class, "keywords.json"));

        this.documentEditService = createDocumentEditService();
        this.codeActionFactory = createCodeActionFactory();

        this.parserErrorService = createParserErrorService();
        this.parserService = createParserService();

        this.inheritVisitor = createInheritVisitor();
        this.formalParametersVisitor = createFormalParametersVisitor();
        this.functionVisitor = createFunctionVisitor();

        this.kfunCompletionUtil = createKfunCompletionUtil();
        this.keywordCompletionUtil = createKeywordCompletionUtil();
        this.inheritLabelCompletionUtil = createInheritLabelCompletionUtil();
        this.functionCompletionUtil = createFunctionCompletionUtil();
    }

    private @NotNull @Unmodifiable Map<String, Kfun> loadKfuns(@NotNull KfunsDocLoader kfunsDocLoader) {
        Kfuns kfuns = kfunsDocLoader.load();
        log.info("Loaded {} kfuns", kfuns.kfuns().size());

        return kfuns.kfuns();
    }

    private @NotNull @Unmodifiable Map<String, LPCKeyword> loadKeywords(@NotNull JsonDocLoader<LPCKeywords> keywordsDocLoader) {
        LPCKeywords keywords = keywordsDocLoader.load();
        log.info("Loaded {} keywords", keywords.keywords().size());

        return keywords.keywords();
    }

    @Contract(value = " -> new", pure = true)
    private @NotNull DocumentEditService createDocumentEditService() {
        return new DocumentEditService();
    }

    @Contract(value = " -> new", pure = true)
    private @NotNull CodeActionFactory createCodeActionFactory() {
        return new CodeActionFactory(this.documentEditService);
    }

    @Contract(" -> new")
    private @NotNull LPCErrorListener createParserErrorService() {
        return new LPCErrorListener();
    }

    @Contract(value = " -> new", pure = true)
    private @NotNull LPCParserService createParserService() {
        return new LPCParserService(this.parserErrorService);
    }

    @Contract(" -> new")
    private @NotNull InheritVisitor createInheritVisitor() {
        return new InheritVisitor();
    }

    @Contract(value = " -> new", pure = true)
    private @NotNull FormalParametersVisitor createFormalParametersVisitor() {
        return new FormalParametersVisitor();
    }

    @Contract(" -> new")
    private @NotNull FunctionVisitor createFunctionVisitor() {
        return new FunctionVisitor(this.formalParametersVisitor);
    }

    @Contract(" -> new")
    private @NotNull KfunCompletionUtil createKfunCompletionUtil() {
        return new KfunCompletionUtil(kfuns);
    }

    @Contract(" -> new")
    private @NotNull LPCKeywordCompletionUtil createKeywordCompletionUtil() {
        return new LPCKeywordCompletionUtil(keywords);
    }

    @Contract(" -> new")
    private @NotNull InheritLabelCompletionUtil createInheritLabelCompletionUtil() {
        return new InheritLabelCompletionUtil(parserService, inheritVisitor);
    }

    @Contract(" -> new")
    private @NotNull FunctionCompletionUtil createFunctionCompletionUtil() {
        return new FunctionCompletionUtil(parserService, functionVisitor);
    }
}
