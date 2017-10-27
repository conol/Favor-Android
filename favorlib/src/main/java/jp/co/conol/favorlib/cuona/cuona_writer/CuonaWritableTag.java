package jp.co.conol.favorlib.cuona.cuona_writer;

import java.io.IOException;

public abstract class CuonaWritableTag {

    protected boolean useShortKey;

    public boolean useShortKey() {
        return useShortKey;
    }

    public void setUseShortKey(boolean useShortKey) {
        this.useShortKey = useShortKey;
    }

    public abstract void writeJson(String json) throws IOException;

}
