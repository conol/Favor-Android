package jp.co.conol.favor_android.activity;

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
import jp.co.conol.favor_android.adapter.ShopHistoryRecyclerAdapter;
import jp.co.conol.favorlib.cuona.Cuona;
import jp.co.conol.favorlib.favor.Favor;
import jp.co.conol.favorlib.favor.model.Shop;
import jp.co.conol.favorlib.favor.model.User;

public class ShopHistoryActivity extends AppCompatActivity {

    private final Gson mGson = new Gson();
    private User mUser;
    @BindView(R.id.ShopHistoryRecyclerView) RecyclerView mShopHistoryRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_history);
        ButterKnife.bind(this);

        // ユーザー情報を取得
        mUser = mGson.fromJson(MyUtil.SharedPref.get(this, "userSetting"), User.class);

        // 入店履歴を取得
        new Favor(new Favor.AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                @SuppressWarnings("unchecked")
                List<Shop> shopList = (List<Shop>) object;

                // レイアウトマネージャーのセット
                mShopHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(ShopHistoryActivity.this));

                // アダプターのセット
                ShopHistoryRecyclerAdapter shopHistoryRecyclerAdapter
                        = new ShopHistoryRecyclerAdapter(ShopHistoryActivity.this, shopList);
                mShopHistoryRecyclerView.setAdapter(shopHistoryRecyclerAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("onFailure", e.toString());
            }
        }).setAppToken(mUser.getAppToken()).execute(Favor.Task.GetVisitedShopHistory);
    }
}
