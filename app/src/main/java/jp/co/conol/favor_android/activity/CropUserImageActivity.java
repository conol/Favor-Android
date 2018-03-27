package jp.co.conol.favor_android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.isseiaoki.simplecropview.CropImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.conol.favor_android.MyUtil;
import jp.co.conol.favor_android.R;


public class CropUserImageActivity extends AppCompatActivity {

    @BindView(R.id.cropImageView) CropImageView mCropImageView;
    @BindView(R.id.cropImageButton) Button mCropImageButton;
    @BindView(R.id.rotateLeftButton) ImageView mRotateLeftButton;
    @BindView(R.id.rotateRightButton) ImageView mRotateRightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_user_image);
        ButterKnife.bind(this);

        // TopicsRecyclerAdapterからインテントを受け取り
        Intent intent = getIntent();

        // インテントから画像データを取得
        final Uri imageUri = intent.getParcelableExtra("imageUri");

        // 画像データを表示
        if (imageUri != null) {
            Picasso.with(CropUserImageActivity.this)
                    .load(imageUri)
                    .fit()
                    .centerInside()
                    .into(mCropImageView);
        }

        // 設定するボタンを押した場合
        mCropImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // リサイズされた画像データ
                Bitmap cropImage = mCropImageView.getCroppedBitmap();
                Bitmap resizedImage = MyUtil.Transform.resizeBitmap(cropImage, 100, 100);

                // 画像データを内部ストレージに保存
                MyUtil.App.saveBitmapToInternalStorage(CropUserImageActivity.this, resizedImage, "userImage", 100);

                // トップページのインテント作成
                Intent intent = new Intent(CropUserImageActivity.this, UserActivity.class);
                intent.putExtra("isBackCropUserImageActivity", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   // 以前のActivityのスタックを削除（移動完了時）
                startActivity(intent);
            }
        });

        // 左回転ボタンがクリックされた時
        mRotateLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 左回転
                mCropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
            }
        });

        // 右回転ボタンがクリックされた時
        mRotateRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 右回転
                mCropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });
    }
}