package novemberkilo.dgdlpclangserver.dgdlpc.parser.visitor;

import org.eclipse.lsp4j.Diagnostic;

import java.util.List;

public interface HasDiagnostics {
    List<Diagnostic> getDiagnostics();
}
