package novemberkilo.dgdlpclangserver.dgdlpc;

import lombok.experimental.UtilityClass;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class DiagnosticUtil {
    public Diagnostic error(Range range, @NotNull String message) {
        String code = message.replaceAll("\\s+", "-");
        return new Diagnostic(
                range,
                message,
                DiagnosticSeverity.Error,
                "lpc-parser",
                code
        );
    }
}
