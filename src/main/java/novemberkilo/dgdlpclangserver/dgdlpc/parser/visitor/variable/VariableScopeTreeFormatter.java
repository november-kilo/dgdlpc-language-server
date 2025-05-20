package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.variable;

import lombok.experimental.UtilityClass;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.variable.VariableDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class VariableScopeTreeFormatter {
    private static final String TREE_BRANCH = "├── ";
    private static final String TREE_LAST_BRANCH = "└── ";
    private static final String TREE_VERTICAL = "│   ";
    private static final String TREE_SPACE = "    ";

    public @NotNull String format(VariableScope scope) {
        StringBuilder sb = new StringBuilder();
        buildTreeString(scope, sb, "", "");
        return sb.toString();
    }

    private void buildTreeString(VariableScope scope, StringBuilder sb, String prefix, String childrenPrefix) {
        appendScopeHeader(scope, sb, prefix);
        appendVariables(scope, sb, childrenPrefix);
        appendChildren(scope, sb, childrenPrefix);
    }

    private void appendScopeHeader(@NotNull VariableScope scope, @NotNull StringBuilder sb, String prefix) {
        VariableScope.ScopeType scopeType = scope.getPosition() == null ?
                VariableScope.ScopeType.GLOBAL : VariableScope.ScopeType.LOCAL;
        sb.append(prefix).append(scopeType.format(scope.getPosition())).append('\n');
    }

    private void appendVariables(@NotNull VariableScope scope, StringBuilder sb, String prefix) {
        if (scope.getVariables().isEmpty()) return;

        for (VariableDefinition var : scope.getVariables()) {
            sb.append(prefix)
                    .append(TREE_BRANCH)
                    .append("Variable: ")
                    .append(formatVariable(var))
                    .append('\n');
        }
    }

    private @NotNull String formatVariable(@NotNull VariableDefinition var) {
        StringBuilder varStr = new StringBuilder()
                .append(var.type())
                .append(" ");

        if (var.arrayDimensions() > 0 && !var.type().endsWith("*")) {
            varStr.append("*".repeat(var.arrayDimensions()));
        }

        varStr.append(var.name());

        List<String> attributes = getAttributes(var);

        if (!attributes.isEmpty()) {
            varStr.append(" (")
                    .append(String.join(") (", attributes))
                    .append(")");
        }

        return varStr.toString();
    }

    private @NotNull List<String> getAttributes(@NotNull VariableDefinition var) {
        List<String> attributes = new ArrayList<>();

        if (var.scopePosition().startIndex() == 0) {
            attributes.add("global");
        }

        if (var.isFunctionParameter()) {
            attributes.add("parameter");
        }

        if (var.isOptional()) {
            attributes.add("optional");
        }

        if (var.isVariadic()) {
            attributes.add("variadic");
        }

        if (var.isStatic()) {
            attributes.add("static");
        }

        if (var.isPrivate()) {
            attributes.add("private");
        }

        return attributes;
    }

    private void appendChildren(@NotNull VariableScope scope, StringBuilder sb, String childrenPrefix) {
        List<VariableScope> children = scope.getChildren();
        for (int i = 0; i < children.size(); i++) {
            boolean isLast = (i == children.size() - 1);
            VariableScope child = children.get(i);

            String newPrefix = childrenPrefix + (isLast ? TREE_LAST_BRANCH : TREE_BRANCH);
            String newChildrenPrefix = childrenPrefix + (isLast ? TREE_SPACE : TREE_VERTICAL);
            buildTreeString(child, sb, newPrefix, newChildrenPrefix);
        }
    }
}
