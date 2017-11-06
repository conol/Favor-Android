package jp.co.conol.favor_android.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.fragment.ShopDetailInfoFragment;
import jp.co.conol.favor_android.fragment.ShopOrderHistoryFragment;
import jp.co.conol.favor_android.fragment.UserFavoriteFragment;
import jp.co.conol.favor_android.fragment.UserOrderHistoryFragment;

/**
 * Created by Masafumi_Ito on 2017/10/24.
 */

public class ShopDetailFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private int mVisitGroupId;

    public ShopDetailFragmentStatePagerAdapter(Context context, FragmentManager fm, int visitGroupId) {
        super(fm);
        mContext = context;
        mVisitGroupId = visitGroupId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // 店舗情報詳細のフラグメントを返す
                return new ShopDetailInfoFragment();
            default:
                // 注文履歴のフラグメントを返す
                return ShopOrderHistoryFragment.newInstance(mVisitGroupId);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Resources res = mContext.getResources();
        switch (position) {
            case 0:
                return res.getString(R.string.shop_detail_info);
            default:
                return res.getString(R.string.shop_detail_order_menu);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
