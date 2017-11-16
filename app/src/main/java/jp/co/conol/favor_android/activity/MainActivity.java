package jp.co.conol.favor_android.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favorlib.Util;
import jp.co.conol.favorlib.cuona.Cuona;
import jp.co.conol.favorlib.cuona.CuonaException;
import jp.co.conol.favorlib.cuona.NfcNotAvailableException;
import jp.co.conol.favorlib.favor.Favor;
import jp.co.conol.favorlib.favor.model.Shop;
import jp.co.conol.favorlib.favor.model.User;

public class MainActivity extends AppCompatActivity {

    private boolean isScanning = false;
    private Handler mScanDialogAutoCloseHandler = new Handler();
    private final Gson mGson = new Gson();
    private Cuona mCuona;
    private User mUser;
    List<String> mDeviceIds = new ArrayList<>();    // Favorのサービスに登録されているデバイスのID一覧
    @BindView(R.id.shopHistoryConstraintLayout) ConstraintLayout mShopHistoryConstraintLayout;
    @BindView(R.id.userSettingConstraintLayout) ConstraintLayout mUserSettingConstraintLayout;
    @BindView(R.id.userSettingTextView) TextView mUserSettingTextView;
    @BindView(R.id.ScanBackgroundConstraintLayout) ConstraintLayout mScanBackgroundConstraintLayout;
    @BindView(R.id.scanDialogConstraintLayout) ConstraintLayout mScanDialogConstraintLayout;
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
        } catch (NfcNotAvailableException e) {
            Log.d("CUONA", e.toString());
            finish();
        }

        // Android6.0以上はACCESS_COARSE_LOCATIONの許可が必要
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 許可されていない場合
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // 許可を求めるダイアログを表示
                ActivityCompat.requestPermissions(this,
                        new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION },
                        PERMISSION_REQUEST_CODE
                );
            }
        }

        // nfcがオフの場合はダイアログを表示
        if(!mCuona.isEnable()) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.nfc_dialog))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        }

        // ユーザー情報を取得
        mUser = mGson.fromJson(MyUtil.SharedPref.get(this, "userSetting"), User.class);

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

            // スキャン画面が開いているときは、背景のタップを出来ないように設定
            mScanBackgroundConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return isScanning;
                }
            });
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        if(isScanning) {

            // サーバーに登録されているデバイスIDを取得
            final Handler handler = new Handler();
            if (Util.Network.isEnable(this) || Util.Wifi.isEnable(MainActivity.this)) {
                new Favor(new Favor.AsyncCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        @SuppressWarnings("unchecked")
                        List<String> deviceIdList = (List<String>) object;

                        // 接続成功してもデバイスID一覧が無ければエラー
                        if(deviceIdList == null || deviceIdList.size() == 0) {
                            showAlertDialog();
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
                        } catch (CuonaException e) {
                            Log.d("CuonaReader", e.toString());
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage(getString(R.string.error_not_exist_in_devise_ids))
                                    .setPositiveButton(getString(R.string.ok), null)
                                    .show();
                            return;
                        }

                        // サーバーに登録されているWifiHelper利用可能なデバイスに、タッチされたNFCが含まれているか否か確認
                        if(mDeviceIds != null && deviceId != null) {

                            // デバイスIDを小文字にする
                            deviceId = deviceId.toLowerCase();

                            if (!mDeviceIds.contains(deviceId)) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setMessage(getString(R.string.error_not_exist_in_devise_ids))
                                        .setPositiveButton(getString(R.string.ok), null)
                                        .show();
                            }
                            // 含まれていれば処理を進める
                            else {

                                Intent shopDetailIntent = new Intent(MainActivity.this, ShopDetailActivity.class);
                                shopDetailIntent.putExtra("deviceId", deviceId);
                                startActivity(shopDetailIntent);
                                isScanning = false;
                                closeScanPage();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        showAlertDialog();
                    }

                    // デバイスID取得失敗でアラートを表示
                    private void showAlertDialog() {
                        handler.post(new Runnable() {
                            public void run() {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setMessage(getString(R.string.error_fail_get_device_ids))
                                        .setPositiveButton(getString(R.string.ok), null)
                                        .show();
                            }
                        });
                    }
                }).execute(Favor.Task.GetAvailableDevices);
            }
            // ネットに未接続の場合はエラー
            else {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.error_network_disable))
                        .setPositiveButton(getString(R.string.ok), null)
                        .show();
            }
        }
    }

    public void onStartScanButtonClicked(View view) {
        // Android6.0以上はACCESS_COARSE_LOCATIONの許可が必要（ログ送信用）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // 許可を求めるダイアログを表示
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_REQUEST_CODE
            );

        }
        // nfcがオフの場合はダイアログを表示
        else if(!mCuona.isEnable()) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.nfc_dialog))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        } else {
            if (!isScanning) {

                // nfc読み込み待機
                mCuona.enableForegroundDispatch(MainActivity.this);
                isScanning = true;
                openScanPage();

                // 60秒後に自動で閉じる
                mScanDialogAutoCloseHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isScanning) {
                            cancelScan();
                        }
                    }
                }, 60000);
            }
        }
    }

    public void onCancelScanButtonClicked(View view) {
        if(isScanning) {
            cancelScan();

            // 60秒後に閉じる予約をキャンセル
            mScanDialogAutoCloseHandler.removeCallbacksAndMessages(null);
        }
    }

    private void cancelScan() {
        // nfc読み込み待機を解除
        mCuona.disableForegroundDispatch(MainActivity.this);
        isScanning = false;
        closeScanPage();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK) {

            // 読み込み中に戻るタップでスキャン中止
            if(isScanning) {
                cancelScan();
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

    private void closeScanPage() {
        mScanDialogConstraintLayout.setVisibility(View.GONE);
        mScanBackgroundConstraintLayout.setVisibility(View.GONE);
        mScanDialogConstraintLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_to_bottom));
        mScanBackgroundConstraintLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_slowly));
        mScanDialogAutoCloseHandler.removeCallbacksAndMessages(null);
    }

    // 読み込み画面を表示
    private void openScanPage() {
        mScanDialogConstraintLayout.setVisibility(View.VISIBLE);
        mScanBackgroundConstraintLayout.setVisibility(View.VISIBLE);
        mScanDialogConstraintLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom));
        mScanBackgroundConstraintLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_slowly));
    }
}
