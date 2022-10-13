package com.tencent.qcloud.tuikit.tuicontact.ui.pages;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.tencent.qcloud.tuicore.TUIConfig;
import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.TUIThemeManager;
import com.tencent.qcloud.tuicore.component.LineControllerView;
import com.tencent.qcloud.tuicore.component.TitleBarLayout;
import com.tencent.qcloud.tuicore.component.activities.BaseLightActivity;
import com.tencent.qcloud.tuicore.component.activities.SelectionActivity;
import com.tencent.qcloud.tuicore.component.dialog.TUIKitDialog;
import com.tencent.qcloud.tuicore.component.gatherimage.MultiImageData;
import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tuicore.component.interfaces.ITitleBarLayout;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.BackgroundTasks;
import com.tencent.qcloud.tuicore.util.ImageUtil;
import com.tencent.qcloud.tuicore.util.ThreadHelper;
import com.tencent.qcloud.tuicore.util.ToastUtil;

import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;
import com.tencent.qcloud.tuikit.tuicontact.bean.ChatInfo;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.bean.GroupInfo;
import com.tencent.qcloud.tuikit.tuicontact.bean.GroupMemberInfo;
import com.tencent.qcloud.tuikit.tuicontact.bean.UploadBean;
import com.tencent.qcloud.tuikit.tuicontact.presenter.ContactPresenter;
import com.tencent.qcloud.tuikit.tuicontact.ui.Common;
import com.tencent.qcloud.tuikit.tuicontact.ui.OkHttp3_0Utils;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactCustomerListView;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactListView;
import com.tencent.qcloud.tuikit.tuicontact.util.ContactUtils;
import com.tencent.qcloud.tuikit.tuicontact.util.EncryptUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class StartGroupChatActivity extends BaseLightActivity {
    private static final String TAG = StartGroupChatActivity.class.getSimpleName();
    private TitleBarLayout mTitleBar;
    private ContactCustomerListView mContactListView;
    private LineControllerView mJoinType;
    private ArrayList<GroupMemberInfo> mMembers = new ArrayList<>();
    private int mGroupType = -1;
    private int mJoinTypeIndex = 2;
    private ArrayList<String> mJoinTypes = new ArrayList<>();
    private ArrayList<String> mGroupTypeValue = new ArrayList<>();
    private boolean mCreating;
    List urlList = new ArrayList();

    private ContactPresenter presenter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_start_group_chat_activity);
        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        init();
    }

    private void init() {
        String[] array = getResources().getStringArray(R.array.group_type);
        mGroupTypeValue.addAll(Arrays.asList(array));
        array = getResources().getStringArray(R.array.group_join_type);
        mJoinTypes.addAll(Arrays.asList(array));
        GroupMemberInfo memberInfo = new GroupMemberInfo();
        memberInfo.setAccount(ContactUtils.getLoginUser());

     //   mMembers.add(0, memberInfo);
        mTitleBar = findViewById(R.id.group_create_title_bar);
        mTitleBar.getLeftIcon().setBackground(getResources().getDrawable(R.drawable.back));
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        mTitleBar.setTitle(getResources().getString(R.string.sure), ITitleBarLayout.Position.RIGHT);
        mTitleBar.getRightIcon().setVisibility(View.GONE);

        RelativeLayout rootView = mTitleBar.getRootView();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rootView.getLayoutParams();
        params.setMargins(0,0,0,0);
        rootView.setLayoutParams(params);

        mTitleBar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建群
                createGroupChat();
            }
        });
        mTitleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mJoinType = findViewById(R.id.group_type_join);
        mJoinType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJoinTypePickerView();
            }
        });
        mJoinType.setCanNav(true);
        mJoinType.setContent(mJoinTypes.get(2));

        mContactListView = findViewById(R.id.group_create_member_list);

        presenter = new ContactPresenter(null);
        presenter.setFriendListListener();
        mContactListView.setPresenter(presenter);
        presenter.setContactListView(mContactListView);

        mContactListView.loadDataSource(ContactListView.DataSource.FRIEND_LIST);
        mContactListView.setOnSelectChangeListener(new ContactCustomerListView.OnSelectChangedListener() {
            @Override
            public void onSelectChanged(ContactItemBean contact, boolean selected) {
                if (selected) {
                    GroupMemberInfo memberInfo = new GroupMemberInfo();
                    memberInfo.setAccount(contact.getId());
                    memberInfo.setNickName(contact.getNickName());
                    memberInfo.setIconUrl(contact.getAvatarUrl());
                    mMembers.add(memberInfo);
                } else {
                    for (int i = mMembers.size() - 1; i >= 0; i--) {
                        if (mMembers.get(i).getAccount().equals(contact.getId())) {
                            mMembers.remove(i);
                        }
                    }
                }
            }
        });

        setGroupType(getIntent().getIntExtra("type", TUIContactConstants.GroupType.PRIVATE));

    }

    public void setGroupType(int type) {
        mGroupType = type;
        switch (type) {
            case TUIContactConstants.GroupType.PUBLIC:
                //
                mJoinType.setVisibility(View.GONE);

                String titleName = getIntent().getStringExtra("titleName");
                if (!TextUtils.isEmpty(titleName)) {
                    mTitleBar.getMiddleTitle().setText(titleName);
                } else {
                    mTitleBar.setTitle(getResources().getString(R.string.create_group_chat), ITitleBarLayout.Position.MIDDLE);
                }


                break;
            case TUIContactConstants.GroupType.CHAT_ROOM:
                mTitleBar.setTitle(getResources().getString(R.string.create_chat_room), ITitleBarLayout.Position.MIDDLE);
                mJoinType.setVisibility(View.VISIBLE);
                break;
            case TUIContactConstants.GroupType.COMMUNITY:
                mTitleBar.setTitle(getResources().getString(R.string.create_community), ITitleBarLayout.Position.MIDDLE);
                mJoinType.setVisibility(View.VISIBLE);
                break;
            case TUIContactConstants.GroupType.PRIVATE:
            default:
                mTitleBar.setTitle(getResources().getString(R.string.create_private_group), ITitleBarLayout.Position.MIDDLE);
                mJoinType.setVisibility(View.GONE);


                break;
        }
    }

    private static String SP_NAME = "huimiaomiao_share_date";
    private SharedPreferences sp;

    private void showJoinTypePickerView() {
        Bundle bundle = new Bundle();
        bundle.putString(SelectionActivity.Selection.TITLE, getResources().getString(R.string.group_join_type));
        bundle.putStringArrayList(SelectionActivity.Selection.LIST, mJoinTypes);
        bundle.putInt(SelectionActivity.Selection.DEFAULT_SELECT_ITEM_INDEX, mJoinTypeIndex);
        SelectionActivity.startListSelection(this, bundle, new SelectionActivity.OnResultReturnListener() {
            @Override
            public void onReturn(Object text) {
                mJoinType.setContent(mJoinTypes.get((Integer) text));
                mJoinTypeIndex = (Integer) text;
            }
        });
    }

    private void createGroupChat() {
        if (mCreating) {
            return;
        }
        if (mGroupType < 3 && mMembers.size()==0) {
            ToastUtil.toastLongMessage(getResources().getString(R.string.tips_empty_group_member));
            return;
        }
        if (mGroupType > 0 && mJoinTypeIndex == -1) {
            ToastUtil.toastLongMessage(getResources().getString(R.string.tips_empty_group_type));
            return;
        }
        if (mGroupType == 0) {
            mJoinTypeIndex = -1;
        }
        final GroupInfo groupInfo = new GroupInfo();
        String groupName = "";
        urlList.clear();
       String groupNameList = "";
        for (int i = 0; i < mMembers.size(); i++) {
            groupName = groupName+ mMembers.get(i).getNickName() + "、";
            groupNameList =groupNameList+ mMembers.get(i).getAccount() +"、";
            urlList.add(mMembers.get(i).getIconUrl());
        }

        if (groupName.length() > 20) {
            groupName = groupName.substring(0, 17) + "...";
        }else {
            groupName = groupName.substring(0, groupName.length()-1);
        }
        MultiImageData copyMultiImageData = new MultiImageData(urlList, 0);
        copyMultiImageData.setMaxWidthHeight(100, 100);
        int[] gridParam = calculateGridParam(copyMultiImageData.size());
        copyMultiImageData.rowCount = gridParam[0];
        copyMultiImageData.columnCount = gridParam[1];
        copyMultiImageData.targetImageSize = (copyMultiImageData.maxWidth - (copyMultiImageData.columnCount + 1)
                * copyMultiImageData.gap) / (copyMultiImageData.columnCount == 1 ? 2 : copyMultiImageData.columnCount);//图片尺寸

        String finalGroupName = groupName;
        String finalGroupNameList = groupNameList;


        createGroup(finalGroupName,finalGroupNameList.substring(0,finalGroupNameList.length()-1));

        ThreadHelper.INST.execute(new Runnable() {
            @Override
            public void run() {
                // 收集图片
                final MultiImageData finalCopyMultiImageData = copyMultiImageData;
                asyncLoadImageList(finalCopyMultiImageData);
                // 合成图片
                final Bitmap bitmap = synthesizeImageList(finalCopyMultiImageData);
                //先生成本地的图片上传 上传后返回一个图片地址
                try {
                    File file = saveFile(bitmap, "tiantian.jpg");
                    uploadHeadIconFile(file, finalGroupName.substring(0,finalGroupName.length()-1), finalGroupNameList.substring(0,finalGroupNameList.length()-1));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 创建群信息
     */
    private void createGroup(String groupName, String groupNameList) {
        String url = Common.CREATE_GROUP;
        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = EncryptUtil.getInstance(this).decrypt(token);
        Map<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("token", token);
        JSONArray jsonArray = new JSONArray();
        if (!TextUtils.isEmpty(groupNameList)) {
            String[] split = groupNameList.split("、");
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                Integer aLong = Integer.valueOf(s);
                jsonArray.put(aLong);
            }
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("groupName", groupName);
            jsonObject.put("members", jsonArray);
           // jsonObject.put("headImage", photoUrl);
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
                    Log.i(TAG, "onResponse: " + content);
                    try {
                        JSONObject jsonObject1 = new JSONObject(content);
                        int code = jsonObject1.optInt("code");
                        String msg = jsonObject1.optString("msg");
                        if (code == 200) {
                            BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.toastLongMessage("创建群成功");
                                    finish();
                                }
                            });
                        } else if (code == 403) {
                            Intent intent = new Intent();
                            intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                            intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                    "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                            sendBroadcast(intent);
                        }else if (code == 9000){

                            BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.toastLongMessage(msg);

                                }
                            });

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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            finish();
        }
    }

    private void showNotSupportDialog() {
        String string = getResources().getString(R.string.contact_im_flagship_edition_update_tip, getString(R.string.contact_community));
        String buyingGuidelines = getResources().getString(R.string.contact_buying_guidelines);
        int buyingGuidelinesIndex = string.lastIndexOf(buyingGuidelines);
        final int foregroundColor = getResources().getColor(TUIThemeManager.getAttrResId(this, R.attr.core_primary_color));
        //需要显示的字串
        SpannableString spannedString = new SpannableString(string);
        //设置点击字体颜色
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(foregroundColor);
        spannedString.setSpan(colorSpan2, buyingGuidelinesIndex, buyingGuidelinesIndex + buyingGuidelines.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                if (TextUtils.equals(TUIThemeManager.getInstance().getCurrentLanguage(), "zh")) {
                    openWebUrl(TUIConstants.BuyingFeature.BUYING_PRICE_DESC);
                } else {
                    openWebUrl(TUIConstants.BuyingFeature.BUYING_PRICE_DESC_EN);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //点击事件去掉下划线
                ds.setUnderlineText(false);
            }
        };
        spannedString.setSpan(clickableSpan2, buyingGuidelinesIndex, buyingGuidelinesIndex + buyingGuidelines.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //开始响应点击事件
        TUIKitDialog.TUIIMUpdateDialog.getInstance()
                .createDialog(this)
                .setMovementMethod(LinkMovementMethod.getInstance())
                // 只在 debug 模式下弹窗
                .setShowOnlyDebug(true)
                .setCancelable(true)
                .setCancelOutside(true)
                .setTitle(spannedString)
                .setDialogWidth(0.75f)
                .setDialogFeatureName(TUIConstants.BuyingFeature.BUYING_FEATURE_COMMUNITY)
                .setPositiveButton(getString(R.string.contact_no_more_reminders), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TUIKitDialog.TUIIMUpdateDialog.getInstance().dismiss();
                        TUIKitDialog.TUIIMUpdateDialog.getInstance().setNeverShow(true);
                    }
                })
                .setNegativeButton(getString(R.string.contact_i_know), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TUIKitDialog.TUIIMUpdateDialog.getInstance().dismiss();
                    }
                })
                .show();
    }

    private void openWebUrl(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri contentUrl = Uri.parse(url);
        intent.setData(contentUrl);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public boolean asyncLoadImageList(MultiImageData imageData) {
        boolean loadSuccess = true;
        List<Object> imageUrls = imageData.getImageUrls();
        for (int i = 0; i < imageUrls.size(); i++) {
            Bitmap defaultIcon = BitmapFactory.decodeResource(getResources(), TUIThemeManager.getAttrResId(this, R.attr.core_default_user_icon));
            //下载图片
            try {
                Bitmap bitmap = asyncLoadImage(imageUrls.get(i), imageData.targetImageSize);
                imageData.putBitmap(bitmap, i);
            } catch (InterruptedException e) {
                e.printStackTrace();
                imageData.putBitmap(defaultIcon, i);
            } catch (ExecutionException e) {
                e.printStackTrace();
                imageData.putBitmap(defaultIcon, i);
            }
        }
        //下载完毕
        return loadSuccess;
    }

    /**
     * 同步加载图片
     *
     * @param imageUrl
     * @param targetImageSize
     * @return
     */
    private Bitmap asyncLoadImage(Object imageUrl, int targetImageSize) throws ExecutionException, InterruptedException {
        return GlideEngine.loadBitmap(imageUrl, targetImageSize);
    }

    /**
     * 设置宫格参数
     *
     * @param imagesSize 图片数量
     * @return 宫格参数 gridParam[0] 宫格行数 gridParam[1] 宫格列数
     */
    protected int[] calculateGridParam(int imagesSize) {
        int[] gridParam = new int[2];
        if (imagesSize < 3) {
            gridParam[0] = 1;
            gridParam[1] = imagesSize;
        } else if (imagesSize <= 4) {
            gridParam[0] = 2;
            gridParam[1] = 2;
        } else {
            gridParam[0] = imagesSize / 3 + (imagesSize % 3 == 0 ? 0 : 1);
            gridParam[1] = 3;
        }
        return gridParam;
    }

    public void drawDrawable(Canvas canvas, MultiImageData imageData) {
        //画背景
        canvas.drawColor(imageData.bgColor);
        //画组合图片
        int size = imageData.size();
        int t_center = (imageData.maxHeight + imageData.gap) / 2;//中间位置以下的顶点（有宫格间距）
        int b_center = (imageData.maxHeight - imageData.gap) / 2;//中间位置以上的底部（有宫格间距）
        int l_center = (imageData.maxWidth + imageData.gap) / 2;//中间位置以右的左部（有宫格间距）
        int r_center = (imageData.maxWidth - imageData.gap) / 2;//中间位置以左的右部（有宫格间距）
        int center = (imageData.maxHeight - imageData.targetImageSize) / 2;//中间位置以上顶部（无宫格间距）
        for (int i = 0; i < size; i++) {
            int rowNum = i / imageData.columnCount;//当前行数
            int columnNum = i % imageData.columnCount;//当前列数

            int left = ((int) (imageData.targetImageSize * (imageData.columnCount == 1 ? columnNum + 0.5 : columnNum) + imageData.gap * (columnNum + 1)));
            int top = ((int) (imageData.targetImageSize * (imageData.columnCount == 1 ? rowNum + 0.5 : rowNum) + imageData.gap * (rowNum + 1)));
            int right = left + imageData.targetImageSize;
            int bottom = top + imageData.targetImageSize;

            Bitmap bitmap = imageData.getBitmap(i);
            if (size == 1) {
                drawBitmapAtPosition(canvas, left, top, right, bottom, bitmap);
            } else if (size == 2) {
                drawBitmapAtPosition(canvas, left, center, right, center + imageData.targetImageSize, bitmap);
            } else if (size == 3) {
                if (i == 0) {
                    drawBitmapAtPosition(canvas, center, top, center + imageData.targetImageSize, bottom, bitmap);
                } else {
                    drawBitmapAtPosition(canvas, imageData.gap * i + imageData.targetImageSize * (i - 1), t_center, imageData.gap * i + imageData.targetImageSize * i, t_center + imageData.targetImageSize, bitmap);
                }
            } else if (size == 4) {
                drawBitmapAtPosition(canvas, left, top, right, bottom, bitmap);
            } else if (size == 5) {
                if (i == 0) {
                    drawBitmapAtPosition(canvas, r_center - imageData.targetImageSize, r_center - imageData.targetImageSize, r_center, r_center, bitmap);
                } else if (i == 1) {
                    drawBitmapAtPosition(canvas, l_center, r_center - imageData.targetImageSize, l_center + imageData.targetImageSize, r_center, bitmap);
                } else {
                    drawBitmapAtPosition(canvas, imageData.gap * (i - 1) + imageData.targetImageSize * (i - 2), t_center, imageData.gap * (i - 1) + imageData.targetImageSize * (i - 1), t_center +
                            imageData.targetImageSize, bitmap);
                }
            } else if (size == 6) {
                if (i < 3) {
                    drawBitmapAtPosition(canvas, imageData.gap * (i + 1) + imageData.targetImageSize * i, b_center - imageData.targetImageSize, imageData.gap * (i + 1) + imageData.targetImageSize * (i + 1), b_center, bitmap);
                } else {
                    drawBitmapAtPosition(canvas, imageData.gap * (i - 2) + imageData.targetImageSize * (i - 3), t_center, imageData.gap * (i - 2) + imageData.targetImageSize * (i - 2), t_center +
                            imageData.targetImageSize, bitmap);
                }
            } else if (size == 7) {
                if (i == 0) {
                    drawBitmapAtPosition(canvas, center, imageData.gap, center + imageData.targetImageSize, imageData.gap + imageData.targetImageSize, bitmap);
                } else if (i > 0 && i < 4) {
                    drawBitmapAtPosition(canvas, imageData.gap * i + imageData.targetImageSize * (i - 1), center, imageData.gap * i + imageData.targetImageSize * i, center + imageData.targetImageSize, bitmap);
                } else {
                    drawBitmapAtPosition(canvas, imageData.gap * (i - 3) + imageData.targetImageSize * (i - 4), t_center + imageData.targetImageSize / 2, imageData.gap * (i - 3) + imageData.targetImageSize * (i - 3), t_center + imageData.targetImageSize / 2 + imageData.targetImageSize, bitmap);
                }
            } else if (size == 8) {
                if (i == 0) {
                    drawBitmapAtPosition(canvas, r_center - imageData.targetImageSize, imageData.gap, r_center, imageData.gap + imageData.targetImageSize, bitmap);
                } else if (i == 1) {
                    drawBitmapAtPosition(canvas, l_center, imageData.gap, l_center + imageData.targetImageSize, imageData.gap + imageData.targetImageSize, bitmap);
                } else if (i > 1 && i < 5) {
                    drawBitmapAtPosition(canvas, imageData.gap * (i - 1) + imageData.targetImageSize * (i - 2), center, imageData.gap * (i - 1) + imageData.targetImageSize * (i - 1), center + imageData.targetImageSize, bitmap);
                } else {
                    drawBitmapAtPosition(canvas, imageData.gap * (i - 4) + imageData.targetImageSize * (i - 5), t_center + imageData.targetImageSize / 2, imageData.gap * (i - 4) + imageData.targetImageSize * (i - 4), t_center + imageData.targetImageSize / 2 + imageData.targetImageSize, bitmap);
                }
            } else if (size == 9) {
                drawBitmapAtPosition(canvas, left, top, right, bottom, bitmap);
            }
        }
    }

    /**
     * 根据坐标画图
     *
     * @param canvas
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param bitmap
     */
    public void drawBitmapAtPosition(Canvas canvas, int left, int top, int right, int bottom, Bitmap bitmap) {
        if (null == bitmap) {
            //图片为空用默认图片
            //设置过默认id
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.index_image);
        }
        if (null != bitmap) {
            Rect rect = new Rect(left, top, right, bottom);
            canvas.drawBitmap(bitmap, null, rect, null);
        }
    }

    public Bitmap synthesizeImageList(MultiImageData imageData) {
        Bitmap mergeBitmap = Bitmap.createBitmap(imageData.maxWidth, imageData.maxHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mergeBitmap);
        drawDrawable(canvas, imageData);
        canvas.save();
        canvas.restore();
        return mergeBitmap;
    }
    public File saveFile(Bitmap bm, String fileName) throws IOException {//将Bitmap类型的图片转化成file类型，便于上传到服务器
        String path = Environment.getExternalStorageDirectory() + "/Ask";
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myCaptureFile;
    }
    private void uploadHeadIconFile(File file,String groupName,String groupNameList) {
        String url = Common.UPLOAD;
        Map<String, String> mapParams = new LinkedHashMap<>();
        OkHttp3_0Utils.getInstance().asyncPostSingleOkHttp(url, "file", mapParams, file, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message message = Message.obtain();
                message.what = Common.Interface_err;
                message.obj = "头像上传失败，请稍后重试";
                BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.toastLongMessage("头像上传失败，请稍后重试");
                    }
                });
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

                        UploadBean.DataDTO data = bean.getData();
                        if (data!=null){
                            String url1 = data.getUrl();

                        }


                    } else {

                        BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.toastLongMessage(msg);
                            }
                        });
                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = Common.Interface_err;
                    message.obj = "头像上传失败，请稍后重试";

                    BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.toastLongMessage("头像上传失败，请稍后重试");
                        }
                    });

                }
            }
        });
    }
}
