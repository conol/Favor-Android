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

/**
 * Created by Masafumi_Ito on 2017/10/25.
 */

public class ShopMenuRecyclerAdapter extends RecyclerView.Adapter<ShopMenuRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<Menu> mMenuList = new ArrayList<>();
    protected void showOrderDialog() {}

    // ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.menuNameTextView) TextView mMenuNameTextView;

        // ViewHolderのコンストラクタ
        private ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    // コンストラクタ
    public ShopMenuRecyclerAdapter(Context context, List<Menu> menuList) {
        mContext = context;
        mMenuList = menuList;
    }

    // ViewHolder作成
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_menu, parent, false);

        // ViewHolder作成
        final ViewHolder holder = new ViewHolder(view);

        // クリック時の処理
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 注文ダイアログを表示
                showOrderDialog();

                // positionを取得
//                final int position = holder.getAdapterPosition();

                // 店舗詳細ページへ移動
//                Intent intent = new Intent(mContext, ShopDetailActivity.class);
//                mContext.startActivity(intent);
            }
        });

        return holder;
    }

    // 画面に表示する内容をセット
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mMenuNameTextView.setText(mMenuList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mMenuList.size();
    }
}