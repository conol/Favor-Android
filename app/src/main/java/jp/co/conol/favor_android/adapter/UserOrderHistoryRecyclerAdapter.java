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
import jp.co.conol.favorlib.favor.model.UsersAllOrder;

/**
 * Created by Masafumi_Ito on 2017/10/24.
 */

public class UserOrderHistoryRecyclerAdapter extends RecyclerView.Adapter<UserOrderHistoryRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<UsersAllOrder> mUsersAllOrderList;

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.userOrderMenuTextView) TextView mUserOrderMenuTextView;
        @BindView(R.id.userOrderMenuPriceTextView) TextView mUserOrderMenuPriceTextView;

        // ViewHolderのコンストラクタ
        private ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    // コンストラクタ
    public UserOrderHistoryRecyclerAdapter(Context context, List<UsersAllOrder> usersAllOrderList) {
        mContext = context;
        mUsersAllOrderList = usersAllOrderList;
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

        // 注文内容のオブジェクトを取得
        UsersAllOrder usersAllOrder = mUsersAllOrderList.get(position);

        // 内容を反映
        holder.mUserOrderMenuTextView.setText(usersAllOrder.getOrderedItemName());
        holder.mUserOrderMenuPriceTextView.setText(String.valueOf(usersAllOrder.getOrderedItemPrice()));
    }

    @Override
    public int getItemCount() {
        return mUsersAllOrderList.size();
    }
}
