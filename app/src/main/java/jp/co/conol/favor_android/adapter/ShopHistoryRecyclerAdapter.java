package jp.co.conol.favor_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.activity.MainActivity;
import jp.co.conol.favor_android.activity.ShopDetailActivity;
import jp.co.conol.favorlib.cuona.favor_model.Shop;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import jp.wasabeef.picasso.transformations.gpu.BrightnessFilterTransformation;

/**
 * Created by Masafumi_Ito on 2017/09/04.
 */

public class ShopHistoryRecyclerAdapter extends RecyclerView.Adapter<ShopHistoryRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<Shop> mShopList = new ArrayList<>();

    // ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.shopNameTextView) TextView mShopNameTextView;
        @BindView(R.id.shopEnterAtTextView) TextView mShopEnterAtTextView;
        @BindView(R.id.shopImageView) ImageView mShopImageView;

        // ViewHolderのコンストラクタ
        private ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    // コンストラクタ
    public ShopHistoryRecyclerAdapter(Context context, List<Shop> shopList) {
        mContext = context;
        mShopList = shopList;
    }

    // ViewHolder作成
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_history, parent, false);

        // ViewHolder作成
        final ViewHolder holder = new ViewHolder(view);

        // クリック時の処理
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // positionを取得
                final int position = holder.getAdapterPosition();

                // 店舗情報を取得
                Shop shop = mShopList.get(position);

                // 店舗詳細ページへ移動
                Intent intent = new Intent(mContext, ShopDetailActivity.class);
                MyUtil.SharedPref.saveBoolean(mContext, "isEntering", false);
                intent.putExtra("shop", new Gson().toJson(shop));
                mContext.startActivity(intent);
            }
        });

        return holder;
    }

    // 画面に表示する内容をセット
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // 店舗情報を取得
        Shop shop = mShopList.get(position);

        if(shop != null) {
            holder.mShopNameTextView.setText(shop.getShopName());
            DateTimeFormatter DEF_FMT = DateTimeFormat.forPattern("yyyy/MM/dd (E) HH:mm~"); // 入店時間
            holder.mShopEnterAtTextView.setText(DEF_FMT.print(DateTime.parse(shop.getEnterShopAt())));
            Picasso.with(mContext).load(shop.getShopImages()[0])
                    .fit()
                    .transform(new RoundedCornersTransformation(12, 0))
                    .transform(new BrightnessFilterTransformation(mContext, -0.2f))
                    .into(holder.mShopImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mShopList.size();
    }
}


