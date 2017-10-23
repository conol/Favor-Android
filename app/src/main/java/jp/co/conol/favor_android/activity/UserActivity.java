package jp.co.conol.favor_android.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.UserFragmentStatePagerAdapter;
import jp.co.conol.favorlib.favor.model.User;

public class UserActivity extends AppCompatActivity {

    private UserFragmentStatePagerAdapter mUserFragmentStatePagerAdapter;
    private TextView mUserNameTextView;
    private TextView mUserGenderTextView;
    private TextView mUserAgeTextView;
    private TabLayout mUserTabLayout;
    private ViewPager mUserViewPager;
    private FloatingActionButton mAddUserFavoriteFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mUserNameTextView = (TextView) findViewById(R.id.userNameTextView);
        mUserGenderTextView = (TextView) findViewById(R.id.userGenderTextView);
        mUserAgeTextView = (TextView) findViewById(R.id.userAgeTextView);
        mUserTabLayout = (TabLayout) findViewById(R.id.userTabLayout);
        mUserViewPager = (ViewPager) findViewById(R.id.userViewPager);
        mAddUserFavoriteFloatingActionButton = (FloatingActionButton) findViewById(R.id.addUserFavoriteFloatingActionButton);

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
        User user = gson.fromJson(MyUtil.SharedPref.get(this, "userSetting"), User.class);

        // ユーザー情報のセット
        mUserNameTextView.setText(user.getNickname());
        mUserGenderTextView.setText(user.getGender());
        mUserAgeTextView.setText(String.valueOf(user.getAge()));
    }
}
