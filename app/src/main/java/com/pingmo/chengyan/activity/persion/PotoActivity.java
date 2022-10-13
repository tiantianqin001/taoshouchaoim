package com.pingmo.chengyan.activity.persion;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.bean.LoginBean;
import com.pingmo.chengyan.bean.UploadBean;
import com.pingmo.chengyan.glide.GlideEngine;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.MD5Utils;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.PicUtil;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;
import com.tencent.qcloud.tuicore.TUILogin;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PotoActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PotoActivity";
    private static final int ACTIVITY_REQUEST_TAKE_PHOTO = 1;
    private static final int ACTIVITY_REQUEST_PHOTO_PICKED_WITH_DATA = 2;
    private static final int SAVE_SUCC = 3;//保存成功
    private static final int SAVE_FAILED = 4;//保存失败
    private static final String FILE_PROVIDER_AUTHORITY = "com.pingmo.chengyan.fileprovider";

    public static final int UPLOAD_SUCCESS = 0x1001;
    public static final int UPLOAD_FAILED = 0x1002;
    public static final int MODIFY_SUCCESS = 0x1003;
    public static final int MODIFY_FAILED = 0x1004;

    private Dialog bottomDialog;
    private ImageView iv_sheck_image;
    private Uri headIconUri;
    private File croppedIconFile = null;

    private String filePath = "";

    private LoginBean.DataDTO user;
    private Uri mImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_layout);
        ImmersionBar.setTitleBar(this, findViewById(R.id.title));
        iv_sheck_image = findViewById(R.id.iv_sheck_image);
        findViewById(R.id.ll_get_photo).setOnClickListener(this);
        findViewById(R.id.ll_base_back).setOnClickListener(this);
        String userInfo = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("user_info");
        addActivity(this);
        if (!TextUtils.isEmpty(userInfo)) {
            user = JSON.parseObject(userInfo, LoginBean.DataDTO.class);
            Glide.with(PotoActivity.this)
                    .asBitmap()
                    .fitCenter()
                    .load(user.getHeadImage())
                    .format(DecodeFormat.PREFER_ARGB_8888)//设置图片解码格式
                    .placeholder(R.drawable.picture_image_placeholder)
                    .error(R.drawable.picture_image_placeholder)
                    .into(iv_sheck_image);
            SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("headImage", user.getHeadImage());
        }
    }

    private class EnvironmentalAccessoriesFree implements OnResultCallbackListener<LocalMedia> {
        @Override
        public void onResult(List<LocalMedia> result) {
            if (result != null && result.size() > 0) {
                for (LocalMedia localMedia : result) {
                    String path = localMedia.getRealPath();
                    uploadHeadIconFile(new File(path));
                    break;
                }
            }
        }

        @Override
        public void onCancel() {
            Log.i("", "PictureSelector Cancel");
        }
    }


    private void doGetPicFromCapture() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File imageFile = createImageFile();//创建用来保存照片的文件
            if (imageFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    /*7.0以上要通过FileProvider将File转化为Uri*/
                    mImageUri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, imageFile);
                } else {
                    /*7.0以下则直接使用Uri的fromFile方法将File转化为Uri*/
                    mImageUri = Uri.fromFile(imageFile);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(intent, ACTIVITY_REQUEST_TAKE_PHOTO);
            }

         /*   headIconUri = PicUtil.getPicUri(this, "headIcon",
                    "head_photo" + System.currentTimeMillis() + ".jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, headIconUri);
            DocumentsHelper.addUriPermission(intent);*/


        }
    }

    /**
     * 创建用来存储图片的文件，以时间来命名就不会产生命名冲突
     *
     * @return 创建的图片文件
     */
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }


    private void initCroppedIcon() {
        if (croppedIconFile != null && croppedIconFile.exists()) {
            croppedIconFile.delete();
        }
        croppedIconFile = new File(PicUtil.getDir("headIcon"), "cropped_head_icon.png");
    }

    private void doCropPhoto(File f) {
        if (f == null) {
            return;
        }
        try {
            Intent intent = new Intent(this, ClipPictureActivity.class);
            intent.putExtra(ClipPictureActivity.IMAGE_PATH_ORIGINAL, f.getAbsolutePath());
            //还需要传递裁剪框宽高
            int width = PicUtil.getDeviceSize(this).x;
            int height = 0;
            width = 480;//头像宽高
            height = 480;
            intent.putExtra(ClipPictureActivity.CLIP_WIDTH, width);
            intent.putExtra(ClipPictureActivity.CLIP_HEIGHT, height);
            intent.putExtra(ClipPictureActivity.SAVED_WIDTH, width);
            intent.putExtra(ClipPictureActivity.SAVED_HEIGHT, height);
            intent.putExtra(ClipPictureActivity.IMAGE_PATH_AFTER_CROP, croppedIconFile.getAbsolutePath());
            startActivityForResult(intent, ACTIVITY_REQUEST_PHOTO_PICKED_WITH_DATA);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadHeadIconFile(File file) {
        Glide.with(PotoActivity.this).load(file).into(iv_sheck_image);
        showWaitingDlg();
        String deviceName = QZXTools.getSystemModel();
        String deviceId = QZXTools.getDeviceId(this);
        String url = Common.UPLOAD;
        Map<String, String> mapParams = new LinkedHashMap<>();
        OkHttp3_0Utils.getInstance().asyncPostSingleOkHttp(url, "file", mapParams, file, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = UPLOAD_FAILED;
                message.obj = "头像上传失败，请稍后重试";
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                    int code = jsonObject.optInt("code");
                    String msg = jsonObject.optString("msg");
                    if (code == 200) {
                        UploadBean bean = JSON.parseObject(content, UploadBean.class);
                        Message message = Message.obtain();
                        message.what = UPLOAD_SUCCESS;
                        message.obj = bean;
                        mHandler.sendMessage(message);
                    } else {
                        Message message = Message.obtain();
                        message.what = UPLOAD_FAILED;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = UPLOAD_FAILED;
                    message.obj = "头像上传失败，请稍后重试";
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    private void modifyPhoto(String photo) {
        showWaitingDlg();
        String deviceName = QZXTools.getSystemModel();
        String deviceId = QZXTools.getDeviceId(this);
        String url = Common.UPDATE_DATA;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("headImage", photo);
        Map<String, String> headParams = new LinkedHashMap<>();
        headParams.put("token", user.getToken());
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, headParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = MODIFY_FAILED;
                message.obj = "修改头像失败，请稍后重试";
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                    int code = jsonObject.optInt("code");
                    String msg = jsonObject.optString("msg");
                    if (code == 200) {
                        Message message = Message.obtain();
                        message.what = MODIFY_SUCCESS;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                        if (user != null) {
                            user.setHeadImage(photo);
                            SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("user_info", JSON.toJSONString(user));
                            //腾讯设置名称和头像
                            TUILogin.setUserInfo(user.getUserId() + "", user.getNickName(), user.getHeadImage());
                        }
                    } else {
                        Message message = Message.obtain();
                        message.what = MODIFY_FAILED;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = MODIFY_FAILED;
                    message.obj = "修改头像失败，请稍后重试";
                    mHandler.sendMessage(message);
                }


            }
        });
    }

    private void savePhotoToSDCard() {
        if (user != null && !TextUtils.isEmpty(user.getHeadImage())) {
            try {
                filePath = PicUtil.getPublicDirByType(Environment.DIRECTORY_PICTURES, "inforPicFolder") + File.separator + MD5Utils.MD5(user.getHeadImage()) + ".jpg";
                if (new File(filePath).exists()) {
                    QZXTools.logD("该文件在目录下已存在，无需再下载 " + filePath);
                    ToastUtils.show("图片保存成功");
                } else {
                    // 下载：
                    downloadFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadFile() {
        showWaitingDlg();
        if (user != null && !TextUtils.isEmpty(user.getHeadImage())) {
            OkHttp3_0Utils.getInstance().downloadSingleFileForOnce(user.getHeadImage(), "head_photo" + System.currentTimeMillis() + ".jpg", new OkHttp3_0Utils.DownloadCallback() {
                @Override
                public void downloadProcess(int value) {
                    QZXTools.logE("downloadProcess: " + value, null);
                }

                @Override
                public void downloadComplete(String filePath) {
                    QZXTools.logE("downloadComplete: " + filePath, null);
                    ToastUtils.show("图片保存成功");
                    saveLocalFile(filePath);
                    dismissWaitingDlg();
                }

                @Override
                public void downloadFailure() {
                    QZXTools.logE("downloadFailure: ", null);
                    dismissWaitingDlg();
                }
            });
        }
    }

    private void saveLocalFile(final String copyFilePath) {
        if (!TextUtils.isEmpty(copyFilePath)) {
            QZXTools.logD("saveLocalFile 源文件路径：" + copyFilePath + "|" + "目标文件路径："
                    + filePath);
            FileOutputStream os = null;
            FileInputStream in = null;
            try {
                os = new FileOutputStream(filePath);
                in = new FileInputStream(copyFilePath);
                byte[] buffer = new byte[1024];
                int c = -1;
                if (in != null) {
                    while ((c = in.read(buffer)) > 0) {
                        os.write(buffer, 0, c);
                    }
                }
                os.flush();
                mHandler.sendEmptyMessage(SAVE_SUCC);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(SAVE_FAILED);
            } catch (Exception e) {
                mHandler.sendEmptyMessage(SAVE_FAILED);
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
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == ACTIVITY_REQUEST_TAKE_PHOTO) {
                    Log.i(TAG, "拍照返回");
                    //裁剪头像
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageUri));

                    iv_sheck_image.setImageBitmap(bitmap);
                    File file = saveFile(bitmap, System.currentTimeMillis() + ".jpg");
                    uploadHeadIconFile(file);
                } else if (requestCode == ACTIVITY_REQUEST_PHOTO_PICKED_WITH_DATA) {

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public File saveFile(Bitmap bm, String fileName) throws IOException {//将Bitmap类型的图片转化成file类型，便于上传到服务器
        String path = Environment.getExternalStorageDirectory() + "/Ask";
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path + "/" + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myCaptureFile;

    }


    @Override
    protected void handleActMessage(Message msg) {
        super.handleActMessage(msg);
        switch (msg.what) {
            case UPLOAD_FAILED:
                String info = (String) msg.obj;
                ToastUtils.show(info);
                dismissWaitingDlg();
                break;
            case UPLOAD_SUCCESS:
                UploadBean uploadBean = (UploadBean) msg.obj;
                UploadBean.DataDTO uploadBeanData = uploadBean.getData();
                modifyPhoto(uploadBeanData.getUrl());
                break;
            case MODIFY_FAILED:
                String modifyInfo = (String) msg.obj;
                ToastUtils.show(modifyInfo);
                dismissWaitingDlg();
                break;
            case MODIFY_SUCCESS:
                dismissWaitingDlg();
                ToastUtils.show("头像修改成功");
                break;
            case SAVE_SUCC:
                QZXTools.logD("保存图片成功");
                ToastUtils.show("图片保存成功");
                // 发广播刷新系统媒体库
                PicUtil.scanFileAsync(PotoActivity.this, filePath);
                break;

            case SAVE_FAILED:
                QZXTools.logD("保存图片失败");
                ToastUtils.show("图片保存失败");
                if (!TextUtils.isEmpty(filePath)) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_base_back:
                finish();
                break;
            case R.id.ll_get_photo:
                showDialoge();
                break;
            case R.id.tv_quess:
                if (bottomDialog != null) {
                    bottomDialog.dismiss();
                    bottomDialog = null;
                }
                break;
            case R.id.tv_save_image:
                if (bottomDialog != null) {
                    bottomDialog.dismiss();
                    bottomDialog = null;
                }
                savePhotoToSDCard();
                break;
            case R.id.tv_photograph:
                //拍照
                if (bottomDialog != null) {
                    bottomDialog.dismiss();
                    bottomDialog = null;
                }
                doGetPicFromCapture();
                break;
            case R.id.tv_dialoge_photo:
                //相册
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                        .isWeChatStyle(true)// 是否开启微信图片选择风格
                        .isWithVideoImage(true)// 图片和视频是否可以同选,只在ofAll模式下有效
                        .maxSelectNum(1)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .maxVideoSelectNum(0) // 视频最大选择数量
                        .imageSpanCount(4)// 每行显示个数
                        .closeAndroidQChangeWH(true)//如果图片有旋转角度则对换宽高,默认为true
                        .closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 如果视频有旋转角度则对换宽高,默认为false
                        .isAndroidQTransform(true)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                        .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                        .isSingleDirectReturn(true)// 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                        .isPreviewImage(true)// 是否可预览图片
                        .isPreviewVideo(true)// 是否可预览视频
                        .isEnablePreviewAudio(true) // 是否可播放音频
                        .isCamera(true)// 是否显示拍照按钮
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认tru3
                        .isCompress(true)// 是否压缩
                        .synOrAsy(false)//同步true或异步false 压缩 默认同步
                        .isGif(true)// 是否显示gif图片
                        .cutOutQuality(90)// 裁剪输出质量 默认100
                        .minimumCompressSize(100)// 小于多少kb的图片不压缩
                        .forResult(new EnvironmentalAccessoriesFree());

                if (bottomDialog != null) {
                    bottomDialog.dismiss();
                    bottomDialog = null;
                }
                break;
        }

    }

    private void showDialoge() {
        bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialoge_content_normal, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        TextView tv_dialoge_photo = bottomDialog.findViewById(R.id.tv_dialoge_photo);
        tv_dialoge_photo.setOnClickListener(this);

        TextView tv_photograph = bottomDialog.findViewById(R.id.tv_photograph);
        tv_photograph.setVisibility(View.VISIBLE);
        tv_photograph.setOnClickListener(this);


        TextView tv_save_image = (TextView) bottomDialog.findViewById(R.id.tv_save_image);
        tv_save_image.setOnClickListener(this);

        TextView tv_quess = bottomDialog.findViewById(R.id.tv_quess);
        tv_quess.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
