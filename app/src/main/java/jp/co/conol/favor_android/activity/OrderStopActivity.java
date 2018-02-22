package jp.co.conol.favor_android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopOrderHistoryRecyclerAdapter;
import jp.co.conol.favor_android.custom.ProgressDialog;
import jp.co.conol.favor_android.custom.SimpleAlertDialog;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.FavorException;
import jp.co.conol.favorlib.cuona.favor_model.Order;
import jp.co.conol.favorlib.cuona.favor_model.Shop;

public class OrderStopActivity extends AppCompatActivity {

    private final Gson mGson = new Gson();
    private ShopOrderHistoryRecyclerAdapter mShopOrderHistoryRecyclerAdapter;
    @BindView(R.id.shopImageView) ImageView mShopImageView; // 店舗画像
    @BindView(R.id.shopNameTextView) TextView mShopNameTextView; // 店舗名
    @BindView(R.id.sumPriceTextView) TextView mSumPriceTextView; // 合計金額
    @BindView(R.id.membersPriceTextView) TextView mMembersPriceTextView; // 一人当たりの金額
    @BindView(R.id.orderStopRecyclerView) RecyclerView mOrderStopRecyclerView;
    @BindView(R.id.orderStopButtonConstraintLayout) ConstraintLayout mOrderStopButtonConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_stop);
        ButterKnife.bind(this);

        // 店舗情報の取得
        Intent intent = getIntent();
        final Shop shop = mGson.fromJson(intent.getStringExtra("shop"), Shop.class);

        // 店舗情報の表示
        if(shop != null) {
            Picasso.with(this).load(shop.getImageUrls()[0]).into(mShopImageView);
            mShopNameTextView.setText(shop.getName());
        } else {
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 会計するボタンを押した場合、サーバーに退店を通知する
        mOrderStopButtonConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MyUtil.Network.isEnable(OrderStopActivity.this)) {

                    // 読み込みダイアログを表示
                    final ProgressDialog progressDialog = new ProgressDialog(OrderStopActivity.this);
                    progressDialog.setMessage(getString(R.string.main_progress_message));
                    progressDialog.show();

                    new Favor(new Favor.AsyncCallback() {
                        @Override
                        public void onSuccess(Object object) {

                            // 読み込みダイアログを非表示
                            progressDialog.dismiss();

                            if(object != null) {
                                Intent intent = new Intent(OrderStopActivity.this, MainActivity.class);
                                MyUtil.SharedPref.saveBoolean(OrderStopActivity.this, "isEntering", false);   // 退店
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                new SimpleAlertDialog(OrderStopActivity.this, getString(R.string.error_common)).show();
                            }
                        }

                        @Override
                        public void onFailure(FavorException e) {
                            Log.e("onFailure", e.toString());
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 読み込みダイアログを非表示
                                    progressDialog.dismiss();

                                    new SimpleAlertDialog(OrderStopActivity.this, getString(R.string.error_common)).show();
                                }
                            });
                        }
                    }).setContext(OrderStopActivity.this).setVisitHistoryId(shop.getVisitHistoryId()).execute(Favor.Task.OrderStop);
                } else {
                    new SimpleAlertDialog(OrderStopActivity.this, getString(R.string.error_network_disable)).show();
                }
            }
        });

        // 注文履歴を取得
        if(MyUtil.Network.isEnable(OrderStopActivity.this)) {
            if(shop.getVisitHistoryId() != 0) {

                // 読み込みダイアログを表示
                final ProgressDialog progressDialog = new ProgressDialog(OrderStopActivity.this);
                progressDialog.setMessage(getString(R.string.main_progress_message));
                progressDialog.show();

                new Favor(new Favor.AsyncCallback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(Object object) {
                        List<Order> orderList = (List<Order>) object;

                        // 読み込みダイアログを非表示
                        progressDialog.dismiss();

                        if (orderList != null) {

                            // レイアウトマネージャーのセット
                            mOrderStopRecyclerView.setLayoutManager(new LinearLayoutManager(OrderStopActivity.this));

                            // アダプターのセット
                            mShopOrderHistoryRecyclerAdapter = new ShopOrderHistoryRecyclerAdapter(OrderStopActivity.this, orderList);
                            mOrderStopRecyclerView.setAdapter(mShopOrderHistoryRecyclerAdapter);

                            // 金額の取得
                            int sumPrice = 0;
                            int memberNum = 0;
                            List<Integer> memberIdList = new ArrayList<>();
                            for(Order order : orderList) {
                                sumPrice += order.getOrderedItemPriceCents() * order.getOrderedItemQuantity();
                                if(!memberIdList.contains(order.getOrderedUserId())) {
                                    memberIdList.add(order.getOrderedUserId());
                                    memberNum++;
                                }
                            }
                            mSumPriceTextView.setText(String.valueOf(sumPrice) + "円");
                            if(!Float.isNaN((float)sumPrice / memberNum)) {
                                mMembersPriceTextView.setText(String.valueOf((float) sumPrice / memberNum) + "円");
                            }

                        } else {
                            new SimpleAlertDialog(OrderStopActivity.this, getString(R.string.error_common)).show();
                        }
                    }

                    @Override
                    public void onFailure(FavorException e) {
                        Log.e("onFailure", e.toString());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                // 読み込みダイアログを非表示
                                progressDialog.dismiss();

                                new SimpleAlertDialog(OrderStopActivity.this, getString(R.string.error_common)).show();
                            }
                        });
                    }
                }).setContext(OrderStopActivity.this).setVisitGroupId(shop.getVisitGroupId()).execute(Favor.Task.GetUserGroupsOrderInShop);
            } else {
                new SimpleAlertDialog(OrderStopActivity.this, getString(R.string.error_network_disable)).show();
            }
        }
    }
}
