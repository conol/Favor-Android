package jp.co.conol.favor_android.activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.R;

public class OrderDoneActivity extends AppCompatActivity {

    @BindView(R.id.shopNameTextView)
    TextView mShopNameTextView;    // 店舗名表示
    @BindView(R.id.orderDoneCloseButtonConstraintLayout) ConstraintLayout mOrderDoneCloseButtonConstraintLayout;    // 画面を閉じるボタン

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_done);
        ButterKnife.bind(this);

        // 店舗メニューページからのインテントを取得
        Intent intent = getIntent();
        String shopName = intent.getStringExtra("shopName");    // 店舗名を取得

        // 店舗名を反映
        mShopNameTextView.setText(shopName);

        // この画面を閉じるボタンが押された場合は閉じる
        mOrderDoneCloseButtonConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                finish();
                return false;
            }
        });
    }
}
