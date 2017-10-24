package jp.co.conol.favor_android.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopDetailFragmentStatePagerAdapter;

public class ShopDetailActivity extends AppCompatActivity {

    private TabLayout mShopDetailTabLayout;
    private ViewPager mShopDetailViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        mShopDetailTabLayout = (TabLayout) findViewById(R.id.shopDetailTabLayout);
        mShopDetailViewPager = (ViewPager) findViewById(R.id.shopDetailViewPager);

        // ViewPagerにアダプターをセット
        ShopDetailFragmentStatePagerAdapter shopDetailFragmentStatePagerAdapter
                = new ShopDetailFragmentStatePagerAdapter(this, getSupportFragmentManager());
        mShopDetailViewPager.setAdapter(shopDetailFragmentStatePagerAdapter);
        mShopDetailTabLayout.setupWithViewPager(mShopDetailViewPager);
    }
}
