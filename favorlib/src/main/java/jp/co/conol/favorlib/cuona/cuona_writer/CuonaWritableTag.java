package jp.co.conol.favorlib.cuona.cuona_writer;

import java.io.IOException;

public abstract class CuonaWritableTag {

    public abstract void writeJSON(String json) throws IOException;

}
