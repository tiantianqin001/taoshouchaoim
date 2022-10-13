package com.tencent.qcloud.tuikit.tuicontact.ui.pages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.gyf.immersionbar.ImmersionBar;
import com.tencent.imsdk.v2.V2TIMFriendInfo;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.component.TitleBarLayout;
import com.tencent.qcloud.tuicore.component.activities.BaseLightActivity;
import com.tencent.qcloud.tuicore.component.interfaces.ITitleBarLayout;
import com.tencent.qcloud.tuicore.util.ErrorMessageConverter;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.util.TUIContactLog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyServerActivity extends BaseLightActivity {

    private static final String TAG = "MyServerActivity";
    private TitleBarLayout mTitleBar;
    private RecyclerView rv_my_server;

    private List<V2TIMFriendInfo> datas = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();

        setContentView(R.layout.my_server_layout);
        init();

        initData();
    }

    private void initData() {

        V2TIMManager.getFriendshipManager().getFriendList(new V2TIMValueCallback<List<V2TIMFriendInfo>>() {
            @Override
            public void onError(int code, String desc) {
                TUIContactLog.e(TAG, "getFriendList err code = " + code + ", desc = " + ErrorMessageConverter.convertIMError(code, desc));

            }

            @Override
            public void onSuccess(List<V2TIMFriendInfo> v2TIMFriendInfos) {
                if (v2TIMFriendInfos != null && v2TIMFriendInfos.size() > 0) {
                    for (V2TIMFriendInfo friendInfo : v2TIMFriendInfos) {
                        Log.i(TAG, "onSuccess: "+friendInfo.toString());

                        String userID = friendInfo.getUserID();
                        if (userID.equals("13532325")){
                            datas.add(friendInfo);
                            break;

                        }

                    }
                    rv_my_server.setLayoutManager(new LinearLayoutManager(MyServerActivity.this));
                    MyServerAdapter adapter = new MyServerAdapter(MyServerActivity.this,datas, 100);
                    rv_my_server.setAdapter(adapter);

//                    Intent intent = new Intent(TUIContactService.getAppContext(), FriendProfileActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra(TUIContactConstants.ProfileType.CONTENT, contact);
//                    TUIContactService.getAppContext().startActivity(intent);
//                    finish();
                }
            }
        });


    }

    private void init() {
        mTitleBar = findViewById(R.id.main_title_bar);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        mTitleBar.getLeftIcon().setBackground(getResources().getDrawable(R.drawable.back));

        mTitleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleBar.setTitle(getResources().getString(R.string.my_server), ITitleBarLayout.Position.MIDDLE);

        rv_my_server = findViewById(R.id.rv_my_server);
    }
}
