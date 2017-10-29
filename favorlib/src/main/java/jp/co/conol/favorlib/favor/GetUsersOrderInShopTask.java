//package jp.co.conol.favorlib.favor;
//
//import android.os.AsyncTask;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import jp.co.conol.favorlib.Util;
//import jp.co.conol.favorlib.favor.model.Order;
//
///**
// * Created by Masafumi_Ito on 2017/10/26.
// */
//
//public class GetUsersOrderInShopTask extends AsyncTask<Void, Void, List<Order>> {
//
//    private AsyncCallback mAsyncCallback = null;
//    private String mAppToken = null;
//    private String mVisitHistoryId = null;
//
//    public GetUsersOrderInShopTask setAppToken(String appToken) {
//        mAppToken = appToken;
//        return this;
//    }
//
//    public GetUsersOrderInShopTask setVisitHistoryId(int visitHistoryId) {
//        mVisitHistoryId = String.valueOf(visitHistoryId);
//        return this;
//    }
//
//    public interface AsyncCallback {
//        void onSuccess(List<Order> orderList);
//        void onFailure(Exception e);
//    }
//
//    public GetUsersOrderInShopTask(AsyncCallback asyncCallback) {
//        this.mAsyncCallback = asyncCallback;
//    }
//
//    @Override
//    protected List<Order> doInBackground(Void... params) {
//
//        Gson gson = new Gson();
//
//        // サーバーにjsonを送信
//        String responseJsonString = null;
//        try {
//            responseJsonString
//                    = Util.Http.get("http://52.196.33.58/api/users/visit_histories/" + mVisitHistoryId + "/orders.json", mAppToken);
//        } catch (Exception e) {
//            onFailure(e);
//        }
//
//        return gson.fromJson(responseJsonString, new TypeToken<ArrayList<Order>>(){}.getType());
//    }
//
//    @Override
//    protected void onPostExecute(List<Order> orderList) {
//        super.onPostExecute(orderList);
//        onSuccess(orderList);
//    }
//
//    private void onSuccess(List<Order> orderList) {
//        this.mAsyncCallback.onSuccess(orderList);
//    }
//
//    private void onFailure(Exception e) {
//        this.mAsyncCallback.onFailure(e);
//    }
//}
