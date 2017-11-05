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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
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
    private int mTappedPosition;    // メニューをタップした際のメニュー位置
    @BindView(R.id.shopMenuRecyclerView) RecyclerView mShopMenuRecyclerView;
    @BindView(R.id.layoutOrderDialog) ConstraintLayout mLayoutOrderDialog;
    @BindView(R.id.orderNumConstraintLayout) ConstraintLayout mOrderNumConstraintLayout;
    @BindView(R.id.orderNumTextView) TextView mOrderNumTextView;
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

                // メニュー注文数のリスト
                final List<Integer> orderNumList = new ArrayList<>();
                for(int i = 0; i < menuList.size(); i++) {
                    orderNumList.add(0);
                }

                // レイアウトマネージャーのセット
                mShopMenuRecyclerView.setLayoutManager(new LinearLayoutManager(ShopMenuActivity.this));

                // アダプターをセット
                final ShopMenuRecyclerAdapter shopMenuRecyclerAdapter
                        = new ShopMenuRecyclerAdapter(ShopMenuActivity.this, menuList, orderNumList) {
                    // メニュータップ時に注文ダイアログを表示
                    @Override
                    protected void showOrderDialog(int position, int orderNum) {
                        mTappedPosition = position; // タップされたポジションを取得
                        mOrderNumTextView.setText(String.valueOf(orderNum));
                        mLayoutOrderDialog.setVisibility(View.VISIBLE);
                        isShownOrderDialog = true;
                    }
                };
                mShopMenuRecyclerView.setAdapter(shopMenuRecyclerAdapter);

                // 注文数タップ時の処理
                mOrderNumConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            if(isShownOrderDialog) {
                                NumberPickerDialog dialog = NumberPickerDialog.newInstance(
                                        R.layout.layout_number_picker_dialog,
                                        getString(R.string.shop_menu_order_dialog_title),
                                        0,
                                        9,
                                        getString(R.string.ok),
                                        getString(R.string.cancel_kana)
                                );
                                dialog.show(getSupportFragmentManager(), "numberPickerDialog");
                            }
                        }
                        return false;
                    }
                });

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

                            // 注文ダイアログを非表示
                            mLayoutOrderDialog.setVisibility(View.GONE);
                            isShownOrderDialog = false;

                            // 注文数をメニューに表示
                            orderNumList.set(mTappedPosition, Integer.parseInt(mOrderNumTextView.getText().toString()));
                            shopMenuRecyclerAdapter.notifyItemChanged(mTappedPosition);
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

    // 注文数を選択したあとの処理
    @Override
    public void onPositiveButtonClicked(int value) {
        mOrderNumTextView.setText(String.valueOf(value));
    }
}
