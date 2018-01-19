package jp.co.conol.favor_android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.R;
import jp.co.conol.favorlib.cuona.favor_model.Shop;
import jp.co.conol.favorlib.cuona.favor_model.ShopsExtensionField;

/**
 * Created by m_ito on 2018/01/17.
 */

public class ShopDetailRecyclerAdapter extends RecyclerView.Adapter<ShopDetailRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private Shop mShop;
    private int mItemCount = 1;             // 住所は必須
    private boolean hasPhoneNumber = false; // 電話番号が設定されているか否か
    private boolean hasExtensionFields = false;  // ExtensionFieldsが設定されれいるか否か
    private ShopsExtensionField[] mShopsExtensionFields;  // ExtensionFieldの配列
    private int mShopsExtensionFieldsCount = 0;  // ExtensionFieldの配列のインデックス

    // ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemTitleTextView) TextView itemTitleTextView;
        @BindView(R.id.itemTextView) TextView itemTextView;

        // ViewHolderのコンストラクタ
        private ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    // コンストラクタ
    public ShopDetailRecyclerAdapter (Context context, Shop shop) {
        mContext = context;
        mShop = shop;
        if(mShop.getShopPhoneNumber() != null || !Objects.equals(mShop.getShopPhoneNumber(), "")) {
            mItemCount++;
            hasPhoneNumber = true;
        }
        if(mShop.getShopsExtensionFields() != null && mShop.getShopsExtensionFields().length != 0) {
            mItemCount += mShop.getShopsExtensionFields().length;
            mShopsExtensionFields = mShop.getShopsExtensionFields();
            hasExtensionFields = true;
        }
    }

    // ViewHolder作成
    @Override
    public ShopDetailRecyclerAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_detail, parent, false);

        // ViewHolder作成
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    // 画面に表示する内容をセット
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // 住所を表示
        if(position == 0) {
            holder.itemTitleTextView.setText(mContext.getString(R.string.shop_address));
            holder.itemTextView.setText(mShop.getShopAddress());
        }
        // 電話番号を表示
        else if(hasPhoneNumber && position == 1) {
            holder.itemTitleTextView.setText(mContext.getString(R.string.shop_phone_number));
            holder.itemTextView.setText(mShop.getShopPhoneNumber());
        }
        // ExtensionFieldsを表示
        else if(hasExtensionFields) {
            holder.itemTitleTextView.setText(mShopsExtensionFields[mShopsExtensionFieldsCount].getLabel());
            holder.itemTextView.setText(mShopsExtensionFields[mShopsExtensionFieldsCount].getValue());
            mShopsExtensionFieldsCount++;
        }
    }

    @Override
    public int getItemCount() {
        return mItemCount;
    }
}
