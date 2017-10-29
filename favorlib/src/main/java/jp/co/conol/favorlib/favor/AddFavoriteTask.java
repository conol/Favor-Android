//package jp.co.conol.favorlib.favor;
//
//import android.os.AsyncTask;
//
//import com.google.gson.Gson;
//
//import jp.co.conol.favorlib.Util;
//import jp.co.conol.favorlib.favor.model.Favorite;
//import jp.co.conol.favorlib.favor.model.Shop;
//
///**
// * Created by Masafumi_Ito on 2017/10/26.
// */
//
//public class AddFavoriteTask extends AsyncTask<Void, Void, Favorite> {
//
//    private AsyncCallback mAsyncCallback = null;
//    private String mAppToken = null;
//    private String mName = null;
//    private int mLevel = 3;
//
//    public AddFavoriteTask setAppToken(String appToken) {
//        mAppToken = appToken;
//        return this;
//    }
//
//    public AddFavoriteTask setName(String name) {
//        mName = name;
//        return this;
//    }
//
//    public AddFavoriteTask setLevel(int level) {
//        mLevel = level;
//        return this;
//    }
//
//    public interface AsyncCallback {
//        void onSuccess(Favorite favorite);
//        void onFailure(Exception e);
//    }
//
//    public AddFavoriteTask(AsyncCallback asyncCallback) {
//        this.mAsyncCallback = asyncCallback;
//    }
//
//    @Override
//    protected Favorite doInBackground(Void... params) {
//
//        Gson gson = new Gson();
//
//        // お気に入り追加用のjsonを作成
//        String favoriteString = "{\"name\": \"" + mName + "\",\"level\": " + mLevel + "}";
//
//        // サーバーにjsonを送信
//        String responseJsonString = null;
//        try {
//            responseJsonString = Util.Http.post("http://52.196.33.58/api/users/favorites.json", mAppToken, favoriteString);
//        } catch (Exception e) {
//            onFailure(e);
//        }
//
//        return gson.fromJson(responseJsonString, Favorite.class);
//    }
//
//    @Override
//    protected void onPostExecute(Favorite favorite) {
//        super.onPostExecute(favorite);
//        onSuccess(favorite);
//    }
//
//    private void onSuccess(Favorite favorite) {
//        this.mAsyncCallback.onSuccess(favorite);
//    }
//
//    private void onFailure(Exception e) {
//        this.mAsyncCallback.onFailure(e);
//    }
//}
