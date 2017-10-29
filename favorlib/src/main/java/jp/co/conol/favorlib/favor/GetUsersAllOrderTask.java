package jp.co.conol.favorlib.favor;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import jp.co.conol.favorlib.Util;
import jp.co.conol.favorlib.favor.model.Order;
import jp.co.conol.favorlib.favor.model.UsersAllOrder;

/**
 * Created by Masafumi_Ito on 2017/10/26.
 */

public class GetUsersAllOrderTask extends AsyncTask<Void, Void, List<UsersAllOrder>> {

    private AsyncCallback mAsyncCallback = null;
    private String mAppToken = null;

    public GetUsersAllOrderTask setAppToken(String appToken) {
        mAppToken = appToken;
        return this;
    }

    public interface AsyncCallback {
        void onSuccess(List<UsersAllOrder> usersAllOrderList);
        void onFailure(Exception e);
    }

    public GetUsersAllOrderTask(AsyncCallback asyncCallback) {
        this.mAsyncCallback = asyncCallback;
    }

    @Override
    protected List<UsersAllOrder> doInBackground(Void... params) {

        Gson gson = new Gson();

        // サーバーにjsonを送信
        String responseJsonString = null;
        try {
            responseJsonString
                    = Util.Http.get("http://52.196.33.58/api/users/orders.json?page=1&per=20", mAppToken);
        } catch (Exception e) {
            onFailure(e);
        }

        return gson.fromJson(responseJsonString, new TypeToken<ArrayList<UsersAllOrder>>(){}.getType());
    }

    @Override
    protected void onPostExecute(List<UsersAllOrder> usersAllOrderList) {
        super.onPostExecute(usersAllOrderList);
        onSuccess(usersAllOrderList);
    }

    private void onSuccess(List<UsersAllOrder> usersAllOrderList) {
        this.mAsyncCallback.onSuccess(usersAllOrderList);
    }

    private void onFailure(Exception e) {
        this.mAsyncCallback.onFailure(e);
    }
}