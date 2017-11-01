package jp.co.conol.favor_android.activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopDetailFragmentStatePagerAdapter;
import jp.co.conol.favorlib.favor.model.Shop;

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
        Shop shop = mGson.fromJson(intent.getStringExtra("shop"), Shop.class);

        // 店舗情報をViewに反映
        if(shop != null) {
            mShopNameTextView.setText(shop.getShopName());
            mShopIntroductionTextView.setText(shop.getShopIntroduction());
        }

        // ViewPagerにアダプターをセット
        ShopDetailFragmentStatePagerAdapter shopDetailFragmentStatePagerAdapter
                = new ShopDetailFragmentStatePagerAdapter(this, getSupportFragmentManager());
        mShopDetailViewPager.setAdapter(shopDetailFragmentStatePagerAdapter);
        mShopDetailTabLayout.setupWithViewPager(mShopDetailViewPager);

        // 「注文メニューを表示する」をタップした場合
        mShowShopMenuConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(ShopDetailActivity.this, ShopMenuActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }
}
