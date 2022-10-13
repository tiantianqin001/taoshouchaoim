package com.tencent.qcloud.tuikit.tuicontact.ui.pages;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.gyf.immersionbar.ImmersionBar;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.component.TitleBarLayout;
import com.tencent.qcloud.tuicore.component.activities.BaseLightActivity;
import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tuicore.component.interfaces.ITitleBarLayout;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactService;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.bean.GroupInfo;
import com.tencent.qcloud.tuikit.tuicontact.net.Common;
import com.tencent.qcloud.tuikit.tuicontact.presenter.AddMorePresenter;
import com.tencent.qcloud.tuikit.tuicontact.ui.OkHttp3_0Utils;
import com.tencent.qcloud.tuikit.tuicontact.ui.interfaces.IAddMoreActivity;
import com.tencent.qcloud.tuikit.tuicontact.util.EncryptUtil;
import com.tencent.qcloud.tuikit.tuicontact.util.EncryptUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddMoreActivity extends BaseLightActivity implements IAddMoreActivity {

    private static final String TAG = AddMoreActivity.class.getSimpleName();

    private TitleBarLayout mTitleBar;
    private EditText searchEdit;
    private TextView idLabel;
    private TextView notFoundTip;
    private TextView searchBtn;
    private View detailArea;

    private static int READ_REQUEST_CONTACTS = 0x345;

    private ImageView faceImgView;
    private TextView idTextView;
    private TextView groupTypeView;
    private TextView groupTypeTagView;
    private TextView nickNameView;

    private boolean mIsGroup;

    private AddMorePresenter presenter;
    private static String SP_NAME = "huimiaomiao_share_date";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();

        setContentView(R.layout.contact_add_activity);
        if (getIntent() != null) {
            mIsGroup = getIntent().getExtras().getBoolean(TUIContactConstants.GroupType.GROUP);
        }

        presenter = new AddMorePresenter();
        presenter.setAddMoreActivity(this);

        faceImgView = findViewById(R.id.friend_icon);
        idTextView = findViewById(R.id.friend_account);
        groupTypeView = findViewById(R.id.group_type);
        nickNameView = findViewById(R.id.friend_nick_name);
        groupTypeTagView = findViewById(R.id.group_type_tag);

        mTitleBar = findViewById(R.id.add_friend_titlebar);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        mTitleBar.setTitle(mIsGroup ? getResources().getString(R.string.add_group) : getResources().getString(R.string.add_friend), ITitleBarLayout.Position.MIDDLE);
        mTitleBar.getMiddleTitle().setTextColor(getResources().getColor(R.color.text_add_friend));
        mTitleBar.getLeftIcon().setVisibility(View.VISIBLE);
        mTitleBar.getLeftIcon().setBackground(getResources().getDrawable(R.drawable.back));
        mTitleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleBar.getRightGroup().setVisibility(View.GONE);

        searchEdit = findViewById(R.id.search_edit);
        if (mIsGroup) {
            searchEdit.setHint(R.string.hint_search_group_id);
        }



        //我的二维码
        RelativeLayout rl_my_qr_code = findViewById(R.id.rl_my_qr_code);
        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
        userId = sp.getString("userId", "");
        String headUrl = sp.getString("headUrl", "");
        if (!TextUtils.isEmpty(userId)) {
            userId = EncryptUtils.getInstance(this).decrypt(userId);
        }

        Log.i(TAG, "onCreate: " + userId);
        rl_my_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("userId", userId);
                intent.setAction("cn.twle.android.sendbroadcast.GetGeneratec");
                intent.setComponent(new ComponentName("com.pingmo.chengyan",
                        "com.pingmo.chengyan.receiver.GetGeneratecBroadcastReceiver"));
                sendBroadcast(intent);


            }
        });
        //扫一扫 ll_scan
        RelativeLayout ll_scan = findViewById(R.id.ll_scan);
        ll_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送广播
                Intent intent = new Intent();
                intent.setAction("cn.twle.android.sendbroadcast.MS_BROADCAST");
                intent.setComponent(new ComponentName("com.pingmo.chengyan",
                        "com.pingmo.chengyan.receiver.ScanBroadcastReceiver"));
                sendBroadcast(intent);

            }
        });

        //手机联系人
        RelativeLayout rl_phone_contact = findViewById(R.id.rl_phone_contact);
        rl_phone_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //**版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取**
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //ContextCompat.checkSelfPermission() 方法 指定context和某个权限 返回PackageManager.PERMISSION_DENIED或者PackageManager.PERMISSION_GRANTED
                    if (ContextCompat.checkSelfPermission(AddMoreActivity.this, android.Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        // 若不为GRANTED(即为DENIED)则要申请权限了
                        // 申请权限 第一个为context 第二个可以指定多个请求的权限 第三个参数为请求码
                        ActivityCompat.requestPermissions(AddMoreActivity.this,
                                new String[]{android.Manifest.permission.READ_CONTACTS},
                                READ_REQUEST_CONTACTS);
                    } else {
                        //权限已经被授予，在这里直接写要执行的相应方法即可
                        intentToContact();
                    }
                } else {
                    // 低于6.0的手机直接访问
                    intentToContact();
                }
            }
        });
        idLabel = findViewById(R.id.id_label);
        notFoundTip = findViewById(R.id.not_found_tip);
        searchBtn = findViewById(R.id.search_button);
        detailArea = findViewById(R.id.friend_detail_area);
        //这个也是搜索 只是现在不用了
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //搜索监听键盘搜索的点击
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    notFoundTip.setVisibility(View.GONE);
                    String id = searchEdit.getText().toString();

                    //判断是不是手机号 todo  要判断开关是不是开 ，同时要判断接口状态不对的问题
                    boolean mobile = isMobile(id);
                    //如果是手机号 要根据手机号获取到用户id  再获取用户好友
                    if (mobile){
                        String url = Common.GET_FRIEND_INFORMATION;
                        Map<String, String> mapParams = new LinkedHashMap<>();
                        mapParams.put("search",id);
                        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                        String token = sp.getString("token", "");
                        token = EncryptUtil.getInstance(AddMoreActivity.this).decrypt(token);
                        Map<String, String> headerParams = new LinkedHashMap<>();
                        headerParams.put("token", token);
                        OkHttp3_0Utils.getInstance().asyncGetHeaderOkHttp(url, mapParams, headerParams,
                                new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                String content = response.body().string();
                                Log.i(TAG, "onResponse: "+content);
                                try {
                                    JSONObject jsonObject = new JSONObject(content);
                                    int code = jsonObject.optInt("code");
                                    if (code == 200){
                                        JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                                        if (jsonObject1!=null){
                                            String userId = jsonObject1.optString("id");

                                            if (mIsGroup) {
                                                presenter.getGroupInfo(userId, new IUIKitCallback<GroupInfo>() {
                                                    @Override
                                                    public void onSuccess(GroupInfo data) {
                                                        setGroupDetail(data);
                                                        detailArea.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Intent intent = new Intent(TUIContactService.getAppContext(), FriendProfileActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                intent.putExtra(TUIContactConstants.ProfileType.CONTENT, data);
                                                                TUIContactService.getAppContext().startActivity(intent);
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onError(String module, int errCode, String errMsg) {
                                                        setNotFound(true);
                                                    }
                                                });
                                                return ;
                                            }

                                            presenter.getUserInfo(userId, new IUIKitCallback<ContactItemBean>() {
                                                @Override
                                                public void onSuccess(ContactItemBean data) {
                                                    setFriendDetail(data.getAvatarUrl(), data.getId(), data.getNickName());
                                                    detailArea.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(TUIContactService.getAppContext(), FriendProfileActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            intent.putExtra(TUIContactConstants.ProfileType.CONTENT, data);
                                                            TUIContactService.getAppContext().startActivity(intent);
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onError(String module, int errCode, String errMsg) {
                                                    setNotFound(false);
                                                }
                                            });

                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                    }else {
                        if (mIsGroup) {
                            presenter.getGroupInfo(id, new IUIKitCallback<GroupInfo>() {
                                @Override
                                public void onSuccess(GroupInfo data) {
                                    setGroupDetail(data);
                                    detailArea.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(TUIContactService.getAppContext(), FriendProfileActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra(TUIContactConstants.ProfileType.CONTENT, data);
                                            TUIContactService.getAppContext().startActivity(intent);
                                        }
                                    });
                                }

                                @Override
                                public void onError(String module, int errCode, String errMsg) {
                                    setNotFound(true);
                                }
                            });
                            return true;
                        }

                        presenter.getUserInfo(id, new IUIKitCallback<ContactItemBean>() {
                            @Override
                            public void onSuccess(ContactItemBean data) {
                                setFriendDetail(data.getAvatarUrl(), data.getId(), data.getNickName());
                                detailArea.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(TUIContactService.getAppContext(), FriendProfileActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra(TUIContactConstants.ProfileType.CONTENT, data);
                                        TUIContactService.getAppContext().startActivity(intent);
                                    }
                                });
                            }

                            @Override
                            public void onError(String module, int errCode, String errMsg) {
                                setNotFound(false);
                            }
                        });
                    }




                    //隐藏软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                    return true;

                }

                return false;
            }
        });

        if (!mIsGroup) {
            idLabel.setText(getString(R.string.contact_my_user_id, TUILogin.getLoginUser()));
        }

        searchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    idLabel.setVisibility(View.GONE);
                }
            }
        });

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(searchEdit.getText())) {
                    detailArea.setVisibility(View.GONE);
                    notFoundTip.setVisibility(View.GONE);
                }
            }
        });

    }


    // 用户权限 申请 的回调方法
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == READ_REQUEST_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intentToContact();
            } else {

                Toast.makeText(AddMoreActivity.this, "授权被禁止", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
    }

    private void intentToContact() {
        // 跳转到联系人界面
        Intent intent = new Intent();
        intent.setAction("android.intent.action.PICK");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("vnd.android.cursor.dir/phone_v2");
        startActivityForResult(intent, 0x30);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x30) {
            if (data != null) {
                Uri uri = data.getData();
                String phoneNum = null;
                String contactName = null;
                // 创建内容解析者
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = null;
                if (uri != null) {
                    cursor = contentResolver.query(uri,
                            new String[]{"display_name", "data1"}, null, null, null);
                }
                while (cursor.moveToNext()) {
                    contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                cursor.close();
                //  把电话号码中的  -  符号 替换成空格
                if (phoneNum != null) {
                    phoneNum = phoneNum.replaceAll("-", " ");
                    // 空格去掉  为什么不直接-替换成"" 因为测试的时候发现还是会有空格 只能这么处理
                    phoneNum = phoneNum.replaceAll(" ", "");
                }

//                editCustomerName.setText(contactName);
//                editContact.setText(phoneNum);
            }
        }
    }

    private void setGroupDetail(GroupInfo groupInfo) {
        int radius = getResources().getDimensionPixelSize(R.dimen.contact_profile_face_radius);
        GlideEngine.loadUserIcon(faceImgView, groupInfo.getFaceUrl(), R.drawable.core_default_group_icon_serious, radius);
        idTextView.setText(groupInfo.getId());
        nickNameView.setText(groupInfo.getGroupName());
        groupTypeTagView.setVisibility(View.VISIBLE);
        groupTypeView.setVisibility(View.VISIBLE);
        groupTypeView.setText(groupInfo.getGroupType());
        detailArea.setVisibility(View.VISIBLE);
    }

    private void setFriendDetail(String faceUrl, String id, String nickName) {
        int radius = getResources().getDimensionPixelSize(R.dimen.contact_profile_face_radius);
        //todo 加载图片地址目前还没有
        GlideEngine.loadUserIcon(faceImgView, faceUrl, radius);
        idTextView.setText(id);
        if (TextUtils.isEmpty(nickName)) {
            nickNameView.setText(id);
        } else {
            nickNameView.setText(nickName);
        }
        groupTypeTagView.setVisibility(View.GONE);
        groupTypeView.setVisibility(View.GONE);
        detailArea.setVisibility(View.VISIBLE);
    }

    private void setNotFound(boolean isGroup) {
        detailArea.setVisibility(View.GONE);
        if (isGroup) {
            notFoundTip.setText(getString(R.string.contact_no_such_group));
        } else {
            notFoundTip.setText(getString(R.string.contact_no_such_user));
        }
        notFoundTip.setVisibility(View.VISIBLE);
    }

    @Override
    public void finish() {
        super.finish();
    }
    //判断是不是又手机号
    public  boolean isMobile(String mobile) {

        String str = mobile;
        String pattern = "^(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57]|19[89]|166)[0-9]{8}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);

        return m.matches();
    }


}
