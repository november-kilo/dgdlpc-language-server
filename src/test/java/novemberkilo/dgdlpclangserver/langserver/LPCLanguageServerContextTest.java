package novemberkilo.dgdlpclangserver.langserver;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LPCLanguageServerContextTest {

    @Test
    void testGetInstance_ShouldReturnSingletonInstance() {
        LPCLanguageServerContext instance1 = LPCLanguageServerContext.getInstance();
        LPCLanguageServerContext instance2 = LPCLanguageServerContext.getInstance();

        assertThat(instance1).isNotNull();
        assertThat(instance2).isNotNull();
        assertThat(instance1).isSameAs(instance2);
    }
}
