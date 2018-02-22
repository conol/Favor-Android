package jp.co.conol.favor_android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import jp.co.conol.favor_android.R;
import jp.co.conol.favorlib.cuona.favor_model.Order;

/**
 * Created by Masafumi_Ito on 2017/10/24.
 */

public class UserOrderHistoryRecyclerAdapter extends RecyclerView.Adapter<UserOrderHistoryRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<Order> mUsersAllOrderList;
    private final int HEADER = 0;
    private final int MENU = 1;

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mUserOrderMenuTextView;
        private TextView mUserOrderMenuPriceTextView;
        private TextView mUserOrderDateTextView;
        private TextView mUserOrderShopNameTextView;
        private TextView mUserOrderMenuQuantityTextView;
        private ImageView mOrderMenuImageView;

        // ViewHolderのコンストラクタ
        private ViewHolder(View v, int viewType) {
            super(v);
            switch (viewType) {

                // 要素がヘッダーの場合
                case HEADER:
                    mUserOrderDateTextView = (TextView) v.findViewById(R.id.userOrderDateTextView);
                    mUserOrderShopNameTextView = (TextView) v.findViewById(R.id.userOrderShopNameTextView);
                    break;

                // 要素が注文した商品の場合
                case MENU:
                    mUserOrderMenuTextView = (TextView) v.findViewById(R.id.userOrderMenuNameTextView);
                    mUserOrderMenuPriceTextView = (TextView) v.findViewById(R.id.userOrderMenuPriceTextView);
                    mUserOrderMenuQuantityTextView = (TextView) v.findViewById(R.id.userOrderMenuQuantityTextView);
                    mOrderMenuImageView = (ImageView) v.findViewById(R.id.orderMenuImageView);
                    break;

                default:
                    break;
            }
        }
    }

    // コンストラクタ
    public UserOrderHistoryRecyclerAdapter(Context context, List<Order> usersAllOrderList) {
        mContext = context;
        mUsersAllOrderList = usersAllOrderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view  = null;
        switch (viewType) {

            // 要素がヘッダーの場合
            case HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_order_history_header, parent, false);
                break;

            // 要素が注文した商品の場合
            case MENU:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_order_history, parent, false);
                break;

            default:
                break;
        }

        // ViewHolder作成
        final ViewHolder holder = new ViewHolder(view, viewType);

        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // 注文内容のオブジェクトを取得
        Order usersAllOrder = mUsersAllOrderList.get(position);

        // 内容を反映
        if(holder.mUserOrderMenuTextView != null && holder.mUserOrderMenuPriceTextView != null) {
            if(usersAllOrder.getOrderedItemImages() != null && usersAllOrder.getOrderedItemImages().length != 0) {
                holder.mOrderMenuImageView.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(usersAllOrder.getOrderedItemImages()[0]).into(holder.mOrderMenuImageView);
            } else {
                holder.mOrderMenuImageView.setVisibility(View.GONE);
            }
            holder.mUserOrderMenuTextView.setText(usersAllOrder.getOrderedItemName());
            holder.mUserOrderMenuPriceTextView.setText(String.valueOf(usersAllOrder.getOrderedItemQuantity() * usersAllOrder.getOrderedItemPriceCents()) + usersAllOrder.getOrderedItemPriceUnit());
            if(1 < usersAllOrder.getOrderedItemQuantity()) {
                holder.mUserOrderMenuQuantityTextView.setText("(x" + String.valueOf(usersAllOrder.getOrderedItemQuantity()) + ")");
            }
        } else if(holder.mUserOrderDateTextView != null && holder.mUserOrderShopNameTextView != null && 1 < mUsersAllOrderList.size()) {
            DateTimeFormatter DEF_FMT = DateTimeFormat.forPattern("yyyy/MM/dd (E) HH:mm~"); // 入店時間
            holder.mUserOrderDateTextView.setText(DEF_FMT.print(DateTime.parse(mUsersAllOrderList.get(position + 1).getEnterAt())));
            holder.mUserOrderShopNameTextView.setText(mUsersAllOrderList.get(position + 1).getShopName());
        }
    }

    @Override
    public int getItemCount() {
        return mUsersAllOrderList.size();
    }

    @Override
    public int getItemViewType(int position) {

        Order usersAllOrder = mUsersAllOrderList.get(position);

        int viewType = HEADER;
        if(usersAllOrder != null) {
            viewType = MENU;
        }

        // ヘッダーの場合は0、それ以外の場合は1を返す
        return viewType;
    }
}
