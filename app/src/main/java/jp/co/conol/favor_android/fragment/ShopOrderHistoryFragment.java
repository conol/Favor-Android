package jp.co.conol.favor_android.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopOrderHistoryRecyclerAdapter;
import jp.co.conol.favor_android.adapter.UserOrderHistoryRecyclerAdapter;

public class ShopOrderHistoryFragment extends Fragment {

    private Context mContext;
    private ShopOrderHistoryRecyclerAdapter mShopOrderHistoryRecyclerAdapter;
    private RecyclerView mShopOrderHistoryRecyclerView;

    public ShopOrderHistoryFragment() {
    }

    public static ShopOrderHistoryFragment newInstance() {
        ShopOrderHistoryFragment fragment = new ShopOrderHistoryFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    // Android6.0以下ではonAttachは次の仕様で呼ばれる
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        mContext = (Context) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_user_order_history, container, false);
        mShopOrderHistoryRecyclerView = (RecyclerView) view.findViewById(R.id.shopOrderHistoryRecyclerView);

        // レイアウトマネージャーのセット
        mShopOrderHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        // アダプターのセット
        mShopOrderHistoryRecyclerAdapter = new ShopOrderHistoryRecyclerAdapter(mContext);
        mShopOrderHistoryRecyclerView.setAdapter(mShopOrderHistoryRecyclerAdapter);

        return view;
    }
}
