package novemberkilo.dgdlpclangserver.dgdlpc.definition.function;

public record FunctionModifiers(
        boolean isPrivate,
        boolean isStatic,
        boolean isNomask,
        boolean isAtomic
) {
}
