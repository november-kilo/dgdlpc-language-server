package novemberkilo.dgdlpclangserver.dgdlpc.json.records;

import lombok.Generated;

import java.util.List;

@Generated
public record Kfun(
        String name,
        String description,
        String returnType,
        String synopsis,
        List<KfunParameter> parameters,
        String documentation,
        List<String> seeAlso
) {
}
