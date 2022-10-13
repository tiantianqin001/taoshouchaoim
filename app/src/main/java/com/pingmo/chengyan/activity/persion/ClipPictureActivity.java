package com.pingmo.chengyan.activity.persion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.gyf.immersionbar.ImmersionBar;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.customview.ClipView;
import com.pingmo.chengyan.utils.PicUtil;
import com.pingmo.chengyan.utils.QZXTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ClipPictureActivity extends BaseActivity implements
        OnTouchListener, OnClickListener {
    public static final String TAG = "ClipPictureActivity";
    public static final String IMAGE_PATH_ORIGINAL = "image_path_original";
    public static final String IMAGE_PATH_AFTER_CROP = "image_path_after_crop";

    //剪裁框的宽高
    public static final String CLIP_WIDTH = "clip_width";
    public static final String CLIP_HEIGHT = "clip_height";
    //保存文件的宽高
    public static final String SAVED_WIDTH = "saved_width";
    public static final String SAVED_HEIGHT = "saved_height";
    private int clip_width;
    private int clip_height;

    private ImageView srcPic;
    private ClipView clipview;
    private String croppedImagePath;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    /**
     * 动作标志：无
     */
    private static final int NONE = 0;
    /**
     * 动作标志：拖动
     */
    private static final int DRAG = 1;
    /**
     * 动作标志：缩放
     */
    private static final int ZOOM = 2;
    /**
     * 初始化动作标志
     */
    private int mode = NONE;

    /**
     * 记录起始坐标
     */
    private PointF start = new PointF();
    /**
     * 记录缩放时两指中间点坐标
     */
    private PointF mid = new PointF();
    private float oldDist = 1f;

    private Bitmap bitmap;
    /**
     * 剪裁后保存到文件图片的宽高
     * 以头像上传的值为默认值
     * 头像最终上传的尺寸--butel中该值从dimens中读取，目前设置为480*480px
     */
    private int uploadIconWidth;
    private int uploadIconHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QZXTools.logD("BEGIN:::");

        setContentView(R.layout.clip_picture);
        initView();

        QZXTools.logD("END:::");
    }

    private void initView() {
        QZXTools.logD("BEGIN:::");
        srcPic = this.findViewById(R.id.src_pic);
        srcPic.setOnTouchListener(this);
        addActivity(this);
        ViewTreeObserver observer = srcPic.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                srcPic.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initClipView(srcPic.getTop());
            }
        });

        getTitleBar().enableBack();
        getTitleBar().setTitle("裁剪");
        getTitleBar().enableRightBtn("完成", this);
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());

        uploadIconWidth = getResources().getDimensionPixelSize(
                R.dimen.qb_px_480);
        uploadIconHeight = getResources().getDimensionPixelSize(
                R.dimen.qb_px_480);

        uploadIconWidth = getIntent().getIntExtra(SAVED_WIDTH, uploadIconWidth);
        uploadIconHeight = getIntent().getIntExtra(SAVED_HEIGHT, uploadIconHeight);

        QZXTools.logD("uploadIconX-x:" + uploadIconWidth + "\n uploadIconHeight="
                + uploadIconHeight);

        QZXTools.logD("END:::");
    }


    public static int calculateInSampSize(BitmapFactory.Options options,
                                          int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            inSampleSize = ((Double) Math.floor(height * 1.0f / reqHeight))
                    .intValue();
            int wSampSize = ((Double) Math.floor(width * 1.0f / reqWidth))
                    .intValue();
            if (inSampleSize < wSampSize) {
                inSampleSize = wSampSize;
            }
        }
        QZXTools.logD("inSampleSize:" + inSampleSize);
        return inSampleSize;
    }

    /**
     * 获取本地文件的宽高，用‘，’分割，宽在前 如720,360
     *
     * @param filepath 本地文件的路径
     * @return
     */
    public static String getImageFileWidthAndHeight(String filepath) {
        String widthAndHeight = "";
        if (!TextUtils.isEmpty(filepath)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
            widthAndHeight = options.outWidth + "," + options.outHeight;
            bitmap = null;
            QZXTools.logD("decode image File:" + options.outWidth + "|" + options.outHeight);
        }
        return widthAndHeight;
    }

    /**
     * Decode and sample down a bitmap from a file to the requested width and height.
     *
     * @param filename  The full path of the file to decode
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     * that are equal to or greater than the requested width and height
     */
    public static Bitmap decodeSampledBitmapFromFile(String filename,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);
        QZXTools.logD("before decode image File:" + options.outWidth + "|" + options.outHeight);

        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inSampleSize = calculateInSampSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Config.RGB_565;
        try {
            Bitmap val = BitmapFactory.decodeFile(filename, options);
            QZXTools.logD("after decode image File:" + options.outWidth + "|" + options.outHeight);
            return val;
        } catch (Exception e) {
            Log.e(TAG, "decodeSampledBitmapFromFile - Exception"
                    + e);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "decodeSampledBitmapFromFile - OutOfMemoryError"
                    + e);
        } finally {

        }
        return null;
    }

    /**
     * 初始化截图区域，并将源图按裁剪框比例缩放
     *
     * @param top
     */
    private void initClipView(int top) {
        QZXTools.logD("BEGIN:::");
        Intent intent = getIntent();
        final String originalImgPath = intent
                .getStringExtra(IMAGE_PATH_ORIGINAL);
        croppedImagePath = intent.getStringExtra(IMAGE_PATH_AFTER_CROP);
        clip_width = intent.getIntExtra(CLIP_WIDTH, -1);
        clip_height = intent.getIntExtra(CLIP_HEIGHT, -1);


        // 首先判断源文件是否存在--防止垃圾数据的影响：一张图片在SD卡上已经被删除，但是媒体库中还有该数据。
        // 在图库中还能看到不能正常显示的一张图片--2015/9/7
        File file = new File(originalImgPath);
        if (!file.exists()) {

            Toast.makeText(this, "源文件在SD卡上不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        int reqWidth = PicUtil.getDeviceSize(this).x;
        int reqHeight = PicUtil.getDeviceSize(this).y;
        // 取bitmap的时候就压缩照片，同时解决了照片过大(>2048*2048px)的硬件加速问题(渲染不了)
        // 和图片过大(20M),内存溢出问题
        QZXTools.logD("originalImgPath:" + originalImgPath);
        bitmap = decodeSampledBitmapFromFile(originalImgPath,
                reqWidth, reqHeight);

        // 2015-9-7
        if (bitmap == null) {
            Toast.makeText(this, "不是合法的图片文件，请重新选择图片", Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }
        int rotation = PicUtil.getImageRotationByPath(
                ClipPictureActivity.this, originalImgPath);
        // 取出照片角度，解决某些手机上照片显示角度问题
        if (rotation != 0) {
            Matrix m = new Matrix();
            m.setRotate(rotation);
            try {
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), m, true);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }

        // 因为某些不规范操作导致出现0bit的图片，使得bitmap==null，抛出NullPointerException
        if (bitmap == null) {
            Toast.makeText(this, "没有图片", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        clipview = new ClipView(ClipPictureActivity.this);
        // 头像裁剪框与背景裁剪框不同
        //头像-正方形；背景框--矩形
        clipview.setClipWidth(clip_width);
        clipview.setClipHeight(clip_height);
        clipview.setCustomTopBarHeight(top);
        clipview.addOnDrawCompleteListener(new ClipView.OnDrawListenerComplete() {

            @Override
            public void onDrawCompelete() {
                clipview.removeOnDrawCompleteListener();
                int clipHeight = clipview.getClipHeight();
                int clipWidth = clipview.getClipWidth();
                int midX = clipview.getClipLeftMargin() + (clipWidth / 2);
                int midY = clipview.getClipTopMargin() + (clipHeight / 2);

                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();
                // 按裁剪框求缩放比例
                float scale = (clipWidth * 1.0f) / imageWidth;
                //4月14日 扬州华要求图片压缩，以宽度填充优先，首先将宽度填满，所以注销下面代码。
//                if (imageWidth > imageHeight) {
//                    scale = (clipHeight * 1.0f) / imageHeight;
//                }

                // 起始中心点
                float imageMidX = imageWidth * scale / 2;
                float imageMidY = clipview.getCustomTopBarHeight()
                        + imageHeight * scale / 2;
                srcPic.setScaleType(ScaleType.MATRIX);

                // 缩放
                matrix.postScale(scale, scale);
                // 平移
                matrix.postTranslate(midX - imageMidX, midY - imageMidY);

                srcPic.setImageMatrix(matrix);
                srcPic.setImageBitmap(bitmap);
            }
        });

        matrix.reset();
        this.addContentView(clipview, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        QZXTools.logD("END:::");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // 设置开始点位置
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
            default:
                break;
        }
        view.setImageMatrix(matrix);
        return true;
    }

    /**
     * 多点触控时，计算最先放下的两指距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 多点触控时，计算最先放下的两指中心坐标
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * @return void
     * @Title: saveMyBitmap
     * @Description: 保存bitmap对象到文件中 此方法是从SettingFragment中copy过来的
     * 剪切完成，将剪切后的照片保存到指定位置以供上传-wxy 2015-3-10
     */
    public static void saveMyBitmap(File file, Bitmap mBitmap) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 60, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return 裁剪后的图片
     * @description 获取裁剪框内截图
     */
    private Bitmap getBitmap() {
        // 获取截屏
        View view = this.getWindow().getDecorView();
        // view.setDrawingCacheEnabled(true);
        // view.buildDrawingCache();

        try {
            // view.getDrawingCache()在某些情况下报错了。现在用一种新的
            // 获取view中图像的方法取代getDrawingCache()方法.另：在使用createBitmap()
            // 增加try..catch..以防止不断生成bitmap可能导致的oom
            Bitmap finalBitmap = Bitmap.createBitmap(
                    loadBitmapFromView(view, true),
                    clipview.getClipLeftMargin(), clipview.getClipTopMargin(), clipview.getClipWidth(),
                    clipview.getClipHeight());
            // Bitmap finalBitmap = Bitmap.createBitmap(view.getDrawingCache(),
            // clipview.getClipLeftMargin(), clipview.getClipTopMargin()
            // + statusBarHeight, clipview.getClipWidth(),
            // clipview.getClipHeight());
            // 释放资源
            view.destroyDrawingCache();
            return finalBitmap;
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            Toast.makeText(this, "上传头像失败", Toast.LENGTH_SHORT).show();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "上传头像失败!", Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    public Bitmap loadBitmapFromView(View v, boolean isParemt) {
        if (v == null) {
            return null;
        }
        try {
            Bitmap screenshot;
            screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                    Config.ARGB_8888);
            Canvas c = new Canvas(screenshot);
            v.draw(c);
            return screenshot;
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        clipPicture();
    }

    private void clipPicture() {
        if (bitmap != null) {
            // 取出裁剪图片
            Bitmap clipBitmap = getBitmap();
            Log.d(TAG, "裁剪照片尺寸：clip-x=" + clipBitmap.getWidth() + ",clip-y="
                    + clipBitmap.getHeight());
            File file = new File(croppedImagePath);
            // 裁剪后的图片再缩放，生成满足上传条件的头像,butel中需求是480*480
            Bitmap uploadBitmap = scaleBitmap(clipBitmap, uploadIconWidth,
                    uploadIconHeight);
            Log.d(TAG, "上传头像尺寸：upload-x=" + uploadBitmap.getWidth()
                    + ",upload-y=" + uploadBitmap.getHeight());
            saveMyBitmap(file, uploadBitmap);
        }

        Intent intent = new Intent();
        ClipPictureActivity.this.setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * @param bitmap
     * @return 返回大小为widthPix*heightPix的头像
     * @description 将bitmap的尺寸缩放到widthPix*heightPix
     */
    private Bitmap scaleBitmap(Bitmap bitmap, int widthPix, int heightPix) {

        Matrix sMatrix = new Matrix();
        // 图片缩放比
        double widthRatio = (widthPix * 1.0) / bitmap.getWidth();
        double heightRatio = (heightPix * 1.0) / bitmap.getHeight();
        sMatrix.postScale((float) widthRatio, (float) heightRatio);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), sMatrix, true);
        return resizeBmp;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }

}