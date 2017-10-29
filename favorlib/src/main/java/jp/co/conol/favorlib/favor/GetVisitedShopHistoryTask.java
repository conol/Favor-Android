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
//import jp.co.conol.favorlib.favor.model.Shop;
//
///**
// * Created by Masafumi_Ito on 2017/10/25.
// */
//
//public class GetVisitedShopHistoryTask extends AsyncTask<Void, Void, List<Shop>> {
//
//    private AsyncCallback mAsyncCallback = null;
//    private String mAppToken = null;
//
//    public GetVisitedShopHistoryTask setAppToken(String appToken) {
//        mAppToken = appToken;
//        return this;
//    }
//
//    public interface AsyncCallback {
//        void onSuccess(List<Shop> shopList);
//        void onFailure(Exception e);
//    }
//
//    public GetVisitedShopHistoryTask(AsyncCallback asyncCallback) {
//        this.mAsyncCallback = asyncCallback;
//    }
//
//    @Override
//    protected List<Shop> doInBackground(Void... params) {
//
//        Gson gson = new Gson();
//
//        // サーバーにjsonを送信
//        String responseJsonString = null;
//        try {
//            responseJsonString = Util.Http.get("http://52.196.33.58/api/users/visit_histories.json?page=1&per=20", mAppToken);
//        } catch (Exception e) {
//            onFailure(e);
//        }
//
//        List<Shop> test = gson.fromJson(responseJsonString, new TypeToken<ArrayList<Shop>>(){}.getType());
//
//        return gson.fromJson(responseJsonString, new TypeToken<ArrayList<Shop>>(){}.getType());
//    }
//
//    @Override
//    protected void onPostExecute(List<Shop> shopList) {
//        super.onPostExecute(shopList);
//        onSuccess(shopList);
//    }
//
//    private void onSuccess(List<Shop> shopList) {
//        this.mAsyncCallback.onSuccess(shopList);
//    }
//
//    private void onFailure(Exception e) {
//        this.mAsyncCallback.onFailure(e);
//    }
//}
