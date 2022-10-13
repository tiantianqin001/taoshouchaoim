package com.pingmo.chengyan.activity.persion;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.activity.zxing.ScanGeneratectivity;
import com.pingmo.chengyan.bean.LoginBean;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;
import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PersonalInformationActivity extends BaseActivity implements View.OnClickListener {
    LoginBean.DataDTO mUser;
    private Dialog bottomDialog;

    private ImageView mHead;
    private TextView mName;
    private TextView mId;
    private TextView mSex;
    private TextView mPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personnal_infomation_layout);
        getTitleBar().setTitle("个人信息");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        mHead = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        mId = findViewById(R.id.id);
        mSex = findViewById(R.id.sex);
        mPhone = findViewById(R.id.phone);
        findViewById(R.id.rl_my_avatar).setOnClickListener(this);
        findViewById(R.id.rl_nick_name).setOnClickListener(this);
        findViewById(R.id.rl_sex).setOnClickListener(this);
        findViewById(R.id.rl_qt_code).setOnClickListener(this);
        addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String userInfo = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("user_info");
        if (!TextUtils.isEmpty(userInfo)) {
            mUser = JSON.parseObject(userInfo, LoginBean.DataDTO.class);
            int radius = getResources().getDimensionPixelSize(R.dimen.qb_px_34);
            GlideEngine.loadUserIcon(mHead, mUser.getHeadImage(), radius);
            mName.setText(mUser.getNickName());
            mId.setText("" + mUser.getUserId());
            mSex.setText(mUser.getGender() == 0 ? "男" : mUser.getGender() == 1 ? "女" : "未知");
            mPhone.setText(mUser.getPhone());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_my_avatar:
                //头像的点击
                Intent intent = new Intent(this, PotoActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_nick_name:
                Intent intent1 = new Intent(this, NickActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_qt_code:
                //二维码
                if (mUser != null) {
                    Intent intent2 = new Intent(PersonalInformationActivity.this, ScanGeneratectivity.class);
                    // 要传userid
                    intent2.putExtra("userId", mUser.getUserId() + "");
                    intent2.putExtra("photo", mUser.getHeadImage());
                    intent2.putExtra("nameTxt", mUser.getNickName());
                    startActivity(intent2);
                }
                break;
            case R.id.rl_sex:
                showDialoge();
                break;
            case R.id.tv_dialoge_photo:
                if (bottomDialog != null) {
                    bottomDialog.dismiss();
                    bottomDialog = null;
                }
                modifySex(0);
                mSex.setText("男");
                break;
            case R.id.tv_save_image:
                if (bottomDialog != null) {
                    bottomDialog.dismiss();
                    bottomDialog = null;
                }
                modifySex(1);
                mSex.setText("女");
                break;
            case R.id.tv_quess:
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
        tv_dialoge_photo.setText("男");


        TextView tv_photograph = bottomDialog.findViewById(R.id.tv_photograph);
        tv_photograph.setOnClickListener(this);


        TextView tv_save_image = bottomDialog.findViewById(R.id.tv_save_image);
        tv_save_image.setOnClickListener(this);
        tv_save_image.setText("女");

        TextView tv_quess = bottomDialog.findViewById(R.id.tv_quess);
        tv_quess.setOnClickListener(this);
    }

    private void modifySex(int sex) {
        showWaitingDlg();
        String deviceName = QZXTools.getSystemModel();
        String deviceId = QZXTools.getDeviceId(this);
        String url = Common.UPDATE_DATA;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("gender", "" + sex);
        Map<String, String> headParams = new LinkedHashMap<>();
        headParams.put("token", mUser.getToken());
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, headParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = Common.Interface_err;
                message.obj = "修改性别失败，请稍后重试";
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
                        message.what = Common.Interface_success;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                        if (mUser != null) {
                            mUser.setGender(sex);
                            SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("user_info", JSON.toJSONString(mUser));
                        }
                    } else {
                        Message message = Message.obtain();
                        message.what = Common.Interface_err;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = Common.Interface_err;
                    message.obj = "修改性别失败，请稍后重试";
                    mHandler.sendMessage(message);
                }


            }
        });
    }

    @Override
    protected void handleActMessage(Message msg) {
        super.handleActMessage(msg);
        switch (msg.what) {
            case Common.Interface_err:
                String failedInfo = (String) msg.obj;
                ToastUtils.show(failedInfo);
                dismissWaitingDlg();
                break;
            case Common.Interface_success:
                dismissWaitingDlg();
                String successInfo = (String) msg.obj;
                ToastUtils.show(successInfo);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }

}
