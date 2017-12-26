package jp.co.conol.favor_android.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.custom.CuonaUtil;
import jp.co.conol.favor_android.custom.ScanCuonaDialog;
import jp.co.conol.favor_android.custom.SimpleAlertDialog;
import jp.co.conol.favorlib.cuona.Cuona;
import jp.co.conol.favorlib.cuona.NFCNotAvailableException;
import jp.co.conol.favorlib.cuona.cuona_reader.CuonaReaderException;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.favor_model.Shop;
import jp.co.conol.favorlib.cuona.favor_model.User;

public class MainActivity extends AppCompatActivity {

    private ScanCuonaDialog mScanCuonaDialog;
    private final Gson mGson = new Gson();
    private Cuona mCuona;
    private User mUser;
    List<String> mDeviceIds = new ArrayList<>();    // Favorのサービスに登録されているデバイスのID一覧
    @BindView(R.id.shopHistoryConstraintLayout) ConstraintLayout mShopHistoryConstraintLayout;
    @BindView(R.id.userSettingConstraintLayout) ConstraintLayout mUserSettingConstraintLayout;
    @BindView(R.id.userSettingTextView) TextView mUserSettingTextView;
//    @BindView(R.id.shopHistoryTitleTextView) TextView mShopHistoryTitleTextView;
    @BindView(R.id.shopNameTextView) TextView mShopNameTextView;
    private final int PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        try {
            mCuona = new Cuona(this);
        } catch (NFCNotAvailableException e) {
            Log.d("CUONA", e.toString());
            finish();
        }

        // CUONAスキャンダイアログのインスタンスを生成
        mScanCuonaDialog = new ScanCuonaDialog(MainActivity.this, mCuona, 60000, false);

        // Android6.0以上はACCESS_COARSE_LOCATIONの許可が必要
        CuonaUtil.checkAccessCoarseLocationPermission(this, PERMISSION_REQUEST_CODE);

        // nfcがオフの場合はダイアログを表示
        CuonaUtil.checkNfcSetting(this, mCuona);

        // ユーザー情報を取得
        mUser = mGson.fromJson(MyUtil.SharedPref.getString(this, "userSetting"), User.class);

        if(mUser != null) {

            // 入店履歴を取得
            new Favor(new Favor.AsyncCallback() {
                @Override
                public void onSuccess(Object object) {
                    @SuppressWarnings("unchecked")
                    List<Shop> shopList = (List<Shop>) object;

                    if (shopList != null && shopList.size() != 0) {

                        // 最後に入店した店舗のオブジェクトを取得
                        Shop shop = shopList.get(shopList.size() - 1);

                        mShopNameTextView.setText(shop.getShopName());
                    } else {
                        mShopNameTextView.setText(getString(R.string.shop_history_none));
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("onFailure", e.toString());
                }
            }).setAppToken(mUser.getAppToken()).execute(Favor.Task.GetVisitedShopHistory);

            // 入店履歴をタップした時の動作
            mShopHistoryConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        Intent intent = new Intent(MainActivity.this, ShopHistoryActivity.class);
                        startActivity(intent);
                    }
                    return false;
                }
            });

            // ユーザー名を表示
            String userSettingTitle = mUser.getNickname() + getResources().getString(R.string.user_setting_title);
            mUserSettingTextView.setText(userSettingTitle);

            // ユーザー情報をタップした時の動作
            mUserSettingConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        Intent intent = new Intent(MainActivity.this, UserActivity.class);
                        startActivity(intent);
                    }
                    return false;
                }
            });
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
        if(mScanCuonaDialog.isShowing()) {

            // サーバーに登録されているデバイスIDを取得
            if (MyUtil.Network.isEnable(this)) {
                new Favor(new Favor.AsyncCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        @SuppressWarnings("unchecked")
                        List<String> deviceIdList = (List<String>) object;

                        // 接続成功してもデバイスID一覧が無ければエラー
                        if(deviceIdList == null || deviceIdList.size() == 0) {
                            new SimpleAlertDialog(MainActivity.this, getString(R.string.error_fail_get_device_ids)).show();
                            return;
                        } else {
                            // デバイスIDのリストを作成
                            mDeviceIds.addAll(deviceIdList);
                        }

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
                        if(mDeviceIds != null && deviceId != null) {

                            // デバイスIDを小文字にする
                            deviceId = deviceId.toLowerCase();

                            if (!mDeviceIds.contains(deviceId)) {
                                new SimpleAlertDialog(MainActivity.this, getString(R.string.error_not_exist_in_devise_ids)).show();
                            }
                            // 含まれていれば処理を進める
                            else {

                                Intent shopDetailIntent = new Intent(MainActivity.this, ShopDetailActivity.class);
                                shopDetailIntent.putExtra("deviceId", deviceId);
                                startActivity(shopDetailIntent);
                                mScanCuonaDialog.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                new SimpleAlertDialog(MainActivity.this, getString(R.string.error_fail_get_device_ids)).show();
                            }
                        });
                    }
                }).execute(Favor.Task.GetAvailableDevices);
            }
            // ネットに未接続の場合はエラー
            else {
                new SimpleAlertDialog(MainActivity.this, getString(R.string.error_network_disable)).show();
            }
        }
    }

    public void onStartScanButtonClicked(View view) {

        // Android6.0以上はACCESS_COARSE_LOCATIONの許可が必要
        CuonaUtil.checkAccessCoarseLocationPermission(this, PERMISSION_REQUEST_CODE);

        // nfcがオフの場合はダイアログを表示
        CuonaUtil.checkNfcSetting(this, mCuona);

        // BluetoothとNFCが許可されている場合処理を進める
        if(mCuona.isNfcEnabled()) {
            mScanCuonaDialog.show();
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
