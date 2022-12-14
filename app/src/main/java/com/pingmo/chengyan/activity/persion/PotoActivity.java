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
    private static final int SAVE_SUCC = 3;//????????????
    private static final int SAVE_FAILED = 4;//????????????
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
                    .format(DecodeFormat.PREFER_ARGB_8888)//????????????????????????
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
            File imageFile = createImageFile();//?????????????????????????????????
            if (imageFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    /*7.0???????????????FileProvider???File?????????Uri*/
                    mImageUri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, imageFile);
                } else {
                    /*7.0?????????????????????Uri???fromFile?????????File?????????Uri*/
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
     * ?????????????????????????????????????????????????????????????????????????????????
     *
     * @return ?????????????????????
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
            //??????????????????????????????
            int width = PicUtil.getDeviceSize(this).x;
            int height = 0;
            width = 480;//????????????
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
                message.obj = "????????????????????????????????????";
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
                    message.obj = "????????????????????????????????????";
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
                message.obj = "????????????????????????????????????";
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
                            //???????????????????????????
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
                    message.obj = "????????????????????????????????????";
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
                    QZXTools.logD("???????????????????????????????????????????????? " + filePath);
                    ToastUtils.show("??????????????????");
                } else {
                    // ?????????
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
                    ToastUtils.show("??????????????????");
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
            QZXTools.logD("saveLocalFile ??????????????????" + copyFilePath + "|" + "?????????????????????"
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
                    Log.i(TAG, "????????????");
                    //????????????
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

    public File saveFile(Bitmap bm, String fileName) throws IOException {//???Bitmap????????????????????????file?????????????????????????????????
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
                ToastUtils.show("??????????????????");
                break;
            case SAVE_SUCC:
                QZXTools.logD("??????????????????");
                ToastUtils.show("??????????????????");
                // ??????????????????????????????
                PicUtil.scanFileAsync(PotoActivity.this, filePath);
                break;

            case SAVE_FAILED:
                QZXTools.logD("??????????????????");
                ToastUtils.show("??????????????????");
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
                //??????
                if (bottomDialog != null) {
                    bottomDialog.dismiss();
                    bottomDialog = null;
                }
                doGetPicFromCapture();
                break;
            case R.id.tv_dialoge_photo:
                //??????
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofAll())// ??????.PictureMimeType.ofAll()?????????.ofImage()?????????.ofVideo()?????????.ofAudio()
                        .imageEngine(GlideEngine.createGlideEngine())// ??????????????????????????????????????????
                        .isWeChatStyle(true)// ????????????????????????????????????
                        .isWithVideoImage(true)// ?????????????????????????????????,??????ofAll???????????????
                        .maxSelectNum(1)// ????????????????????????
                        .minSelectNum(1)// ??????????????????
                        .maxVideoSelectNum(0) // ????????????????????????
                        .imageSpanCount(4)// ??????????????????
                        .closeAndroidQChangeWH(true)//??????????????????????????????????????????,?????????true
                        .closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// ??????????????????????????????????????????,?????????false
                        .isAndroidQTransform(true)// ??????????????????Android Q ??????????????????????????????????????????compress(false); && .isEnableCrop(false);??????,????????????
                        .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// ????????????Activity????????????????????????????????????
                        .isSingleDirectReturn(true)// ????????????????????????????????????PictureConfig.SINGLE???????????????
                        .isPreviewImage(true)// ?????????????????????
                        .isPreviewVideo(true)// ?????????????????????
                        .isEnablePreviewAudio(true) // ?????????????????????
                        .isCamera(true)// ????????????????????????
                        .isZoomAnim(true)// ?????????????????? ???????????? ??????tru3
                        .isCompress(true)// ????????????
                        .synOrAsy(false)//??????true?????????false ?????? ????????????
                        .isGif(true)// ????????????gif??????
                        .cutOutQuality(90)// ?????????????????? ??????100
                        .minimumCompressSize(100)// ????????????kb??????????????????
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
