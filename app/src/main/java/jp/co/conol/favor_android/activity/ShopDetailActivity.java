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

import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopDetailFragmentStatePagerAdapter;

public class ShopDetailActivity extends AppCompatActivity {

    private TabLayout mShopDetailTabLayout;
    private ViewPager mShopDetailViewPager;
    private ConstraintLayout mShowShopMenuConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        mShopDetailTabLayout = (TabLayout) findViewById(R.id.shopDetailTabLayout);
        mShopDetailViewPager = (ViewPager) findViewById(R.id.shopDetailViewPager);
        mShowShopMenuConstraintLayout = (ConstraintLayout) findViewById(R.id.showShopMenuConstraintLayout);

        // ViewPagerにアダプターをセット
        ShopDetailFragmentStatePagerAdapter shopDetailFragmentStatePagerAdapter
                = new ShopDetailFragmentStatePagerAdapter(this, getSupportFragmentManager());
        mShopDetailViewPager.setAdapter(shopDetailFragmentStatePagerAdapter);
        mShopDetailTabLayout.setupWithViewPager(mShopDetailViewPager);

        // 注文メニューを表示する場合
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
