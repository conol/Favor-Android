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

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.custom.ProgressDialog;
import jp.co.conol.favor_android.custom.SimpleAlertDialog;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.FavorException;
import jp.co.conol.favorlib.cuona.favor_model.User;

public class RegistrationActivity extends AppCompatActivity {

    @BindView(R.id.userNameEditText) EditText mUserNameEditText;
    @BindView(R.id.userAgeEditText) TextView mUserAgeEditText;
    @BindView(R.id.userGenderEditText) TextView mUserGenderEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        // ユーザー情報が保存されていれば、登録画面は表示しない
        if(Favor.hasToken(this)) {
            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // 手動入力を禁止
        mUserAgeEditText.setKeyListener(null);      // 年代
        mUserGenderEditText.setKeyListener(null);   // 性別

        // 年代がタップされた場合
        mUserAgeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) showAgeEditDialog();
            }
        });
        mUserAgeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAgeEditDialog();
            }
        });

        // 性別がタップされた場合
        mUserGenderEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) showGenderEditDialog();
            }
        });
        mUserGenderEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGenderEditDialog();
            }
        });
    }

    public void onUserRegistrationButtonClicked(View view) {

        // 入力フォームのバリデーション
        if(Objects.equals(mUserNameEditText.getText().toString(), "")
                || Objects.equals(mUserAgeEditText.getText().toString(), "")
                || Objects.equals(mUserGenderEditText.getText().toString(), "")) {
            new SimpleAlertDialog(this, getString(R.string.validation_user_setting)).show();
            return;
        }

        // ネットワークに接続されているか確認
        if(!MyUtil.Network.isEnable(RegistrationActivity.this)) {
            new SimpleAlertDialog(RegistrationActivity.this, getString(R.string.error_network_disable)).show();
            return;
        }

        // 読み込みダイアログを表示
        final ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this);
        progressDialog.setMessage(getString(R.string.registration_user_progress));
        progressDialog.show();

        // 登録するユーザー情報
        String gender = "male";
        if (mUserGenderEditText.getText().toString().equals("女性")) gender = "female";
        User user = new User(
                mUserNameEditText.getText().toString(),
                gender,
                Integer.parseInt(mUserAgeEditText.getText().toString().replace("代", "")),
                null,
                null
        );

        // ユーザー情報を登録
        new Favor(new Favor.AsyncCallback() {
            @Override
            public void onSuccess(Object object) {

                // 読み込みダイアログを非表示
                progressDialog.dismiss();

                // ページ移動
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(FavorException e) {
                Log.d("onFailure", e.toString());
                runOnUiThread(new Runnable() {
                    public void run() {
                        // 読み込みダイアログを非表示
                        progressDialog.dismiss();

                        new SimpleAlertDialog(RegistrationActivity.this, getString(R.string.error_common)).show();
                    }
                });
            }
        }).setContext(this).setUser(user).execute(Favor.Task.ResisterUser);
    }

    private void showAgeEditDialog() {
        final String[] ages = {"10代", "20代", "30代", "40代", "50代", "60代", "70代", "80代"};
        new AlertDialog.Builder(RegistrationActivity.this)
                .setItems(ages, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUserAgeEditText.setText(ages[which]);
                    }
                })
                .show();
    }

    private void showGenderEditDialog() {
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
