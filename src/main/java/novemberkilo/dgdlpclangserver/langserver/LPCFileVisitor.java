package novemberkilo.dgdlpclangserver.langserver;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Set;

public interface LPCFileVisitor {
    void addWorkspaceFolder(Path folderPath) throws IOException;

    Set<URI> getFiles();

    void add(URI fileUri);

    void remove(URI fileUri);
}
