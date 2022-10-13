package com.pingmo.chengyan.activity.zxing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.utils.PicUtil;
import com.pingmo.chengyan.utils.QZXTools;
import com.tencent.qcloud.tuikit.tuicontact.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import androidx.appcompat.app.AppCompatActivity;
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class ScanGeneratectivity extends AppCompatActivity {
    private ImageView mChineseIv;
    private TextView name;
    private String userId;
    private View mRwm;

    private boolean creatingQRCodeFile = false;
    private String localFilePath = "";
    private String photo;
    private String nameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        setContentView(R.layout.activity_test_generate);
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        photo = intent.getStringExtra("photo");
        nameTxt = intent.getStringExtra("nameTxt");

        initView();
        createQRCode();
    }

    private void initView() {
        mRwm = findViewById(R.id.rwm);
        mChineseIv = findViewById(R.id.iv_chinese);
        name = findViewById(R.id.name);
        ImageView iv_heade_image = findViewById(R.id.iv_heade_image);
        if (!TextUtils.isEmpty(photo)) {
            Glide.with(this)
                    .asBitmap()
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .error(getResources().getDrawable(R.drawable.core_default_user_icon_serious))
                    .load(photo)
                    .into(iv_heade_image);
        }
        if (!TextUtils.isEmpty(nameTxt)) {
            name.setText(nameTxt);
        }

        RelativeLayout rl_generate_back = findViewById(R.id.rl_generate_back);
        rl_generate_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //保存
        TextView tv_generate_save = findViewById(R.id.tv_generate_save);
        tv_generate_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!creatingQRCodeFile) {
                    viewSaveToImage(mRwm);
                }
            }
        });
    }

    private void createQRCode() {
        createChineseQRCode();
    }

    private void createChineseQRCode() {
        /*
        这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
        请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github
        .com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
         */
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return QRCodeEncoder.syncEncodeQRCode("user_info?u="+userId, BGAQRCodeUtil.dp2px(ScanGeneratectivity.this, 150));
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    mChineseIv.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(ScanGeneratectivity.this, "生成中文二维码失败", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    public void decodeChinese(View v) {
        mChineseIv.setDrawingCacheEnabled(true);
        Bitmap bitmap = mChineseIv.getDrawingCache();
        decode(bitmap, "解析中文二维码失败");
    }

    private void decode(final Bitmap bitmap, final String errorTip) {
        /*
        这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
        请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github
        .com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
         */
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return QRCodeDecoder.syncDecodeQRCode(bitmap);
            }

            @Override
            protected void onPostExecute(String result) {
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(ScanGeneratectivity.this, errorTip, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ScanGeneratectivity.this, result, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void getQRCodeFilePath() {
        if (TextUtils.isEmpty(localFilePath)) {
            localFilePath = PicUtil.getDir("inforPicFolder") + "ewm" + System.currentTimeMillis() + ".png";
        }
        QZXTools.logD("localFilePath:" + localFilePath);
    }


    private void viewSaveToImage(View view) {
        creatingQRCodeFile = true;
        Bitmap bitmap = getBitmapFromView(view);
        if (bitmap != null) {
            bitmap = getRoundedCornerBitmap(bitmap, 0);
            if (bitmap != null) {
                saveSrcImg(bitmap);
                bitmap.recycle();
                bitmap = null;
            }
        }
        creatingQRCodeFile = false;
    }

    public static Bitmap getBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        // Draw background
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(c);
        } else {
            c.drawColor(Color.WHITE);
        }
        // Draw view to canvas
        v.draw(c);
        return b;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private void saveSrcImg(Bitmap bitmap) {
        try {
            getQRCodeFilePath();
            FileOutputStream fos = new FileOutputStream(localFilePath);
            //输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            savePosterToSD();
        } catch (Exception e) {
            e.printStackTrace();
            QZXTools.logD("保存失败");
            deleteQrCodeFile(localFilePath);
        }
    }

    private boolean deleteQrCodeFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file != null && file.exists()) {
                return file.delete();
            }
        }
        return false;
    }

    private void savePosterToSD() {
        if (!TextUtils.isEmpty(localFilePath) && new File(localFilePath).exists()) {
            String filePath = PicUtil.getPublicDirByType(Environment.DIRECTORY_PICTURES, "inforPicFolder") + File.separator + "ewm" + System.currentTimeMillis() + ".jpg";
            saveLocalFile(filePath);
        }
    }

    private void saveLocalFile(String filePath) {
        if (!TextUtils.isEmpty(localFilePath) && !TextUtils.isEmpty(filePath)) {
            QZXTools.logD(
                    "saveLocalFile 源文件路径：" + localFilePath + "|" + "目标文件路径：" + filePath);
            OutputStream os = null;
            FileInputStream in = null;
            try {
                os = new FileOutputStream(filePath);
                in = new FileInputStream(localFilePath);
                byte[] buffer = new byte[1024];
                int c = -1;
                if (in != null) {
                    while ((c = in.read(buffer)) > 0) {
                        os.write(buffer, 0, c);
                    }
                }
                os.flush();
                // 发广播刷新系统媒体库
                PicUtil.scanFileAsync(ScanGeneratectivity.this, filePath);
                ToastUtils.show("保存成功");
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                ToastUtils.show("保存失败");
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.show("保存失败");
            } finally {
                try {
                    if (in != null) {
                        in.close();
                        os = null;
                    }
                    if (os != null) {
                        os.close();
                        os = null;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    ToastUtils.show("保存失败");
                }
            }
        }
    }

}