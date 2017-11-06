package jp.co.conol.favor_android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopMenuRecyclerAdapter;
import jp.co.conol.favor_android.custom.NumberPickerDialog;
import jp.co.conol.favorlib.cuona.Cuona;
import jp.co.conol.favorlib.cuona.NfcNotAvailableException;
import jp.co.conol.favorlib.favor.Favor;
import jp.co.conol.favorlib.favor.model.Menu;
import jp.co.conol.favorlib.favor.model.Order;
import jp.co.conol.favorlib.favor.model.User;

public class ShopMenuActivity extends AppCompatActivity implements NumberPickerDialog.OnPositiveButtonClickedListener {

    private boolean isScanning = false;
    private Handler mScanDialogAutoCloseHandler = new Handler();
    private Cuona mCuona;
    private final Gson mGson = new Gson();
    private User mUser;
    private int mVisitHistoryId;
    private List<Order> mOrderList = new ArrayList<>(); // 注文するメニューのリスト
    private boolean isShownOrderDialog = false;
    private int mTappedPosition;    // メニューをタップした際のメニュー位置
    @BindView(R.id.shopMenuRecyclerView) RecyclerView mShopMenuRecyclerView;
    @BindView(R.id.layoutOrderDialog) ConstraintLayout mLayoutOrderDialog;
    @BindView(R.id.orderNumConstraintLayout) ConstraintLayout mOrderNumConstraintLayout;
    @BindView(R.id.orderNumTextView) TextView mOrderNumTextView;
    @BindView(R.id.cancelButtonConstraintLayout) ConstraintLayout mCancelButtonConstraintLayout;
    @BindView(R.id.selectButtonConstraintLayout) ConstraintLayout mSelectButtonConstraintLayout;
    @BindView(R.id.orderButtonConstraintLayout) ConstraintLayout mOrderButtonConstraintLayout;
    @BindView(R.id.ScanBackgroundConstraintLayout) ConstraintLayout mScanBackgroundConstraintLayout;
    @BindView(R.id.scanDialogConstraintLayout) ConstraintLayout mScanDialogConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_menu);
        ButterKnife.bind(this);

        try {
            mCuona = new Cuona(this);
        } catch (NfcNotAvailableException e) {
            Log.d("Corona", e.toString());
            finish();
        }

        // 遷移前の情報を取得
        Intent intent = getIntent();
        int shopId = intent.getIntExtra("shopId", 0);
        mVisitHistoryId = intent.getIntExtra("visitHistoryId", 0);
        mUser = mGson.fromJson(MyUtil.SharedPref.get(this, "userSetting"), User.class);

        new Favor(new Favor.AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                @SuppressWarnings("unchecked")
                final List<Menu> menuList = (List<Menu>) object;

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
                            int orderNum =  Integer.parseInt(mOrderNumTextView.getText().toString());
                            orderNumList.set(mTappedPosition,orderNum);
                            shopMenuRecyclerAdapter.notifyItemChanged(mTappedPosition);

                            // 注文するメニューのオブジェクトを作成し、注文リストに追加
                            mOrderList.add(new Order(menuList.get(mTappedPosition).getId(), orderNum));
                        }
                        return false;
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("onFailure", e.toString());
            }
        }).setAppToken(mUser.getAppToken()).setShopId(shopId).execute(Favor.Task.GetMenu);

        // スキャン画面が開いているときは、背景のタップを出来ないように設定
        mScanBackgroundConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isScanning;
            }
        });
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

    // NFCにタップされた時の処理
    @Override
    protected void onNewIntent(final Intent intent) {
        if(isScanning) {

            // 注文処理
            new Favor(new Favor.AsyncCallback() {
                @Override
                public void onSuccess(Object object) {
//                    @SuppressWarnings("unchecked")
//                    List<Order> orderList = (List<Order>) object;
                    Intent intent = new Intent(ShopMenuActivity.this, OrderDoneActivity.class);
                    startActivity(intent);
                    cancelScan();
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("onFailure", e.toString());
                }
            }).setAppToken(mUser.getAppToken()).setVisitHistoryId(mVisitHistoryId).setOrder(mOrderList).execute(Favor.Task.Order);
        }
    }

    public void onStartScanButtonClicked(View view) {
        // nfcがオフの場合はダイアログを表示
        if(!mCuona.isEnable()) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.nfc_dialog))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        } else {
            if (!isScanning) {

                // nfc読み込み待機
                mCuona.enableForegroundDispatch(ShopMenuActivity.this);
                isScanning = true;
                openScanPage();

                // 60秒後に自動で閉じる
                mScanDialogAutoCloseHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isScanning) {
                            cancelScan();
                        }
                    }
                }, 60000);
            }
        }
    }

    public void onCancelScanButtonClicked(View view) {
        if(isScanning) {
            cancelScan();

            // 60秒後に閉じる予約をキャンセル
            mScanDialogAutoCloseHandler.removeCallbacksAndMessages(null);
        }
    }

    private void cancelScan() {
        // nfc読み込み待機を解除
        mCuona.disableForegroundDispatch(ShopMenuActivity.this);
        isScanning = false;
        closeScanPage();
    }

    private void closeScanPage() {
        mScanDialogConstraintLayout.setVisibility(View.GONE);
        mScanBackgroundConstraintLayout.setVisibility(View.GONE);
        mScanDialogConstraintLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_to_bottom));
        mScanBackgroundConstraintLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_slowly));
        mScanDialogAutoCloseHandler.removeCallbacksAndMessages(null);
    }

    // 読み込み画面を表示
    private void openScanPage() {
        mScanDialogConstraintLayout.setVisibility(View.VISIBLE);
        mScanBackgroundConstraintLayout.setVisibility(View.VISIBLE);
        mScanDialogConstraintLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom));
        mScanBackgroundConstraintLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_slowly));
    }
}
