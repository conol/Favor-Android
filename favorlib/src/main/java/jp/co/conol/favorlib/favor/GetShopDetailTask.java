package jp.co.conol.favorlib.favor;

import android.os.AsyncTask;

import com.google.gson.Gson;

import jp.co.conol.favorlib.Util;
import jp.co.conol.favorlib.favor.model.Shop;

/**
 * Created by Masafumi_Ito on 2017/10/25.
 */

public class GetShopDetailTask extends AsyncTask<Void, Void, Shop> {

    private AsyncCallback mAsyncCallback = null;
    private String mAppToken = null;

    public GetShopDetailTask setAppToken(String appToken) {
        mAppToken = appToken;
        return this;
    }

    public interface AsyncCallback {
        void onSuccess(Shop shop);
        void onFailure(Exception e);
    }

    public GetShopDetailTask(AsyncCallback asyncCallback) {
        this.mAsyncCallback = asyncCallback;
    }

    @Override
    protected Shop doInBackground(Void... params) {

        Gson gson = new Gson();

        // サーバーにjsonを送信
        String responseJsonString = null;
        try {
            responseJsonString = Util.Http.get("http://52.196.33.58/api/users/shop/1.json", mAppToken);
        } catch (Exception e) {
            onFailure(e);
        }

        return gson.fromJson(responseJsonString, Shop.class);
    }

    @Override
    protected void onPostExecute(Shop shop) {
        super.onPostExecute(shop);
        onSuccess(shop);
    }

    private void onSuccess(Shop shop) {
        this.mAsyncCallback.onSuccess(shop);
    }

    private void onFailure(Exception e) {
        this.mAsyncCallback.onFailure(e);
    }
}
