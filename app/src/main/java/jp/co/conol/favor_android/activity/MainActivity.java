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

import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favorlib.corona.Corona;
import jp.co.conol.favorlib.corona.NfcNotAvailableException;
import jp.co.conol.favorlib.favor.AddFavoriteTask;
import jp.co.conol.favorlib.favor.DeleteFavoriteTask;
import jp.co.conol.favorlib.favor.EnterShopTask;
import jp.co.conol.favorlib.favor.GetFavoritesTask;
import jp.co.conol.favorlib.favor.GetShopDetailTask;
import jp.co.conol.favorlib.favor.GetShopMenuTask;
import jp.co.conol.favorlib.favor.GetVisitedShopHistoriesTask;
import jp.co.conol.favorlib.favor.OrderMenuTask;
import jp.co.conol.favorlib.favor.model.Favorite;
import jp.co.conol.favorlib.favor.model.Order;
import jp.co.conol.favorlib.favor.model.Shop;
import jp.co.conol.favorlib.favor.model.ShopMenu;
import jp.co.conol.favorlib.favor.model.User;
import jp.co.conol.favorlib.favor.model.VisitedShop;

public class MainActivity extends AppCompatActivity {

    private boolean isScanning = false;
    private Handler mScanDialogAutoCloseHandler = new Handler();
    private Corona mCorona;
    private ConstraintLayout mShopHistoryConstraintLayout;
    private ConstraintLayout mUserSettingConstraintLayout;
    private TextView mUserSettingTextView;
    private ConstraintLayout mScanBackgroundConstraintLayout;
    private ConstraintLayout mScanDialogConstraintLayout;
    private final int PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShopHistoryConstraintLayout = (ConstraintLayout) findViewById(R.id.shopHistoryConstraintLayout);
        mUserSettingConstraintLayout = (ConstraintLayout) findViewById(R.id.userSettingConstraintLayout);
        mUserSettingTextView = (TextView) findViewById(R.id.userSettingTextView);
        mScanBackgroundConstraintLayout = (ConstraintLayout) findViewById(R.id.ScanBackgroundConstraintLayout);
        mScanDialogConstraintLayout = (ConstraintLayout) findViewById(R.id.scanDialogConstraintLayout);





        Gson gsonTmp = new Gson();
        User userTmp = gsonTmp.fromJson(MyUtil.SharedPref.get(this, "userSetting"), User.class);

        // 入店履歴一覧取得API
        new GetVisitedShopHistoriesTask(new GetVisitedShopHistoriesTask.AsyncCallback() {
            @Override
            public void onSuccess(List<VisitedShop> visitedShopList) {
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("onFailure", e.toString());
            }
        }).setAppToken(userTmp.getAppToken()).execute();


        // 店舗詳細取得API
        new GetShopDetailTask(new GetShopDetailTask.AsyncCallback() {
            @Override
            public void onSuccess(Shop shop) {
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("onFailure", e.toString());
            }
        }).setAppToken(userTmp.getAppToken()).execute();


        // 店舗メニュー取得API
        new GetShopMenuTask(new GetShopMenuTask.AsyncCallback() {
            @Override
            public void onSuccess(List<ShopMenu> shopMenuList) {
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("onFailure", e.toString());
            }
        }).setAppToken(userTmp.getAppToken()).setShopId(1).execute();


        // 入店API
        new EnterShopTask(new EnterShopTask.AsyncCallback() {
            @Override
            public void onSuccess(Shop shop) {
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("onFailure", e.toString());
            }
        }).setAppToken(userTmp.getAppToken()).setDeviceId("04 b5 38 01 72 d5 38").execute();


        // お気に入り追加
        new AddFavoriteTask(new AddFavoriteTask.AsyncCallback() {
            @Override
            public void onSuccess(Favorite favorite) {
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("onFailure", e.toString());
            }
        }).setAppToken(userTmp.getAppToken()).setName("酢豚").setLevel(5).execute();

        // お気に入り一覧取得
        new GetFavoritesTask(new GetFavoritesTask.AsyncCallback() {
            @Override
            public void onSuccess(List<Favorite> favoriteList) {
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("onFailure", e.toString());
            }
        }).setAppToken(userTmp.getAppToken()).execute();

        // お気に入り削除
//        new DeleteFavoriteTask(new DeleteFavoriteTask.AsyncCallback() {
//            @Override
//            public void onSuccess(List<Favorite> favoriteList) {
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                Log.d("onFailure", e.toString());
//            }
//        }).setAppToken(userTmp.getAppToken()).setFavoriteId(7).execute();

        // 注文する
        List<Order> orderList = new ArrayList<>();
        orderList.add(new Order(1, 2));
        new OrderMenuTask(new OrderMenuTask.AsyncCallback() {
            @Override
            public void onSuccess(List<Order> orderList) {
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("onFailure", e.toString());
            }
        }).setAppToken(userTmp.getAppToken()).setVisitHistoryId(17).setOrder(orderList).execute();





        try {
            mCorona = new Corona(this);
        } catch (NfcNotAvailableException e) {
            Log.d("Corona", e.toString());
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
        if(!mCorona.isEnable()) {
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

        // 入店履歴をタップした時の動作
        mShopHistoryConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(MainActivity.this, ShopHistoryActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        // ユーザー情報を取得
        Gson gson = new Gson();
        User user = gson.fromJson(MyUtil.SharedPref.get(this, "userSetting"), User.class);

        // ユーザー名を表示
        String userSettingTitle = user.getNickname() + getResources().getString(R.string.user_setting_title);
        mUserSettingTextView.setText(userSettingTitle);

        // ユーザー情報をタップした時の動作
        mUserSettingConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
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
        else if(!mCorona.isEnable()) {
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
                mCorona.enableForegroundDispatch(MainActivity.this);
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
        mCorona.disableForegroundDispatch(MainActivity.this);
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
