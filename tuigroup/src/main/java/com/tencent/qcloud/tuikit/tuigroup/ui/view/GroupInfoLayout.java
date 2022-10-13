package com.tencent.qcloud.tuikit.tuigroup.ui.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnImageCompleteCallback;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMGroupInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.TUIConfig;
import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.TUIThemeManager;
import com.tencent.qcloud.tuicore.component.LineControllerView;
import com.tencent.qcloud.tuicore.component.TitleBarLayout;
import com.tencent.qcloud.tuicore.component.activities.ImageSelectActivity;
import com.tencent.qcloud.tuicore.component.activities.SelectionActivity;
import com.tencent.qcloud.tuicore.component.dialog.TUIKitDialog;

import com.tencent.qcloud.tuicore.component.gatherimage.SynthesizedImageView;
import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tuicore.component.interfaces.ITitleBarLayout;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.component.popupcard.PopupInputCard;
import com.tencent.qcloud.tuicore.util.BackgroundTasks;
import com.tencent.qcloud.tuicore.util.DateTimeUtil;
import com.tencent.qcloud.tuicore.util.ImageUtil;
import com.tencent.qcloud.tuicore.util.ScreenUtil;
import com.tencent.qcloud.tuicore.util.ThreadHelper;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuigroup.R;
import com.tencent.qcloud.tuikit.tuigroup.TUIGroupService;
import com.tencent.qcloud.tuikit.tuigroup.TUIGroupConstants;
import com.tencent.qcloud.tuikit.tuigroup.bean.GroupInfo;
import com.tencent.qcloud.tuikit.tuigroup.component.BottomSelectSheet;
import com.tencent.qcloud.tuikit.tuigroup.net.Common;
import com.tencent.qcloud.tuikit.tuigroup.net.EncryptUtil;
import com.tencent.qcloud.tuikit.tuigroup.net.OkHttp3_0Utils;
import com.tencent.qcloud.tuikit.tuigroup.presenter.GroupInfoPresenter;
import com.tencent.qcloud.tuikit.tuigroup.ui.interfaces.IGroupMemberLayout;
import com.tencent.qcloud.tuikit.tuigroup.ui.interfaces.IGroupMemberListener;
import com.tencent.qcloud.tuikit.tuigroup.ui.page.GroupInfoActivity;
import com.tencent.qcloud.tuikit.tuigroup.ui.page.GroupInfoFragment;
import com.tencent.qcloud.tuikit.tuigroup.ui.page.GroupMemberActivity;
import com.tencent.qcloud.tuikit.tuigroup.ui.page.GroupNoticeActivity;
import com.tencent.qcloud.tuikit.tuigroup.ui.page.ManageGroupActivity;
import com.tencent.qcloud.tuikit.tuigroup.ui.page.SetGroupManagerActivity;
import com.tencent.qcloud.tuikit.tuigroup.util.TUIGroupLog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class GroupInfoLayout extends LinearLayout implements IGroupMemberLayout, View.OnClickListener {

    private static final String TAG = GroupInfoLayout.class.getSimpleName();
    private TitleBarLayout mTitleBar;
    private LineControllerView mMemberView;
    private GroupInfoAdapter mMemberAdapter;
    private IGroupMemberListener mMemberPreviewListener;
    private LineControllerView mGroupTypeView;
    private TextView mGroupIDView;
    private TextView mGroupNameView;
    private View groupDetailArea;
    private ImageView mGroupIcon;
    private ImageView mGroupDetailArrow;
    private View mGroupNotice;
    private TextView mGroupNoticeText;
    private LineControllerView mGroupManageView;
    private LineControllerView mNickView;

    private LineControllerView mTopSwitchView;
    private LineControllerView mMsgRevOptionSwitchView;
    private TextView mDissolveBtn;
    private TextView mClearMsgBtn;
    private TextView mChangeOwnerBtn;
    private GridView memberList;

    private GroupInfo mGroupInfo;
    private GroupInfoPresenter mPresenter;
    private ArrayList<String> mJoinTypes = new ArrayList<>();
    private GroupInfoFragment.OnModifyGroupAvatarListener onModifyGroupAvatarListener;
    private String token;


    public GroupInfoLayout(Context context) {
        super(context);
        init();
    }

    public GroupInfoLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GroupInfoLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.group_info_layout, this);
        // 标题
        mTitleBar = findViewById(R.id.group_info_title_bar);

        RelativeLayout rootView = mTitleBar.getRootView();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rootView.getLayoutParams();
        params.setMargins(0,0,0,0);
        rootView.setLayoutParams(params);


        //修改群名称
        sp =  getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");

        token = EncryptUtil.getInstance(getContext()).decrypt(token);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        mTitleBar.getRightGroup().setVisibility(GONE);

        mTitleBar.setTitle(getResources().getString(R.string.group_detail), ITitleBarLayout.Position.MIDDLE);
        mTitleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) getContext()).finish();
            }
        });
        mTitleBar.getLeftIcon().setBackground(getResources().getDrawable(R.drawable.back));

        // 成员标题
        mMemberView = findViewById(R.id.group_member_bar);
        mMemberView.setOnClickListener(this);
        mMemberView.setCanNav(true);
        // 成员列表
        memberList = findViewById(R.id.group_members);
        mMemberAdapter = new GroupInfoAdapter(getContext());
        memberList.setAdapter(mMemberAdapter);
        // 群类型，只读
        mGroupTypeView = findViewById(R.id.group_type_bar);
        // 群ID，只读
        mGroupIDView = findViewById(R.id.group_account);
        // 群聊名称
        mGroupNameView = findViewById(R.id.group_name);

        groupDetailArea = findViewById(R.id.group_detail_area);
        groupDetailArea.setOnClickListener(this);
        // 群头像
        mGroupIcon = findViewById(R.id.group_icon);
        mGroupIcon.setOnClickListener(this);

        // 群详情右箭头
        mGroupDetailArrow = findViewById(R.id.group_detail_arrow);

        // 群公告
        mGroupNotice = findViewById(R.id.group_notice);
        mGroupNotice.setOnClickListener(this);
        mGroupNoticeText = findViewById(R.id.group_notice_text);
        // 群管理
        mGroupManageView = findViewById(R.id.group_manage);
        mGroupManageView.setOnClickListener(this);

        mJoinTypes.addAll(Arrays.asList(getResources().getStringArray(R.array.group_join_type)));
        // 群昵称
        mNickView = findViewById(R.id.self_nickname_bar);
        mNickView.setOnClickListener(this);
        mNickView.setCanNav(true);
        // 是否置顶
        mTopSwitchView = findViewById(R.id.chat_to_top_switch);
        mTopSwitchView.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (mGroupInfo == null) {
                    return;
                }
                mPresenter.setTopConversation(mGroupInfo.getId(), isChecked, new IUIKitCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {

                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        ToastUtil.toastShortMessage(module + ", Error code = " + errCode + ", desc = " + errMsg);
                        buttonView.setChecked(false);
                    }
                });
            }
        });
        // 消息接收选项
        mMsgRevOptionSwitchView = findViewById(R.id.msg_rev_option);

        // 退群
        mDissolveBtn = findViewById(R.id.group_dissolve_button);
        mDissolveBtn.setOnClickListener(this);

        // 清空群消息按钮
        mClearMsgBtn = findViewById(R.id.group_clear_msg_button);
        mClearMsgBtn.setOnClickListener(this);

        // 转让群主
        mChangeOwnerBtn = findViewById(R.id.group_change_owner_button);
        mChangeOwnerBtn.setOnClickListener(this);



    }

    private void initView() {
        int radius = ScreenUtil.dip2px(5);
        String faceUrl = mGroupInfo.getFaceUrl();
        Log.i(TAG, "initView: 3333...."+faceUrl);
        String savedIcon = ImageUtil.getGroupConversationAvatar("group_"+mGroupInfo.getId());
        if (TextUtils.isEmpty(faceUrl)){
            ThreadHelper.INST.execute(new Runnable() {
                @Override
                public void run() {
                    //根据id获取存储的文件路径
                    final File file = new File( savedIcon);
                    boolean cacheBitmapExists = false;
                    Bitmap existsBitmap = null;
                    if (file.exists() && file.isFile()) {
                        //文件存在，加载到内存
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        existsBitmap = BitmapFactory.decodeFile(file.getPath(), options);
                        if (options.outWidth > 0 && options.outHeight > 0) {
                            //当前文件是图片
                            cacheBitmapExists = true;
                        }
                    }
                    if (!cacheBitmapExists) {

                    } else {
                        final Bitmap finalExistsBitmap = existsBitmap;
                        BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GlideEngine.loadUserIcon(mGroupIcon, finalExistsBitmap);
                            }
                        });
                    }
                }
            });









//            GlideEngine.loadUserIcon1(mGroupIcon, new File(savedIcon), TUIThemeManager.getAttrResId(getContext(),
//                    R.attr.core_default_group_icon), radius);

        }else {
            GlideEngine.loadUserIcon(mGroupIcon, faceUrl, TUIThemeManager.getAttrResId(getContext(),
                    R.attr.core_default_group_icon), radius);
        }
        if (!mGroupInfo.isCanManagerGroup()) {
            mGroupDetailArrow.setVisibility(GONE);
        }
        if (mGroupInfo.isOwner()) {
            mChangeOwnerBtn.setVisibility(VISIBLE);
        } else {
            mChangeOwnerBtn.setVisibility(GONE);

        }
    }

    public void setGroupInfoPresenter(GroupInfoPresenter presenter) {
        this.mPresenter = presenter;
        if (mMemberAdapter != null) {
            mMemberAdapter.setPresenter(presenter);
        }
    }
    private static String SP_NAME = "huimiaomiao_share_date";
    private SharedPreferences sp;
    private Handler handler = new Handler(Looper.getMainLooper());

    private class EnvironmentalAccessoriesFree implements OnResultCallbackListener<LocalMedia> {
        @Override
        public void onResult(List<LocalMedia> result) {
            if (result != null && result.size() > 0) {
                for (LocalMedia localMedia : result) {
                    String path = localMedia.getRealPath();
                    //上传头像
                    String url = Common.UPLOAD;

                    Map<String, String> mapParams = new LinkedHashMap<>();
                    OkHttp3_0Utils.getInstance().asyncPostSingleOkHttp(url, "file", mapParams, new File(path),
                            new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Message message = Message.obtain();
                            message.what = Common.Interface_err;
                            message.obj = "头像上传失败，请稍后重试";

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
                                    JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                                    String url1 = jsonObject1.optString("url");
                                    modifyGroupAvatar(url1);

                                   /* V2TIMGroupInfo v2TIMGroupInfo = new V2TIMGroupInfo();
                                    v2TIMGroupInfo.setFaceUrl(url1);
                                    v2TIMGroupInfo.setGroupID(mGroupInfo.getId());
                                    V2TIMManager.getGroupManager().setGroupInfo(v2TIMGroupInfo, new V2TIMCallback() {
                                        @Override
                                        public void onSuccess() {

                                            int radius = ScreenUtil.dip2px(5);
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.i(TAG, "onSuccess: 1111...."+url1);
                                                   *//* GlideEngine.loadUserIcon(mGroupIcon, url1, TUIThemeManager.getAttrResId(getContext(),
                                                            R.attr.core_default_group_icon), radius);*//*


                                                }
                                            });

                                        }

                                        @Override
                                        public void onError(int i, String s) {

                                        }
                                    });*/
                                    sp =  getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                                    String token = sp.getString("token", "");

                                    token = EncryptUtil.getInstance(getContext()).decrypt(token);
                                    String url = Common.UPDATA_GROUP;

                                    Map<String , String> headerParams = new LinkedHashMap<>();
                                    headerParams.put("token", token);

                                    JSONObject jsonObject3 = new JSONObject();
                                    try {
                                        jsonObject3.put("groupId", Integer.valueOf(mGroupInfo.getId()));
                                        jsonObject3.put("headImage", url1);
                                        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, jsonObject3, headerParams, new Callback() {
                                            @Override
                                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                            }
                                            @Override
                                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                String content = response.body().string();
                                                Log.i(TAG, "onResponse: "+content);
                                                try {
                                                    JSONObject jsonObject1 = new JSONObject(content);
                                                    int code = jsonObject1.optInt("code");
                                                    if (code == 403){
                                                        Intent intent = new Intent();
                                                        intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                                        intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                                                "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                                        getContext().sendBroadcast(intent);
                                                    }else if (code == 200){
                                                        //修改头像
                                                    }


                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                } else {
                                    Message message = Message.obtain();
                                    message.what = Common.Interface_err;
                                    message.obj = msg;

                                }
                            } catch (Exception e) {
                                Message message = Message.obtain();
                                message.what = Common.Interface_err;
                                message.obj = "头像上传失败，请稍后重试";

                            }
                        }
                    });

                    break;
                }
            }

        }

        @Override
        public void onCancel() {
            Log.i("", "PictureSelector Cancel");
        }
    }
    @Override
    public void onClick(View v) {
        if (mGroupInfo == null) {
            TUIGroupLog.e(TAG, "mGroupInfo is NULL");
            return;
        }
        if (v.getId() == R.id.group_member_bar) {
            if (mMemberPreviewListener != null) {
                mMemberPreviewListener.forwardListMember(mGroupInfo);
            }
        } else if (v.getId() == R.id.group_detail_area) {
            if (!mGroupInfo.isCanManagerGroup()) {
                return;
            }
            PopupInputCard popupInputCard = new PopupInputCard((Activity) getContext());
            BottomSelectSheet sheet = new BottomSelectSheet(getContext());
            List<String> stringList = new ArrayList<>();
            String modify_group_image = getResources().getString(R.string.modify_group_image);
            String modifyGroupName = getResources().getString(R.string.modify_group_name);
            String modifyGroupNotice = getResources().getString(R.string.modify_group_notice);
            stringList.add(modify_group_image);
            stringList.add(modifyGroupName);
            stringList.add(modifyGroupNotice);

            sheet.setSelectList(stringList);
            sheet.setOnClickListener(new BottomSelectSheet.BottomSelectSheetOnClickListener() {
                @Override
                public void onSheetClick(int index) {
                    if (index == 0){
                        //修改群头像
                        PictureSelector.create((Activity)GroupInfoLayout.this.getContext())
                                .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                                .imageEngine(com.tencent.qcloud.tuikit.tuigroup.util.GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
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


                    }else if (index == 1) {
                        popupInputCard.setContent(mGroupNameView.getText().toString());
                        popupInputCard.setTitle(modifyGroupName);
                        popupInputCard.setOnPositive((result -> {
                            //修改群名称
                            sp =  getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                            String token = sp.getString("token", "");

                            token = EncryptUtil.getInstance(getContext()).decrypt(token);
                            String url = Common.UPDATA_GROUP;

                            Map<String , String> headerParams = new LinkedHashMap<>();
                            headerParams.put("token", token);

                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("groupId", Integer.valueOf(mGroupInfo.getId()));
                                jsonObject.put("groupName", modifyGroupName);
                                OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, jsonObject, headerParams, new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                    }
                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        String content = response.body().string();
                                        Log.i(TAG, "onResponse: "+content);
                                        try {
                                            JSONObject jsonObject1 = new JSONObject(content);
                                            int code = jsonObject1.optInt("code");
                                            if (code == 403){
                                                Intent intent = new Intent();
                                                intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                                intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                                        "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                                getContext().sendBroadcast(intent);
                                            }


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                            mPresenter.modifyGroupName(result);
                            if (!TextUtils.isEmpty(result)) {
                                mGroupNameView.setText(result);
                            }
                        }));
                        popupInputCard.show(groupDetailArea, Gravity.BOTTOM);
                    } else if (index == 2) {
                        popupInputCard.setContent(mGroupNoticeText.getText().toString());
                        popupInputCard.setTitle(modifyGroupNotice);
                        popupInputCard.setOnPositive((result -> {
                            mPresenter.modifyGroupNotice(result);
                            if (TextUtils.isEmpty(result)) {
                                mGroupNoticeText.setText(getResources().getString(R.string.group_notice_empty_tip));
                            } else {
                                mGroupNoticeText.setText(result);
                            }
                        }));
                        popupInputCard.show(groupDetailArea, Gravity.BOTTOM);
                    }
                }
            });
            sheet.show();
        } else if (v.getId() == R.id.group_icon) {
            if (!mGroupInfo.isCanManagerGroup()) {
                return;
            }

            if (onModifyGroupAvatarListener != null) {
                onModifyGroupAvatarListener.onModifyGroupAvatar(mGroupInfo.getFaceUrl());
            }
        } else if (v.getId() == R.id.group_notice) {
            Intent intent = new Intent(getContext(), GroupNoticeActivity.class);
            GroupNoticeActivity.setOnGroupNoticeChangedListener(new GroupNoticeActivity.OnGroupNoticeChangedListener() {
                @Override
                public void onChanged(String notice) {
                    if (TextUtils.isEmpty(notice)) {
                        mGroupNoticeText.setText(getResources().getString(R.string.group_notice_empty_tip));
                    } else {
                        mGroupNoticeText.setText(notice);
                    }
                }
            });
            intent.putExtra(TUIGroupConstants.Group.GROUP_INFO, mGroupInfo);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } else if (v.getId() == R.id.self_nickname_bar) {
            PopupInputCard popupInputCard = new PopupInputCard((Activity) getContext());
            popupInputCard.setContent(mNickView.getContent());
            popupInputCard.setTitle(getResources().getString(R.string.modify_nick_name_in_goup));
            popupInputCard.setOnPositive((result -> {
                mPresenter.modifyMyGroupNickname(result);
                mNickView.setContent(result);

                String url = Common.UPDATA_GROUP;

                Map<String , String> headerParams = new LinkedHashMap<>();
                headerParams.put("token", token);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("groupId", Integer.valueOf(mGroupInfo.getId()));
                    jsonObject.put("groupName", result);
                    OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, jsonObject, headerParams, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String content = response.body().string();
                            Log.i(TAG, "onResponse: "+content);
                            try {
                                JSONObject jsonObject1 = new JSONObject(content);
                                int code = jsonObject1.optInt("code");
                                if (code == 403){
                                    Intent intent = new Intent();
                                    intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                    intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                            "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                    getContext().sendBroadcast(intent);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }));
            popupInputCard.show(mNickView, Gravity.BOTTOM);

        } else if (v.getId() == R.id.join_type_bar) {
            if (mGroupTypeView.getContent().equals(getContext().getString(R.string.chat_room))) {
                ToastUtil.toastLongMessage(getContext().getString(R.string.chat_room_tip));
                return;
            }




//            Bundle bundle = new Bundle();
//            bundle.putString(SelectionActivity.Selection.TITLE, getResources().getString(R.string.group_join_type));
//            bundle.putStringArrayList(SelectionActivity.Selection.LIST, mJoinTypes);
//            bundle.putInt(SelectionActivity.Selection.DEFAULT_SELECT_ITEM_INDEX, mGroupInfo.getJoinType());
//            SelectionActivity.startListSelection((Activity) getContext(), bundle, new SelectionActivity.OnResultReturnListener() {
//                @Override
//                public void onReturn(final Object text) {
//                    mPresenter.modifyGroupInfo((Integer) text, TUIGroupConstants.Group.MODIFY_GROUP_JOIN_TYPE);
//                }
//            });
        } else if (v.getId() == R.id.group_dissolve_button) {
            if (mGroupInfo.isOwner() &&
                    (!mGroupInfo.getGroupType().equals(TUIConstants.GroupType.TYPE_WORK)
                            && !mGroupInfo.getGroupType().equals(TUIConstants.GroupType.TYPE_PRIVATE))) {
                new TUIKitDialog(getContext())
                        .builder()
                        .setCancelable(true)
                        .setCancelOutside(true)
                        .setTitle(getContext().getString(R.string.dismiss_group_tip))
                        .setDialogWidth(0.75f)
                        .setPositiveButton(getContext().getString(R.string.sure), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //解散群
                                String id = mGroupInfo.getId();
                                sp =  getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                                String token = sp.getString("token", "");

                                token = EncryptUtil.getInstance(getContext()).decrypt(token);
                                String url = Common.DISBAND_GROUP;

                                Map<String , String> headerParams = new LinkedHashMap<>();
                                headerParams.put("token", token);

                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("groupId", id);
                                    OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, jsonObject, headerParams, new Callback() {
                                        @Override
                                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                        }
                                        @Override
                                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                            String content = response.body().string();
                                            Log.i(TAG, "onResponse: "+content);
                                            try {
                                                JSONObject jsonObject1 = new JSONObject(content);
                                                int code = jsonObject1.optInt("code");
                                                if (code == 200){
                                                    ((Activity) GroupInfoLayout.this.getContext()).finish();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }



//                                mPresenter.deleteGroup(new IUIKitCallback<Void>() {
//                                    @Override
//                                    public void onSuccess(Void data) {
//                                        ((Activity) GroupInfoLayout.this.getContext()).finish();
//                                    }
//
//                                    @Override
//                                    public void onError(String module, int errCode, String errMsg) {
//                                        ToastUtil.toastLongMessage(errMsg);
//                                    }
//                                });
                            }
                        })
                        .setNegativeButton(getContext().getString(R.string.cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
            } else {
                new TUIKitDialog(getContext())
                        .builder()
                        .setCancelable(true)
                        .setCancelOutside(true)
                        .setTitle(getContext().getString(R.string.quit_group_tip))
                        .setDialogWidth(0.75f)
                        .setPositiveButton(getContext().getString(R.string.sure), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //退出群聊


                                String id = mGroupInfo.getId();
                                sp =  getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                                String token = sp.getString("token", "");

                                token = EncryptUtil.getInstance(getContext()).decrypt(token);
                                String url = Common.QUIT_GROUP_CHAT;

                                Map<String , String> headerParams = new LinkedHashMap<>();
                                headerParams.put("token", token);
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("groupId", id);
                                    OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, jsonObject, headerParams, new Callback() {
                                        @Override
                                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                        }
                                        @Override
                                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                            String content = response.body().string();
                                            Log.i(TAG, "onResponse: "+content);
                                            try {
                                                JSONObject jsonObject1 = new JSONObject(content);
                                                int code = jsonObject1.optInt("code");
                                                if (code == 200){
                                                    ((Activity) GroupInfoLayout.this.getContext()).finish();
                                                }
                                                if (code == 403){
                                                    Intent intent = new Intent();
                                                    intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                                    intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                                            "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                                    getContext().sendBroadcast(intent);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                mPresenter.quitGroup(new IUIKitCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void data) {
                                        ((Activity) GroupInfoLayout.this.getContext()).finish();
                                    }

                                    @Override
                                    public void onError(String module, int errCode, String errMsg) {
                                        ((Activity) GroupInfoLayout.this.getContext()).finish();
                                        ToastUtil.toastShortMessage("quitGroup failed, errCode =  " + errCode + " errMsg = " + errMsg);
                                    }
                                });
                            }
                        })
                        .setNegativeButton(getContext().getString(R.string.cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
            }
        } else if (v.getId() == R.id.group_clear_msg_button) {
            new TUIKitDialog(getContext())
                    .builder()
                    .setCancelable(true)
                    .setCancelOutside(true)
                    .setTitle(getContext().getString(R.string.clear_group_msg_tip))
                    .setDialogWidth(0.75f)
                    .setPositiveButton(getContext().getString(R.string.sure), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Map<String, Object> hashMap = new HashMap<>();
                            hashMap.put(TUIConstants.TUIGroup.GROUP_ID, mGroupInfo.getId());
                            TUICore.notifyEvent(TUIConstants.TUIGroup.EVENT_GROUP,
                                    TUIConstants.TUIGroup.EVENT_SUB_KEY_CLEAR_MESSAGE, hashMap);
                        }
                    })
                    .setNegativeButton(getContext().getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show();
        } else if (v.getId() == R.id.group_manage) {
            //获取群消息
            String url = Common.GETGROUPINFO;
            Map<String , String> headerParams = new LinkedHashMap<>();
            headerParams.put("token", token);
            Map<String , String> mapPatams = new LinkedHashMap<>();
            mapPatams.put("groupId",mGroupInfo.getId());

            OkHttp3_0Utils.getInstance().asyncGetHeaderOkHttp(url, mapPatams, headerParams, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String content = response.body().string();
                    Log.i(TAG, "onResponse: "+content);
                    try {
                        JSONObject jsonObject1 = new JSONObject(content);
                        int code = jsonObject1.optInt("code");
                        if (code == 200){
                            JSONObject optJSONObject = jsonObject1.optJSONObject("data");
                            if (optJSONObject!=null){
                                //管理员同意功能:0-关闭;1-开启(关闭后自动加入群)
                                int invitationStatus = optJSONObject.optInt("invitationStatus");
                                int groupMemberProtect = optJSONObject.optInt("groupMemberProtect");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //群管理 设置全员禁言
                                        Intent intent = new Intent(getContext(), ManageGroupActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra(TUIGroupConstants.Group.GROUP_INFO, mGroupInfo);
                                        intent.putExtra("invitationStatus",invitationStatus);
                                        intent.putExtra("groupMemberProtect",groupMemberProtect);

                                        getContext().startActivity(intent);




                                    }
                                });


                            }

                        }
                        if (code == 403){
                            Intent intent = new Intent();
                            intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                            intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                    "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                            getContext(). sendBroadcast(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (v == mChangeOwnerBtn) {
            Intent intent = new Intent(getContext(), GroupMemberActivity.class);
            intent.putExtra(TUIGroupConstants.Selection.IS_SELECT_MODE, true);
            intent.putExtra(TUIGroupConstants.Group.GROUP_INFO, mGroupInfo);
            intent.putExtra(TUIGroupConstants.Selection.LIMIT, 1);
            intent.putExtra(TUIGroupConstants.Selection.TITLE, getResources().getString(R.string.group_transfer_group_owner));
            ((Activity) getContext()).startActivityForResult(intent, GroupInfoActivity.REQUEST_FOR_CHANGE_OWNER);

        }
    }

    public void loadGroupInfo(String groupId) {
        mPresenter.loadGroupInfo(groupId);
    }

    public void getGroupMembers(GroupInfo groupInfo) {
        mPresenter.getGroupMembers(groupInfo, new IUIKitCallback<GroupInfo>() {
            @Override
            public void onSuccess(GroupInfo data) {
                setGroupInfo((GroupInfo) data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    public void setGroupInfo(GroupInfo info) {
        if (info == null) {
            return;
        }
        this.mGroupInfo = info;
        mGroupNameView.setText(info.getGroupName());
        mGroupIDView.setText(info.getId());
        if (TextUtils.isEmpty(info.getNotice())) {
            mGroupNoticeText.setText(getResources().getString(R.string.group_notice_empty_tip));
        } else {
            mGroupNoticeText.setText(info.getNotice());
        }
        mMemberView.setContent(info.getMemberDetails().size() + "人");

        //加群方式的点击事件
        mMemberAdapter.setDataSource(mGroupInfo);

        int columnNum = memberList.getNumColumns();
        int rowNum = (int) Math.ceil(mMemberAdapter.getCount() * 1.0f / columnNum);
        int itemHeight = ScreenUtil.dip2px(88);
        ViewGroup.LayoutParams layoutParams = memberList.getLayoutParams();
        layoutParams.height = itemHeight * rowNum;
        memberList.setLayoutParams(layoutParams);

        mGroupTypeView.setContent(convertGroupText(info.getGroupType()));

        mNickView.setContent(mPresenter.getNickName());
        mTopSwitchView.setChecked(mGroupInfo.isTopChat());

        if (GroupInfo.GROUP_TYPE_MEETING.equals(info.getGroupType())) {
            mMsgRevOptionSwitchView.setVisibility(GONE);
        } else {
            mMsgRevOptionSwitchView.setChecked(mGroupInfo.getMessageReceiveOption());

            mMsgRevOptionSwitchView.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                    mPresenter.setGroupReceiveMessageOpt(mGroupInfo, isChecked);
                }
            });
        }
        //解散群
        mDissolveBtn.setText(R.string.dissolve);
        mClearMsgBtn.setText(R.string.clear_message);
        //加群方式
        if (mGroupInfo.isOwner()) {

            if (mGroupInfo.getGroupType().equals(TUIConstants.GroupType.TYPE_WORK)
                    || mGroupInfo.getGroupType().equals(TUIConstants.GroupType.TYPE_PRIVATE)) {
                mDissolveBtn.setText(R.string.exit_group);
            }
        } else {

            mDissolveBtn.setText(R.string.exit_group);

        }

//        if (mGroupInfo.getGroupType().equals(TUIConstants.GroupType.TYPE_WORK)
//                || mGroupInfo.getGroupType().equals(TUIConstants.GroupType.TYPE_PRIVATE)) {
//            mDissolveBtn.setText(R.string.exit_group);
//        }

        if (mGroupInfo.isCanManagerGroup()) {
            mGroupManageView.setVisibility(VISIBLE);

        }






        mMemberAdapter.setDataSource(mGroupInfo);


        initView();
    }

    private void getGroupInfo(String groupId, GroupInfo dataSource) {

        setGroupInfo(dataSource);

    }

    private String convertGroupText(String groupType) {
        String groupText = "";
        if (TextUtils.isEmpty(groupType)) {
            return groupText;
        }
        if (TextUtils.equals(groupType, TUIConstants.GroupType.TYPE_PRIVATE)
                || TextUtils.equals(groupType, TUIConstants.GroupType.TYPE_WORK)) {
            groupText = getContext().getString(R.string.private_group);
        } else if (TextUtils.equals(groupType, TUIConstants.GroupType.TYPE_PUBLIC)) {
            groupText = getContext().getString(R.string.public_group);
        } else if (TextUtils.equals(groupType, TUIConstants.GroupType.TYPE_CHAT_ROOM)
                || TextUtils.equals(groupType, TUIConstants.GroupType.TYPE_MEETING)) {
            groupText = getContext().getString(R.string.chat_room);
        } else if (TextUtils.equals(groupType, TUIConstants.GroupType.TYPE_COMMUNITY)) {
            groupText = getContext().getString(R.string.community_group);
        }
        return groupText;
    }

    public void onGroupInfoModified(Object value, int type) {
        switch (type) {
            case TUIGroupConstants.Group.MODIFY_GROUP_NAME:
                ToastUtil.toastLongMessage(getResources().getString(R.string.modify_group_name_success));
                mGroupNameView.setText(value.toString());
                break;
            case TUIGroupConstants.Group.MODIFY_GROUP_NOTICE:
                if (TextUtils.isEmpty(value.toString())) {
                    mGroupNoticeText.setText(getResources().getString(R.string.group_notice_empty_tip));
                } else {
                    mGroupNoticeText.setText(value.toString());
                }
                ToastUtil.toastLongMessage(getResources().getString(R.string.modify_group_notice_success));
                break;
            case TUIGroupConstants.Group.MODIFY_GROUP_JOIN_TYPE:
               // mJoinTypeView.setContent(mJoinTypes.get((Integer) value));
                break;
            case TUIGroupConstants.Group.MODIFY_MEMBER_NAME:
                ToastUtil.toastLongMessage(getResources().getString(R.string.modify_nickname_success));
                mNickView.setContent(value.toString());
                break;
        }
    }

    public void setRouter(IGroupMemberListener listener) {
        mMemberPreviewListener = listener;
        mMemberAdapter.setManagerCallBack(listener);
    }

    public void setOnModifyGroupAvatarListener(GroupInfoFragment.OnModifyGroupAvatarListener onModifyGroupAvatarListener) {
        this.onModifyGroupAvatarListener = onModifyGroupAvatarListener;
    }

    public void modifyGroupAvatar(String avatarUrl) {
        mPresenter.modifyGroupFaceUrl(mGroupInfo.getId(), avatarUrl, new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                mGroupInfo.setFaceUrl(avatarUrl);
                setGroupInfo(mGroupInfo);            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastShortMessage(TUIGroupService.getAppContext().getString(R.string.modify_icon_fail) + ", code = " + errCode + ", info = " + errMsg);
            }
        });
    }

    @Override
    public void onGroupInfoChanged(GroupInfo dataSource) {

        //获取群成员的消息
        //
        getGroupInfo(dataSource.getId(),dataSource);

    }

    @Override
    public TitleBarLayout getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void setParentLayout(Object parent) {

    }

}
