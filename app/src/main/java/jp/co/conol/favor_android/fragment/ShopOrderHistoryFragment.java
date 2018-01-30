package jp.co.conol.favor_android.fragment;

import android.app.Activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopOrderHistoryRecyclerAdapter;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.FavorException;
import jp.co.conol.favorlib.cuona.favor_model.Order;
import jp.co.conol.favorlib.cuona.favor_model.User;

public class ShopOrderHistoryFragment extends Fragment {

    private Context mContext;
    private int mVisitGroupId;
    private ShopOrderHistoryRecyclerAdapter mShopOrderHistoryRecyclerAdapter;
    @BindView(R.id.shopOrderHistoryRecyclerView) RecyclerView mShopOrderHistoryRecyclerView;

    public ShopOrderHistoryFragment() {
    }

    public static ShopOrderHistoryFragment newInstance(int visitGroupId) {
        ShopOrderHistoryFragment fragment = new ShopOrderHistoryFragment();

        Bundle args = new Bundle();
        args.putInt("visitGroupId", visitGroupId);
        fragment.setArguments(args);

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

        // argsを取得
        if(getArguments() != null) {
            mVisitGroupId = getArguments().getInt("visitGroupId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shop_order_history, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(MyUtil.Network.isEnable(mContext)) {

            // ユーザーのAppTokenを取得
            String appToken = MyUtil.SharedPref.getString(mContext, "appToken");

            if (mVisitGroupId != 0) {
                new Favor(new Favor.AsyncCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        List<Order> orderList = (List<Order>) object;

                        if (orderList != null) {

                            // レイアウトマネージャーのセット
                            mShopOrderHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

                            // アダプターのセット
                            mShopOrderHistoryRecyclerAdapter = new ShopOrderHistoryRecyclerAdapter(mContext, orderList);
                            mShopOrderHistoryRecyclerView.setAdapter(mShopOrderHistoryRecyclerAdapter);
                        }
                    }

                    @Override
                    public void onFailure(FavorException e) {
                        Log.e("onFailure", e.toString());
                    }
                }).setAppToken(appToken).setVisitGroupId(mVisitGroupId).execute(Favor.Task.GetUserGroupsOrderInShop);
            }
        }
    }
}
