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

public class OrderMenuTask extends AsyncTask<Void, Void, List<Order>> {

    private AsyncCallback mAsyncCallback = null;
    private String mAppToken = null;
    private String mVisitHistoryId = null;
    private List<Order> mOrderList = new ArrayList<>();

    public OrderMenuTask setAppToken(String appToken) {
        mAppToken = appToken;
        return this;
    }

    public OrderMenuTask setVisitHistoryId(int visitHistoryId) {
        mVisitHistoryId = String.valueOf(visitHistoryId);
        return this;
    }

    public OrderMenuTask setOrder(List<Order> orderList) {
        mOrderList = orderList;
        return this;
    }

    public interface AsyncCallback {
        void onSuccess(List<Order> orderList);
        void onFailure(Exception e);
    }

    public OrderMenuTask(AsyncCallback asyncCallback) {
        this.mAsyncCallback = asyncCallback;
    }

    @Override
    protected List<Order> doInBackground(Void... params) {

        Gson gson = new Gson();

        // オーダー用のjsonを作成
        String orderString = gson.toJson(mOrderList).replace("[", "{\"orders\": [").replace("]", "]}");

        // サーバーにjsonを送信
        String responseJsonString = null;
        try {
            responseJsonString
                    = Util.Http.post("http://52.196.33.58/api/users/visit_histories/" + mVisitHistoryId + "/orders.json", mAppToken, orderString);
        } catch (Exception e) {
            onFailure(e);
        }

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
