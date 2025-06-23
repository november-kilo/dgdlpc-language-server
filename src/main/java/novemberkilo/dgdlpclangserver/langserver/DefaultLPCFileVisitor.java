package novemberkilo.dgdlpclangserver.langserver;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultLPCFileVisitor implements LPCFileVisitor {
    private final Set<URI> lpcFiles = ConcurrentHashMap.newKeySet();

    @Override
    public void addWorkspaceFolder(Path folderPath) throws IOException {
        Files.walkFileTree(folderPath, new SimpleFileVisitor<>() {
            @Override
            public @NotNull FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) throws IOException {
                if (isLPCFile(file)) {
                    lpcFiles.add(file.toUri());
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public Set<URI> getFiles() {
        return Collections.unmodifiableSet(lpcFiles);
    }

    @Override
    public void add(URI fileUri) {
        if (isLPCFile(Path.of(fileUri))) {
            lpcFiles.add(fileUri);
        }
    }

    @Override
    public void remove(URI fileUri) {
        lpcFiles.removeIf(uri -> uri.getPath().startsWith(fileUri.getPath()));
    }

    private boolean isLPCFile(Path file) {
        String name = file.toString();
        return name.endsWith(".c") || name.endsWith(".h");
    }
}
