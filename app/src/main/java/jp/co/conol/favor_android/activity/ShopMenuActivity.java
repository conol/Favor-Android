package jp.co.conol.favor_android.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopMenuRecyclerAdapter;
import jp.co.conol.favor_android.custom.NumberPickerDialog;
import jp.co.conol.favorlib.favor.Favor;
import jp.co.conol.favorlib.favor.model.Menu;
import jp.co.conol.favorlib.favor.model.User;

public class ShopMenuActivity extends AppCompatActivity implements NumberPickerDialog.OnPositiveButtonClickedListener {

    private final Gson mGson = new Gson();
    private boolean isShownOrderDialog = false;
    @BindView(R.id.shopMenuRecyclerView) RecyclerView mShopMenuRecyclerView;
    @BindView(R.id.layoutOrderDialog) ConstraintLayout mLayoutOrderDialog;
    @BindView(R.id.cancelButtonConstraintLayout) ConstraintLayout mCancelButtonConstraintLayout;
    @BindView(R.id.selectButtonConstraintLayout) ConstraintLayout mSelectButtonConstraintLayout;

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
                        = new ShopMenuRecyclerAdapter(ShopMenuActivity.this, menuList) {
                    @Override
                    protected void showOrderDialog() {
                        mLayoutOrderDialog.setVisibility(View.VISIBLE);
                        isShownOrderDialog = true;
                    }
                };
                mShopMenuRecyclerView.setAdapter(shopMenuRecyclerAdapter);

                // 注文ダイアログのキャンセルボタンタップ時の処理
                mCancelButtonConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            if(isShownOrderDialog) {
                                mLayoutOrderDialog.setVisibility(View.GONE);
                                isShownOrderDialog = false;
                            }
                        }
                        return false;
                    }
                });

                // 注文ダイアログの選択ボタンタップ時の処理
                mSelectButtonConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            NumberPickerDialog dialog = NumberPickerDialog.newInstance(
                                    R.layout.layout_number_picker_dialog,
                                    getString(R.string.shop_menu_order_dialog_title),
                                    1,
                                    9,
                                    getString(R.string.ok),
                                    getString(R.string.cancel_kana)
                            );
                            dialog.show(getSupportFragmentManager(), "numberPickerDialog");
                        }
                        return false;
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("onFailure", e.toString());
            }
        }).setAppToken(user.getAppToken()).setShopId(shopId).execute(Favor.Task.GetMenu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(isShownOrderDialog) {
                mLayoutOrderDialog.setVisibility(View.GONE);
                isShownOrderDialog = false;
            } else {
                finish();
            }
        }
        return false;
    }

    @Override
    public void onPositiveButtonClicked(int value) {
        mLayoutOrderDialog.setVisibility(View.GONE);
        isShownOrderDialog = false;
        Toast.makeText(this, String.valueOf(value), Toast.LENGTH_LONG).show();
    }
}
