package novemberkilo.dgdlpclangserver.dgdlpc.json.records;

import novemberkilo.dgdlpclangserver.dgdlpc.json.KfunsDocLoader;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KfunsDocLoaderTest {
    @Test
    public void loaderShouldConstructWithoutExceptions() {
        KfunsDocLoader loader = new KfunsDocLoader();

        assertThat(loader).isNotNull();
    }

    @Test
    public void loadShouldLoadRecords() {
        KfunsDocLoader loader = new KfunsDocLoader();

        Kfuns result = loader.load();

        assertThat(result).isNotNull();
        assertThat(result.kfuns().get("acos")).isNotNull();
    }
}
