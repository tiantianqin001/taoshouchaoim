package com.pingmo.chengyan;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;


import com.alibaba.fastjson.JSON;
import com.pingmo.chengyan.activity.login.LoginActivity;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.activity.zxing.CodeScanActivity;
import com.pingmo.chengyan.adapter.FragmentAdapter;
import com.pingmo.chengyan.bean.LoginBean;
import com.pingmo.chengyan.customview.CommonDialog;
import com.pingmo.chengyan.customview.Menu;
import com.pingmo.chengyan.fragment.MeFragment;

import com.pingmo.chengyan.fragment.ShopFragment;

import com.pingmo.chengyan.utils.DemoLog;
import com.pingmo.chengyan.utils.EncryptUtil;

import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;
import com.pingmo.chengyan.utils.TUIUtils;
import com.tencent.imsdk.v2.V2TIMAdvancedMsgListener;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMConversationListener;
import com.tencent.imsdk.v2.V2TIMCustomElem;
import com.tencent.imsdk.v2.V2TIMFriendApplication;
import com.tencent.imsdk.v2.V2TIMFriendApplicationResult;
import com.tencent.imsdk.v2.V2TIMFriendshipListener;
import com.tencent.imsdk.v2.V2TIMGroupInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfo;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMMessageReceipt;
import com.tencent.imsdk.v2.V2TIMValueCallback;

import com.tencent.qcloud.tuicore.TUICore;

import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.component.TitleBarLayout;
import com.tencent.qcloud.tuicore.component.action.PopActionClickListener;
import com.tencent.qcloud.tuicore.component.action.PopMenuAction;
import com.tencent.qcloud.tuicore.component.interfaces.ITitleBarLayout;
import com.tencent.qcloud.tuicore.interfaces.TUILoginListener;
import com.tencent.qcloud.tuicore.util.ErrorMessageConverter;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuichat.bean.GetReadBean;
import com.tencent.qcloud.tuikit.tuichat.bean.GoinGroupBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TipsMessageBean;
import com.tencent.qcloud.tuikit.tuichat.util.ChatMessageParser;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;
import com.tencent.qcloud.tuikit.tuicontact.ui.pages.TUIContactFragment;
import com.pingmo.chengyan.activity.zxing.ScanGeneratectivity;
import com.tencent.qcloud.tuikit.tuiconversation.TUIConversationConstants;
import com.tencent.qcloud.tuikit.tuiconversation.ui.page.TUIConversationFragment;
import com.tencent.qcloud.tuikit.tuigroup.TUIGroupService;
import com.tencent.qcloud.tuikit.tuigroup.net.Common;
import com.tencent.qcloud.tuikit.tuigroup.net.OkHttp3_0Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mConversationBtnText;
    private TextView mContactBtnText;
    private TextView mProfileSelfBtnText;
    private View mConversationBtn;
    private View mContactBtn;
    private View mProfileSelfBtn;
    private ImageView mConversationBtnIcon;
    private ImageView mContactBtnIcon;
    private ImageView mProfileSelfBtnIcon;
    private TextView mMsgUnread;
    private TextView mNewFriendUnread;
    private View mLastTab;

    private TitleBarLayout mainTitleBar;
    private Menu menu;

    private ViewPager2 mainViewPager;
    private static String SP_NAME = "huimiaomiao_share_date";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private List<Fragment> fragments;
    private int count = 0;
    private long lastClickTime = 0;
    private static WeakReference<MainActivity> instance;
    private ImageView contact_find__icon;
    private TextView contact_find;
    private TUIConversationFragment tuiConversationFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        DemoLog.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        instance = new WeakReference<>(this);

        //判断是不是登录
        String token = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token");
        if (QZXTools.isEmpty(token)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else {
            initView();
          //  initTengSdkLogin();
            addActivity(this);
        }
        //初始化数据库
      //  realm = Realm.getDefaultInstance();
        
        //添加单点登录的回调

        try {
            TUILogin.addLoginListener(new TUILoginListener() {
                @Override
                public void onKickedOffline() {
                    super.onKickedOffline();
                    Log.i(TAG, "onKickedOffline: ");

                    CommonDialog commonDialog = new CommonDialog(MainActivity.this);
                    commonDialog.setMessage("你的账号在其他设备登录了");
                    commonDialog.setPositiveButton(new CommonDialog.BtnClickedListener() {
                        @Override
                        public void onBtnClicked() {
                            commonDialog.dismiss();

                            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });
                    commonDialog.setCancelButton(new CommonDialog.BtnClickedListener() {
                        @Override
                        public void onBtnClicked() {
                            commonDialog.dismiss();

                            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });
                    if ( !MainActivity.this.isFinishing()){
                        commonDialog.showDialog();
                    }


                }
            });
        }
        catch (WindowManager.BadTokenException e) {
            Log.i(TAG, "onCreate: "+e.getLocalizedMessage());
            //use a log message
        }


    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        QZXTools.logD("onNewIntent");
        setIntent(intent);
    }
    private void initView() {
        setContentView(R.layout.main_activity);
        mainTitleBar = findViewById(R.id.main_title_bar);
        mainTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        initMenuAction();
        //商城
        mConversationBtnText = findViewById(R.id.conversation);
        mConversationBtnIcon = findViewById(R.id.tab_conversation_icon);
        mConversationBtnText.setTextColor(getResources().getColor(R.color.regist_erification_code));
        mConversationBtnIcon.setBackground(getResources().getDrawable(R.drawable.main_select_find));
        //通讯录
        mContactBtnText = findViewById(R.id.contact);
        mContactBtnIcon = findViewById(R.id.tab_contact_icon);
        //消息
        contact_find = findViewById(R.id.contact_find);
        contact_find__icon = findViewById(R.id.tab_contact_find__icon);
        //我的
        mProfileSelfBtnIcon = findViewById(R.id.tab_profile_icon);
        mProfileSelfBtnText = findViewById(R.id.mine);

        mConversationBtn = findViewById(R.id.conversation_btn_group);
        mContactBtn = findViewById(R.id.contact_btn_group);
        mProfileSelfBtn = findViewById(R.id.myself_btn_group);
        mMsgUnread = findViewById(R.id.msg_total_unread);
        mNewFriendUnread = findViewById(R.id.new_friend_total_unread);

        mainTitleBar.getMiddleTitle().setVisibility(View.GONE);

        fragments = new ArrayList<>();
        //商城
        fragments.add(new ShopFragment());

        //通讯录
        fragments.add(new TUIContactFragment());
        //消息
        tuiConversationFragment = new TUIConversationFragment();
        fragments.add(tuiConversationFragment);
        //我的
       fragments.add(new MeFragment());
      //  fragments.add(new ProfileFragment());
        mainViewPager = findViewById(R.id.view_pager);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(this);
        fragmentAdapter.setFragmentList(fragments);
        // 关闭左右滑动切换页面
        mainViewPager.setUserInputEnabled(false);
        // 设置缓存数量为4 避免销毁重建
        mainViewPager.setOffscreenPageLimit(4);
        mainViewPager.setAdapter(fragmentAdapter);
        mainViewPager.setCurrentItem(0, false);
        setConversationTitleBar();
        if (mLastTab == null) {
            mLastTab = mConversationBtn;
        } else {
            // 初始化时，强制切换tab到上一次的位置
            View tmp = mLastTab;
            mLastTab = null;
            tabClick(tmp);
            mLastTab = tmp;
        }

        prepareToClearAllUnreadMessage();

    }

    private void initMenuAction() {
        int titleBarIconSize = getResources().getDimensionPixelSize(R.dimen.demo_title_bar_icon_size);
        mainTitleBar.getLeftIcon().setMaxHeight(titleBarIconSize);
        mainTitleBar.getLeftIcon().setMaxWidth(titleBarIconSize);
        mainTitleBar.getRightIcon().setMaxHeight(titleBarIconSize);
        mainTitleBar.getRightIcon().setMaxWidth(titleBarIconSize);
        mainTitleBar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menu == null) {
                    return;
                }
                if (menu.isShowing()) {
                    menu.hide();
                } else {
                    menu.show();
                }
            }
        });
        //搜索的点击事件
        mainTitleBar.setOnRightSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              QZXTools.logD("我被点了");

                TUICore.startActivity("SearchMainActivity", new Bundle());
            }
        });
    }
    private void prepareToClearAllUnreadMessage() {
        mMsgUnread.setOnTouchListener(new View.OnTouchListener() {
            private float downX;
            private float downY;
            private boolean isTriggered = false;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = mMsgUnread.getX();
                        downY = mMsgUnread.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isTriggered) {
                            return true;
                        }
                        float viewX = view.getX();
                        float viewY = view.getY();
                        float eventX = event.getX();
                        float eventY = event.getY();
                        float translationX = eventX + viewX - downX;
                        float translationY = eventY + viewY - downY;
                        view.setTranslationX(translationX);
                        view.setTranslationY(translationY);
                        // 移动的 x 和 y 轴坐标超过一定像素则触发一键清空所有会话未读
                        if (Math.abs(translationX) > 200 || Math.abs(translationY) > 200) {
                            isTriggered = true;
                            mMsgUnread.setVisibility(View.GONE);
                            triggerClearAllUnreadMessage();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setTranslationX(0);
                        view.setTranslationY(0);
                        isTriggered = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        isTriggered = false;
                        break;
                }

                return true;
            }
        });
    }

    private void triggerClearAllUnreadMessage() {
        V2TIMManager.getMessageManager().markAllMessageAsRead(new V2TIMCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "markAllMessageAsRead success");
                ToastUtil.toastShortMessage(MainActivity.this.getString(R.string.mark_all_message_as_read_succ));
            }

            @Override
            public void onError(int code, String desc) {
                Log.i(TAG, "markAllMessageAsRead error:" + code + ", desc:" + ErrorMessageConverter.convertIMError(code, desc));
                //ToastUtil.toastShortMessage(MainActivity.this.getString(R.string.mark_all_message_as_read_err_format, code, ErrorMessageConverter.convertIMError(code, desc)));
                mMsgUnread.setVisibility(View.VISIBLE);
            }
        });
    }

    public void tabClick(View view) {
        mLastTab = view;
        //初始化状态
        resetMenuState();
        switch (view.getId()) {
            case R.id.conversation_btn_group:
                //商城
                mainViewPager.setCurrentItem(0, false);
                setProfileTitleBar();
                mConversationBtnText.setTextColor(getResources().getColor(R.color.regist_erification_code));
                mConversationBtnIcon.setBackground(getResources().getDrawable(R.drawable.main_select_find));
                mainTitleBar.getLeftTitle().setText("商城");
                break;
            case R.id.contact_btn_group:
                mainViewPager.setCurrentItem(1, false);
                setMessageTitleBar();
                mContactBtnText.setTextColor(getResources().getColor(R.color.regist_erification_code));
                mContactBtnIcon.setBackground(getResources().getDrawable(R.drawable.main_select_address));

                mainTitleBar.getLeftTitle().setText("通讯录");

                break;
            // <!--消息-->
            case R.id.contact_btn_find_group:
                mainViewPager.setCurrentItem(2, false);
                setMessageTitleBar();
                contact_find.setTextColor(getResources().getColor(R.color.regist_erification_code));
                contact_find__icon.setBackground(getResources().getDrawable(R.drawable.main_select_message));
                mainTitleBar.getLeftTitle().setText("消息");
                tuiConversationFragment.setInitDefaut();
                break;

            case R.id.myself_btn_group:
                mainViewPager.setCurrentItem(3, false);
                setProfileTitleBar();
                mProfileSelfBtnText.setTextColor(getResources().getColor(R.color.regist_erification_code));
                mProfileSelfBtnIcon.setBackground(getResources().getDrawable(R.drawable.main_persion_select));
                mainTitleBar.getLeftTitle().setText("我的");
                break;
            default:
                break;
        }
    }

    private void setConversationTitleBar() {
        mainTitleBar.getLeftTitle().setVisibility(View.VISIBLE);
        mainTitleBar.setTitle(getResources().getString(R.string.conversation_shop), ITitleBarLayout.Position.LEFT);
        mainTitleBar.getLeftTitle().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mainTitleBar.getLeftTitle().setTextColor(getResources().getColor(R.color.color_black_text));
        mainTitleBar.getRightGroup().setVisibility(View.VISIBLE);


        mainTitleBar.getLeftIcon().setVisibility(View.GONE);



    }


    private void setMessageTitleBar() {

        mainTitleBar.getLeftTitle().setVisibility(View.VISIBLE);
        mainTitleBar.setTitle(getResources().getString(R.string.conversation_shop), ITitleBarLayout.Position.LEFT);
        mainTitleBar.getLeftTitle().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mainTitleBar.getLeftTitle().setTextColor(getResources().getColor(R.color.color_black_text));
        mainTitleBar.getRightGroup().setVisibility(View.VISIBLE);


        mainTitleBar.getLeftIcon().setVisibility(View.GONE);

        mainTitleBar.getRightSearchIcon().setVisibility(View.VISIBLE);
        mainTitleBar.setRightSearchIcon(R.mipmap.main_search);

        mainTitleBar.setRightIcon(R.mipmap.title_bar_more_light);
        int titleBarIconSize = getResources().getDimensionPixelSize(R.dimen.demo_title_bar_icon_size);
        ViewGroup.LayoutParams params = mainTitleBar.getRightIcon().getLayoutParams();
        params.width = titleBarIconSize;
        params.height = titleBarIconSize;
        mainTitleBar.getRightIcon().setLayoutParams(params);
        setConversationMenu();
    }

    private void setConversationMenu() {
        //添加好友的点击事件
        menu = new Menu(this, mainTitleBar.getRightIcon());
        PopActionClickListener popActionClickListener = new PopActionClickListener() {
            @Override
            public void onActionClick(int position, Object data) {
                PopMenuAction action = (PopMenuAction) data;
                if (TextUtils.equals(action.getActionName(), getResources().getString(R.string.start_conversation))) {
                    //扫一扫
                    Intent intent = new Intent(MainActivity.this, CodeScanActivity.class);
                    startActivity(intent);
                }

                if (TextUtils.equals(action.getActionName(), getResources().getString(R.string.create_private_group))) {
                    //添加好友
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(TUIContactConstants.GroupType.GROUP, false);
                    TUIUtils.startActivity("AddMoreActivity", bundle);
                }
                //发起群聊
                if (TextUtils.equals(action.getActionName(), getResources().getString(R.string.create_group_chat))) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(TUIConversationConstants.GroupType.TYPE, TUIConversationConstants.GroupType.PUBLIC);
                    TUIUtils.startActivity("StartGroupChatActivity", bundle);
                }
                //我的二维码
                if (TextUtils.equals(action.getActionName(), getResources().getString(R.string.create_chat_room))) {
                    Intent intent = new Intent(MainActivity.this, ScanGeneratectivity.class);
                    // 要传userid
                    sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                    editor = sp.edit();
                    String userId = sp.getString("userId", "");
                    String user_info = sp.getString("user_info", "");
                    user_info = EncryptUtil.getInstance(MainActivity.this).decrypt(user_info);
                    LoginBean.DataDTO  user = JSON.parseObject(user_info, LoginBean.DataDTO.class);
                    //这个userId是加密的
                     userId = EncryptUtil.getInstance(MainActivity.this).decrypt(userId);
                    intent.putExtra("userId", userId);
                    intent.putExtra("photo",user.getHeadImage());
                    intent.putExtra("nameTxt",user.getNickName());
                    startActivity(intent);
                }
                if (TextUtils.equals(action.getActionName(), getResources().getString(R.string.create_community))) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(TUIConversationConstants.GroupType.TYPE, TUIConversationConstants.GroupType.COMMUNITY);
                    TUIUtils.startActivity("StartGroupChatActivity", bundle);
                }
                menu.hide();
            }
        };

        // 设置右上角+号显示PopAction
        List<PopMenuAction> menuActions = new ArrayList<>();

        PopMenuAction action = new PopMenuAction();

        action.setActionName(getResources().getString(R.string.start_conversation));
        action.setActionClickListener(popActionClickListener);
        action.setIconResId(R.mipmap.add_scan);
        menuActions.add(action);
        action = new PopMenuAction();
        action.setActionName(getResources().getString(R.string.create_private_group));
        action.setIconResId(R.mipmap.add_friend);
        action.setActionClickListener(popActionClickListener);
        menuActions.add(action);

        action = new PopMenuAction();
        action.setActionName(getResources().getString(R.string.create_group_chat));
        action.setIconResId(R.mipmap.add_group_chat);
        action.setActionClickListener(popActionClickListener);
        menuActions.add(action);

        action = new PopMenuAction();
        action.setActionName(getResources().getString(R.string.create_chat_room));
        action.setIconResId(R.mipmap.add_qr_code);
        action.setActionClickListener(popActionClickListener);
        menuActions.add(action);


        menu.setMenuAction(menuActions);
    }


    public void setContactMenu() {
        menu = new Menu(this, mainTitleBar.getRightIcon());
        List<PopMenuAction> menuActionList = new ArrayList<>(2);
        PopActionClickListener popActionClickListener = new PopActionClickListener() {
            @Override
            public void onActionClick(int index, Object data) {
                PopMenuAction action = (PopMenuAction) data;
                if (TextUtils.equals(action.getActionName(), getResources().getString(R.string.add_friend))) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(TUIContactConstants.GroupType.GROUP, false);
                    TUIUtils.startActivity("AddMoreActivity", bundle);
                }
                if (TextUtils.equals(action.getActionName(), getResources().getString(R.string.add_group))) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(TUIContactConstants.GroupType.GROUP, true);
                    TUIUtils.startActivity("AddMoreActivity", bundle);
                }
                menu.hide();
            }
        };
        PopMenuAction action = new PopMenuAction();
        action.setActionName(getResources().getString(R.string.add_friend));
        // action.setIconResId(R.drawable.demo_add_friend);
        action.setActionClickListener(popActionClickListener);
        menuActionList.add(action);

        action = new PopMenuAction();
        action.setActionName(getResources().getString(R.string.add_group));
        // action.setIconResId(R.drawable.demo_add_group);
        action.setActionClickListener(popActionClickListener);
        menuActionList.add(action);
        menu.setMenuAction(menuActionList);
    }

    private void setProfileTitleBar() {
        mainTitleBar.getLeftGroup().setVisibility(View.VISIBLE);
        mainTitleBar.getRightGroup().setVisibility(View.GONE);
        mainTitleBar.getRightSearchIcon().setVisibility(View.GONE);
        mainTitleBar.setTitle(getResources().getString(R.string.profile), ITitleBarLayout.Position.LEFT);
    }

    private void setShopTitleBar() {
        mainTitleBar.getLeftGroup().setVisibility(View.VISIBLE);
        mainTitleBar.getRightGroup().setVisibility(View.GONE);
        mainTitleBar.getRightSearchIcon().setVisibility(View.GONE);
        mainTitleBar.setTitle(getResources().getString(R.string.profile), ITitleBarLayout.Position.LEFT);
    }

    private void resetMenuState() {
        // <!--商城-->
        mConversationBtnText.setTextColor(getResources().getColor(R.color.regist_no_erification_code));
        mConversationBtnIcon.setBackground(getResources().getDrawable(R.drawable.main_no_select_find));
        //通讯录
        mContactBtnText.setTextColor(getResources().getColor(R.color.regist_no_erification_code));
        mContactBtnIcon.setBackground(getResources().getDrawable(R.drawable.main_no_select_persion));

        //消息
        contact_find.setTextColor(getResources().getColor(R.color.regist_no_erification_code));
        contact_find__icon.setBackground(getResources().getDrawable(R.drawable.main_no_select_message));
        //我的
        mProfileSelfBtnText.setTextColor(getResources().getColor(R.color.regist_no_erification_code));
        mProfileSelfBtnIcon.setBackground(getResources().getDrawable(R.drawable.main_no_select_my));
    }

    private final V2TIMConversationListener unreadListener = new V2TIMConversationListener() {
        @Override
        public void onTotalUnreadMessageCountChanged(long totalUnreadCount) {
            if (totalUnreadCount > 0) {
                mMsgUnread.setVisibility(View.GONE);
            } else {
                mMsgUnread.setVisibility(View.GONE);
            }
            String unreadStr = "" + totalUnreadCount;
            if (totalUnreadCount > 100) {
                unreadStr = "99+";
            }
            mMsgUnread.setText(unreadStr);
            // 华为离线推送角标
            //  OfflineMessageDispatcher.updateBadge(MainActivity.this, (int) totalUnreadCount);
        }
    };

    private final V2TIMFriendshipListener friendshipListener = new V2TIMFriendshipListener() {
        @Override
        public void onFriendApplicationListAdded(List<V2TIMFriendApplication> applicationList) {
            refreshFriendApplicationUnreadCount();
        }

        @Override
        public void onFriendApplicationListDeleted(List<String> userIDList) {
            refreshFriendApplicationUnreadCount();
        }

        @Override
        public void onFriendApplicationListRead() {
            refreshFriendApplicationUnreadCount();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CommonDialog commonDialog = new CommonDialog(this);
            commonDialog.setMessage("你确定要退出么？");
            commonDialog.setPositiveButton(new CommonDialog.BtnClickedListener() {
                @Override
                public void onBtnClicked() {
                    finish();
                    finishAll();
                }
            });
            commonDialog.setCancelButton(new CommonDialog.BtnClickedListener() {
                @Override
                public void onBtnClicked() {

                }
            });
            commonDialog.showDialog();

        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
    }



    @Override
    protected void onResume() {

        super.onResume();
        registerUnreadListener();
        handleOfflinePush();
    }

    private void registerUnreadListener() {
        //添加会话列表的监听
        V2TIMManager.getConversationManager().addConversationListener(unreadListener);
        V2TIMManager.getConversationManager().getTotalUnreadMessageCount(new V2TIMValueCallback<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                runOnUiThread(() -> unreadListener.onTotalUnreadMessageCountChanged(aLong));
            }

            @Override
            public void onError(int code, String desc) {

            }
        });

        V2TIMManager.getFriendshipManager().addFriendListener(friendshipListener);
        refreshFriendApplicationUnreadCount();

        //设置群聊的监听
        TUIGroupService.getInstance().init(this);


        V2TIMManager.getMessageManager().addAdvancedMsgListener(new V2TIMAdvancedMsgListener() {
            @Override
            public void onRecvNewMessage(V2TIMMessage msg) {
                TUIMessageBean message = ChatMessageParser.parseMessage(msg);
                // 通话信令存在发送 null 的情况，此处加下判断
                if (message == null) {
                    return;
                }
                //群禁言 禁言具体的人保存到本地
                if (message instanceof TipsMessageBean){
                    int tipType = ((TipsMessageBean) message).getTipType();
                    String text = ((TipsMessageBean) message).getText();
                    if (tipType == 263){
                        //禁言 "被禁言"365天"     //0 取消个人禁言   1 添加个人禁言 2 全员禁言 3 取消全员禁言
                        if (text.contains("365天")) {
                            String userID = "";
                            Log.i(TAG, "onRecvNewMessage: 个人被禁言");
                            List<V2TIMGroupMemberInfo> memberList = msg.getGroupTipsElem().getMemberList();
                            if (memberList != null && memberList.size() > 0) {
                                V2TIMGroupMemberInfo v2TIMGroupMemberInfo = memberList.get(0);
                                userID = v2TIMGroupMemberInfo.getUserID();

                            }
                        }

                        }else if (text.contains("被取消禁言")){
                            Log.i(TAG, "onRecvNewMessage: 个人取消禁言");


                    }
                }

                //判断当前消息是不是群拉人消息
                V2TIMCustomElem customElem = message.getV2TIMMessage().getCustomElem();
                if (customElem!=null){
                    String groupID = message.getV2TIMMessage().getGroupID();
                    byte[] data = customElem.getData();
                    if (data!=null && !TextUtils.isEmpty(groupID)){
                        String elemString = new String(data);
                        if (!TextUtils.isEmpty(elemString)){
                            try {
                                JSONObject jsonObject = new JSONObject(elemString);
                                String businessID = jsonObject.optString("businessID");
                                if (businessID.equals("group_invitation")){
                                    Log.i(TAG, "onRecvNewMessage: "+elemString);
                                    JSONArray members = jsonObject.optJSONArray("members");
                                    String numbersList = members.toString();
                                    Log.i(TAG, "onRecvNewMessage: "+numbersList);




                                }else if (businessID.equals("group_create_custom")){
                                    //创建群消息 要添加自定义消息 创建一个群

                                 String   token = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token");
                                    String url = Common.UPDATA_GROUP;

                                    Map<String , String> headerParams = new LinkedHashMap<>();
                                    headerParams.put("token", token);
                                    JSONObject jsonObject3 = new JSONObject();
                                    try {
                                        jsonObject3.put("groupId", Integer.valueOf(groupID));
                                        jsonObject3.put("invitationStatus", 1);
                                        jsonObject3.put("groupMemberProtect", 0);
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
                                                        sendBroadcast(intent);
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
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            @Override
            public void onRecvMessageReadReceipts(List<V2TIMMessageReceipt> receiptList) {

            }

            @Override
            public void onRecvMessageRevoked(String msgId) {

            }

            @Override
            public void onRecvMessageModified(V2TIMMessage msg) {

            }
        });
    }

    private void refreshFriendApplicationUnreadCount() {
        V2TIMManager.getFriendshipManager().getFriendApplicationList(new V2TIMValueCallback<V2TIMFriendApplicationResult>() {
            @Override
            public void onSuccess(V2TIMFriendApplicationResult v2TIMFriendApplicationResult) {
                QZXTools.logE(v2TIMFriendApplicationResult.getFriendApplicationList().toString(), null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int unreadCount = v2TIMFriendApplicationResult.getUnreadCount();
                        if (unreadCount > 0) {
                            mNewFriendUnread.setVisibility(View.VISIBLE);
                        } else {
                            mNewFriendUnread.setVisibility(View.GONE);
                        }
                        String unreadStr = "" + unreadCount;
                        if (unreadCount > 100) {
                            unreadStr = "99+";
                        }
                        mNewFriendUnread.setText(unreadStr);
                    }
                });
            }

            @Override
            public void onError(int code, String desc) {
                QZXTools.logE(desc, null);
            }
        });
    }

    private void handleOfflinePush() {
//        if (V2TIMManager.getInstance().getLoginStatus() == V2TIMManager.V2TIM_STATUS_LOGOUT) {
//            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
//            if (getIntent() != null) {
//                intent.setData(getIntent().getData());
//                intent.putExtras(getIntent());
//            }
//            startActivity(intent);
//            finish();
//            return;
//        }
//
//        final OfflineMessageBean bean = OfflineMessageDispatcher.parseOfflineMessage(getIntent());
//        if (bean != null) {
//            setIntent(null);
//            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            if (manager != null) {
//                manager.cancelAll();
//            }
//
//            if (bean.action == OfflineMessageBean.REDIRECT_ACTION_CHAT) {
//                if (TextUtils.isEmpty(bean.sender)) {
//                    return;
//                }
//                TUIUtils.startChat(bean.sender, bean.nickname, bean.chatType);
//            }
//        }
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }

    @Override
    protected void onPause() {
        DemoLog.i(TAG, "onPause");
        super.onPause();
        V2TIMManager.getConversationManager().removeConversationListener(unreadListener);
        V2TIMManager.getFriendshipManager().removeFriendListener(friendshipListener);
    }


    @Override
    protected void onDestroy() {
        mLastTab = null;
        removeActivity(this);
        super.onDestroy();


    }


    public static void finishMainActivity() {
        if (instance != null && instance.get() != null) {
            instance.get().finish();
        }
    }


}
