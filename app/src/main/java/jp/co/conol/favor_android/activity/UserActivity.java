package jp.co.conol.favor_android.activity;

import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.UserFragmentStatePagerAdapter;
import jp.co.conol.favor_android.fragment.UserFavoriteFragment;
import jp.co.conol.favorlib.cuona.Favor;
import jp.co.conol.favorlib.cuona.favor_model.Favorite;
import jp.co.conol.favorlib.cuona.favor_model.User;

public class UserActivity extends AppCompatActivity {

    private UserFragmentStatePagerAdapter mUserFragmentStatePagerAdapter;
    private User mUser;
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

        // ユーザー情報を取得
        Gson gson = new Gson();
        mUser = gson.fromJson(MyUtil.SharedPref.getString(this, "userSetting"), User.class);

        // ユーザー情報のセット
        String userGender = getString(R.string.user_gender_male);
        if(!Objects.equals(mUser.getGender(), "male")) userGender = getString(R.string.user_gender_female);
        mUserNameTextView.setText(mUser.getNickname());
        mUserNameEditText.setText(mUser.getNickname());
        mUserGenderTextView.setText(userGender);
        mUserGenderEditText.setText(userGender);
        mUserAgeTextView.setText(String.valueOf(mUser.getAge()) + "代");
        mUserAgeEditText.setText(String.valueOf(mUser.getAge()));

        // FABを押した場合、お気に入り追加画面を表示
        mAddUserFavoriteFloatingActionButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    openFavoriteDialog();
                }
                return false;
            }
        });

        // お気に入りのハートのアイコンを押した時の処理
        mFavIcon1.setOnTouchListener(tapFavIcon);
        mFavIcon2.setOnTouchListener(tapFavIcon);
        mFavIcon3.setOnTouchListener(tapFavIcon);
        mFavIcon4.setOnTouchListener(tapFavIcon);
        mFavIcon5.setOnTouchListener(tapFavIcon);

        // 「お気に入りを追加する」ボタンをタップした場合
        mAddFavoriteButtonConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
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
                                if(fragment != null) {
                                    ((UserFavoriteFragment) fragment).notifyDataChanged(favorite);
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e("onFailure", e.toString());
                            }
                        }).setAppToken(mUser.getAppToken()).setFavorite(favorite).execute(Favor.Task.AddFavorite);
                    } else {
                        Toast.makeText(UserActivity.this, getString(R.string.add_favorite_error), Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        // ユーザー情報タップでユーザー情報編集画面を表示
        mUserProfConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    openEditUserDialog();
                }
                return false;
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
                    final String[] ages = {"20", "30", "40", "50", "60", "70"};
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
    private View.OnTouchListener tapFavIcon = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {

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
            return false;
        }
    };

    // ユーザー情報変更ボタンを押した場合の処理
    public void onUserEditButtonClicked(View view) {

        // 登録するユーザー情報
        String gender = "male";
        if(mUserGenderEditText.getText().toString().equals("女性")) gender = "female";
        User editUser = new User(
                mUserNameEditText.getText().toString(),
                gender,
                Integer.parseInt(mUserAgeEditText.getText().toString()),
                null,
                null
        );

        // ユーザー情報のセット
        String userGender = getString(R.string.user_gender_male);
        if(!Objects.equals(editUser.getGender(), "male")) userGender = getString(R.string.user_gender_female);
        mUserNameTextView.setText(editUser.getNickname());
        mUserGenderTextView.setText(userGender);
        mUserAgeTextView.setText(String.valueOf(editUser.getAge()) + "代");

        // ダイアログを閉じる
        closeEditUserDialog();

        // ユーザー情報を登録
        new Favor(new Favor.AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                User user = (User) object;

                // ユーザー情報を端末に保存
                Gson gson = new Gson();
                MyUtil.SharedPref.saveString(UserActivity.this, "userSetting", gson.toJson(user));
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("onFailure", e.toString());
            }
        }).setAppToken(mUser.getAppToken()).setUser(editUser).execute(Favor.Task.EditUser);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(isShownAddFavorite) {
                closeFavoriteDialog();
            } else if(isShownEditUserSetting) {
                closeEditUserDialog();
            } else {
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
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        mEditUserLayout.setVisibility(View.INVISIBLE);
        mEditUserLayout.setAnimation(animationSet);
        isShownEditUserSetting = false;
    }

}
