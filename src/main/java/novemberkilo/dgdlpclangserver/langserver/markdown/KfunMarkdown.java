package novemberkilo.dgdlpclangserver.langserver.markdown;

import lombok.experimental.UtilityClass;
import novemberkilo.dgdlpclangserver.dgdlpc.json.records.Kfun;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@UtilityClass
public final class KfunMarkdown {
    public @NotNull String create(@NotNull Kfun kfun, String uri) {
        StringBuilder md = new StringBuilder();
        addSynopsis(kfun, md);
        addParameters(kfun, md);
        addReturnType(kfun, md);
        addSeeAlso(kfun, uri, md);
        return md.toString();
    }

    private void addSeeAlso(@NotNull Kfun kfun, String uri, StringBuilder md) {
        Optional.ofNullable(kfun.seeAlso())
                .filter(seeAlso -> !seeAlso.isEmpty())
                .ifPresent(seeAlso -> {
                    md.append("### See Also\n");
                    seeAlso.forEach(related ->
                            md.append("- [")
                                    .append(related)
                                    .append("](")
                                    .append(DocumentLink.createDocumentationUri(uri, related))
                                    .append(")\n")
                    );
                });
    }

    private void addReturnType(@NotNull Kfun kfun, StringBuilder md) {
        Optional.ofNullable(kfun.returnType())
                .ifPresent(returnType ->
                        md.append("### Returns\n")
                                .append("_")
                                .append(returnType)
                                .append("_\n\n")
                );
    }

    private void addParameters(@NotNull Kfun kfun, StringBuilder md) {
        Optional.ofNullable(kfun.parameters())
                .filter(params -> !params.isEmpty())
                .ifPresent(params -> {
                    md.append("### Parameters\n\n");
                    params.forEach(param -> {
                        md.append("- `")
                                .append(param.label())
                                .append("` ");
                        if (param.optional()) {
                            md.append("_(optional)_ ");
                        }
                        md.append("- ")
                                .append(param.documentation())
                                .append("\n");
                    });
                    md.append("\n");
                });
    }

    private void addSynopsis(@NotNull Kfun kfun, @NotNull StringBuilder md) {
        md.append("```lpc\n")
                .append(kfun.synopsis())
                .append("\n```\n\n")
                .append(kfun.description())
                .append("\n\n");
    }
}
