package jp.co.conol.favorlib.cuona;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.co.conol.favorlib.cuona.favor_model.Favorite;
import jp.co.conol.favorlib.cuona.favor_model.Menu;
import jp.co.conol.favorlib.cuona.favor_model.Order;
import jp.co.conol.favorlib.cuona.favor_model.Shop;
import jp.co.conol.favorlib.cuona.favor_model.User;
import jp.co.conol.favorlib.cuona.favor_model.UsersAllOrder;

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
    private User mUser = null;
    private Favorite mFavorite = null;
    private String mFavoriteId = null;
    private List<Order> mOrderList = new ArrayList<>();

    public enum Task {
        GetAvailableDevices,        // Favorを利用可能なデバイスのデバイスID一覧取得
        ResisterUser,               // ユーザー情報登録
        EditUser,                   // ユーザー情報編集
        EnterShop,                  // 入店
        GetVisitedShopHistory,      // 入店履歴取得
        GetShopDetail,              // 店舗詳細取得
        GetMenu,                    // 店舗メニュー取得
        Order,                      // 注文
        GetUsersOrderInShop,        // 注文履歴一覧取得(来店個人単位)
        GetUserGroupsOrderInShop,   // 注文履歴一覧取得(来店グループ単位)
        GetUsersAllOrder,           // 注文履歴一覧取得(ユーザーの全店舗での注文履歴)
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
        mDeviceId = deviceIdForServer(deviceId); // サーバーで送信可能な形式に変換
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

    public Favor setUser(User user) {
        mUser = user;
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
        String favorEndPoint = Constants.Urls.FAVOR_URL;
        String deviceManagerEndPoint = Constants.Urls.CDMS_URL;

        // サーバーに送信用するjsonをURLを作成
        String apiUrl = null;
        String requestJsonString = null;
        JSONObject json = null;
        String responseJsonString = null;
        String responseJsonDataString = null;
        JSONObject jsonObject = null;
        Type type = null;
        boolean isDevelopment = false;

        try {
            switch (params[0]) {

                // Favorを利用可能なデバイスのデバイスID一覧取得
                case GetAvailableDevices:
                    String appToken = null;
                    isDevelopment = Cuona.isDevelopment();
                    if(isDevelopment) {
                        apiUrl = "/api/admins/devices.json";
                        appToken = "J5B4o9y2iJTbckKfxsLsKq23";
                    } else {
                        apiUrl = "/api/services/" + Constants.Urls.SERVICE_KEY + ".json";
                    }
                    responseJsonString = get(deviceManagerEndPoint + apiUrl, appToken);
                    type = new TypeToken<ArrayList<String>>(){}.getType();

                    // APIレスポンスからdata部分を取得
                    jsonObject = new JSONObject(responseJsonString);
                    responseJsonDataString = jsonObject.getString("data");

                    List<String> availableDeviceIdList = new ArrayList<>();
                    if(responseJsonDataString != null) {
                        try {
                            JSONArray jsonArray = new JSONArray(responseJsonDataString);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jRec = jsonArray.getJSONObject(i);
                                if(isDevelopment && !jRec.getBoolean("is_development")) continue;   // 開発版鍵のときはCUONA開発版のデバイスID全てを取得
                                String deviceId = jRec.getString("device_id");
                                availableDeviceIdList.add(deviceId);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // デバイスのオブジェクト一覧のjsonをデバイスIDのオブジェクト一覧に変更
                    responseJsonDataString = gson.toJson(availableDeviceIdList);
                    break;

                // ユーザー情報登録
                case ResisterUser:
                    apiUrl = "/api/users/register.json";
                    requestJsonString = gson.toJson(mUser);
                    responseJsonString = post(favorEndPoint + apiUrl, null, requestJsonString);
                    type = new TypeToken<User>(){}.getType();
                    break;

                // ユーザー情報編集
                case EditUser:
                    apiUrl = "/api/users/setting.json";
                    requestJsonString = gson.toJson(mUser);
                    responseJsonString = patch(favorEndPoint + apiUrl, mAppToken, requestJsonString);
                    type = new TypeToken<User>(){}.getType();
                    break;

                // 入店
                case EnterShop:
                    apiUrl = "/api/users/enter.json";
                    json = new JSONObject("{\"device_id\": \"" + mDeviceId + "\"}");
                    requestJsonString = json.toString();
                    responseJsonString = post(favorEndPoint + apiUrl, mAppToken, requestJsonString);
                    type = new TypeToken<Shop>(){}.getType();
                    break;

                // 入店履歴取得
                case GetVisitedShopHistory:
                    apiUrl = "/api/users/visit_histories.json?page=1&per=20";
                    responseJsonString = get(favorEndPoint + apiUrl, mAppToken);
                    type = new TypeToken<ArrayList<Shop>>(){}.getType();
                    break;

                // 店舗詳細取得
                case GetShopDetail:
                    apiUrl = "/api/users/shops/" + mShopId + ".json";
                    responseJsonString = get(favorEndPoint + apiUrl, mAppToken);
                    type = new TypeToken<Shop>(){}.getType();
                    break;

                // 店舗メニュー取得
                case GetMenu:
                    apiUrl = "/api/users/shops/" + mShopId + "/menu.json";
                    responseJsonString = get(favorEndPoint + apiUrl, mAppToken);
                    type = new TypeToken<ArrayList<Menu>>(){}.getType();
                    break;

                // 注文
                case Order:
                    apiUrl = "/api/users/visit_histories/" + mVisitHistoryId + "/orders.json";
                    requestJsonString = gson.toJson(mOrderList).replace("[", "{\"orders\": [").replace("]", "]}");
                    responseJsonString = post(favorEndPoint + apiUrl, mAppToken, requestJsonString);
                    type = new TypeToken<ArrayList<Order>>(){}.getType();
                    break;

                // 注文履歴一覧取得(来店個人単位)
                case GetUsersOrderInShop:
                    apiUrl = "/api/users/visit_histories/" + mVisitHistoryId + "/orders.json";
                    responseJsonString = get(favorEndPoint + apiUrl, mAppToken);
                    type = new TypeToken<ArrayList<Order>>(){}.getType();
                    break;

                // 注文履歴一覧取得(来店グループ単位)
                case GetUserGroupsOrderInShop:
                    apiUrl = "/api/users/visit_groups/" + mVisitGroupId + "/orders.json";
                    responseJsonString = get(favorEndPoint + apiUrl, mAppToken);
                    type = new TypeToken<ArrayList<Order>>(){}.getType();
                    break;

                // 注文履歴一覧取得(ユーザーの全店舗での注文履歴)
                case GetUsersAllOrder:
                    apiUrl = "/api/users/orders.json?page=1&per=20";
                    responseJsonString = get(favorEndPoint + apiUrl, mAppToken);
                    type = new TypeToken<ArrayList<UsersAllOrder>>(){}.getType();
                    break;

                // オーダーストップ
                case OrderStop:
                    apiUrl = "/api/users/visit_histories/" + mVisitHistoryId + "/order_stop.json";
                    responseJsonString = put(favorEndPoint + apiUrl, mAppToken, null);
                    type = new TypeToken<ArrayList<Order>>(){}.getType();
                    break;

                // お気に入り登録
                case AddFavorite:
                    apiUrl = "/api/users/favorites.json";
                    json = new JSONObject("{\"name\": \"" + mFavorite.getName() + "\",\"level\": " + String.valueOf(mFavorite.getLevel()) + "}");
                    requestJsonString = json.toString();
                    responseJsonString = post(favorEndPoint + apiUrl, mAppToken, requestJsonString);
                    type = new TypeToken<Favorite>(){}.getType();
                    break;

                // お気に入り編集
                case EditFavorite:
                    apiUrl = "/api/users/favorites/" + mFavoriteId + ".json";
                    json = new JSONObject("{\"name\": \"" + mFavorite.getName() + "\",\"level\": " + String.valueOf(mFavorite.getLevel()) + "}");
                    requestJsonString = json.toString();
                    responseJsonString = patch(favorEndPoint + apiUrl, mAppToken, requestJsonString);
                    type = new TypeToken<Favorite>(){}.getType();
                    break;

                // お気に入り一覧取得
                case GetFavorites:
                    apiUrl = "/api/users/favorites.json";
                    responseJsonString = get(favorEndPoint + apiUrl, mAppToken);
                    type = new TypeToken<ArrayList<Favorite>>(){}.getType();
                    break;

                // お気に入り削除
                case DeleteFavorite:
                    apiUrl = "/api/users/favorites/" + mFavoriteId + ".json";
                    responseJsonString = delete(favorEndPoint + apiUrl, mAppToken);
                    type = new TypeToken<ArrayList<Favorite>>(){}.getType();
                    break;

                default:
                    break;
            }

            // APIレスポンスからdata部分を取得
            if(params[0] != Task.GetAvailableDevices) { // GetAvailableDevicesは別処理でjsonを作成
                jsonObject = new JSONObject(responseJsonString);
                responseJsonDataString = jsonObject.getString("data");
            }

        } catch (Exception e) {
            Log.e("FavorError", "Please set Task in execute() argument");
            onFailure(e);
        }

        return gson.fromJson(responseJsonDataString, type);
    }

    @Override
    protected void onPostExecute(Object obj) {
        super.onPostExecute(obj);
        onSuccess(obj);
    }

    // デバイスIDをサーバーに送信可能な形式に変換
    private String deviceIdForServer(String deviceId) {
        if(deviceId != null) {
            String deviceIdTmp = deviceId.replace(" ", "").toLowerCase();
            StringBuilder deviceIdToSend = new StringBuilder(deviceIdTmp);
            for (int i = 0; i < (deviceIdTmp.length() - 2) / 2; i++) {
                deviceIdToSend.insert((deviceIdTmp.length() - 2) - (2 * i), " ");
            }
            return deviceIdToSend.toString();
        } else {
            return null;
        }
    }

    private void onSuccess(Object obj) {
        this.mAsyncCallback.onSuccess(obj);
    }

    private void onFailure(Exception e) {
        this.mAsyncCallback.onFailure(e);
    }

    private String post(String url, String appToken, String body) {
        String responseJsonString = null;
        try {
            String buffer = "";
            HttpURLConnection con = null;
            URL urlTmp = new URL(url);
            con = (HttpURLConnection) urlTmp.openConnection();
            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Accept-Language", "jp");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            if(appToken != null && !Objects.equals(appToken, "")) con.setRequestProperty("Authorization", "Bearer " + appToken);
            OutputStream os = con.getOutputStream();
            PrintStream ps = new PrintStream(os);
            ps.print(body);
            ps.close();

            // レスポンスを取得
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            responseJsonString = reader.readLine();

            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("HttpException", e.toString());
        }

        return responseJsonString;
    }

    private String get(String url, String appToken) {
        HttpURLConnection urlCon;
        InputStream in;

        try {
            urlCon = (HttpURLConnection) new URL(url).openConnection();
            urlCon.setRequestMethod("GET");
            urlCon.setDoInput(true);
            urlCon.setRequestProperty("Authorization", "Bearer " + appToken);
            urlCon.connect();

            String str_json;
            in = urlCon.getInputStream();
            InputStreamReader objReader = new InputStreamReader(in);
            BufferedReader objBuf = new BufferedReader(objReader);
            StringBuilder strBuilder = new StringBuilder();
            String sLine;
            while((sLine = objBuf.readLine()) != null){
                strBuilder.append(sLine);
            }
            str_json = strBuilder.toString();
            in.close();

            return str_json;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("HttpException", e.toString());
            return null;
        }
    }

    private String patch(String url, String appToken, String body) {
        String responseJsonString = null;
        try {
            String buffer = "";
            HttpURLConnection con = null;
            URL urlTmp = new URL(url);
            con = (HttpURLConnection) urlTmp.openConnection();
            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Accept-Language", "jp");
            con.setRequestProperty("X-HTTP-Method", "PATCH"); // Microsoft
            con.setRequestProperty("X-HTTP-Method-Override", "PATCH");  // Google/GData
            con.setRequestProperty("X-Method-Override", "PATCH");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            if(appToken != null || appToken != "") con.setRequestProperty("Authorization", "Bearer " + appToken);
            OutputStream os = con.getOutputStream();
            PrintStream ps = new PrintStream(os);
            ps.print(body);
            ps.close();

            // レスポンスを取得
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            responseJsonString = reader.readLine();

            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("HttpException", e.toString());
        }

        return responseJsonString;
    }

    private String put(String url, String appToken, String body) {
        String responseJsonString = null;
        try {
            String buffer = "";
            HttpURLConnection con = null;
            URL urlTmp = new URL(url);
            con = (HttpURLConnection) urlTmp.openConnection();
            con.setRequestMethod("PUT");
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Accept-Language", "jp");
            con.setDoOutput(true);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            if(appToken != null || appToken != "") con.setRequestProperty("Authorization", "Bearer " + appToken);
            OutputStream os = con.getOutputStream();
            PrintStream ps = new PrintStream(os);
            ps.print(body);
            ps.close();

            // レスポンスを取得
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            responseJsonString = reader.readLine();

            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("HttpException", e.toString());
        }

        return responseJsonString;
    }

    private String delete(String url, String appToken) {
        String responseJsonString = null;
        try {
            String buffer = "";
            HttpURLConnection con = null;
            URL urlTmp = new URL(url);
            con = (HttpURLConnection) urlTmp.openConnection();
            con.setRequestMethod("DELETE");
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Accept-Language", "jp");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            if(appToken != null || appToken != "") con.setRequestProperty("Authorization", "Bearer " + appToken);
            con.getResponseCode();

            // レスポンスを取得
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            responseJsonString = reader.readLine();

            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("HttpException", e.toString());
        }

        return responseJsonString;
    }
}