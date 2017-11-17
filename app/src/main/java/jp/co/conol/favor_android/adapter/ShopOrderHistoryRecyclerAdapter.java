package jp.co.conol.favor_android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.R;
import jp.co.conol.favorlib.favor.model.Menu;
import jp.co.conol.favorlib.favor.model.Order;

/**
 * Created by Masafumi_Ito on 2017/10/24.
 */

public class ShopOrderHistoryRecyclerAdapter extends RecyclerView.Adapter<ShopOrderHistoryRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<Order> mOrderList = new ArrayList<>();

    class ViewHolder extends RecyclerView.ViewHolder {

//        @BindView(R.id.shopOrderUserNameTextView) TextView mShopOrderUserNameTextView;
        @BindView(R.id.shopOrderMenuNameTextView) TextView mShopOrderMenuNameTextView;
        @BindView(R.id.shopOrderMenuPriceTextView) TextView mShopOrderMenuPriceTextView;

        // ViewHolderのコンストラクタ
        private ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    // コンストラクタ
    public ShopOrderHistoryRecyclerAdapter(Context context, List<Order> orderList) {
        mContext = context;
        mOrderList = orderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_order_history, parent, false);

        // ViewHolder作成
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // 注文内容のオブジェクトを取得
        Order order = mOrderList.get(position);

        // 内容を反映
//        holder.mShopOrderUserNameTextView.setText(order.getOrderedUserNickname());
        holder.mShopOrderMenuNameTextView.setText(order.getOrderedItemName());
        holder.mShopOrderMenuPriceTextView.setText(order.getOrderedItemPriceFormat());
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }
}
