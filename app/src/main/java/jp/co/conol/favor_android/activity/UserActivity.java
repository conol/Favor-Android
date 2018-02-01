package jp.co.conol.favor_android.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.UserFragmentStatePagerAdapter;
import jp.co.conol.favor_android.custom.ProgressDialog;
import jp.co.conol.favor_android.custom.SimpleAlertDialog;
import jp.co.conol.favor_android.fragment.UserFavoriteFragment;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.FavorException;
import jp.co.conol.favorlib.cuona.favor_model.Favorite;
import jp.co.conol.favorlib.cuona.favor_model.User;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;

public class UserActivity extends AppCompatActivity {

    private UserFragmentStatePagerAdapter mUserFragmentStatePagerAdapter;
    private User mUser;
    private boolean isBackCropUserImageActivity;  // ユーザー画像設定画面から戻ってきたか否か
    private boolean isShownAddFavorite = false;  // お気に入り追加画面を開いているか否か
    private boolean isShownEditUserSetting = false;  // ユーザー情報編集画面を開いているか否か
    @BindView(R.id.userNameTextView) TextView mUserNameTextView;
    @BindView(R.id.userGenderTextView) TextView mUserGenderTextView;
    @BindView(R.id.userAgeTextView) TextView mUserAgeTextView;
    @BindView(R.id.userTabLayout) TabLayout mUserTabLayout;
    @BindView(R.id.userViewPager) ViewPager mUserViewPager;
    @BindView(R.id.addUserFavoriteFloatingActionButton) FloatingActionButton mAddUserFavoriteFloatingActionButton;
    @BindView(R.id.userProfConstraintLayout) ConstraintLayout mUserProfConstraintLayout; // ユーザー情報を表示している部分
    @BindView(R.id.editUserLayout) ConstraintLayout mEditUserLayout; // ユーザー情報編集画面
    @BindView(R.id.userEditImageView) ImageView mUserEditImageView;   // ユーザー画像編集
    @BindView(R.id.userImageView) ImageView mUserImageView;   // ユーザー画像
    @BindView(R.id.userNameEditText) EditText mUserNameEditText;     // ユーザー名編集
    @BindView(R.id.userGenderEditText) EditText mUserGenderEditText; // ユーザー性別編集
    @BindView(R.id.userAgeEditText) EditText mUserAgeEditText;       // ユーザー年代編集
    @BindView(R.id.userEditButtonConstraintLayout) ConstraintLayout mUserEditButtonConstraintLayout; // ユーザー情報編集ボタン
    @BindView(R.id.addFavoriteLayout) ConstraintLayout mAddFavoriteLayout; // お気に入り追加画面
    @BindView(R.id.favIcon1) ImageView mFavIcon1; // お気に入りのハート画像1
    @BindView(R.id.favIcon2) ImageView mFavIcon2; // お気に入りのハート画像2
    @BindView(R.id.favIcon3) ImageView mFavIcon3; // お気に入りのハート画像3
    @BindView(R.id.favIcon4) ImageView mFavIcon4; // お気に入りのハート画像4
    @BindView(R.id.favIcon5) ImageView mFavIcon5; // お気に入りのハート画像5
    @BindView(R.id.favoriteLecelTextView) TextView mFavoriteLevelTextView; // お気に入りのレベル
    @BindView(R.id.addFavoriteEditText) EditText mAddFavoriteEditText; // お気に入り入力欄
    @BindView(R.id.addFavoriteButtonConstraintLayout) ConstraintLayout mAddFavoriteButtonConstraintLayout; // お気に入り追加ボタン
    private static final int PERMISSION_REQUEST_CODE = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    private static final int FILE_PERMISSION_REQUEST_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        // ViewPagerにアダプターをセット
        mUserFragmentStatePagerAdapter = new UserFragmentStatePagerAdapter(this, getSupportFragmentManager());
        mUserViewPager.setAdapter(mUserFragmentStatePagerAdapter);
        mUserTabLayout.setupWithViewPager(mUserViewPager);

        // 表示タブによりFABの表示を切り替え
        mUserViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        mAddUserFavoriteFloatingActionButton.setAlpha(1 - positionOffset);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mAddUserFavoriteFloatingActionButton.setEnabled(true);
                        break;
                    default:
                        mAddUserFavoriteFloatingActionButton.setEnabled(false);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // 読み込みダイアログを表示
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.main_progress_message));
        progressDialog.show();

        // ユーザー情報を取得
        new Favor(new Favor.AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                mUser = (User) object;

                // 読み込みダイアログを非表示
                progressDialog.dismiss();

                // ユーザー情報を表示
                String userGender = getString(R.string.user_gender_male);
                if (!Objects.equals(mUser.getGender(), "male")) userGender = getString(R.string.user_gender_female);
                mUserNameTextView.setText(mUser.getNickname());
                mUserNameEditText.setText(mUser.getNickname());
                mUserGenderTextView.setText(userGender);
                mUserGenderEditText.setText(userGender);
                mUserAgeTextView.setText(String.valueOf(mUser.getAge()) + "代");
                mUserAgeEditText.setText(String.valueOf(mUser.getAge()) + "代");
                if(!isBackCropUserImageActivity) {
                    if (mUser.getImageUrl() != null) {
                        Picasso.with(UserActivity.this).load(mUser.getImageUrl())
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .transform(new CropCircleTransformation())
                                .into(mUserEditImageView);
                        Picasso.with(UserActivity.this).load(mUser.getImageUrl())
                                .transform(new CropCircleTransformation())
                                .into(mUserImageView);
                    } else {
                        Picasso.with(UserActivity.this).load(R.drawable.ic_user_prof)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(mUserEditImageView);
                        Picasso.with(UserActivity.this).load(R.drawable.ic_user_prof).into(mUserImageView);
                    }
                }
            }

            @Override
            public void onFailure(FavorException e) {
                Log.e("onFailure", e.toString());
                runOnUiThread(new Runnable() {
                    public void run() {
                        // 読み込みダイアログを非表示
                        progressDialog.dismiss();

                        new SimpleAlertDialog(UserActivity.this, getString(R.string.error_common)).show();
                    }
                });
            }
        }).setContext(this).execute(Favor.Task.GetUser);

        // ユーザー画像設定から戻った場合はユーザー情報編集ダイアログを表示
        isBackCropUserImageActivity = getIntent().getBooleanExtra("isBackCropUserImageActivity", false);
        if(isBackCropUserImageActivity) {
            Picasso.with(UserActivity.this).load(Uri.fromFile(getFileStreamPath("userImage")))
                    .transform(new CropCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(mUserEditImageView);
            openEditUserDialog();
        }

        // FABを押した場合、お気に入り追加画面を表示
        mAddUserFavoriteFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFavoriteDialog();
            }
        });

        // お気に入りのハートのアイコンを押した時の処理
        mFavIcon1.setOnClickListener(tapFavIcon);
        mFavIcon2.setOnClickListener(tapFavIcon);
        mFavIcon3.setOnClickListener(tapFavIcon);
        mFavIcon4.setOnClickListener(tapFavIcon);
        mFavIcon5.setOnClickListener(tapFavIcon);

        // 「お気に入りを追加する」ボタンをタップした場合
        mAddFavoriteButtonConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MyUtil.Str.isBlank(mAddFavoriteEditText.getText().toString())) {
                    // 登録するお気に入りの内容
                    Favorite favorite = new Favorite(
                            mAddFavoriteEditText.getText().toString(),
                            Integer.parseInt(mFavoriteLevelTextView.getText().toString().replace(".0", ""))
                    );

                    new Favor(new Favor.AsyncCallback() {
                        @Override
                        public void onSuccess(Object object) {
                            Favorite favorite = (Favorite) object;

                            // お気に入り追加画面を初期化して非表示
                            mFavIcon2.setImageResource(R.drawable.ic_heart_fill);
                            mFavIcon3.setImageResource(R.drawable.ic_heart_fill);
                            mFavIcon4.setImageResource(R.drawable.ic_heart_blank);
                            mFavIcon5.setImageResource(R.drawable.ic_heart_blank);
                            mFavoriteLevelTextView.setText("3.0");
                            mAddFavoriteEditText.setText("");
                            closeFavoriteDialog();

                            // お気に入り一覧のフラグメントを取得して、変更を伝える
                            Fragment fragment = (Fragment) mUserFragmentStatePagerAdapter.instantiateItem(mUserViewPager, 0);
                            ((UserFavoriteFragment) fragment).notifyDataChanged(favorite);
                        }

                        @Override
                        public void onFailure(FavorException e) {
                            Log.e("onFailure", e.toString());
                        }
                    }).setContext(UserActivity.this).setFavorite(favorite).execute(Favor.Task.AddFavorite);
                } else {
                    Toast.makeText(UserActivity.this, getString(R.string.add_favorite_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ユーザー情報タップでユーザー情報編集画面を表示
        mUserProfConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditUserDialog();
            }
        });

        // ユーザー画像クリック時の動作
        mUserEditImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 選択肢の配列
                final String[] items = {
                        getResources().getString(R.string.user_image_take_photo),
                        getResources().getString(R.string.user_image_select_photo)
                };

                // ダイアログの表示
                new AlertDialog.Builder(UserActivity.this)
                        .setTitle(getResources().getString(R.string.user_image_title))
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {

                                switch (position) {

                                    // 「写真を撮る」を選択時
                                    case 0:

                                        // パーミッションが許可されていない場合
                                        if (ContextCompat.checkSelfPermission(UserActivity.this,
                                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                                            // パーミッションの許可を行うダイアログを表示
                                            // （許可 or 許可しない で onRequestPermissionsResultが呼ばれる）
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                                            }
                                        }
                                        // パーミッションが許可されている場合
                                        else {
                                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            startActivityForResult(cameraIntent, CAMERA_PERMISSION_REQUEST_CODE);
                                        }
                                        break;

                                    // 「ライブラリから選択」を選択時
                                    case 1:
                                        // 端末に保存されている画像を取得する
                                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);   // 暗黙的インテントの作成
                                        galleryIntent.setType("image/*");
                                        startActivityForResult(
                                                Intent.createChooser(galleryIntent, "画像を選択します"),
                                                FILE_PERMISSION_REQUEST_CODE); // 起動したアクティビティを特定する定数
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), null)
                        .show();
            }
        });

        // 手動入力を禁止
        mUserGenderEditText.setKeyListener(null);
        mUserAgeEditText.setKeyListener(null);

        // 年代がタップされた場合
        mUserAgeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    final String[] ages = {"10代", "20代", "30代", "40代", "50代", "60代", "70代", "80代"};
                    new AlertDialog.Builder(UserActivity.this)
                            .setItems(ages, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mUserAgeEditText.setText(ages[which]);
                                }
                            })
                            .show();
                }
            }
        });

        // 性別がタップされた場合
        mUserGenderEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    final String[] genders = {"男性", "女性"};
                    new AlertDialog.Builder(UserActivity.this)
                            .setItems(genders, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mUserGenderEditText.setText(genders[which]);
                                }
                            })
                            .show();
                }
            }
        });

        // お気に入り追加画面が開いているときは、背景のタップを出来ないように設定
        mAddFavoriteLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isShownAddFavorite;
            }
        });

        // ユーザー情報編集画面が開いているときは、背景のタップを出来ないように設定
        mEditUserLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isShownEditUserSetting;
            }
        });
    }

    // お気に入りのハートのアイコンを押した時の処理の内容
    private View.OnClickListener tapFavIcon = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isShownAddFavorite) {
                switch (view.getId()) {
                    case R.id.favIcon1:
                        mFavIcon2.setImageResource(R.drawable.ic_heart_blank);
                        mFavIcon3.setImageResource(R.drawable.ic_heart_blank);
                        mFavIcon4.setImageResource(R.drawable.ic_heart_blank);
                        mFavIcon5.setImageResource(R.drawable.ic_heart_blank);
                        mFavoriteLevelTextView.setText("1.0");
                        break;
                    case R.id.favIcon2:
                        mFavIcon2.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon3.setImageResource(R.drawable.ic_heart_blank);
                        mFavIcon4.setImageResource(R.drawable.ic_heart_blank);
                        mFavIcon5.setImageResource(R.drawable.ic_heart_blank);
                        mFavoriteLevelTextView.setText("2.0");
                        break;
                    case R.id.favIcon3:
                        mFavIcon2.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon3.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon4.setImageResource(R.drawable.ic_heart_blank);
                        mFavIcon5.setImageResource(R.drawable.ic_heart_blank);
                        mFavoriteLevelTextView.setText("3.0");
                        break;
                    case R.id.favIcon4:
                        mFavIcon2.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon3.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon4.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon5.setImageResource(R.drawable.ic_heart_blank);
                        mFavoriteLevelTextView.setText("4.0");
                        break;
                    case R.id.favIcon5:
                        mFavIcon2.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon3.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon4.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon5.setImageResource(R.drawable.ic_heart_fill);
                        mFavoriteLevelTextView.setText("5.0");
                        break;
                    default:
                        break;
                }
            }
        }
    };

    // ユーザー情報変更ボタンを押した場合の処理
    public void onUserEditButtonClicked(View view) {
        if(MyUtil.Network.isEnable(this)) {
            // 登録するユーザー情報
            String gender = "male";
            if (mUserGenderEditText.getText().toString().equals("女性")) gender = "female";
            User editUser = new User(
                    mUserNameEditText.getText().toString(),
                    gender,
                    Integer.parseInt(mUserAgeEditText.getText().toString().replace("代", "")),
                    MyUtil.Transform.bmpToDataUriScheme(MyUtil.App.loadBitmapFromInternalStorage(this, "userImage"), 100),
                    null
            );

            // ダイアログを閉じる
            closeEditUserDialog();

            // 読み込みダイアログを表示
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.main_progress_message));
            progressDialog.show();

            // ユーザー情報を登録
            new Favor(new Favor.AsyncCallback() {
                @Override
                public void onSuccess(Object object) {
                    User user = (User) object;

                    // 読み込みダイアログを非表示
                    progressDialog.dismiss();

                    // ユーザー情報のセット
                    String userGender = getString(R.string.user_gender_male);
                    if (!Objects.equals(user.getGender(), "male"))
                        userGender = getString(R.string.user_gender_female);
                    mUserNameTextView.setText(user.getNickname());
                    mUserGenderTextView.setText(userGender);
                    mUserAgeTextView.setText(String.valueOf(user.getAge()) + "代");
                    if(user.getImageUrl() != null) {
                        Picasso.with(UserActivity.this).load(user.getImageUrl())
                                .transform(new CropCircleTransformation())
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(mUserEditImageView);
                        Picasso.with(UserActivity.this).load(user.getImageUrl())
                                .transform(new CropCircleTransformation())
                                .into(mUserImageView);
                    } else {
                        Picasso.with(UserActivity.this).load(R.drawable.ic_user_prof)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(mUserEditImageView);
                        Picasso.with(UserActivity.this).load(R.drawable.ic_user_prof).into(mUserImageView);
                    }
                }

                @Override
                public void onFailure(FavorException e) {
                    Log.d("onFailure", e.toString());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // 読み込みダイアログを非表示
                            progressDialog.dismiss();

                            new SimpleAlertDialog(UserActivity.this, getString(R.string.error_common)).show();
                        }
                    });
                }
            }).setContext(UserActivity.this).setUser(editUser).execute(Favor.Task.EditUser);
        } else {
            new SimpleAlertDialog(this, getString(R.string.error_network_disable)).show();
        }
    }

    // ユーザー画像選択から選択された画像を受け取り
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        // 画像読み込みからの呼び出し または カメラからの呼び出し
        if (( requestCode == FILE_PERMISSION_REQUEST_CODE || requestCode == CAMERA_PERMISSION_REQUEST_CODE )
                && resultCode == Activity.RESULT_OK) {

            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();

                // 画像リサイズ用の画面へ移動
                Intent intent = new Intent(UserActivity.this, CropUserImageActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NO_HISTORY);  // スタックには追加しない
                intent.putExtra("imageUri", uri);           // 画像データを渡す
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(isShownAddFavorite) {
                closeFavoriteDialog();
            } else if(isShownEditUserSetting) {
                closeEditUserDialog();
            } else {
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        return false;
    }

    // お気に入り追加画面を表示
    private void openFavoriteDialog() {
        mAddFavoriteLayout.setVisibility(View.VISIBLE);
        mAddFavoriteLayout.setAnimation(AnimationUtils.loadAnimation(UserActivity.this, R.anim.fade_in_slowly));
        isShownAddFavorite = true;
    }

    // お気に入り追加画面を非表示
    private void closeFavoriteDialog() {

        final AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(AnimationUtils.loadAnimation(UserActivity.this, R.anim.fade_out_slowly));
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            // アニメーションを終了した後の処理
            @Override
            public void onAnimationEnd(Animation animation) {
                mAddFavoriteLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        mAddFavoriteLayout.setVisibility(View.INVISIBLE);
        mAddFavoriteLayout.setAnimation(animationSet);
        isShownAddFavorite = false;
    }

    // ユーザー情報編集画面を表示
    private void openEditUserDialog() {
        mEditUserLayout.setVisibility(View.VISIBLE);
        mEditUserLayout.setAnimation(AnimationUtils.loadAnimation(UserActivity.this, R.anim.fade_in_slowly));
        isShownEditUserSetting = true;
    }

    // ユーザー情報編集画面を非表示
    private void closeEditUserDialog() {

        final AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(AnimationUtils.loadAnimation(UserActivity.this, R.anim.fade_out_slowly));
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            // アニメーションを終了した後の処理
            @Override
            public void onAnimationEnd(Animation animation) {
                mAddFavoriteLayout.setVisibility(View.GONE);
                MyUtil.SharedPref.saveString(UserActivity.this, "userImage", null);
                if(mUser.getImageUrl() != null) {
                    Picasso.with(UserActivity.this).load(mUser.getImageUrl())
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .transform(new CropCircleTransformation())
                            .into(mUserEditImageView);
                } else {
                    Picasso.with(UserActivity.this).load(R.drawable.ic_user_prof)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(mUserEditImageView);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        mEditUserLayout.setVisibility(View.INVISIBLE);
        mEditUserLayout.setAnimation(animationSet);
        isShownEditUserSetting = false;
    }

}
