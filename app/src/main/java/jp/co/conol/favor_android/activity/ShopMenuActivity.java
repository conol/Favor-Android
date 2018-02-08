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
import android.widget.Toast;

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
import jp.co.conol.favor_android.custom.ProgressDialog;
import jp.co.conol.favor_android.custom.ScanCuonaDialog;
import jp.co.conol.favor_android.custom.SimpleAlertDialog;
import jp.co.conol.favorlib.cuona.Cuona;
import jp.co.conol.favorlib.cuona.FavorException;
import jp.co.conol.favorlib.cuona.NFCNotAvailableException;
import jp.co.conol.favorlib.cuona.cuona_reader.CuonaReaderException;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.favor_model.Menu;
import jp.co.conol.favorlib.cuona.favor_model.Order;
import jp.co.conol.favorlib.cuona.favor_model.Shop;
import jp.co.conol.favorlib.cuona.favor_model.User;

public class ShopMenuActivity extends AppCompatActivity implements NumberPickerDialog.OnPositiveButtonClickedListener {

    private ScanCuonaDialog mScanCuonaDialog;
    private Cuona mCuona;
    private final Gson mGson = new Gson();
    private Shop mShop;
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
        mShop = mGson.fromJson(intent.getStringExtra("shop"), Shop.class);
        if(mShop == null) {
            Toast.makeText(this,  getString(R.string.error_common), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 入店か履歴から表示か
        final boolean isEntering = MyUtil.SharedPref.getBoolean(this, "isEntering", false);

        // 履歴から表示の場合は「注文する」ボタンを非表示
        if(!isEntering) {
            mOrderButtonConstraintLayout.setVisibility(View.GONE);
        }

        if(MyUtil.Network.isEnable(this)) {

            // 読み込みダイアログを表示
            final ProgressDialog progressDialog = new ProgressDialog(ShopMenuActivity.this);
            progressDialog.setMessage(getString(R.string.main_progress_message));
            progressDialog.show();

            // 店舗名を反映
            mShopNameTextView.setText(mShop.getShopName());
            new Favor(new Favor.AsyncCallback() {
                @Override
                public void onSuccess(Object object) {
                    @SuppressWarnings("unchecked") final List<Menu> menuList = (List<Menu>) object;
                    List<Integer> insertHeaderPosition = new ArrayList<>();
                    progressDialog.dismiss();

                    if (menuList != null && menuList.size() != 0) {

                        // メニュー注文数のリスト
                        final List<Integer> orderNumList = new ArrayList<>();
                        for (int i = 0; i < menuList.size(); i++) {
                            orderNumList.add(0);
                        }

                        // メニューカテゴリーを表示するためのヘッダー部分の要素を追加
                        for (int i = 0; i < menuList.size() - 1; i++) {
                            if (!Objects.equals(menuList.get(i).getCategoryName(), menuList.get(i + 1).getCategoryName())) {
                                insertHeaderPosition.add(i + 1);
                            }
                        }
                        for (int i = 0; i < insertHeaderPosition.size(); i++) {
                            menuList.add(insertHeaderPosition.get(insertHeaderPosition.size() - 1 - i), null);
                            orderNumList.add(insertHeaderPosition.get(insertHeaderPosition.size() - 1 - i), 0);
                        }
                        if (1 <= menuList.size()) menuList.add(0, null); // 先頭にヘッダー用要素を追加
                        if (1 <= orderNumList.size())
                            orderNumList.add(0, 0); // 注文用リストの先頭にヘッダー用要素を追加

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
                                if (menu.getImages() != null && menu.getImages().length != 0) {
                                    mMenuImageView.setVisibility(View.VISIBLE);
                                    Picasso.with(ShopMenuActivity.this).load(menu.getImages()[0]).into(mMenuImageView);
                                }
                                mMenuNameTextView.setText(menu.getName());
                                mMenuPriceTextView.setText(menu.getPriceFormat());
                                if (menu.getNotes() != null)
                                    mMenuNoteTextView.setText(menu.getNotes());

                                openOrderDialog();
                            }
                        };
                        mShopMenuRecyclerView.setAdapter(shopMenuRecyclerAdapter);

                        // 入店時のみ注文可能
                        if(isEntering) {

                            // 注文数タップ時の処理
                            mOrderNumConstraintLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
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
                            });

                            // 注文ダイアログの「キャンセル」ボタンタップ時の処理
                            mCancelButtonConstraintLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (isShownOrderDialog) {
                                        closeOrderDialog();
                                    }
                                }
                            });

                            // 注文ダイアログの「選択」ボタンタップ時の処理
                            mSelectButtonConstraintLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // 注文ダイアログを非表示
                                    closeOrderDialog();

                                    // 注文数をメニューに表示
                                    int orderNum = Integer.parseInt(mOrderNumTextView.getText().toString());
                                    orderNumList.set(mTappedPosition, orderNum);
                                    shopMenuRecyclerAdapter.notifyItemChanged(mTappedPosition);

                                    // 注文するメニューのオブジェクトを作成し、注文リストに追加
                                    mOrderList.add(new Order(menuList.get(mTappedPosition).getId(), orderNum));
                                }
                            });
                        }
                    } else {
                        Toast.makeText(ShopMenuActivity.this, getString(R.string.error_common), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(FavorException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // 読み込みダイアログを非表示
                            progressDialog.dismiss();

                            new SimpleAlertDialog(ShopMenuActivity.this, getString(R.string.error_common)).show();
                        }
                    });
                }
            }).setContext(this).setShopId(mShop.getShopId()).execute(Favor.Task.GetMenu);
        } else {
            new SimpleAlertDialog(ShopMenuActivity.this, getString(R.string.error_network_disable)).show();
        }

        // 注文ダイアログが開いているときは、背景のタップを出来ないように設定
        MyUtil.App.enableTouchBackground(mLayoutOrderDialog, isShownOrderDialog);
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
            if(MyUtil.Network.isEnable(this)) {

                // 読み込みダイアログを表示
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage(getString(R.string.main_progress_message));
                progressDialog.show();

                new Favor(new Favor.AsyncCallback() {
                    @Override
                    public void onSuccess(Object object) {

                        // 読み込みダイアログを非表示
                        progressDialog.dismiss();

                        // ログ送信
                        mCuona.setReadLogMessage("注文");
                        try {
                            mCuona.readDeviceId(intent);
                        } catch (CuonaReaderException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(ShopMenuActivity.this, OrderDoneActivity.class);
                        intent.putExtra("shop", mGson.toJson(mShop));
                        startActivity(intent);
                        mScanCuonaDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onFailure(FavorException e) {
                        // 読み込みダイアログを非表示
                        progressDialog.dismiss();

                        if(e.getCode() == FavorException.BAD_REQUEST) {
                            new SimpleAlertDialog(ShopMenuActivity.this, getString(R.string.error_already_order_stopped)).show();
                        } else {
                            new SimpleAlertDialog(ShopMenuActivity.this, getString(R.string.error_common)).show();
                        }
                    }
                }).setContext(this).setVisitHistoryId(mShop.getVisitHistoryId()).setOrder(mOrderList).execute(Favor.Task.Order);
            } else {
                new SimpleAlertDialog(ShopMenuActivity.this, getString(R.string.error_network_disable)).show();
            }
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
