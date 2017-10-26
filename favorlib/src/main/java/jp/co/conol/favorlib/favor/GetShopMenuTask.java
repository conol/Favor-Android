package jp.co.conol.favorlib.favor;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import jp.co.conol.favorlib.Util;
import jp.co.conol.favorlib.favor.model.ShopMenu;

/**
 * Created by Masafumi_Ito on 2017/10/25.
 */

public class GetShopMenuTask extends AsyncTask<Void, Void, List<ShopMenu>> {

    private AsyncCallback mAsyncCallback = null;
    private String mAppToken = null;
    private String mShopId = null;

    public GetShopMenuTask setAppToken(String appToken) {
        mAppToken = appToken;
        return this;
    }

    public GetShopMenuTask setShopId(int shopId) {
        mShopId = String.valueOf(shopId);
        return this;
    }

    public interface AsyncCallback {
        void onSuccess(List<ShopMenu> shopMenuList);
        void onFailure(Exception e);
    }

    public GetShopMenuTask(AsyncCallback asyncCallback) {
        this.mAsyncCallback = asyncCallback;
    }

    @Override
    protected List<ShopMenu> doInBackground(Void... params) {

        Gson gson = new Gson();

        // サーバーにjsonを送信
        String responseJsonString = null;
        try {
            responseJsonString = Util.Http.get("http://52.196.33.58/api/users/shops/" + mShopId + "/menu.json", mAppToken);
        } catch (Exception e) {
            onFailure(e);
        }

        return gson.fromJson(responseJsonString, new TypeToken<ArrayList<ShopMenu>>(){}.getType());
    }

    @Override
    protected void onPostExecute(List<ShopMenu> shopMenuList) {
        super.onPostExecute(shopMenuList);
        onSuccess(shopMenuList);
    }

    private void onSuccess(List<ShopMenu> shopMenuList) {
        this.mAsyncCallback.onSuccess(shopMenuList);
    }

    private void onFailure(Exception e) {
        this.mAsyncCallback.onFailure(e);
    }
}
