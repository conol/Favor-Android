package jp.co.conol.favorlib.favor;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import jp.co.conol.favorlib.Util;
import jp.co.conol.favorlib.favor.model.Menu;

/**
 * Created by Masafumi_Ito on 2017/10/25.
 */

public class GetMenuTask extends AsyncTask<Void, Void, List<Menu>> {

    private AsyncCallback mAsyncCallback = null;
    private String mAppToken = null;
    private String mShopId = null;

    public GetMenuTask setAppToken(String appToken) {
        mAppToken = appToken;
        return this;
    }

    public GetMenuTask setShopId(int shopId) {
        mShopId = String.valueOf(shopId);
        return this;
    }

    public interface AsyncCallback {
        void onSuccess(List<Menu> menuList);
        void onFailure(Exception e);
    }

    public GetMenuTask(AsyncCallback asyncCallback) {
        this.mAsyncCallback = asyncCallback;
    }

    @Override
    protected List<Menu> doInBackground(Void... params) {

        Gson gson = new Gson();

        // サーバーにjsonを送信
        String responseJsonString = null;
        try {
            responseJsonString = Util.Http.get("http://52.196.33.58/api/users/shops/" + mShopId + "/menu.json", mAppToken);
        } catch (Exception e) {
            onFailure(e);
        }

        return gson.fromJson(responseJsonString, new TypeToken<ArrayList<Menu>>(){}.getType());
    }

    @Override
    protected void onPostExecute(List<Menu> menuList) {
        super.onPostExecute(menuList);
        onSuccess(menuList);
    }

    private void onSuccess(List<Menu> menuList) {
        this.mAsyncCallback.onSuccess(menuList);
    }

    private void onFailure(Exception e) {
        this.mAsyncCallback.onFailure(e);
    }
}
