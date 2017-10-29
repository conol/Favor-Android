//package jp.co.conol.favorlib.favor;
//
//import android.os.AsyncTask;
//
//import com.google.gson.Gson;
//
//import jp.co.conol.favorlib.Util;
//import jp.co.conol.favorlib.favor.model.UsersSetting;
//import jp.co.conol.favorlib.favor.model.User;
//
///**
// * Created by Masafumi_Ito on 2017/10/29.
// */
//
//public class EditUserTask extends AsyncTask<Void, Void, User> {
//
//    private AsyncCallback mAsyncCallback = null;
//    private String mAppToken = null;
//    private UsersSetting mUsersSetting = null;
//
//    public interface AsyncCallback{
//        void onSuccess(User user);
//        void onFailure(Exception e);
//    }
//
//    public EditUserTask setAppToken(String appToken) {
//        mAppToken = appToken;
//        return this;
//    }
//
//    public EditUserTask setUsersSetting(UsersSetting usersSetting) {
//        mUsersSetting = usersSetting;
//        return this;
//    }
//
//    public EditUserTask(AsyncCallback asyncCallback){
//        this.mAsyncCallback = asyncCallback;
//    }
//
//    @Override
//    protected User doInBackground(Void... params) {
//
//        Gson gson = new Gson();
//
//        // ユーザー登録用のjsonを作成
//        String editUserString = gson.toJson(mUsersSetting);
//
//        // サーバーにjsonを送信
//        String responseJsonString = null;
//        try {
//            responseJsonString = Util.Http.patch("http://52.196.33.58/api/users/setting.json", mAppToken, editUserString);
//        } catch (Exception e) {
//            onFailure(e);
//        }
//
//        User test = gson.fromJson(responseJsonString, User.class);
//
//        return gson.fromJson(responseJsonString, User.class);
//    }
//
//    @Override
//    protected void onPostExecute(User user) {
//        super.onPostExecute(user);
//        onSuccess(user);
//    }
//
//    private void onSuccess(User user) {
//        this.mAsyncCallback.onSuccess(user);
//    }
//
//    private void onFailure(Exception e) {
//        this.mAsyncCallback.onFailure(e);
//    }
//}
