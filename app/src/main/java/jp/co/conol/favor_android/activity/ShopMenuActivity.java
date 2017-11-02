package jp.co.conol.favor_android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopMenuRecyclerAdapter;
import jp.co.conol.favorlib.favor.Favor;
import jp.co.conol.favorlib.favor.model.Menu;
import jp.co.conol.favorlib.favor.model.User;

public class ShopMenuActivity extends AppCompatActivity {

    private final Gson mGson = new Gson();
    @BindView(R.id.shopMenuRecyclerView) RecyclerView mShopMenuRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_menu);
        ButterKnife.bind(this);

        // 遷移前の情報を取得
        Intent intent = getIntent();
        int shopId = intent.getIntExtra("shopId", 0);
        User user = mGson.fromJson(MyUtil.SharedPref.get(this, "userSetting"), User.class);

        new Favor(new Favor.AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                @SuppressWarnings("unchecked")
                List<Menu> menuList = (List<Menu>) object;

                // レイアウトマネージャーのセット
                mShopMenuRecyclerView.setLayoutManager(new LinearLayoutManager(ShopMenuActivity.this));

                // アダプターをセット
                ShopMenuRecyclerAdapter shopMenuRecyclerAdapter
                        = new ShopMenuRecyclerAdapter(ShopMenuActivity.this, menuList);
                mShopMenuRecyclerView.setAdapter(shopMenuRecyclerAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("onFailure", e.toString());
            }
        }).setAppToken(user.getAppToken()).setShopId(shopId).execute(Favor.Task.GetMenu);
    }
}
