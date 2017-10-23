package jp.co.conol.favorlib.favor;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.co.conol.favorlib.favor.model.Register;
import jp.co.conol.favorlib.favor.model.User;

/**
 * Created by Masafumi_Ito on 2017/10/13.
 */

public class RegistrationUser extends AsyncTask<Register, Void, User> {

    private AsyncCallback mAsyncCallback = null;

    public interface AsyncCallback{
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public RegistrationUser(AsyncCallback asyncCallback){
        this.mAsyncCallback = asyncCallback;
    }

    @Override
    protected User doInBackground(Register... params) {

        // ユーザー登録用のjsonを作成
        JSONObject registrationUserJson = new JSONObject();
        try {
            registrationUserJson.put("nickname", params[0].getNickname());
            registrationUserJson.put("gender", params[0].getGender());
            registrationUserJson.put("age", params[0].getAge());
            registrationUserJson.put("custom_area1", params[0].getCustom_area1());
            registrationUserJson.put("custom_area2", params[0].getCustom_area2());
            registrationUserJson.put("custom_area3", params[0].getCustom_area3());
            registrationUserJson.put("push_token", params[0].getPush_token());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // サーバーにjsonを送信
        JSONObject responseJson = null;
        User user = null;
        try {
            String buffer = "";
            HttpURLConnection con = null;
            URL url = new URL("http://52.196.33.58/api/users/register.json"); // TODO ドメイン変える
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Accept-Language", "jp");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            OutputStream os = con.getOutputStream();
            PrintStream ps = new PrintStream(os);
            ps.print(registrationUserJson.toString());
            ps.close();

            // レスポンスを取得
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String responseJsonString = reader.readLine();
            responseJson = new JSONObject(responseJsonString);

            con.disconnect();

            // Userオブジェクトを作成
            user = new User(
                    responseJson.getInt("id"),
                    responseJson.getString("nickname"),
                    responseJson.getString("gender"),
                    responseJson.getInt("age"),
                    responseJson.getString("custom_area1"),
                    responseJson.getString("custom_area2"),
                    responseJson.getString("custom_area3"),
                    responseJson.getString("app_token"),
                    responseJson.getString("push_token"),
                    responseJson.getBoolean("notifiable"),
                    responseJson.getString("created_at"),
                    responseJson.getString("updated_at")
            );

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SendJson", e.toString());
            onFailure(e);
        }

        return user;
    }

    @Override
    protected void onPostExecute(User user) {
        super.onPostExecute(user);
        onSuccess(user);
    }

    private void onSuccess(User user) {
        this.mAsyncCallback.onSuccess(user);
    }

    private void onFailure(Exception e) {
        this.mAsyncCallback.onFailure(e);
    }
}
