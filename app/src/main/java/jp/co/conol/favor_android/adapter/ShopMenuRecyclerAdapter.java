package jp.co.conol.favor_android.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favorlib.cuona.favor_model.Menu;

/**
 * Created by Masafumi_Ito on 2017/10/25.
 */

public class ShopMenuRecyclerAdapter extends RecyclerView.Adapter<ShopMenuRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<Menu> mMenuList = new ArrayList<>();
    private List<Integer> mOrderNumList = new ArrayList<>();
    private boolean isEntering = false;
    protected void showOrderDialog(int position, int orderNum) {}
    private final int HEADER = 0;
    private final int MENU = 1;

    // ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mMenuCategoryTextView; // ヘッダーのメニューカテゴリ名
        private ImageView mMenuImageView;       // メニューの画像
        private TextView mMenuNameTextView;     // メニュー名
        private TextView mMenuPriceTextView;    // メニューの値段
        private ConstraintLayout mSelectedOrderNumConstraintLayout; // 注文数を表示するエリア
        private TextView mOrderNumTextView; // 注文数

        // ViewHolderのコンストラクタ
        private ViewHolder(View v, int viewType) {
            super(v);
            switch (viewType) {

                // 要素がヘッダーの場合
                case HEADER:
                    mMenuCategoryTextView = (TextView) v.findViewById(R.id.menuCategoryTextView);
                    break;

                // 要素がメニューの場合
                case MENU:
                    mMenuNameTextView = (TextView) v.findViewById(R.id.menuNameTextView);
                    mMenuImageView = (ImageView) v.findViewById(R.id.menuImageView);
                    mMenuPriceTextView = (TextView) v.findViewById(R.id.menuPriceTextView);
                    mSelectedOrderNumConstraintLayout = (ConstraintLayout) v.findViewById(R.id.selectedOrderNumConstraintLayout);
                    mOrderNumTextView = (TextView) v.findViewById(R.id.orderNumTextView);
                    break;

                default:
                    break;
            }

            ButterKnife.bind(this, v);
        }
    }

    // コンストラクタ
    public ShopMenuRecyclerAdapter(Context context, List<Menu> menuList, List<Integer> orderNumList) {
        mContext = context;
        mMenuList = menuList;
        mOrderNumList = orderNumList;

        // 入店か履歴から表示か
        isEntering = MyUtil.SharedPref.getBoolean(context, "isEntering", false);
    }

    // ViewHolder作成
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view  = null;
        ViewHolder holder = null;
        switch (viewType) {

            // 要素がヘッダーの場合
            case HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_menu_header, parent, false);
                holder = new ViewHolder(view, viewType);
                break;

            // 要素がメニューの場合
            case MENU:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_menu, parent, false);
                holder = new ViewHolder(view, viewType);

                // クリック時の処理（入店時のみ）
                if(isEntering) {
                    final ViewHolder finalHolder = holder;
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // positionを取得
                            final int position = finalHolder.getAdapterPosition();

                            // 注文ダイアログを表示
                            int orderNum;
                            if (mOrderNumList.get(position) != null && mOrderNumList.get(position) != 0) {
                                orderNum = Integer.parseInt(mOrderNumList.get(position).toString());
                            } else {
                                orderNum = 1; // 注文数の初期値
                            }
                            showOrderDialog(position, orderNum);
                        }
                    });
                }

                break;

            default:
                break;
        }

        return holder;
    }

    // 画面に表示する内容をセット
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // 要素がメニューの時
        if (holder.mMenuNameTextView != null) {
            Menu menu = mMenuList.get(position);
            if(menu.getImageUrls() != null && menu.getImageUrls().length != 0) {
                holder.mMenuImageView.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(menu.getImageUrls()[0]).into(holder.mMenuImageView);
            } else {
                holder.mMenuImageView.setVisibility(View.GONE);
            }
            holder.mMenuNameTextView.setText(menu.getName());    // メニュー名の設定
            holder.mMenuPriceTextView.setText(menu.getPriceFormat());    // メニュー値段の設定

            // 注文数が指定された場合、注文数を表示
            Integer orderNum = mOrderNumList.get(position);
            if (orderNum != null && orderNum != 0) {
                holder.mSelectedOrderNumConstraintLayout.setVisibility(View.VISIBLE);
                holder.mOrderNumTextView.setText(String.valueOf(orderNum));
            } else {
                holder.mSelectedOrderNumConstraintLayout.setVisibility(View.GONE);
            }
        }
        // 要素がヘッダーの時
        else if(1 < mMenuList.size()) {
            String categoryName = mMenuList.get(position + 1).getCategoryName();
            if(MyUtil.Str.isBlank(categoryName)) categoryName = "未分類";
            holder.mMenuCategoryTextView.setText(categoryName);
        }
    }

    @Override
    public int getItemCount() {
        return mMenuList.size();
    }

    @Override
    public int getItemViewType(int position) {

        Menu menu = mMenuList.get(position);

        int viewType = HEADER;
        if(menu != null) {
            viewType = MENU;
        }

        // ヘッダーの場合は0、それ以外の場合は1を返す
        return viewType;
    }
}