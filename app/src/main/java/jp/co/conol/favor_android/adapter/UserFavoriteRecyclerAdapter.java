package jp.co.conol.favor_android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.R;
import jp.co.conol.favorlib.favor.model.Favorite;
import jp.co.conol.favorlib.favor.model.UsersAllOrder;

/**
 * Created by Masafumi_Ito on 2017/10/24.
 */

public class UserFavoriteRecyclerAdapter extends RecyclerView.Adapter<UserFavoriteRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<Favorite> mFavoriteList;

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.userFavoriteLevelTextView) TextView mUserFavoriteLevelTextView;
        @BindView(R.id.userFavoriteMenuTextView) TextView mUserFavoriteMenuTextView;

        // ViewHolderのコンストラクタ
        private ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    // コンストラクタ
    public UserFavoriteRecyclerAdapter(Context context, List<Favorite> favoriteList) {
        mContext = context;
        mFavoriteList = favoriteList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_favorite, parent, false);

        // ViewHolder作成
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(UserFavoriteRecyclerAdapter.ViewHolder holder, int position) {

        // お気に入りのオブジェクトを取得
        Favorite favorite = mFavoriteList.get(position);

        holder.mUserFavoriteLevelTextView.setText(String.valueOf(favorite.getLevel()) + ".0");
        holder.mUserFavoriteMenuTextView.setText(favorite.getName());
    }

    @Override
    public int getItemCount() {
        return mFavoriteList.size();
    }

    // 要素を削除
    public void remove(int position) {
        mFavoriteList.remove(position);
        notifyItemRemoved(position);
    }
}
