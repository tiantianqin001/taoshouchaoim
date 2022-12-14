package com.tencent.qcloud.tuikit.tuicontact.ui.pages;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSON;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.tencent.qcloud.tuicore.component.activities.BaseLightActivity;

import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.bean.UploadBean;
import com.tencent.qcloud.tuikit.tuicontact.ui.Common;
import com.tencent.qcloud.tuikit.tuicontact.ui.OkHttp3_0Utils;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.SelectableRoundedImageView;
import com.tencent.qcloud.tuikit.tuicontact.util.DocumentsHelper;
import com.tencent.qcloud.tuikit.tuicontact.util.GlideEngine;
import com.tencent.qcloud.tuikit.tuicontact.util.PicUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateGroupActivity extends BaseLightActivity implements View.OnClickListener {
    private static final String TAG = "";
    private Dialog bottomDialog;
    private static final int ACTIVITY_REQUEST_TAKE_PHOTO = 1;
    private static final int ACTIVITY_REQUEST_PHOTO_PICKED_WITH_DATA = 2;
    private Uri headIconUri;
    private LoadDialog mDialog;
    private File croppedIconFile = null;
    private SelectableRoundedImageView main_iv_create_group_portrait;
    private static final String FILE_PROVIDER_AUTHORITY = "com.pingmo.chengyan.fileprovider";
    private String photoUrl;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Common.Interface_err:
                    String info = (String) msg.obj;
                    ToastUtils.s(CreateGroupActivity.this,info);
                    dismissWaitingDlg();
                    break;
                case Common.Interface_success:
                    dismissWaitingDlg();
                    UploadBean uploadBean = (UploadBean) msg.obj;
                    UploadBean.DataDTO uploadBeanData = uploadBean.getData();
                    photoUrl = uploadBeanData.getUrl();


                    break;
                case 1267:
                    Intent intent = new Intent();
                    //?????????????????????Intent
                    intent.putExtra("result", "My name is linjiqin");
                    setResult(RESULT_OK,intent);
                    CreateGroupActivity.this.finish();
                    break;
            }
        }
    };
    private Button main_btn_confirm_create;
    private EditText main_et_create_group_name;
    private String token;
    private String members;
    private String groupNameList;


    private class EnvironmentalAccessoriesFree implements OnResultCallbackListener<LocalMedia> {
        @Override
        public void onResult(List<LocalMedia> result) {
            if (result != null && result.size() > 0) {
                for (LocalMedia localMedia : result) {
                    String path = localMedia.getRealPath();
                    uploadHeadIconFile(new File(path));
                    Glide.with(CreateGroupActivity.this)
                            .asBitmap()
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .load(path)
                            .into(main_iv_create_group_portrait);
                    break;
                }
            }

        }

        @Override
        public void onCancel() {
            Log.i("", "PictureSelector Cancel");
        }
    }

    private void uploadHeadIconFile(File file) {

        showWaitingDlg();
        Glide.with(CreateGroupActivity.this)
                .asBitmap()
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .load(file)
                .into(main_iv_create_group_portrait);
        String url = Common.UPLOAD;
        Map<String, String> mapParams = new LinkedHashMap<>();
        OkHttp3_0Utils.getInstance().asyncPostSingleOkHttp(url, "file", mapParams, file, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message message = Message.obtain();
                message.what = Common.Interface_err;
                message.obj = "????????????????????????????????????";
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String content = response.body().string();

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                    int code = jsonObject.optInt("code");
                    String msg = jsonObject.optString("msg");
                    if (code == 200) {
                        UploadBean bean = JSON.parseObject(content, UploadBean.class);
                        Message message = Message.obtain();
                        message.what = Common.Interface_success;
                        message.obj = bean;
                        mHandler.sendMessage(message);
                    } else {
                        Message message = Message.obtain();
                        message.what = Common.Interface_err;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = Common.Interface_err;
                    message.obj = "????????????????????????????????????";
                    mHandler.sendMessage(message);
                }
            }
        });
    }
    protected void showWaitingDlg() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = new LoadDialog(this);
        mDialog.show();
    }

    protected void dismissWaitingDlg() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_create_group);
        main_iv_create_group_portrait = findViewById(R.id.main_iv_create_group_portrait);
        main_iv_create_group_portrait.setOnClickListener(this);
        //???????????????????????????
        main_btn_confirm_create = findViewById(R.id.main_btn_confirm_create);
        main_btn_confirm_create.setOnClickListener(this);
        main_et_create_group_name = findViewById(R.id.main_et_create_group_name);

        token = getIntent().getStringExtra("token");
        groupNameList = getIntent().getStringExtra("groupName");

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_iv_create_group_portrait) {
            //???????????????
            showDialoge();
        }
        if (v.getId() == R.id.tv_photograph){
            //??????
            if (bottomDialog != null) {
                bottomDialog.dismiss();
                bottomDialog = null;
            }
            doGetPicFromCapture();
        }
        if (v.getId() == R.id.main_btn_confirm_create){
            //????????????
            String groupName = main_et_create_group_name.getText().toString().trim();
            if (TextUtils.isEmpty(groupName)){
                ToastUtils.s(CreateGroupActivity.this,"?????????????????????");
                return;
            }
            if (TextUtils.isEmpty(photoUrl)){
                ToastUtils.s(CreateGroupActivity.this,"?????????????????????");
                return;
            }
            //?????????????????????
            createGroup(groupName,photoUrl);

        }
        if (v.getId() == R.id.tv_dialoge_photo){
            //?????????????????????
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
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == ACTIVITY_REQUEST_TAKE_PHOTO) {
                    Log.i(TAG, "????????????");
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageUri));

                    File file = saveFile(bitmap, "tiantian.jpg");
                    uploadHeadIconFile(file);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public File saveFile(Bitmap bm, String fileName) throws IOException {//???Bitmap????????????????????????file?????????????????????????????????
        String path = Environment.getExternalStorageDirectory() + "/Ask";
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myCaptureFile;

    }



    private void initCroppedIcon() {
        if (croppedIconFile != null && croppedIconFile.exists()) {
            croppedIconFile.delete();
        }
        croppedIconFile = new File(PicUtil.getDir(this,"headIcon"), "cropped_head_icon.png");
    }

    private Uri mImageUri;
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

    private void showDialoge() {
        bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialoge_content_normal_bootom, null);
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
        tv_photograph.setOnClickListener(this);



        TextView tv_quess = bottomDialog.findViewById(R.id.tv_quess);
        tv_quess.setOnClickListener(this);
    }

    /**
     * ???????????????
     * @param groupName
     * @param photoUrl
     */
    private void createGroup(String groupName, String photoUrl) {
        String url = Common.CREATE_GROUP;
        Map<String , String> headerParams = new LinkedHashMap<>();
        headerParams.put("token", token);
        JSONArray jsonArray = new JSONArray();
        if (!TextUtils.isEmpty(groupNameList)){
            String[] split = groupNameList.split("???");
            for (int i = 0; i <split.length ; i++) {
                String s = split[i];
                Integer aLong = Integer.valueOf(s);
                jsonArray.put(aLong);
            }
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("groupName", groupName);
            jsonObject.put("members", jsonArray);
            jsonObject.put("headImage",photoUrl);
            OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, jsonObject, headerParams, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //  mCreating = false;
//                if (errCode == TUIConstants.BuyingFeature.ERR_SDK_INTERFACE_NOT_SUPPORT || errCode == 11000) {
//                    showNotSupportDialog();
//                }
//                ToastUtil.toastLongMessage("createGroupChat fail:" + errCode + "=" + errMsg);

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String content = response.body().string();
                    Log.i(TAG, "onResponse: "+content);
                    try {
                        JSONObject jsonObject1 = new JSONObject(content);
                        int code = jsonObject1.optInt("code");
                        if (code == 200){
                            Message message = Message.obtain();
                            message.what = 1267;
                            mHandler.sendMessage(message);





                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    ContactUtils.startChatActivity(data, ChatInfo.TYPE_GROUP, groupInfo.getGroupName(), groupInfo.getGroupType());

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
