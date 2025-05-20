package novemberkilo.dgdlpclangserver.dgdlpc.json;

import novemberkilo.dgdlpclangserver.dgdlpc.json.records.Kfuns;

public class KfunsDocLoader extends JsonDocLoader<Kfuns> {
    public KfunsDocLoader() {
        super(Kfuns.class, "kfuns.json");
    }
}
