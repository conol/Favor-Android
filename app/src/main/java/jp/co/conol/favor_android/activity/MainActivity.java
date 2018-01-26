package jp.co.conol.favor_android.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.custom.CuonaUtil;
import jp.co.conol.favor_android.custom.ProgressDialog;
import jp.co.conol.favor_android.custom.ScanCuonaDialog;
import jp.co.conol.favor_android.custom.SimpleAlertDialog;
import jp.co.conol.favorlib.cuona.Cuona;
import jp.co.conol.favorlib.cuona.NFCNotAvailableException;
import jp.co.conol.favorlib.cuona.cuona_reader.CuonaReaderException;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.favor_model.Shop;
import jp.co.conol.favorlib.cuona.favor_model.User;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity {

    private ScanCuonaDialog mScanCuonaDialog;
    private final Gson mGson = new Gson();
    private Cuona mCuona;
    private boolean isSuccessfulConnection = false;
    List<String> mAvailableDeviceIdList = new ArrayList<>();    // Favorのサービスに登録されているデバイスのID一覧
    @BindView(R.id.shopHistoryConstraintLayout) ConstraintLayout mShopHistoryConstraintLayout;
    @BindView(R.id.userSettingConstraintLayout) ConstraintLayout mUserSettingConstraintLayout;
    @BindView(R.id.userSettingTextView) TextView mUserSettingTextView;
    @BindView(R.id.userImageView) ImageView mUserImageView;
    @BindView(R.id.shopNameTextView) TextView mShopNameTextView;
    @BindView(R.id.shopImageView) RoundedImageView mShopImageView;  // 入店履歴の背景画像
    private final int PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // shop情報が保存されている場合は入店後画面へ（入店中にアプリ再起動への対応）
        if(MyUtil.SharedPref.getString(this, "shop") != null) {
            Intent shopDetailIntent = new Intent(MainActivity.this, ShopDetailActivity.class);
            startActivity(shopDetailIntent);
            finish();
        }

        try {
            mCuona = new Cuona(this);
        } catch (NFCNotAvailableException e) {
            Log.d("CuonaError", e.toString());
            finish();
        }

        // CUONAスキャンダイアログのインスタンスを生成
        mScanCuonaDialog = new ScanCuonaDialog(MainActivity.this, mCuona, 60000, false);

        // Android6.0以上はACCESS_COARSE_LOCATIONの許可が必要
        CuonaUtil.checkAccessCoarseLocationPermission(this, PERMISSION_REQUEST_CODE);

        // nfcがオフの場合はダイアログを表示
        CuonaUtil.checkNfcSetting(this, mCuona);

        // ユーザーのAppTokenを取得
        String appToken = MyUtil.SharedPref.getString(this, "appToken");

        if(appToken != null) {
            if(MyUtil.Network.isEnable(MainActivity.this)) {

                // 読み込みダイアログを表示
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage(getString(R.string.main_progress_message));
                progressDialog.show();

                // ユーザー情報を取得
                new Favor(new Favor.AsyncCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        @SuppressWarnings("unchecked")
                        User user = (User) object;

                        // ユーザー情報を表示
                        String userSettingTitle = user.getNickname() + getResources().getString(R.string.user_setting_title);
                        mUserSettingTextView.setText(userSettingTitle);
                        if(user.getImageUrl() != null) {
                            Picasso.with(MainActivity.this)
                                    .load(user.getImageUrl())
                                    .transform(new CropCircleTransformation())
                                    .into(mUserImageView);
                        } else {
                            Picasso.with(MainActivity.this).load(R.drawable.ic_user_prof).into(mUserImageView);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("onFailure", e.toString());
                    }
                }).setAppToken(appToken).execute(Favor.Task.GetUser);

                // 入店履歴を取得
                new Favor(new Favor.AsyncCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        @SuppressWarnings("unchecked")
                        List<Shop> shopList = (List<Shop>) object;

                        if (shopList.size() != 0) {

                            // 最後に入店した店舗のオブジェクトを取得
                            Shop shop = shopList.get(shopList.size() - 1);

                            // 最後に入店した店舗の情報を画面に反映
                            mShopNameTextView.setText(shop.getShopName());  // 店舗名
                            Picasso.with(MainActivity.this).load(shop.getShopImages()[0]).into(mShopImageView); // 画像
                        } else {
                            mShopNameTextView.setText(getString(R.string.shop_history_none));
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("onFailure", e.toString());
                    }
                }).setAppToken(appToken).execute(Favor.Task.GetVisitedShopHistory);

                // Favorのサービスに登録されているデバイスID一覧を取得
                new Favor(new Favor.AsyncCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        mAvailableDeviceIdList = (List<String>) object;

                        // 読み込みダイアログを非表示
                        progressDialog.dismiss();

                        isSuccessfulConnection = true;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                // 読み込みダイアログを非表示
                                progressDialog.dismiss();

                                new SimpleAlertDialog(MainActivity.this, getString(R.string.error_common)).show();
                            }
                        });
                    }
                }).execute(Favor.Task.GetAvailableDevices);

                // 入店履歴をタップした時の動作
                mShopHistoryConstraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, ShopHistoryActivity.class);
                        startActivity(intent);
                    }
                });

                // ユーザー情報をタップした時の動作
                mUserSettingConstraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, UserActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                new SimpleAlertDialog(MainActivity.this, getString(R.string.error_network_disable_reboot)).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCuona != null) mCuona.enableForegroundDispatch(MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mCuona != null) mCuona.disableForegroundDispatch(MainActivity.this);
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        if(mScanCuonaDialog.isShowing() && isSuccessfulConnection) {

            // nfc読み込み処理実行
            String deviceId;
            try {
                mCuona.setReadLogMessage("入店");
                deviceId = mCuona.readDeviceId(intent);
            } catch (CuonaReaderException e) {
                Log.d("CuonaReader", e.toString());
                new SimpleAlertDialog(MainActivity.this, getString(R.string.error_not_exist_in_devise_ids)).show();
                return;
            }

            // サーバーに登録されているFavor利用可能なデバイスに、タッチされたNFCが含まれているか否か確認
            if (!mAvailableDeviceIdList.contains(deviceId)) {
                new SimpleAlertDialog(MainActivity.this, getString(R.string.error_not_exist_in_devise_ids)).show();
            }
            // 含まれていれば店舗詳細ページへ移動
            else {
                Intent shopDetailIntent = new Intent(MainActivity.this, ShopDetailActivity.class);
                shopDetailIntent.putExtra("deviceId", deviceId);
                MyUtil.SharedPref.saveBoolean(MainActivity.this, "isEntering", true);
                startActivity(shopDetailIntent);
                finish();
                mScanCuonaDialog.dismiss();
            }
        }
    }

    public void onStartScanButtonClicked(View view) {

        // Android6.0以上はACCESS_COARSE_LOCATIONの許可が必要
        CuonaUtil.checkAccessCoarseLocationPermission(this, PERMISSION_REQUEST_CODE);

        // nfcがオフの場合はダイアログを表示
        CuonaUtil.checkNfcSetting(this, mCuona);

        // NFCが許可されている場合処理を進める
        if(mCuona.isNfcEnabled()) {
            mScanCuonaDialog.show();
        } else {
            CuonaUtil.checkNfcSetting(this, mCuona);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(mScanCuonaDialog != null && mScanCuonaDialog.isShowing()) {
                mScanCuonaDialog.dismiss();
            } else {
                finish();
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {

            // パーミッションを許可しない場合
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.grant_permission, Toast.LENGTH_LONG).show();
            }
        }
    }
}
