package jp.co.conol.favor_android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favorlib.favor.Favor;
import jp.co.conol.favorlib.favor.model.UsersSetting;
import jp.co.conol.favorlib.favor.model.User;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mUserNameEditText;
    private TextView mUserAgeEditText;
    private TextView mUserGenderEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // ユーザー情報が保存されていれば、登録画面は表示しない
        if(MyUtil.SharedPref.get(RegistrationActivity.this, "userSetting") != null) {
            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        mUserNameEditText = (EditText) findViewById(R.id.userNameEditText);
        mUserAgeEditText = (EditText) findViewById(R.id.userAgeEditText);
        mUserGenderEditText = (EditText) findViewById(R.id.userGenderEditText);

        // 手動入力を禁止
        mUserAgeEditText.setKeyListener(null);
        mUserGenderEditText.setKeyListener(null);

        // 年代がタップされた場合
        mUserAgeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    final String[] ages = {"20", "30", "40", "50", "60", "70"};
                    new AlertDialog.Builder(RegistrationActivity.this)
                            .setItems(ages, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mUserAgeEditText.setText(ages[which]);
                                }
                            })
                            .show();
                }
            }
        });

        // 性別がタップされた場合
        mUserGenderEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    final String[] genders = {"男性", "女性"};
                    new AlertDialog.Builder(RegistrationActivity.this)
                            .setItems(genders, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mUserGenderEditText.setText(genders[which]);
                                }
                            })
                            .show();
                }
            }
        });
    }

    public void onUserRegistrationButtonClicked(View view) {
        // 登録するユーザー情報
        String gender = "male";
        if(mUserGenderEditText.getText().toString().equals("女性")) gender = "female";
        UsersSetting usersSetting = new UsersSetting(
                mUserNameEditText.getText().toString(),
                gender,
                Integer.parseInt(mUserAgeEditText.getText().toString()),
                null
        );

        // ユーザー情報を登録
        new Favor(new Favor.AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                User user = (User) object;

                // ユーザー情報を端末に保存
                Gson gson = new Gson();
                MyUtil.SharedPref.save(RegistrationActivity.this, "userSetting", gson.toJson(user));

                // ページ移動
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("failUserRegistration", e.toString());
            }
        }).setUsersSetting(usersSetting).execute();
    }
}
