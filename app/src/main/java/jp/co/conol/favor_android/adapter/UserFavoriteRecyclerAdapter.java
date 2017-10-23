package jp.co.conol.favor_android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.co.conol.favor_android.R;

/**
 * Created by Masafumi_Ito on 2017/10/24.
 */

public class UserFavoriteRecyclerAdapter extends RecyclerView.Adapter<UserFavoriteRecyclerAdapter.ViewHolder> {

    private Context mContext;

    class ViewHolder extends RecyclerView.ViewHolder {

        // ViewHolderのコンストラクタ
        private ViewHolder(View v) {
            super(v);

            // ButterKnifeのバインド
//            ButterKnife.bind(this, v);
        }
    }

    // コンストラクタ
    public UserFavoriteRecyclerAdapter(Context context) {
        mContext = context;
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

    }

    @Override
    public int getItemCount() {
        return 20;
    }
}
