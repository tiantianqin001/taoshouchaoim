package com.pingmo.chengyan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.persion.CommonWebviewActivity;
import com.pingmo.chengyan.activity.persion.FeedbackActivity;
import com.pingmo.chengyan.activity.persion.OrderActivity;
import com.pingmo.chengyan.activity.persion.PersonalInformationActivity;
import com.pingmo.chengyan.activity.persion.SafeActivity;
import com.pingmo.chengyan.activity.persion.SettingActivity;
import com.pingmo.chengyan.activity.persion.WalletActivity;
import com.pingmo.chengyan.bean.LoginBean;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.tencent.imsdk.v2.V2TIMValueCallback;

import java.util.ArrayList;
import java.util.List;

import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MeFragment extends Fragment implements View.OnClickListener {

    private ImageView mHead;
    private TextView mName;
    private TextView mId;
    private ImageView mSex;
    private TextView mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_fragment, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String userInfo = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("user_info");
        String headImage = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("headImage");
        if (!TextUtils.isEmpty(userInfo)) {
            LoginBean.DataDTO user = JSON.parseObject(userInfo, LoginBean.DataDTO.class);
            int radius = getResources().getDimensionPixelSize(R.dimen.qb_px_34);
            if (!TextUtils.isEmpty(headImage)){
                GlideEngine.loadUserIcon(mHead,user.getHeadImage(), radius);
            }

            mName.setText(user.getNickName());
            mId.setText("城言号：" + user.getUserId());
            mAuth.setVisibility(user.getRealStatus() == 2 ? View.VISIBLE : View.GONE);
        }
    }

    private void initView(View view) {
        mHead = view.findViewById(R.id.avatar);
        mName = view.findViewById(R.id.name);
        mId = view.findViewById(R.id.id);
        mSex = view.findViewById(R.id.gender);
        mAuth = view.findViewById(R.id.auth);
        view.findViewById(R.id.rl_persion_info).setOnClickListener(this);
        view.findViewById(R.id.ll_wallet).setOnClickListener(this);
        view.findViewById(R.id.order).setOnClickListener(this);
        view.findViewById(R.id.helper).setOnClickListener(this);
        view.findViewById(R.id.safe).setOnClickListener(this);
        view.findViewById(R.id.setting).setOnClickListener(this);
        view.findViewById(R.id.report).setOnClickListener(this);
        String userId = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("userId");
        List<String> userIdList = new ArrayList<>();
        userIdList.add(userId);
        V2TIMManager.getInstance().getUsersInfo(userIdList, new V2TIMValueCallback<List<V2TIMUserFullInfo>>() {
            @Override
            public void onSuccess(List<V2TIMUserFullInfo> v2TIMUserFullInfos) {
                //V2TIMUserFullInfo v2TIMUserFullInfo = v2TIMUserFullInfos.get(0);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_persion_info:
                Intent intent = new Intent(getContext(), PersonalInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_wallet:
                //钱包
                Intent intent1 = new Intent(getContext(), WalletActivity.class);
                startActivity(intent1);
                break;
            case R.id.order:
                Intent intentOrder = new Intent(getContext(), OrderActivity.class);
                startActivity(intentOrder);
                break;
            case R.id.report:
                //意见反馈
                Intent intentFeed = new Intent(getContext(), FeedbackActivity.class);
                startActivity(intentFeed);
                break;
            case R.id.helper:
                //String url = Common.SEALTALK_SERVER + "/app/help";
                String url = Common.BaseUrl + "/api/help";
                Intent intentHelper = new Intent(getContext(), CommonWebviewActivity.class);
                intentHelper.putExtra(CommonWebviewActivity.PAGE_TITLE, "帮助中心");
                intentHelper.putExtra(CommonWebviewActivity.PAGE_URL, url);
                startActivity(intentHelper);
                break;
            case R.id.safe:
                Intent intentSafe = new Intent(getContext(), SafeActivity.class);
                startActivity(intentSafe);
                break;
            case R.id.setting:
                Intent intentSetting = new Intent(getContext(), SettingActivity.class);
                startActivity(intentSetting);
                break;
        }
    }
}
