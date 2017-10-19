package jp.co.conol.favor_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import jp.co.conol.favorlib.favor.RegistrationUserAsyncTask;
import jp.co.conol.favorlib.favor.model.Register;
import jp.co.conol.favorlib.favor.model.User;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Register register = new Register("ito", "male", 20, null, null, null, null);
        new RegistrationUserAsyncTask(new RegistrationUserAsyncTask.AsyncCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d("test", user.getApp_token());
            }

            @Override
            public void onFailure(Exception e) {

            }
        }).execute(register);
    }
}
