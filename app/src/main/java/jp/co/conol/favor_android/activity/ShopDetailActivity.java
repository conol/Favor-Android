package jp.co.conol.favor_android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopDetailFragmentStatePagerAdapter;
import jp.co.conol.favorlib.favor.Favor;
import jp.co.conol.favorlib.favor.model.Shop;
import jp.co.conol.favorlib.favor.model.User;

public class ShopDetailActivity extends AppCompatActivity {

    private final Gson mGson = new Gson();
    @BindView(R.id.shopDetailTabLayout) TabLayout mShopDetailTabLayout;
    @BindView(R.id.shopDetailViewPager) ViewPager mShopDetailViewPager;
    @BindView(R.id.showShopMenuConstraintLayout) ConstraintLayout mShowShopMenuConstraintLayout;
    @BindView(R.id.shopNameTextView) TextView mShopNameTextView;
    @BindView(R.id.shopIntroductionTextView) TextView mShopIntroductionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        ButterKnife.bind(this);

        // 遷移前の情報を取得
        Intent intent = getIntent();
        String deviceId = intent.getStringExtra("deviceId");
        int shopId = intent.getIntExtra("shopId", 0);
        User user = mGson.fromJson(MyUtil.SharedPref.get(this, "userSetting"), User.class);

        // 入店処理（入店した際に店舗情報を表示）
        if(deviceId != null) {
            final Handler handler = new Handler();
            new Favor(new Favor.AsyncCallback() {
                @Override
                public void onSuccess(Object object) {
                    Shop shop = (Shop) object;

                    if (shop != null) {
                        setShopInfo(shop);
                    } else {
                        showAlertDialog();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d("onFailure", e.toString());
                    showAlertDialog();
                }

                private void showAlertDialog() {
                    handler.post(new Runnable() {
                        public void run() {
                            new AlertDialog.Builder(ShopDetailActivity.this)
                                    .setMessage(getString(R.string.error_touch_cuona))
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    });
                }
            }).setAppToken(user.getAppToken()).setDeviceId(deviceId).execute(Favor.Task.EnterShop);
        }

        // 店舗詳細表示（入店履歴から表示された場合）
        else {
            new Favor(new Favor.AsyncCallback() {
                @Override
                public void onSuccess(Object object) {
                    Shop shop = (Shop) object;

                    if (shop != null) {
                        setShopInfo(shop);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("onFailure", e.toString());
                }
            }).setAppToken(user.getAppToken()).setShopId(shopId).execute(Favor.Task.GetShopDetail);
        }
    }

    // 店舗情報をセット
    private void setShopInfo(final Shop shop) {

        // 店舗情報をViewに反映
        mShopNameTextView.setText(shop.getShopName());
        mShopIntroductionTextView.setText(shop.getShopIntroduction());

        // 基本情報と注文履歴のViewPagerにアダプターをセット
        ShopDetailFragmentStatePagerAdapter shopDetailFragmentStatePagerAdapter
                = new ShopDetailFragmentStatePagerAdapter(ShopDetailActivity.this, getSupportFragmentManager());
        mShopDetailViewPager.setAdapter(shopDetailFragmentStatePagerAdapter);
        mShopDetailTabLayout.setupWithViewPager(mShopDetailViewPager);

        // 「注文メニューを表示する」をタップした場合
        mShowShopMenuConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(ShopDetailActivity.this, ShopMenuActivity.class);
                    intent.putExtra("shopId", shop.getShopId());
                    intent.putExtra("visitHistoryId", shop.getVisitHistoryId());
                    startActivity(intent);
                }
                return false;
            }
        });
    }
}
