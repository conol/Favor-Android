package jp.co.conol.favor_android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopDetailFragmentStatePagerAdapter;
import jp.co.conol.favor_android.custom.ProgressDialog;
import jp.co.conol.favor_android.custom.SimpleAlertDialog;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.favor_model.Shop;
import jp.co.conol.favorlib.cuona.favor_model.User;

public class ShopDetailActivity extends AppCompatActivity {

    private final Gson mGson = new Gson();
    private Intent mIntent = null;
    private int mVisitGroupId;
    private Shop mShop;
    private boolean isEntering;  // 入店中か否か
    @BindView(R.id.shopImageView) ImageView mShopImageView; // 店舗画像
    @BindView(R.id.shopGenreTextView) TextView mShopGenreTextView; // 店舗のジャンル
    @BindView(R.id.shopNumVisitTextView) TextView mShopNumVisitTextView; // 来店回数表示部分
    @BindView(R.id.orderStopButtonConstraintLayout) ConstraintLayout mOrderStopButtonConstraintLayout; // 「お会計する」ボタン
    @BindView(R.id.shopDetailTabLayout) TabLayout mShopDetailTabLayout;
    @BindView(R.id.shopDetailViewPager) ViewPager mShopDetailViewPager;
    @BindView(R.id.showShopMenuConstraintLayout) ConstraintLayout mShowShopMenuConstraintLayout;
    @BindView(R.id.shopNameTextView) TextView mShopNameTextView;    // 店舗名
    @BindView(R.id.shopNameConstraintLayout) ConstraintLayout mShopNameConstraintLayout;    // 店舗の背景
    @BindView(R.id.shopLastVisitAtTextView) TextView mShopLastVisitAtTextView;    // 前回来店日時表示箇所
    @BindView(R.id.shopIntroductionTextView) TextView mShopIntroductionTextView;    // 店舗説明

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        ButterKnife.bind(this);

        // 遷移前の情報を取得
        mIntent = getIntent();
        String deviceId = mIntent.getStringExtra("deviceId");
        int shopId = mIntent.getIntExtra("shopId", 0);  // 履歴から表示時に取得可能
        User user = mGson.fromJson(MyUtil.SharedPref.getString(this, "userSetting"), User.class);

        // デバイスIDとshop情報がnullなら、履歴から表示されていると判断
        isEntering = MyUtil.SharedPref.getBoolean(this, "isEntering", false);

        // 入店処理（入店した際に店舗情報を表示）
        if(MyUtil.Network.isEnable(this)) {

            // 読み込みダイアログを表示
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.main_progress_message));
            progressDialog.show();

            // 入店時
            if(isEntering) {
                // 店舗情報を取得
                new Favor(new Favor.AsyncCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        mShop = (Shop) object;

                        // 読み込みダイアログを非表示
                        progressDialog.dismiss();

                        if (mShop != null) {
                            setShopInfo(mShop);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("onFailure", e.toString());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                // 読み込みダイアログを非表示
                                progressDialog.dismiss();

                                Toast.makeText(ShopDetailActivity.this, getString(R.string.error_touch_cuona), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ShopDetailActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }).setAppToken(user.getAppToken()).setDeviceId(deviceId).execute(Favor.Task.EnterShop);
            }
            // 入店履歴から表示
            else {

                // 店舗情報を取得
                new Favor(new Favor.AsyncCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        Shop shop = (Shop) object;

                        // 読み込みダイアログを非表示
                        progressDialog.dismiss();

                        if (shop != null) {
                            setShopInfo(shop);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("onFailure", e.toString());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                // 読み込みダイアログを非表示
                                progressDialog.dismiss();

                                Toast.makeText(ShopDetailActivity.this, getString(R.string.error_common), Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    }
                }).setAppToken(user.getAppToken()).setShopId(shopId).execute(Favor.Task.GetShopDetail);
            }
        } else {
            Toast.makeText(ShopDetailActivity.this, getString(R.string.error_network_disable), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ShopDetailActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // 店舗情報をセット（入店・履歴からの表示共通部分）
    private void setShopInfo(final Shop shop) {

        // visitGroupIdを取得（入店時はAPIの店舗情報から、履歴から表示時はintentから）
        // 来店中
        if(isEntering) {
            mVisitGroupId = shop.getVisitGroupId();
            mShopNameConstraintLayout.setBackground(getDrawable(R.drawable.style_gradation_red));   // 店舗名背景
            mShopLastVisitAtTextView.setText(getString(R.string.shop_entering_text));   // 入店中メッセージ
            mShopLastVisitAtTextView.setTextColor(Color.RED);

            // 入店している場合は「お会計する」ボタンを表示
            mOrderStopButtonConstraintLayout.setVisibility(View.VISIBLE);

            // お会計するボタンが押された場合、お会計ページへ移動
            mOrderStopButtonConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ShopDetailActivity.this, OrderStopActivity.class);
                    intent.putExtra("shop", mGson.toJson(mShop));
                    startActivity(intent);
                }
            });
        }
        // 履歴から表示
        else {
            mVisitGroupId = mIntent.getIntExtra("visitGroupId", 0);

            // 入店していない場合は「お会計する」ボタンを非表示
            mOrderStopButtonConstraintLayout.setVisibility(View.GONE);
        }

        // 店舗情報をViewに反映
        Picasso.with(this).load(shop.getShopImages()[0]).into(mShopImageView); // 画像
        mShopNameTextView.setText(shop.getShopName());  // 店舗名
        mShopGenreTextView.setText(shop.getShopGenre());  // ジャンル
        mShopNumVisitTextView.setText(String.valueOf(shop.getNumVisits()));  // 来店回数
        mShopIntroductionTextView.setText(shop.getShopIntroduction());  // 店舗情報

        // 基本情報と注文履歴のViewPagerにアダプターをセット
        ShopDetailFragmentStatePagerAdapter shopDetailFragmentStatePagerAdapter
                = new ShopDetailFragmentStatePagerAdapter(
                    ShopDetailActivity.this,
                    getSupportFragmentManager(),
                    mVisitGroupId,
                    mGson.toJson(shop)
        );
        mShopDetailViewPager.setAdapter(shopDetailFragmentStatePagerAdapter);
        mShopDetailTabLayout.setupWithViewPager(mShopDetailViewPager);

        // 「注文メニューを表示する」をタップした場合
        mShowShopMenuConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopDetailActivity.this, ShopMenuActivity.class);
                intent.putExtra("shop",mGson.toJson(shop));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK ) {
//            if(isEntering) {
//                new AlertDialog.Builder(this)
//                        .setMessage(getString(R.string.shop_exit))
//                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        })
//                        .setNegativeButton(getString(R.string.cancel_kana), null)
//                        .show();
//            } else {
//                finish();
//            }
        }
        return false;
    }
}
