package jp.co.conol.favor_android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopDetailFragmentStatePagerAdapter;
import jp.co.conol.favor_android.adapter.ShopHistoryRecyclerAdapter;
import jp.co.conol.favor_android.custom.ProgressDialog;
import jp.co.conol.favor_android.custom.SimpleAlertDialog;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.FavorException;
import jp.co.conol.favorlib.cuona.favor_model.Shop;
import jp.co.conol.favorlib.cuona.favor_model.User;

public class ShopDetailActivity extends AppCompatActivity {

    private final Gson mGson = new Gson();
    private boolean isEntering;  // 入店中か否か
    @BindView(R.id.shopImageView) ImageView mShopImageView; // 店舗画像
    @BindView(R.id.shopGenreTextView) TextView mShopGenreTextView; // 店舗のジャンル
    @BindView(R.id.shopNumVisitTextView) TextView mShopNumVisitTextView; // 来店回数表示部分
    @BindView(R.id.shopVisitTextView) TextView mShopVisitTextView; // 「来店」
    @BindView(R.id.shopCountTextView) TextView mShopCountTextView; // 「回目」
    @BindView(R.id.shopNumVIsitConstraintLayout) ConstraintLayout mShopNumVisitConstraintLayout; // 来店回数表示部分の囲み部分
    @BindView(R.id.orderStopButtonConstraintLayout) ConstraintLayout mOrderStopButtonConstraintLayout; // 「お会計する」ボタン
    @BindView(R.id.shopDetailTabLayout) TabLayout mShopDetailTabLayout;
    @BindView(R.id.shopDetailViewPager) ViewPager mShopDetailViewPager;
    @BindView(R.id.showShopMenuConstraintLayout) ConstraintLayout mShowShopMenuConstraintLayout;
    @BindView(R.id.shopNameTextView) TextView mShopNameTextView;    // 店舗名
    @BindView(R.id.shopNameConstraintLayout) ConstraintLayout mShopNameConstraintLayout;    // 店舗の背景
    @BindView(R.id.shopEnterAtTextView) TextView mShopEnterAtTextView;    // 前回来店日時表示箇所
    @BindView(R.id.shopIntroductionTextView) TextView mShopIntroductionTextView;    // 店舗説明

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        ButterKnife.bind(this);

        // 入店か履歴から表示か
        isEntering = MyUtil.SharedPref.getBoolean(this, "isEntering", false);

        // 入店中した場合は店舗情報を再取得
        if(isEntering) {

            // 入店履歴を取得
            if(MyUtil.Network.isEnable(this)) {

                // ユーザーのAppTokenを取得
                String appToken = MyUtil.SharedPref.getString(this, "appToken");

                // ショップIDを取得
                int ShopId = MyUtil.SharedPref.getInt(this, "shopId", 0);

                // 読み込みダイアログを表示
                final ProgressDialog progressDialog = new ProgressDialog(ShopDetailActivity.this);
                progressDialog.setMessage(getString(R.string.main_progress_message));
                progressDialog.show();

                new Favor(new Favor.AsyncCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        Shop shop = (Shop) object;

                        setShopInfo(shop);

                        // 読み込みダイアログを非表示
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(FavorException e) {
                        Log.e("onFailure", e.toString());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                // 読み込みダイアログを非表示
                                progressDialog.dismiss();

                                new SimpleAlertDialog(ShopDetailActivity.this, getString(R.string.error_common)).show();
                            }
                        });
                    }
                }).setAppToken(appToken).setShopId(ShopId).execute(Favor.Task.GetShopDetail);
            } else {
                new SimpleAlertDialog(ShopDetailActivity.this, getString(R.string.error_network_disable)).show();
            }
        } else {
            // 遷移前の情報を取得
            Shop shop = new Gson().fromJson(getIntent().getStringExtra("shop"), Shop.class);    // 店舗情報を取得

            setShopInfo(shop);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setShopInfo(final Shop shop) {
        // 来店中
        if(isEntering) {
            mShopNameConstraintLayout.setBackground(getDrawable(R.drawable.style_gradation_red));   // 店舗名背景
            mShopEnterAtTextView.setText(getString(R.string.shop_entering_text));   // 入店中メッセージ
            mShopEnterAtTextView.setTextColor(Color.RED);

            // 入店している場合は「お会計する」ボタンを表示
            mOrderStopButtonConstraintLayout.setVisibility(View.VISIBLE);

            // お会計するボタンが押された場合、お会計ページへ移動
            mOrderStopButtonConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ShopDetailActivity.this, OrderStopActivity.class);
                    intent.putExtra("shop", mGson.toJson(shop));
                    startActivity(intent);
                }
            });
        }
        // 履歴から表示
        else {

            // 入店時間
            DateTimeFormatter DEF_FMT = DateTimeFormat.forPattern("yyyy/MM/dd (E) HH:mm");
            if(shop.getEnterShopAt() != null) {
                mShopEnterAtTextView.setText(DEF_FMT.print(DateTime.parse(shop.getEnterShopAt())) + " に来店しました");
            }

            // 入店していない場合は「お会計する」ボタンを非表示
            mOrderStopButtonConstraintLayout.setVisibility(View.GONE);

            // 来店回数の背景・文字色を変更
            mShopNumVisitConstraintLayout.setBackground(getDrawable(R.drawable.style_round_box_darkgray_back));
            mShopNumVisitTextView.setTextColor(Color.WHITE);
            mShopVisitTextView.setTextColor(Color.WHITE);
            mShopCountTextView.setTextColor(Color.WHITE);
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
                shop.getVisitGroupId(),
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
            if(isEntering) {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.shop_exit))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyUtil.SharedPref.saveBoolean(ShopDetailActivity.this, "isBack", true);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel_kana), null)
                        .show();
            } else {
                finish();
            }
        }
        return false;
    }
}
