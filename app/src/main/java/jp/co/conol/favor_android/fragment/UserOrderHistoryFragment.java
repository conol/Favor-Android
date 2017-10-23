package jp.co.conol.favor_android.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.UserFavoriteRecyclerAdapter;
import jp.co.conol.favor_android.adapter.UserOrderHistoryRecyclerAdapter;

public class UserOrderHistoryFragment extends Fragment {

    private Context mContext;
    private UserOrderHistoryRecyclerAdapter mUserOrderHistoryRecyclerAdapter;
    private RecyclerView mUserOrderHistoryRecyclerView;

    public UserOrderHistoryFragment() {
    }

    public static UserOrderHistoryFragment newInstance() {
        UserOrderHistoryFragment fragment = new UserOrderHistoryFragment();
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
        mUserOrderHistoryRecyclerView = (RecyclerView) view.findViewById(R.id.userOrderHistoryRecyclerView);

        // レイアウトマネージャーのセット
        mUserOrderHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        // アダプターのセット
        mUserOrderHistoryRecyclerAdapter = new UserOrderHistoryRecyclerAdapter(mContext);
        mUserOrderHistoryRecyclerView.setAdapter(mUserOrderHistoryRecyclerAdapter);

        return view;
    }
}
