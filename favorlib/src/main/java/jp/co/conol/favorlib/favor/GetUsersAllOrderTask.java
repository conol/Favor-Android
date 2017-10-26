package jp.co.conol.favorlib.favor;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import jp.co.conol.favorlib.Util;
import jp.co.conol.favorlib.favor.model.Order;

/**
 * Created by Masafumi_Ito on 2017/10/26.
 */

public class GetUsersAllOrderTask extends AsyncTask<Void, Void, List<Order>> {

    private AsyncCallback mAsyncCallback = null;
    private String mAppToken = null;

    public GetUsersAllOrderTask setAppToken(String appToken) {
        mAppToken = appToken;
        return this;
    }

    public interface AsyncCallback {
        void onSuccess(List<Order> orderList);
        void onFailure(Exception e);
    }

    public GetUsersAllOrderTask(AsyncCallback asyncCallback) {
        this.mAsyncCallback = asyncCallback;
    }

    @Override
    protected List<Order> doInBackground(Void... params) {

        Gson gson = new Gson();

        // サーバーにjsonを送信
        String responseJsonString = null;
        try {
            responseJsonString
                    = Util.Http.get("http://52.196.33.58/api/users/orders.json", mAppToken);
        } catch (Exception e) {
            onFailure(e);
        }

        List<Order> test = gson.fromJson(responseJsonString, new TypeToken<ArrayList<Order>>(){}.getType());

        return gson.fromJson(responseJsonString, new TypeToken<ArrayList<Order>>(){}.getType());
    }

    @Override
    protected void onPostExecute(List<Order> orderList) {
        super.onPostExecute(orderList);
        onSuccess(orderList);
    }

    private void onSuccess(List<Order> orderList) {
        this.mAsyncCallback.onSuccess(orderList);
    }

    private void onFailure(Exception e) {
        this.mAsyncCallback.onFailure(e);
    }
}