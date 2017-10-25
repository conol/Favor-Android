package jp.co.conol.favorlib.favor;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import jp.co.conol.favorlib.Util;
import jp.co.conol.favorlib.favor.model.Favorite;

/**
 * Created by Masafumi_Ito on 2017/10/26.
 */

public class DeleteFavoriteTask extends AsyncTask<Void, Void, List<Favorite>> {

    private AsyncCallback mAsyncCallback = null;
    private String mAppToken = null;
    private String mFavoriteId = null;

    public DeleteFavoriteTask setAppToken(String appToken) {
        mAppToken = appToken;
        return this;
    }

    public DeleteFavoriteTask setFavoriteId(int favoriteId) {
        mFavoriteId = String.valueOf(favoriteId);
        return this;
    }

    public interface AsyncCallback {
        void onSuccess(List<Favorite> favoriteList);
        void onFailure(Exception e);
    }

    public DeleteFavoriteTask(AsyncCallback asyncCallback) {
        this.mAsyncCallback = asyncCallback;
    }

    @Override
    protected List<Favorite> doInBackground(Void... params) {

        Gson gson = new Gson();

        // サーバーにjsonを送信
        String responseJsonString = null;
        try {
            responseJsonString = Util.Http.delete("http://52.196.33.58/api/users/favorites/" + mFavoriteId + ".json", mAppToken);
        } catch (Exception e) {
            onFailure(e);
        }

        return gson.fromJson(responseJsonString, new TypeToken<ArrayList<Favorite>>(){}.getType());
    }

    @Override
    protected void onPostExecute(List<Favorite> favoriteList) {
        super.onPostExecute(favoriteList);
        onSuccess(favoriteList);
    }

    private void onSuccess(List<Favorite> favoriteList) {
        this.mAsyncCallback.onSuccess(favoriteList);
    }

    private void onFailure(Exception e) {
        this.mAsyncCallback.onFailure(e);
    }
}
