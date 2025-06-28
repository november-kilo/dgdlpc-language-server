package novemberkilo.dgdlpclangserver.dgdlpc;

import novemberkilo.dgdlpclangserver.dgdlpc.DiagnosticUtil;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiagnosticUtilTest {
    @Test
    void shouldCreateExpectedDiagnostic() {
        Range range = new Range(new Position(1, 5), new Position(1, 10));
        String message = "syntax error";

        Diagnostic result = DiagnosticUtil.error(range, message);

        assertThat(result)
                .hasFieldOrPropertyWithValue("range", range)
                .hasFieldOrPropertyWithValue("message", message)
                .hasFieldOrPropertyWithValue("severity", DiagnosticSeverity.Error)
                .hasFieldOrPropertyWithValue("source", "lpc-parser");
        assertThat(result.getCode().getLeft()).isEqualTo("syntax-error");
    }
}
