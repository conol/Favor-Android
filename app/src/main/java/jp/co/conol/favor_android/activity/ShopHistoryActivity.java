package jp.co.conol.favor_android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopHistoryRecyclerAdapter;
import jp.co.conol.favor_android.custom.ProgressDialog;
import jp.co.conol.favor_android.custom.SimpleAlertDialog;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.FavorException;
import jp.co.conol.favorlib.cuona.favor_model.Shop;
import jp.co.conol.favorlib.cuona.favor_model.User;

public class ShopHistoryActivity extends AppCompatActivity {

    @BindView(R.id.ShopHistoryRecyclerView) RecyclerView mShopHistoryRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_history);
        ButterKnife.bind(this);

        // ネットワークに接続されているか確認
        if(!MyUtil.Network.isEnable(this)) {
            new SimpleAlertDialog(ShopHistoryActivity.this, getString(R.string.error_network_disable)).show();
            return;
        }

        // 読み込みダイアログを表示
        final ProgressDialog progressDialog = new ProgressDialog(ShopHistoryActivity.this);
        progressDialog.setMessage(getString(R.string.main_progress_message));
        progressDialog.show();

        // 入店履歴を取得
        new Favor(new Favor.AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                @SuppressWarnings("unchecked")
                List<Shop> shopList = (List<Shop>) object;
                Collections.reverse(shopList);

                // レイアウトマネージャーのセット
                mShopHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(ShopHistoryActivity.this));

                // アダプターのセット
                ShopHistoryRecyclerAdapter shopHistoryRecyclerAdapter
                        = new ShopHistoryRecyclerAdapter(ShopHistoryActivity.this, shopList);
                mShopHistoryRecyclerView.setAdapter(shopHistoryRecyclerAdapter);

                // 読み込みダイアログを非表示
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(FavorException e) {
                Log.e("onFailure", e.toString());
                runOnUiThread(new Runnable() {
                    public void run() {
                        // 読み込みダイアログを非表示
                        progressDialog.dismiss();

                        new SimpleAlertDialog(ShopHistoryActivity.this, getString(R.string.error_common)).show();
                    }
                });
            }
        }).setContext(this).execute(Favor.Task.GetVisitedShopHistory);
    }
}
