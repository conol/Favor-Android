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

public class UserOrderHistoryRecyclerAdapter extends RecyclerView.Adapter<UserOrderHistoryRecyclerAdapter.ViewHolder> {

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
    public UserOrderHistoryRecyclerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_order_history, parent, false);

        // ViewHolder作成
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }
}
