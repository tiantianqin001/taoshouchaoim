package com.pingmo.chengyan.activity.zxing;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Vibrator;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.glide.GlideEngine;
import com.pingmo.chengyan.utils.QZXTools;
import com.tencent.imsdk.v2.V2TIMFriendInfo;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.util.ErrorMessageConverter;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactService;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.ui.pages.FriendProfileActivity;
import com.tencent.qcloud.tuikit.tuicontact.util.TUIContactLog;


import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class CodeScanActivity extends AppCompatActivity implements QRCodeView.Delegate, View.OnClickListener {
    private static final String TAG = CodeScanActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    private ZXingView mZXingView;
    private LinearLayout ll_scan_back;
    private LinearLayout ll_photo_album;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        setContentView(R.layout.activity_test_scan);
        mZXingView = findViewById(R.id.zxingview);
        //返回
        ll_scan_back = findViewById(R.id.ll_scan_back);
        //相册
        ll_photo_album = findViewById(R.id.ll_photo_album);
        mZXingView.setDelegate(this);

        ll_scan_back.setOnClickListener(this);
        ll_photo_album.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别

        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        mZXingView.onDestroy(); // 开始识别
        Log.i(TAG, "扫描结果为：:" + result);
        if (!result.contains("=")){
            mZXingView.stopCamera();
            ToastUtils.show("当前用户不存在");
            return;
        }

        String[] split = result.split("=");
        result = split[1];
        setTitle("扫描结果为：" + result);
        vibrate();


        List<String> userIdList = new ArrayList<>();
        userIdList.add(result);
        String finalResult = result;
        V2TIMManager.getInstance().getUsersInfo(userIdList, new V2TIMValueCallback<List<V2TIMUserFullInfo>>() {
            @Override
            public void onSuccess(List<V2TIMUserFullInfo> v2TIMUserFullInfos) {
                if (v2TIMUserFullInfos.isEmpty()) {
                    Log.e(TAG, "get logined userInfo failed. list is empty");

                    ToastUtils.show("当前用户不存在");
                    return;
                }
                V2TIMUserFullInfo userFullInfo = v2TIMUserFullInfos.get(0);
                ContactItemBean contact = new ContactItemBean();
                contact.setId(userFullInfo.getUserID());
                contact.setNickName(userFullInfo.getNickName());
                contact.setAvatarUrl(userFullInfo.getFaceUrl());
                contact.setEnable(true);

                contact.setGroup(false);


                V2TIMManager.getFriendshipManager().getFriendList(new V2TIMValueCallback<List<V2TIMFriendInfo>>() {
                    @Override
                    public void onError(int code, String desc) {
                        TUIContactLog.e(TAG, "getFriendList err code = " + code + ", desc = " + ErrorMessageConverter.convertIMError(code, desc));

                    }

                    @Override
                    public void onSuccess(List<V2TIMFriendInfo> v2TIMFriendInfos) {
                        if (v2TIMFriendInfos != null && v2TIMFriendInfos.size() > 0) {
                            for (V2TIMFriendInfo friendInfo : v2TIMFriendInfos) {
                                QZXTools.logE(friendInfo.toString(),null);

                                String userID = friendInfo.getUserID();
                                if (userID.equals(finalResult)){
                                    //说明已经是好友了
                                    contact.setFriend(true);
                                    break;
                                }else {
                                    contact.setFriend(false);
                                }
                            }

                            Intent intent = new Intent(TUIContactService.getAppContext(), FriendProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(TUIContactConstants.ProfileType.CONTENT, contact);
                            TUIContactService.getAppContext().startActivity(intent);
                            finish();
                        }
                    }
                });
            }

            @Override
            public void onError(int code, String desc) {
                Log.e(TAG, "get logined userInfo failed. code : " + code + " desc : " + ErrorMessageConverter.convertIMError(code, desc));
                mZXingView.stopCamera(); // 销毁二维码扫描控件
                ToastUtils.show("当前用户不存在");
            }
        });
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mZXingView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    private static final String EXTRA_SELECTED_PHOTOS = "EXTRA_SELECTED_PHOTOS";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
          //  final String picturePath = BGAPhotoPickerActivity.getSelectedPhotos(data).get(0);
            ArrayList<String> stringArrayListExtra = data.getStringArrayListExtra(EXTRA_SELECTED_PHOTOS);
            String picturePath = stringArrayListExtra.get(0);
            // 本来就用到 QRCodeView 时可直接调 QRCodeView 的方法，走通用的回调
            mZXingView.decodeQRCode(picturePath);

            /*
            没有用到 QRCodeView 时可以调用 QRCodeDecoder 的 syncDecodeQRCode 方法

            这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
            请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github
            .com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
             */
//            new AsyncTask<Void, Void, String>() {
//                @Override
//                protected String doInBackground(Void... params) {
//                    return QRCodeDecoder.syncDecodeQRCode(picturePath);
//                }
//
//                @Override
//                protected void onPostExecute(String result) {
//                    if (TextUtils.isEmpty(result)) {
//                        Toast.makeText(TestScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(TestScanActivity.this, result, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }.execute();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_scan_back:
                finish();
                break;
            case R.id.ll_photo_album:
                //图片
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                        .isWeChatStyle(true)// 是否开启微信图片选择风格
                        .isWithVideoImage(true)// 图片和视频是否可以同选,只在ofAll模式下有效
                        .maxSelectNum(8)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .maxVideoSelectNum(1) // 视频最大选择数量
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
                        .forResult(new EnvironmentalAccessoriesFree("not_conform"));




                break;
        }
    }


    private class EnvironmentalAccessoriesFree implements OnResultCallbackListener<LocalMedia> {
        private String type;

        public EnvironmentalAccessoriesFree(String type) {

            this.type = type;
        }

        @Override
        public void onResult(List<LocalMedia> result) {
            //环境应急资源调查报告附件


            }

        @Override
        public void onCancel() {
            Log.i("", "PictureSelector Cancel");
        }


    }



}