package jp.co.conol.favorlib.cuona;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by m_ito on 2018/01/30.
 */

public class FavorException extends Exception {

    private int mCode = 0;
    private String mType;
    private String mMessage;

    FavorException(String responseJsonMetaString) {
        try {
            JSONObject metaJasonObject = new JSONObject(responseJsonMetaString);
            mCode = metaJasonObject.getInt("code");
            mType = metaJasonObject.getString("type");
            mMessage = metaJasonObject.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getCode() {
        return mCode;
    }

    public String getType() {
        return mType;
    }

    public String getMessage() {
        return mMessage;
    }
}
