package jp.co.conol.favor_android.activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopOrderHistoryRecyclerAdapter;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.favor_model.Order;
import jp.co.conol.favorlib.cuona.favor_model.User;

public class OrderStopActivity extends AppCompatActivity {

    private final Gson mGson = new Gson();
    private int mVisitGroupId;
    private ShopOrderHistoryRecyclerAdapter mShopOrderHistoryRecyclerAdapter;
    @BindView(R.id.orderStopRecyclerView) RecyclerView mOrderStopRecyclerView;
    @BindView(R.id.orderStopButtonConstraintLayout) ConstraintLayout mOrderStopButtonConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_stop);
        ButterKnife.bind(this);

        // ユーザー情報の取得
        final User user = mGson.fromJson(MyUtil.SharedPref.getString(this, "userSetting"), User.class);
        Intent intent = getIntent();
        final int visitHistoryId = intent.getIntExtra("visitHistoryId", 0);
        mVisitGroupId = intent.getIntExtra("visitGroupId", 0);

        // 会計するボタンを押した場合、サーバーに退店を通知する
        mOrderStopButtonConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    new Favor(new Favor.AsyncCallback() {
                        @Override
                        public void onSuccess(Object object) {
                            List<Order> orderList = (List<Order>) object;
                            Toast.makeText(OrderStopActivity.this, "会計するボタンが押されたよ！", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(OrderStopActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("onFailure", e.toString());
                        }
                    }).setAppToken(user.getAppToken()).setVisitHistoryId(visitHistoryId).execute(Favor.Task.OrderStop);
                }
                return false;
            }
        });

        if(mVisitGroupId != 0) {
            new Favor(new Favor.AsyncCallback() {
                @Override
                public void onSuccess(Object object) {
                    List<Order> orderList = (List<Order>) object;

                    if (orderList != null) {

                        // レイアウトマネージャーのセット
                        mOrderStopRecyclerView.setLayoutManager(new LinearLayoutManager(OrderStopActivity.this));

                        // アダプターのセット
                        mShopOrderHistoryRecyclerAdapter = new ShopOrderHistoryRecyclerAdapter(OrderStopActivity.this, orderList);
                        mOrderStopRecyclerView.setAdapter(mShopOrderHistoryRecyclerAdapter);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("onFailure", e.toString());
                }
            }).setAppToken(user.getAppToken()).setVisitGroupId(mVisitGroupId).execute(Favor.Task.GetUserGroupsOrderInShop);
        }
    }
}
