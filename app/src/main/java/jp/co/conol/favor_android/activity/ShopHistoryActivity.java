package jp.co.conol.favor_android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopHistoryRecyclerAdapter;

public class ShopHistoryActivity extends AppCompatActivity {

    private RecyclerView mShopHistoryRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_history);

        mShopHistoryRecyclerView = (RecyclerView) findViewById(R.id.ShopHistoryRecyclerView);

        // レイアウトマネージャーのセット
        mShopHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // アダプターのセット
        ShopHistoryRecyclerAdapter adapter = new ShopHistoryRecyclerAdapter(this);
        mShopHistoryRecyclerView.setAdapter(adapter);
    }
}
