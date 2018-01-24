package jp.co.conol.favor_android.activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.ShopMenuRecyclerAdapter;
import jp.co.conol.favor_android.custom.CuonaUtil;
import jp.co.conol.favor_android.custom.NumberPickerDialog;
import jp.co.conol.favor_android.custom.ScanCuonaDialog;
import jp.co.conol.favorlib.cuona.Cuona;
import jp.co.conol.favorlib.cuona.NFCNotAvailableException;
import jp.co.conol.favorlib.cuona.cuona_reader.CuonaReaderException;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.favor_model.Menu;
import jp.co.conol.favorlib.cuona.favor_model.Order;
import jp.co.conol.favorlib.cuona.favor_model.User;

public class ShopMenuActivity extends AppCompatActivity implements NumberPickerDialog.OnPositiveButtonClickedListener {

    private ScanCuonaDialog mScanCuonaDialog;
    private Cuona mCuona;
    private final Gson mGson = new Gson();
    private User mUser;
    private int mVisitHistoryId;
    private List<Order> mOrderList = new ArrayList<>(); // 注文するメニューのリスト
    private boolean isShownOrderDialog = false; // 注文ダイアログが開いているか否か
    private int mTappedPosition;    // メニューをタップした際のメニュー位置
    @BindView(R.id.innerOrderDialog) ConstraintLayout mInnerOrderDialog;    // メニューダイアログのダイアログ部分
    @BindView(R.id.shopNameTextView) TextView mShopNameTextView;    // タイトルバー部分の店舗名
    @BindView(R.id.shopMenuRecyclerView) RecyclerView mShopMenuRecyclerView;    // メニュー一覧
    @BindView(R.id.layoutOrderDialog) ConstraintLayout mLayoutOrderDialog;  // メニュータップ時の注文数選択ダイアログ
    @BindView(R.id.orderNumConstraintLayout) ConstraintLayout mOrderNumConstraintLayout;
    @BindView(R.id.menuImageView) ImageView mMenuImageView;    // メニュー画像
    @BindView(R.id.menuNameTextView) TextView mMenuNameTextView;    // メニュー名
    @BindView(R.id.menuPriceTextView) TextView mMenuPriceTextView;    // メニュー値段
    @BindView(R.id.menuNoteTextView) TextView mMenuNoteTextView;    // メニュー説明
    @BindView(R.id.orderNumTextView) TextView mOrderNumTextView;    // メニューオーダー数
    @BindView(R.id.cancelButtonConstraintLayout) ConstraintLayout mCancelButtonConstraintLayout;
    @BindView(R.id.selectButtonConstraintLayout) ConstraintLayout mSelectButtonConstraintLayout;
    @BindView(R.id.orderButtonConstraintLayout) ConstraintLayout mOrderButtonConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_menu);
        ButterKnife.bind(this);

        try {
            mCuona = new Cuona(this);
        } catch (NFCNotAvailableException e) {
            Log.d("Corona", e.toString());
            finish();
        }

        // CUONAスキャンダイアログのインスタンスを生成
        mScanCuonaDialog = new ScanCuonaDialog(ShopMenuActivity.this, mCuona, 60000, false);

        // 遷移前の情報を取得
        Intent intent = getIntent();
        int shopId = intent.getIntExtra("shopId", 0);
        String shopName = intent.getStringExtra("shopName");
        mVisitHistoryId = intent.getIntExtra("visitHistoryId", 0);
        mUser = mGson.fromJson(MyUtil.SharedPref.getString(this, "userSetting"), User.class);

        // 店舗名を反映
        if(shopName != null) mShopNameTextView.setText(shopName);

        new Favor(new Favor.AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                @SuppressWarnings("unchecked")
                final List<Menu> menuList = (List<Menu>) object;
                List<Integer> insertHeaderPosition = new ArrayList<>();

                if(menuList != null && menuList.size() != 0) {

                    // メニュー注文数のリスト
                    final List<Integer> orderNumList = new ArrayList<>();
                    for (int i = 0; i < menuList.size(); i++) {
                        orderNumList.add(0);
                    }

                    // メニューカテゴリーを表示するためのヘッダー部分の要素を追加
                    for(int i = 0; i < menuList.size() - 1; i++) {
                        if(!Objects.equals(menuList.get(i).getCategoryName(), menuList.get(i + 1).getCategoryName())) {
                            insertHeaderPosition.add(i + 1);
                        }
                    }
                    for(int i = 0; i < insertHeaderPosition.size(); i++) {
                        menuList.add(insertHeaderPosition.get(insertHeaderPosition.size() - 1 - i), null);
                        orderNumList.add(insertHeaderPosition.get(insertHeaderPosition.size() - 1 - i), 0);
                    }
                    if(1 <= menuList.size()) menuList.add(0, null); // 先頭にヘッダー用要素を追加
                    if(1 <= orderNumList.size()) orderNumList.add(0, 0); // 注文用リストの先頭にヘッダー用要素を追加

                    // レイアウトマネージャーのセット
                    mShopMenuRecyclerView.setLayoutManager(new LinearLayoutManager(ShopMenuActivity.this));

                    // アダプターをセット
                    final ShopMenuRecyclerAdapter shopMenuRecyclerAdapter
                            = new ShopMenuRecyclerAdapter(ShopMenuActivity.this, menuList, orderNumList) {

                        // メニュータップ時に注文ダイアログを表示
                        @Override
                        protected void showOrderDialog(int position, int orderNum) {

                            // タップされたポジション取得し、メニュー項目を取得
                            mTappedPosition = position;
                            Menu menu = menuList.get(position);

                            // オーダー数をダイアログにセット
                            mOrderNumTextView.setText(String.valueOf(orderNum));

                            // ダイアログにメニュー内容をセット
                            if(menu.getImages() != null && menu.getImages().length != 0) {
                                mMenuImageView.setVisibility(View.VISIBLE);
                                Picasso.with(ShopMenuActivity.this).load(menu.getImages()[0]).into(mMenuImageView);
                            }
                            mMenuNameTextView.setText(menu.getName());
                            mMenuPriceTextView.setText(menu.getPriceFormat());
                            if(menu.getNotes() != null) mMenuNoteTextView.setText(menu.getNotes());

                            openOrderDialog();
                        }
                    };
                    mShopMenuRecyclerView.setAdapter(shopMenuRecyclerAdapter);

                    // 注文数タップ時の処理
                    mOrderNumConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                if (isShownOrderDialog) {
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

                    // 注文ダイアログの「キャンセル」ボタンタップ時の処理
                    mCancelButtonConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                if (isShownOrderDialog) {
                                    closeOrderDialog();
                                }
                            }
                            return false;
                        }
                    });

                    // 注文ダイアログの「選択」ボタンタップ時の処理
                    mSelectButtonConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                                // 注文ダイアログを非表示
                                closeOrderDialog();

                                // 注文数をメニューに表示
                                int orderNum = Integer.parseInt(mOrderNumTextView.getText().toString());
                                orderNumList.set(mTappedPosition, orderNum);
                                shopMenuRecyclerAdapter.notifyItemChanged(mTappedPosition);

                                // 注文するメニューのオブジェクトを作成し、注文リストに追加
                                mOrderList.add(new Order(menuList.get(mTappedPosition).getId(), orderNum));
                            }
                            return false;
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("onFailure", e.toString());
            }
        }).setAppToken(mUser.getAppToken()).setShopId(shopId).execute(Favor.Task.GetMenu);

        // 注文ダイアログが開いているときは、背景のタップを出来ないように設定
        mLayoutOrderDialog.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isShownOrderDialog;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCuona != null) mCuona.enableForegroundDispatch(ShopMenuActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mCuona != null) mCuona.disableForegroundDispatch(ShopMenuActivity.this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(isShownOrderDialog) {
                closeOrderDialog();
            } else if(mScanCuonaDialog != null && mScanCuonaDialog.isShowing()){
                mScanCuonaDialog.dismiss();
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

    // 注文するためにNFCにタップされた時の処理
    @Override
    protected void onNewIntent(final Intent intent) {
        if(mScanCuonaDialog.isShowing()) {

            // 注文処理
            new Favor(new Favor.AsyncCallback() {
                @Override
                public void onSuccess(Object object) {

                    // ログ送信
                    mCuona.setReadLogMessage("注文");
                    try {
                        mCuona.readDeviceId(intent);
                    } catch (CuonaReaderException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(ShopMenuActivity.this, OrderDoneActivity.class);
                    intent.putExtra("shopName", mShopNameTextView.getText().toString());
                    startActivity(intent);
                    mScanCuonaDialog.dismiss();
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
        CuonaUtil.checkNfcSetting(this, mCuona);

        // BluetoothとNFCが許可されている場合処理を進める
        if(mCuona.isNfcEnabled()) {
            mScanCuonaDialog.show();
        }
    }

    // 注文ダイアログを表示
    private void openOrderDialog() {
        mLayoutOrderDialog.setVisibility(View.VISIBLE);
        mLayoutOrderDialog.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_slowly));
        isShownOrderDialog = true;
    }

    // 注文ダイアログを非表示
    private void closeOrderDialog() {

        final AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_slowly));
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            // アニメーションを終了した後の処理
            @Override
            public void onAnimationEnd(Animation animation) {
                mLayoutOrderDialog.setVisibility(View.GONE);
                mMenuImageView.setVisibility(View.GONE);
                mMenuNoteTextView.setText("");
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        mLayoutOrderDialog.setVisibility(View.GONE);
        mLayoutOrderDialog.setAnimation(animationSet);
        isShownOrderDialog = false;
    }

}
