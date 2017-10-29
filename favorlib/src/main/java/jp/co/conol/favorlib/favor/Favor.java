package jp.co.conol.favorlib.favor;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jp.co.conol.favorlib.Util;
import jp.co.conol.favorlib.favor.model.Favorite;
import jp.co.conol.favorlib.favor.model.Menu;
import jp.co.conol.favorlib.favor.model.Order;
import jp.co.conol.favorlib.favor.model.Shop;
import jp.co.conol.favorlib.favor.model.User;
import jp.co.conol.favorlib.favor.model.UsersSetting;

/**
 * Created by Masafumi_Ito on 2017/10/29.
 */

public class Favor extends AsyncTask<Favor.Task, Void, Object> {

    private AsyncCallback mAsyncCallback = null;
    private String mAppToken = null;
    private String mDeviceId = null;
    private String mShopId = null;
    private String mVisitHistoryId = null;
    private String mVisitGroupId = null;
    private UsersSetting mUsersSetting = null;
    private Favorite mFavorite = null;
    private String mFavoriteId = null;
    private List<Order> mOrderList = new ArrayList<>();

    public enum Task {
        ResisterUser,               // ユーザー情報登録
        EditUser,                   // ユーザー情報編集
        EnterShop,                  // 入店
        GetVisitedShopHistory,      // 入店履歴取得
        GetShopDetail,              // 店舗詳細取得
        GetMenu,                    // 店舗メニュー取得
        Order,                      // 注文
        GetUsersOrderInShop,        // 注文履歴一覧取得(来店個人単位)
        GetUserGroupsOrderInShop,   // 注文履歴一覧取得(来店グループ単位)
        OrderStop,                  // オーダーストップ
        AddFavorite,                // お気に入り登録
        EditFavorite,               // お気に入り編集
        GetFavorites,               // お気に入り一覧取得
        DeleteFavorite,             // お気に入り削除
    }

    public interface AsyncCallback {
        void onSuccess(Object obj);
        void onFailure(Exception e);
    }

    public Favor(AsyncCallback asyncCallback){
        this.mAsyncCallback = asyncCallback;
    }

    public Favor setAppToken(String appToken) {
        mAppToken = appToken;
        return this;
    }

    public Favor setDeviceId(String deviceId) {
        mDeviceId = Util.Transform.deviceIdForServer(deviceId); // サーバーで送信可能な形式に変換
        return this;
    }

    public Favor setShopId(Integer shopId) {
        mShopId = String.valueOf(shopId);
        return this;
    }

    public Favor setVisitHistoryId(int visitHistoryId) {
        mVisitHistoryId = String.valueOf(visitHistoryId);
        return this;
    }

    public Favor setVisitGroupId(int visitGroupId) {
        mVisitGroupId = String.valueOf(visitGroupId);
        return this;
    }

    public Favor setUsersSetting(UsersSetting usersSetting) {
        mUsersSetting = usersSetting;
        return this;
    }

    public Favor setFavorite(Favorite favorite) {
        mFavorite = favorite;
        return this;
    }

    public Favor setFavoriteId(int favoriteId) {
        mFavoriteId = String.valueOf(favoriteId);
        return this;
    }

    public Favor setOrder(List<Order> orderList) {
        mOrderList = orderList;
        return this;
    }

    @Override
    protected Object doInBackground(Favor.Task... params) {

        Gson gson = new Gson();
        String endPoint = "http://52.196.33.58";

        // サーバーに送信用するjsonをURLを作成
        String apiUrl = null;
        String requestJsonString = null;
        String responseJsonString = null;
        Type type = null;

        try {
            switch (params[0]) {

                // ユーザー情報登録
                case ResisterUser:
                    apiUrl = "/api/users/register.json";
                    requestJsonString = gson.toJson(mUsersSetting);
                    responseJsonString = Util.Http.post(endPoint + apiUrl, null, requestJsonString);
                    type = new TypeToken<User>(){}.getType();
                    break;

                // ユーザー情報編集
                case EditUser:
                    apiUrl = "/api/users/setting.json";
                    requestJsonString = gson.toJson(mUsersSetting);
                    responseJsonString = Util.Http.patch(endPoint + apiUrl, mAppToken, requestJsonString);
                    type = new TypeToken<User>(){}.getType();
                    break;

                // 入店
                case EnterShop:
                    apiUrl = "/api/users/enter.json";
                    requestJsonString = gson.toJson(mDeviceId);
                    responseJsonString = Util.Http.post(endPoint + apiUrl, mAppToken, requestJsonString);
                    type = new TypeToken<Shop>(){}.getType();
                    break;

                // 入店履歴取得
                case GetVisitedShopHistory:
                    apiUrl = "/api/users/visit_histories.json?page=1&per=20";
                    responseJsonString = Util.Http.get(endPoint + apiUrl, mAppToken);
                    type = new TypeToken<ArrayList<Shop>>(){}.getType();
                    break;

                // 店舗詳細取得
                case GetShopDetail:
                    apiUrl = "/api/users/shops/" + mShopId + ".json";
                    responseJsonString = Util.Http.get(endPoint + apiUrl, mAppToken);
                    type = new TypeToken<Shop>(){}.getType();
                    break;

                // 店舗メニュー取得
                case GetMenu:
                    apiUrl = "/api/users/shops/" + mShopId + "/menu.json";
                    responseJsonString = Util.Http.get(endPoint + apiUrl, mAppToken);
                    type = new TypeToken<Menu>(){}.getType();
                    break;

                // 注文
                case Order:
                    apiUrl = "/api/users/visit_histories/" + mVisitHistoryId + "/orders.json";
                    requestJsonString = gson.toJson(mOrderList).replace("[", "{\"orders\": [").replace("]", "]}");
                    responseJsonString = Util.Http.post(endPoint + apiUrl, mAppToken, requestJsonString);
                    type = new TypeToken<ArrayList<Order>>(){}.getType();
                    break;

                // 注文履歴一覧取得(来店個人単位)
                case GetUsersOrderInShop:
                    apiUrl = "/api/users/visit_histories/" + mVisitHistoryId + "/orders.json";
                    responseJsonString = Util.Http.get(endPoint + apiUrl, mAppToken);
                    type = new TypeToken<ArrayList<Order>>(){}.getType();
                    break;

                // 注文履歴一覧取得(来店グループ単位)
                case GetUserGroupsOrderInShop:
                    apiUrl = "/api/users/visit_groups/" + mVisitGroupId + "/orders.json";
                    responseJsonString = Util.Http.get(endPoint + apiUrl, mAppToken);
                    type = new TypeToken<ArrayList<Order>>(){}.getType();
                    break;

                // オーダーストップ
                case OrderStop:
                    apiUrl = "/api/users/visit_histories/" + mVisitHistoryId + "/order_stop.json";
                    responseJsonString = Util.Http.put(endPoint + apiUrl, mAppToken, null);
                    type = new TypeToken<ArrayList<Order>>(){}.getType();
                    break;

                // お気に入り登録
                case AddFavorite:
                    apiUrl = "/api/users/favorites.json";
                    requestJsonString = "{\"name\": \"" + mFavorite.getName() + "\",\"level\": " + String.valueOf(mFavorite.getLevel()) + "}";
                    responseJsonString = Util.Http.post(endPoint + apiUrl, mAppToken, requestJsonString);
                    type = new TypeToken<Favorite>(){}.getType();
                    break;

                // お気に入り編集
                case EditFavorite:
                    apiUrl = "/api/users/favorites/" + mFavoriteId + ".json";
                    requestJsonString = "{\"name\": \"" + mFavorite.getName() + "\",\"level\": " + String.valueOf(mFavorite.getLevel()) + "}";
                    responseJsonString = Util.Http.patch(endPoint + apiUrl, mAppToken, requestJsonString);
                    type = new TypeToken<Favorite>(){}.getType();
                    break;

                // お気に入り一覧取得
                case GetFavorites:
                    apiUrl = "/api/users/favorites.json";
                    responseJsonString = Util.Http.get(endPoint + apiUrl, mAppToken);
                    type = new TypeToken<ArrayList<Favorite>>(){}.getType();
                    break;

                // お気に入り削除
                case DeleteFavorite:
                    apiUrl = "/api/users/favorites/" + mFavoriteId + ".json";
                    responseJsonString = Util.Http.delete(endPoint + apiUrl, mAppToken);
                    type = new TypeToken<ArrayList<Favorite>>(){}.getType();
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            onFailure(e);
        }

        Object test = gson.fromJson(responseJsonString, type);

        return gson.fromJson(responseJsonString, type);
    }

    @Override
    protected void onPostExecute(Object obj) {
        super.onPostExecute(obj);
        onSuccess(obj);
    }

    private void onSuccess(Object obj) {
        this.mAsyncCallback.onSuccess(obj);
    }

    private void onFailure(Exception e) {
        this.mAsyncCallback.onFailure(e);
    }
}