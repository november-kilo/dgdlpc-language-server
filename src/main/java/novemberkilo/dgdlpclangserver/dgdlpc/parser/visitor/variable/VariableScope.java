package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor.variable;

import lombok.Getter;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.PositionDetails;
import novemberkilo.dgdlpclangserver.dgdlpc.definition.variable.VariableDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class VariableScope {
    private final PositionDetails position;
    private final VariableScope parent;
    private final List<VariableScope> children = new ArrayList<>();
    private final List<VariableDefinition> variables = new ArrayList<>();

    public VariableScope(PositionDetails position, VariableScope parent) {
        this.position = position;
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    private void addChild(VariableScope child) {
        children.add(child);
    }

    public void addVariable(VariableDefinition variable) {
        variables.add(variable);
    }

    public boolean contains(PositionDetails pos) {
        return Optional.ofNullable(position)
                .map(p -> pos.startIndex() >= p.startIndex() && pos.startIndex() <= p.endIndex())
                .orElse(true);
    }

    public String toTreeString() {
        return VariableScopeTreeFormatter.format(this);
    }

    public enum ScopeType {
        GLOBAL("Global Scope"),
        LOCAL("Scope[%d-%d]");

        private final String format;

        ScopeType(String format) {
            this.format = format;
        }

        String format(PositionDetails pos) {
            return Optional.ofNullable(pos)
                    .map(p -> String.format(format, p.startIndex(), p.endIndex()))
                    .orElse(format);
        }
    }
}
