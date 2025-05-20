package novemberkilo.dgdlpclangserver.dgdlpc.definition.function;

public record FunctionDetails(
        String returnType,
        String name,
        boolean isOperator,
        String operatorSymbol
) {
}
