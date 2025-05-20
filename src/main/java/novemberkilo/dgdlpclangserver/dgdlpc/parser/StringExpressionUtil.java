package novemberkilo.dgdlpclangserver.dgdlpc.parser;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public final class StringExpressionUtil {
    public String cleanStringExpression(String expression) {
        return Optional.ofNullable(expression)
                .filter(str -> !str.isEmpty())
                .map(str -> {
                    // Remove outer parentheses
                    String cleaned = str.trim();
                    while (cleaned.startsWith("(") && cleaned.endsWith(")")) {
                        cleaned = cleaned.substring(1, cleaned.length() - 1).trim();
                    }

                    // Split by '+' and process each part
                    return Arrays.stream(cleaned.split("\\+"))
                            .map(String::trim)
                            .map(part -> part.startsWith("\"") && part.endsWith("\"")
                                    ? part.substring(1, part.length() - 1)
                                    : part)
                            .collect(Collectors.joining());
                })
                .orElse(expression);
    }
}
