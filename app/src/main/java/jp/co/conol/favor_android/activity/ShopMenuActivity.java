package jp.co.conol.favor_android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopMenuRecyclerAdapter;

public class ShopMenuActivity extends AppCompatActivity {

    private RecyclerView mShopMenuRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_menu);

        mShopMenuRecyclerView = (RecyclerView) findViewById(R.id.shopMenuRecyclerView);

        // レイアウトマネージャーのセット
        mShopMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // アダプターをセット
        ShopMenuRecyclerAdapter shopMenuRecyclerAdapter = new ShopMenuRecyclerAdapter(this);
        mShopMenuRecyclerView.setAdapter(shopMenuRecyclerAdapter);
    }
}
