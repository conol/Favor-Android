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
import jp.co.conol.favorlib.favor.model.EnteredShop;

/**
 * Created by Masafumi_Ito on 2017/10/25.
 */

public class EnteringShop extends AsyncTask<Void, Void, EnteredShop> {

    private AsyncCallback mAsyncCallback = null;
    private String mAppToken = null;
    private String mDeviceId = null;

    public EnteringShop setAppToken(String appToken) {
        mAppToken = appToken;
        return this;
    }

    public EnteringShop setDeviceId(String deviceId) {

        // サーバーで送信可能な形式に変換
        mDeviceId = Util.Transform.deviceIdForServer(deviceId);
        return this;
    }

    public interface AsyncCallback {
        void onSuccess(EnteredShop enteredShop);
        void onFailure(Exception e);
    }

    public EnteringShop(AsyncCallback asyncCallback) {
        this.mAsyncCallback = asyncCallback;
    }

    @Override
    protected EnteredShop doInBackground(Void... params) {

        // 入店用のjson（デバイスID）を作成
        JSONObject deviceIdJson = new JSONObject();
        try {
            deviceIdJson.put("device_id", mDeviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // サーバーにjsonを送信
        JSONObject responseJson = null;
        EnteredShop enteredShop = null;
        try {
            String buffer = "";
            HttpURLConnection con = null;
            URL url = new URL("http://52.196.33.58/api/users/enter.json"); // TODO ドメイン変える
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Accept-Language", "jp");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setRequestProperty("Authorization", "Bearer " + mAppToken);
            OutputStream os = con.getOutputStream();
            PrintStream ps = new PrintStream(os);
            ps.print(deviceIdJson.toString());
            ps.close();

            // レスポンスを取得
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String responseJsonString = reader.readLine();
            responseJson = new JSONObject(responseJsonString);

            con.disconnect();

            Gson gson = new Gson();
            enteredShop = gson.fromJson(responseJson.toString(), EnteredShop.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SendJson", e.toString());
            onFailure(e);
        }

        return enteredShop;
    }

    @Override
    protected void onPostExecute(EnteredShop enteredShop) {
        super.onPostExecute(enteredShop);
        onSuccess(enteredShop);
    }

    private void onSuccess(EnteredShop enteredShop) {
        this.mAsyncCallback.onSuccess(enteredShop);
    }

    private void onFailure(Exception e) {
        this.mAsyncCallback.onFailure(e);
    }
}