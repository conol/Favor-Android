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
// * Created by Masafumi_Ito on 2017/10/13.
// */
//
//public class RegisterUserTask extends AsyncTask<Void, Void, User> {
//
//    private AsyncCallback mAsyncCallback = null;
//    private UsersSetting mUsersSetting = null;
//
//    public interface AsyncCallback{
//        void onSuccess(User user);
//        void onFailure(Exception e);
//    }
//
//    public RegisterUserTask setUsersSetting(UsersSetting usersSetting) {
//        mUsersSetting = usersSetting;
//        return this;
//    }
//
//    public RegisterUserTask(AsyncCallback asyncCallback){
//        this.mAsyncCallback = asyncCallback;
//    }
//
//    @Override
//    protected User doInBackground(Void... params) {
//
//        Gson gson = new Gson();
//
//        // ユーザー登録用のjsonを作成
//        String registrationUserString = gson.toJson(mUsersSetting);
//
//        // サーバーにjsonを送信
//        String responseJsonString = null;
//        try {
//            responseJsonString = Util.Http.post("http://52.196.33.58/api/users/register.json", null, registrationUserString);
//        } catch (Exception e) {
//            onFailure(e);
//        }
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
