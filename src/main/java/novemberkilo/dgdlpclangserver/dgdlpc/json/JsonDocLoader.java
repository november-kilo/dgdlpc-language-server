package novemberkilo.dgdlpclangserver.dgdlpc.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
public class JsonDocLoader<T> {
    private final String resourcePath;
    private final Gson gson;
    private final Class<T> type;

    public JsonDocLoader(Class<T> type, String resourcePath) {
        this.gson = createGson();
        this.type = type;
        this.resourcePath = resourcePath;
    }

    protected Gson createGson() {
        return new GsonBuilder().create();
    }

    public T load() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) {
                log.error("Resource not found: {}", resourcePath);
                return null;
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String jsonContent = reader.lines().collect(Collectors.joining("\n"));
                return gson.fromJson(jsonContent, type);
            }
        } catch (Exception e) {
            log.error("Failed to load JSON document from resource: {}", resourcePath, e);
            return null;
        }
    }
}
