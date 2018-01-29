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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.activity.ShopHistoryActivity;
import jp.co.conol.favor_android.adapter.UserOrderHistoryRecyclerAdapter;
import jp.co.conol.favor_android.custom.SimpleAlertDialog;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.favor_model.User;
import jp.co.conol.favorlib.cuona.favor_model.UsersAllOrder;

public class UserOrderHistoryFragment extends Fragment {

    private Context mContext;
    private UserOrderHistoryRecyclerAdapter mUserOrderHistoryRecyclerAdapter;
    @BindView(R.id.userOrderHistoryRecyclerView) RecyclerView mUserOrderHistoryRecyclerView;

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
        ButterKnife.bind(this, view);

        if(MyUtil.Network.isEnable(mContext)) {

            // ユーザーのAppTokenを取得
            String appToken = MyUtil.SharedPref.getString(mContext, "appToken");

            new Favor(new Favor.AsyncCallback() {
                @Override
                public void onSuccess(Object object) {
                    List<UsersAllOrder> usersAllOrderList = (List<UsersAllOrder>) object;
                    Collections.reverse(usersAllOrderList);

                    // レイアウトマネージャーのセット
                    mUserOrderHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

                    // アダプターのセット
                    mUserOrderHistoryRecyclerAdapter
                            = new UserOrderHistoryRecyclerAdapter(mContext, MyUtil.Transform.addHeader(usersAllOrderList, "getEnterAt"));
                    mUserOrderHistoryRecyclerView.setAdapter(mUserOrderHistoryRecyclerAdapter);

                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("onFailure", e.toString());
                }
            }).setAppToken(appToken).execute(Favor.Task.GetUsersAllOrder);
        }

        return view;
    }
}
