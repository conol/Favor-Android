package jp.co.conol.favor_android.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.fragment.UserFavoriteFragment;
import jp.co.conol.favor_android.fragment.UserOrderHistoryFragment;

/**
 * Created by Masafumi_Ito on 2017/10/23.
 */

public class UserFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

    public UserFragmentStatePagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new UserFavoriteFragment();
            default:
                return new UserOrderHistoryFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Resources res = mContext.getResources();
        switch (position) {
            case 0:
                return res.getString(R.string.user_favorite);
            default:
                return res.getString(R.string.user_order_history);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
