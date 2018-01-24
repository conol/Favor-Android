package jp.co.conol.favor_android.activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    private int mVisitHistoryId;
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
    @BindView(R.id.shopIntroductionTextView) TextView mShopIntroductionTextView;    // 店舗説明

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        ButterKnife.bind(this);

        // 遷移前の情報を取得
        mIntent = getIntent();
        String deviceId = mIntent.getStringExtra("deviceId");
        int shopId = mIntent.getIntExtra("shopId", 0);
        User user = mGson.fromJson(MyUtil.SharedPref.getString(this, "userSetting"), User.class);

        // デバイスIDがnullなら、履歴から表示されていると判断
        isEntering = deviceId != null;

        // 入店処理（入店した際に店舗情報を表示）
        if(MyUtil.Network.isEnable(this)) {

            // 読み込みダイアログを表示
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.main_progress_message));
            progressDialog.show();

            if(isEntering) {
                // 入店している場合は「お会計する」ボタンを表示
                mOrderStopButtonConstraintLayout.setVisibility(View.VISIBLE);

                // お会計するボタンが押された場合、お会計ページへ移動
                mOrderStopButtonConstraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ShopDetailActivity.this, OrderStopActivity.class);
                        intent.putExtra("visitHistoryId", mVisitHistoryId);
                        intent.putExtra("visitGroupId", mVisitGroupId);
                        startActivity(intent);
                    }
                });

                // 店舗情報を取得
                new Favor(new Favor.AsyncCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        Shop shop = (Shop) object;

                        // 読み込みダイアログを非表示
                        progressDialog.dismiss();

                        if (shop != null) {
                            setShopInfo(shop);
                        } else {
                            new SimpleAlertDialog(ShopDetailActivity.this, getString(R.string.error_touch_cuona)).show();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("onFailure", e.toString());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                // 読み込みダイアログを非表示
                                progressDialog.dismiss();

                                new SimpleAlertDialog(ShopDetailActivity.this, getString(R.string.error_touch_cuona)).show();
                            }
                        });
                    }
                }).setAppToken(user.getAppToken()).setDeviceId(deviceId).execute(Favor.Task.EnterShop);
            }
            // 店舗詳細表示（入店履歴から表示された場合）
            else {
                // 入店していない場合は「お会計する」ボタンを非表示
                mOrderStopButtonConstraintLayout.setVisibility(View.GONE);

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

                                new SimpleAlertDialog(ShopDetailActivity.this, getString(R.string.error_common)).show();
                            }
                        });
                    }
                }).setAppToken(user.getAppToken()).setShopId(shopId).execute(Favor.Task.GetShopDetail);
            }
        } else {
            new SimpleAlertDialog(this, getString(R.string.error_network_disable_reboot)).show();
        }
    }

    // 店舗情報をセット（入店・履歴からの表示共通部分）
    private void setShopInfo(final Shop shop) {

        // visitGroupIdを取得（入店時はAPIの店舗情報から、履歴から表示時はintentから）
        if(isEntering) {
            mVisitGroupId = shop.getVisitGroupId();
            mShopNameConstraintLayout.setBackground(getDrawable(R.drawable.style_gradation_red));   // 店舗名背景
        } else {
            mVisitGroupId = mIntent.getIntExtra("visitGroupId", 0);
        }

        // visitHistoryIdを取得
        mVisitHistoryId = shop.getVisitHistoryId();

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
                intent.putExtra("shopId", shop.getShopId());
                intent.putExtra("shopName", shop.getShopName());
                intent.putExtra("visitHistoryId", shop.getVisitHistoryId());
                startActivity(intent);
            }
        });
    }
}
