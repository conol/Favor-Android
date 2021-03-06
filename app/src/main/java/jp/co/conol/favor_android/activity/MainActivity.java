package jp.co.conol.favor_android.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.custom.CuonaUtil;
import jp.co.conol.favor_android.custom.ProgressDialog;
import jp.co.conol.favor_android.custom.ScanCuonaDialog;
import jp.co.conol.favor_android.custom.SimpleAlertDialog;
import jp.co.conol.favorlib.cuona.Cuona;
import jp.co.conol.favorlib.cuona.FavorException;
import jp.co.conol.favorlib.cuona.NFCNotAvailableException;
import jp.co.conol.favorlib.cuona.cuona_reader.CuonaReaderException;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.favor_model.Shop;
import jp.co.conol.favorlib.cuona.favor_model.User;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MainActivity extends AppCompatActivity {

    private ScanCuonaDialog mScanCuonaDialog;
    private Cuona mCuona;
    private boolean isSuccessfulConnectionUser = false;     // ユーザー情報の取得に失敗したか否か
    private boolean isSuccessfulConnectionEnterHistory = false; // 入店履歴の取得に失敗したか否か
    @BindView(R.id.shopHistoryConstraintLayout) ConstraintLayout mShopHistoryConstraintLayout;
    @BindView(R.id.userSettingConstraintLayout) ConstraintLayout mUserSettingConstraintLayout;
    @BindView(R.id.userSettingTextView) TextView mUserSettingTextView;
    @BindView(R.id.userImageView) ImageView mUserImageView;     // ユーザー画像
    @BindView(R.id.backImageView) RoundedImageView mBackImageView;     // ユーザー画像の背景
    @BindView(R.id.shopNameTextView) TextView mShopNameTextView;    // 入店履歴の店舗名
    @BindView(R.id.shopEnterAtTextView) TextView mShopEnterAtTextView;  // 入店履歴の入店日時
    @BindView(R.id.shopImageView) ImageView mShopImageView;  // 入店履歴の背景画像
    private final int PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // 入店中の場合は入店後画面へ（入店中にアプリ再起動への対応）
        if(MyUtil.SharedPref.getBoolean(MainActivity.this, "isEntering", false)) {
            if(MyUtil.SharedPref.getBoolean(MainActivity.this, "isBack", false)) {
                MyUtil.SharedPref.saveBoolean(MainActivity.this, "isBack", false);
                finish();
            } else {
                Intent shopDetailIntent = new Intent(MainActivity.this, ShopDetailActivity.class);
                startActivity(shopDetailIntent);
            }
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

        // ネットワークに繋がっているか確認
        if(!MyUtil.Network.isEnable(MainActivity.this)) {
            new SimpleAlertDialog(MainActivity.this, getString(R.string.error_network_disable_reboot)).show();
            return;
        }

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

                isSuccessfulConnectionUser = true;

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
            public void onFailure(FavorException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        new SimpleAlertDialog(MainActivity.this, getString(R.string.error_get_user)).show();
                    }
                });
            }
        }).setContext(this).execute(Favor.Task.GetUser);

        // 入店履歴を取得
        new Favor(new Favor.AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                @SuppressWarnings("unchecked")
                List<Shop> shopList = (List<Shop>) object;

                // 読み込みダイアログを非表示
                progressDialog.dismiss();

                isSuccessfulConnectionEnterHistory = true;

                if (shopList.size() != 0) {

                    // 最後に入店した店舗のオブジェクトを取得
                    Shop shop = shopList.get(shopList.size() - 1);

                    // 最後に入店した店舗の情報を画面に反映
                    mShopNameTextView.setText(shop.getName());  // 店舗名
                    DateTimeFormatter DEF_FMT = DateTimeFormat.forPattern("yyyy/MM/dd (E) HH:mm~"); // 入店時間
                    mShopEnterAtTextView.setText(DEF_FMT.print(DateTime.parse(shop.getEnteredAt())));
                    Picasso.with(MainActivity.this).load(shop.getImageUrls()[0])   // 画像
                            .fit()
                            .transform(new RoundedCornersTransformation(12, 0))
                            .into(mShopImageView);
                } else {
                    mShopNameTextView.setText(getString(R.string.shop_history_none));
                }
            }

            @Override
            public void onFailure(FavorException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        // 読み込みダイアログを非表示
                        progressDialog.dismiss();

                        new SimpleAlertDialog(MainActivity.this, getString(R.string.error_get_enter_history)).show();
                    }
                });
            }
        }).setContext(this).execute(Favor.Task.GetVisitedShopHistory);

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
                finish();
            }
        });
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

        // CUONA読み込みダイアログが開いているか確認
        if(!mScanCuonaDialog.isShowing()) {
            return;
        }

        // 初回読み込みに成功しているか確認
        if(!isSuccessfulConnectionUser || !isSuccessfulConnectionEnterHistory) {
            new SimpleAlertDialog(MainActivity.this, getString(R.string.error_init_loading)).show();
            return;
        }

        // ネットワークに繋がっているか確認
        if(!MyUtil.Network.isEnable(MainActivity.this)) {
            new SimpleAlertDialog(MainActivity.this, getString(R.string.error_network_disable)).show();
            return;
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

        // 読み込みダイアログを表示
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.main_progress_message));
        progressDialog.show();

        // 店舗情報を取得
        new Favor(new Favor.AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                Shop shop = (Shop) object;

                // 読み込みダイアログを非表示
                progressDialog.dismiss();

                // 店舗詳細ページへ移動
                Intent shopDetailIntent = new Intent(MainActivity.this, ShopDetailActivity.class);
                MyUtil.SharedPref.saveBoolean(MainActivity.this, "isEntering", true);
                MyUtil.SharedPref.saveInt(MainActivity.this, "shopId", shop.getShopId());
                shopDetailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(shopDetailIntent);
                mScanCuonaDialog.dismiss();
            }

            @Override
            public void onFailure(final FavorException e) {
                Log.d("onFailure", e.toString());
                runOnUiThread(new Runnable() {
                    public void run() {
                        // 読み込みダイアログを非表示
                        progressDialog.dismiss();

                        if(Objects.equals(e.getType(), "AlreadyEntered")) {
                            new SimpleAlertDialog(MainActivity.this, getString(R.string.error_already_entered)).show();
                        } else if(Objects.equals(e.getType(), "GroupNotActive")) {
                            new SimpleAlertDialog(MainActivity.this, getString(R.string.error_group_not_active)).show();
                        } else {
                            new SimpleAlertDialog(MainActivity.this, getString(R.string.error_common)).show();
                        }
                    }
                });
            }
        }).setContext(this).setDeviceId(deviceId).execute(Favor.Task.EnterShop);
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
