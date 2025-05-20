package novemberkilo.dgdlpclangserver.dgdlpc.json;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class JsonDocLoaderTest {
    private final String testFilePath = "test-data.json";
    private JsonDocLoader<TestItem> loader;

    @BeforeEach
    void setUp() {
        loader = new JsonDocLoader<>(TestItem.class, testFilePath);
    }

    @Test
    void verifyTestFileExists() {
        var resource = getClass().getClassLoader().getResource(testFilePath);
        assertThat(resource).isNotNull();
    }

    @Test
    void verifyTestFileContent() throws IOException {
        var resource = getClass().getClassLoader().getResourceAsStream(testFilePath);
        org.junit.jupiter.api.Assertions.assertNotNull(resource);
        String content = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        assertThat(content).isNotEmpty();
        // Print content for inspection
        System.out.println("File content: " + content);
    }

    @Test
    void shouldLoadValidJsonFile() {
        TestItem result = loader.load();

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("Test Item");
        assertThat(result.description()).isEqualTo("This is a test item");
        assertThat(result.tags()).containsExactlyInAnyOrder("test", "example");
    }

    @Test
    void shouldReturnEmptyOptionalWhenFileNotFound() {
        JsonDocLoader<TestItem> testLoader = new JsonDocLoader<>(TestItem.class, "not-found.json");

        TestItem result = testLoader.load();

        assertThat(result).isNull();
    }

    @Test
    void shouldReturnEmptyOptionalWhenException() {
        Gson mockGson = Mockito.mock(Gson.class);
        Mockito.when(mockGson.fromJson(anyString(), any(Class.class)))
                .thenThrow(new JsonSyntaxException("Invalid JSON"));

        JsonDocLoader<TestItem> testLoader = new JsonDocLoader<>(TestItem.class, testFilePath) {
            @Override
            protected Gson createGson() {
                return mockGson;
            }
        };

        TestItem result = testLoader.load();

        assertThat(result).isNull();
    }
}

record TestItem(int id, String name, String description, String[] tags) {
}
