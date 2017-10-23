package jp.co.conol.favor_android.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.UserFavoriteRecyclerAdapter;

public class UserFavoriteFragment extends Fragment {

    private Context mContext;
    private UserFavoriteRecyclerAdapter mUserFavoriteRecyclerAdapter;
    private RecyclerView mUserFavoriteRecyclerView;

    public UserFavoriteFragment() {
    }

    public static UserFavoriteFragment newInstance() {
        UserFavoriteFragment fragment = new UserFavoriteFragment();
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
        final View view = inflater.inflate(R.layout.fragment_user_favorite, container, false);
        mUserFavoriteRecyclerView = (RecyclerView) view.findViewById(R.id.userFavoriteRecyclerView);

        // レイアウトマネージャーのセット
        mUserFavoriteRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        // アダプターのセット
        mUserFavoriteRecyclerAdapter = new UserFavoriteRecyclerAdapter(mContext);
        mUserFavoriteRecyclerView.setAdapter(mUserFavoriteRecyclerAdapter);

        return view;
    }
}
