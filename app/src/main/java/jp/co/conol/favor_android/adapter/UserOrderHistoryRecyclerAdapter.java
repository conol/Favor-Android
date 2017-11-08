package jp.co.conol.favor_android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

        private TextView mUserOrderMenuTextView;
        private TextView mUserOrderMenuPriceTextView;
        private TextView mUserOrderDateTextView;
        private TextView mUserOrderShopNameTextView;

        // ViewHolderのコンストラクタ
        private ViewHolder(View v, int viewType) {
            super(v);
            switch (viewType) {

                // 要素がヘッダーの場合
                case 0:
                    mUserOrderDateTextView = (TextView) v.findViewById(R.id.userOrderDateTextView);
                    mUserOrderShopNameTextView = (TextView) v.findViewById(R.id.userOrderShopNameTextView);
                    break;

                // 要素が注文した商品の場合
                case 1:
                    mUserOrderMenuTextView = (TextView) v.findViewById(R.id.userOrderMenuTextView);
                    mUserOrderMenuPriceTextView = (TextView) v.findViewById(R.id.userOrderMenuPriceTextView);
                    break;

                default:
                    break;
            }
        }
    }

    // コンストラクタ
    public UserOrderHistoryRecyclerAdapter(Context context, List<UsersAllOrder> usersAllOrderList) {
        mContext = context;
        mUsersAllOrderList = usersAllOrderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view  = null;
        switch (viewType) {

            // 要素がヘッダーの場合
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_order_history_header, parent, false);
                break;

            // 要素が注文した商品の場合
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_order_history, parent, false);
                break;

            default:
                break;
        }

        // ViewHolder作成
        final ViewHolder holder = new ViewHolder(view, viewType);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // 注文内容のオブジェクトを取得
        UsersAllOrder usersAllOrder = mUsersAllOrderList.get(position);

        // 内容を反映
        if(holder.mUserOrderMenuTextView != null && holder.mUserOrderMenuPriceTextView != null) {
            holder.mUserOrderMenuTextView.setText(usersAllOrder.getOrderedItemName());
            holder.mUserOrderMenuPriceTextView.setText(String.valueOf(usersAllOrder.getOrderedItemPrice()) + "円");
        } else if(holder.mUserOrderDateTextView != null && holder.mUserOrderShopNameTextView != null && 1 < mUsersAllOrderList.size()) {
            String dateString = mUsersAllOrderList.get(position + 1).getEnterAt();
            String[] dateStringTmp = dateString.split("T", 0);
            holder.mUserOrderDateTextView.setText(dateStringTmp[0].replace("-", "/"));
            holder.mUserOrderShopNameTextView.setText(mUsersAllOrderList.get(position + 1).getShopName());
        }
    }

    @Override
    public int getItemCount() {
        return mUsersAllOrderList.size();
    }

    // ViewTypeでリストのポジションを返す
    @Override
    public int getItemViewType(int position) {

        UsersAllOrder usersAllOrder = mUsersAllOrderList.get(position);

        int viewType = 0;
        if(usersAllOrder.getEnterAt() != null) {
            viewType = 1;
        }

        // ヘッダーの場合は0、それ以外の場合は1を返す
        return viewType;
    }
}
