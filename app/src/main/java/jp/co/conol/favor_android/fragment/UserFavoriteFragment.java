package jp.co.conol.favor_android.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.UserFavoriteRecyclerAdapter;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.favor_model.Favorite;
import jp.co.conol.favorlib.cuona.favor_model.User;

public class UserFavoriteFragment extends Fragment {

    private Context mContext;
    private final Gson mGson = new Gson();
    List<Favorite> mFavoriteList;
    private UserFavoriteRecyclerAdapter mUserFavoriteRecyclerAdapter;
    @BindView(R.id.userFavoriteRecyclerView) RecyclerView mUserFavoriteRecyclerView;

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
        ButterKnife.bind(this, view);

        // ユーザー情報の取得
        final User user = mGson.fromJson(MyUtil.SharedPref.getString(mContext, "userSetting"), User.class);

        new Favor(new Favor.AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                mFavoriteList = (List<Favorite>) object;

                if(mFavoriteList != null) {

                    // レイアウトマネージャーのセット
                    mUserFavoriteRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

                    // アダプターのセット
                    mUserFavoriteRecyclerAdapter = new UserFavoriteRecyclerAdapter(mContext, mFavoriteList);
                    mUserFavoriteRecyclerView.setAdapter(mUserFavoriteRecyclerAdapter);

                    // リストをスワイプした時の処理を設定
                    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                            return false;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                            // 横にスワイプされたら要素を消す
                            final int swipedPosition = viewHolder.getAdapterPosition();

                            new Favor(new Favor.AsyncCallback() {
                                @Override
                                public void onSuccess(Object object) {
                                    mUserFavoriteRecyclerAdapter.remove(swipedPosition);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("onFailure", e.toString());
                                }
                            }).setAppToken(user.getAppToken()).setFavoriteId(mFavoriteList.get(swipedPosition).getId()).execute(Favor.Task.DeleteFavorite);
                        }
                    };
                    (new ItemTouchHelper(callback)).attachToRecyclerView(mUserFavoriteRecyclerView);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("onFailure", e.toString());
            }
        }).setAppToken(user.getAppToken()).execute(Favor.Task.GetFavorites);

        return view;
    }

    // お気に入り追加をadapterに伝える（UserActivityから呼び出して使用）
    public void notifyDataChanged(Favorite favorite) {
        if(mFavoriteList != null) {
            mFavoriteList.add(favorite);
            mUserFavoriteRecyclerAdapter.notifyDataSetChanged();
        }
    }
}
