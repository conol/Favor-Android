package jp.co.conol.favorlib.favor;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.co.conol.favorlib.Util;
import jp.co.conol.favorlib.favor.model.EnteringShop;

/**
 * Created by Masafumi_Ito on 2017/10/25.
 */

public class EnteringShopTask extends AsyncTask<Void, Void, EnteringShop> {

    private AsyncCallback mAsyncCallback = null;
    private String mAppToken = null;
    private String mDeviceId = null;

    public EnteringShopTask setAppToken(String appToken) {
        mAppToken = appToken;
        return this;
    }

    public EnteringShopTask setDeviceId(String deviceId) {

        // サーバーで送信可能な形式に変換
        mDeviceId = Util.Transform.deviceIdForServer(deviceId);
        return this;
    }

    public interface AsyncCallback {
        void onSuccess(EnteringShop enteringShop);
        void onFailure(Exception e);
    }

    public EnteringShopTask(AsyncCallback asyncCallback) {
        this.mAsyncCallback = asyncCallback;
    }

    @Override
    protected EnteringShop doInBackground(Void... params) {

        Gson gson = new Gson();

        // 入店用のjson（デバイスID）を作成
        String deviceIdString = gson.toJson(mDeviceId);

        // サーバーにjsonを送信
        String responseJsonString = null;
        try {
            responseJsonString = Util.Http.post("http://52.196.33.58/api/users/enter.json", mAppToken, deviceIdString);
        } catch (Exception e) {
            onFailure(e);
        }

        return gson.fromJson(responseJsonString, EnteringShop.class);
    }

    @Override
    protected void onPostExecute(EnteringShop enteringShop) {
        super.onPostExecute(enteringShop);
        onSuccess(enteringShop);
    }

    private void onSuccess(EnteringShop enteringShop) {
        this.mAsyncCallback.onSuccess(enteringShop);
    }

    private void onFailure(Exception e) {
        this.mAsyncCallback.onFailure(e);
    }
}