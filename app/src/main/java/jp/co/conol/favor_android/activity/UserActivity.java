package jp.co.conol.favor_android.activity;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.UserFragmentStatePagerAdapter;
import jp.co.conol.favorlib.Util;
import jp.co.conol.favorlib.favor.Favor;
import jp.co.conol.favorlib.favor.model.Favorite;
import jp.co.conol.favorlib.favor.model.User;

public class UserActivity extends AppCompatActivity {

    private UserFragmentStatePagerAdapter mUserFragmentStatePagerAdapter;
    private boolean isShownAddFavorite = false;
    @BindView(R.id.userNameTextView) TextView mUserNameTextView;
    @BindView(R.id.userGenderTextView) TextView mUserGenderTextView;
    @BindView(R.id.userAgeTextView) TextView mUserAgeTextView;
    @BindView(R.id.userTabLayout) TabLayout mUserTabLayout;
    @BindView(R.id.userViewPager) ViewPager mUserViewPager;
    @BindView(R.id.addUserFavoriteFloatingActionButton) FloatingActionButton mAddUserFavoriteFloatingActionButton;
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
        final User user = gson.fromJson(MyUtil.SharedPref.get(this, "userSetting"), User.class);

        // ユーザー情報のセット
        mUserNameTextView.setText(user.getNickname());
        mUserGenderTextView.setText(user.getGender());
        mUserAgeTextView.setText(String.valueOf(user.getAge()));

        // FABを押した場合、お気に入り追加画面を表示
        mAddUserFavoriteFloatingActionButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mAddFavoriteLayout.setVisibility(View.VISIBLE);
                    isShownAddFavorite = true;
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

                    if (!Util.Str.isBlank(mAddFavoriteEditText.getText().toString())) {
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
                                mFavIcon2.setImageResource(R.drawable.ic_healt_blank);
                                mFavIcon3.setImageResource(R.drawable.ic_healt_blank);
                                mFavIcon4.setImageResource(R.drawable.ic_healt_blank);
                                mFavIcon5.setImageResource(R.drawable.ic_healt_blank);
                                mFavoriteLevelTextView.setText("1.0");
                                mAddFavoriteEditText.setText("");
                                mAddFavoriteLayout.setVisibility(View.GONE);
                                isShownAddFavorite = false;
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e("onFailure", e.toString());
                            }
                        }).setAppToken(user.getAppToken()).setFavorite(favorite).execute(Favor.Task.AddFavorite);
                    } else {
                        Toast.makeText(UserActivity.this, getString(R.string.add_favorite_error), Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        // お気に入り追加画面が開いているときは、背景のタップを出来ないように設定
        mAddFavoriteLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isShownAddFavorite;
            }
        });
    }

    // お気に入りのハートのアイコンを押した時の処理の内容
    private View.OnTouchListener tapFavIcon = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {

                switch (view.getId()) {
                    case R.id.favIcon1:
                        mFavIcon2.setImageResource(R.drawable.ic_healt_blank);
                        mFavIcon3.setImageResource(R.drawable.ic_healt_blank);
                        mFavIcon4.setImageResource(R.drawable.ic_healt_blank);
                        mFavIcon5.setImageResource(R.drawable.ic_healt_blank);
                        mFavoriteLevelTextView.setText("1.0");
                        break;
                    case R.id.favIcon2:
                        mFavIcon2.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon3.setImageResource(R.drawable.ic_healt_blank);
                        mFavIcon4.setImageResource(R.drawable.ic_healt_blank);
                        mFavIcon5.setImageResource(R.drawable.ic_healt_blank);
                        mFavoriteLevelTextView.setText("2.0");
                        break;
                    case R.id.favIcon3:
                        mFavIcon2.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon3.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon4.setImageResource(R.drawable.ic_healt_blank);
                        mFavIcon5.setImageResource(R.drawable.ic_healt_blank);
                        mFavoriteLevelTextView.setText("3.0");
                        break;
                    case R.id.favIcon4:
                        mFavIcon2.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon3.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon4.setImageResource(R.drawable.ic_heart_fill);
                        mFavIcon5.setImageResource(R.drawable.ic_healt_blank);
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
            return false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(isShownAddFavorite) {
                mAddFavoriteLayout.setVisibility(View.GONE);
                isShownAddFavorite = false;
            } else {
                finish();
            }
        }
        return false;
    }

}
