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
import jp.co.conol.favorlib.favor.model.Register;
import jp.co.conol.favorlib.favor.model.User;

/**
 * Created by Masafumi_Ito on 2017/10/13.
 */

public class RegistrationUserTask extends AsyncTask<Register, Void, User> {

    private AsyncCallback mAsyncCallback = null;

    public interface AsyncCallback{
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public RegistrationUserTask(AsyncCallback asyncCallback){
        this.mAsyncCallback = asyncCallback;
    }

    @Override
    protected User doInBackground(Register... params) {

        Gson gson = new Gson();

        // ユーザー登録用のjsonを作成
        String registrationUserString = gson.toJson(params[0]);

        // サーバーにjsonを送信
        String responseJsonString = null;
        try {
            responseJsonString = Util.Http.post("http://52.196.33.58/api/users/register.json", null, registrationUserString);
        } catch (Exception e) {
            onFailure(e);
        }

        return gson.fromJson(responseJsonString, User.class);
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
