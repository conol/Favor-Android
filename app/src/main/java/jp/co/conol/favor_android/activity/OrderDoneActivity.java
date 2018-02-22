package jp.co.conol.favor_android.activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.R;
import jp.co.conol.favorlib.cuona.favor_model.Shop;

public class OrderDoneActivity extends AppCompatActivity {

    @BindView(R.id.shopImageView) ImageView mShopImageView;    // 店舗画像
    @BindView(R.id.shopNameTextView) TextView mShopNameTextView;    // 店舗名
    @BindView(R.id.orderDoneCloseButtonConstraintLayout) ConstraintLayout mOrderDoneCloseButtonConstraintLayout;    // 画面を閉じるボタン

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_done);
        ButterKnife.bind(this);

        // 店舗メニューページからのインテントを取得
        Gson gson = new Gson();
        Intent intent = getIntent();
        Shop shop = gson.fromJson(intent.getStringExtra("shop"), Shop.class);

        if(shop != null) {
            Picasso.with(this).load(shop.getImageUrls()[0]).into(mShopImageView);  // 店舗画像を反映
            mShopNameTextView.setText(shop.getName()); // 店舗名を反映
        }

        // この画面を閉じるボタンが押された場合は閉じる
        mOrderDoneCloseButtonConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
