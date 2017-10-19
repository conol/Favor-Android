package jp.co.conol.favor_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import jp.co.conol.favorlib.favor.RegistrationUserAsyncTask;
import jp.co.conol.favorlib.favor.model.Register;
import jp.co.conol.favorlib.favor.model.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
