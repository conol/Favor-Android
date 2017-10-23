package jp.co.conol.favor_android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.co.conol.favor_android.R;

/**
 * Created by Masafumi_Ito on 2017/09/04.
 */

public class ShopHistoryRecyclerAdapter extends RecyclerView.Adapter<ShopHistoryRecyclerAdapter.ViewHolder> {

    private Context mContext;

    // ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder {

        // ViewHolderのコンストラクタ
        private ViewHolder(View v) {
            super(v);

            // ButterKnifeのバインド
//            ButterKnife.bind(this, v);
        }
    }

    // コンストラクタ
    public ShopHistoryRecyclerAdapter(Context context) {
        mContext = context;
    }

    // ViewHolder作成
    @Override
    public ShopHistoryRecyclerAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_history, parent, false);

        // ViewHolder作成
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    // 画面に表示する内容をセット
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 20;
    }
}


