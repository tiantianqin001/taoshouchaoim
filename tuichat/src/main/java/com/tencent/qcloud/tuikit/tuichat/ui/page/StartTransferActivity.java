package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.ErrorMessageConverter;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.bean.CardBean;
import com.tencent.qcloud.tuikit.tuichat.dialoge.CommonDialog;
import com.tencent.qcloud.tuikit.tuichat.setting.KeyBoardUtil;
import com.tencent.qcloud.tuikit.tuichat.ui.view.CustomerKeyboard;
import com.tencent.qcloud.tuikit.tuichat.ui.view.CustomerKeyboardPassWord;
import com.tencent.qcloud.tuikit.tuichat.ui.view.DIYKeyboardView;
import com.tencent.qcloud.tuikit.tuichat.ui.view.NoteContentEditText;
import com.tencent.qcloud.tuikit.tuichat.ui.view.PasswordEditText;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactService;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.ui.pages.FriendProfileActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
public class StartTransferActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "StartTransferActivity";
    public static IUIKitCallback mCallBack;
    private CustomerKeyboard contomer_board;
    private NoteContentEditText et_input_money;
    private EditText et_transfer_description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        EventBus.getDefault().register(this);
        setContentView(R.layout.start_transfer_layout);
        String chatName = getIntent().getStringExtra("chatName");
        String toUserId = getIntent().getStringExtra("toUserId");
        et_input_money = findViewById(R.id.et_input_money);
        et_transfer_description = findViewById(R.id.et_transfer_description);
        //隐藏软键盘
        hideKeyboard(et_input_money);
        //禁止软键盘弹出
        if (android.os.Build.VERSION.SDK_INT > 10) {//4.0以上 danielinbiti
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus",
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(et_input_money, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        et_input_money.setOnSelectEndPositionListener(new NoteContentEditText.SelectEndPositionListener() {
            @Override
            public void lastPosition() {
                et_input_money.setSelection(et_input_money.getText().length());
            }
        });


        ImageView iv_avatar = findViewById(R.id.iv_avatar);
        TextView tv_nink_name = findViewById(R.id.tv_nink_name);
        findViewById(R.id.ll_transfer_back).setOnClickListener(this);


        List<String> userIdList = new ArrayList<>();
        userIdList.add(toUserId);
        V2TIMManager.getInstance().getUsersInfo(userIdList, new V2TIMValueCallback<List<V2TIMUserFullInfo>>() {
            @Override
            public void onSuccess(List<V2TIMUserFullInfo> v2TIMUserFullInfos) {
                if (v2TIMUserFullInfos.isEmpty()) {
                    Log.e(TAG, "get logined userInfo failed. list is empty");
                    return;
                }
                V2TIMUserFullInfo userFullInfo = v2TIMUserFullInfos.get(0);
                if (userFullInfo != null) {
                    String nickName = userFullInfo.getNickName();
                    String faceUrl = userFullInfo.getFaceUrl();
                    Glide.with(StartTransferActivity.this)
                            .asBitmap()
                            .load(faceUrl)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .error(R.drawable.image_index)
                            .into(iv_avatar);
                    if (!TextUtils.isEmpty(nickName)) {
                        tv_nink_name.setText(nickName + "(**" + nickName.substring(nickName.length() - 1) + ")");
                    }

                }

            }

            @Override
            public void onError(int code, String desc) {
                Log.e(TAG, "get logined userInfo failed. code : " + code + " desc : " + ErrorMessageConverter.convertIMError(code, desc));
            }
        });

        contomer_board = findViewById(R.id.contomer_board);
        contomer_board.setOnCustomerKeyboardClickListener(new CustomerKeyboard.CustomerKeyboardClickListener() {
            @Override
            public void click(String number) {
                String money = "";
                if (number.equals("转账")){
                    money = et_input_money.getText().toString().trim();
                }else {
                    money = et_input_money.getText().toString().trim() + number;
                }
                //如果刚开始输入的是0
                if (!TextUtils.isEmpty(money) && money.length() == 2) {
                    String one = money.substring(0, 1);
                    String two = money.substring(1, 2);
                    //第一位输入了一个0
                    if (one.equals("0")) {
                        if (two.equals("0")) {
                            et_input_money.setText("0");

                        } else {
                            //第2位是点
                           // et_input_money.setText("0.");
                            if (!number.equals("转账")) {
                                if (number.equals(".")){
                                    et_input_money.setText("0.");
                                }else {
                                    et_input_money.setText(number);
                                }
                            }
                        }
                        et_input_money.setSelection(et_input_money.getText().length());
                        return;
                    }
                }
                if (number.equals(".")) {
                    //刚开始输入了.
                    if (money.length() == 1 ) {
                        et_input_money.setText("0.");
                    }else {
                        //.后面截取两位
                        if (money.startsWith("0.")){
                            et_input_money.setText("0.");
                        }else {
                            Log.i(TAG, "click: "+money);
                          //  money = money + "-1";
                        String[] split = money.split("\\.");
                        if (split.length == 1){
                            money = split[0] + ".";
                            et_input_money.setText(money);
                            et_input_money.setSelection(money.length());
                        }else {
                            String twoInfo = split[1];
                            if (twoInfo.length()>2){
                                twoInfo = twoInfo.substring(0,3);
                            }
                            money = split[0]+"." + twoInfo;
                            et_input_money.setText(money);
                            et_input_money.setSelection(money.length());
                          }
                        }

                    }
                    et_input_money.setSelection(et_input_money.getText().length());

                } else if (number.equals("转账")) {

                    //开始转账
                    String info = et_input_money.getText().toString().trim();
                    if (TextUtils.isEmpty(info)) {
                        ToastUtils.show("请先填写金额");
                        return;
                    }
//                    Intent intent = new Intent(StartTransferActivity.this, IntoenvelopeActivity.class);
//                    intent.putExtra("chatName",chatName);
//                    intent.putExtra("mony",info);
//                    intent.putExtra("recipientId",toUserId);
//                   startActivity(intent);

                    final CommonDialog.Builder builder = new CommonDialog.Builder(StartTransferActivity.this).fullWidth().fromBottom()
                            .setView(R.layout.dialog_customer_keyboard_transfer);
                    builder.setOnClickListener(R.id.delete_dialog, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                        }
                    });
                    builder.create().show();

                    final CustomerKeyboardPassWord mCustomerKeyboard = builder.getView(R.id.custom_key_board);
                    final PasswordEditText mPasswordEditText = builder.getView(R.id.password_edit_text);
                    final LinearLayoutCompat llc_use_money = builder.getView(R.id.llc_use_money);
                    final TextView tv_use = builder.getView(R.id.tv_use);
                    final TextView tv_money = builder.getView(R.id.tv_money);
                    tv_money.setText(info);
                    tv_use.setText("向" + chatName + "转账");

                    mCustomerKeyboard.setOnCustomerKeyboardClickListener(new CustomerKeyboardPassWord.CustomerKeyboardClickListener() {
                        @Override
                        public void click(String number) {
                            mPasswordEditText.addPassword(number);
                        }

                        @Override
                        public void delete() {
                            mPasswordEditText.deleteLastPassword();
                        }
                    });
                    //密码填满的监听
                    mPasswordEditText.setOnPasswordFullListener(new PasswordEditText.PasswordFullListener() {
                        @Override
                        public void passwordFull(String password) {
                            CardBean cardBean = new CardBean();
                            cardBean.cardPassword = password;
                            cardBean.money = info;
                            cardBean.note = "你发起了一笔转账";
                            cardBean.recipientId = toUserId;
                            EventBus.getDefault().post(cardBean);
                        }
                    });

                } else {



                    //如果是已0.开头的最多能输入3个小数
                    boolean b = money.startsWith("0.");
                    if (b) {
                        if (money.length() > 4) {
                            money = money.substring(0, 4);
                            et_input_money.setText(money);
                            et_input_money.setSelection(money.length());
                        } else {
                            et_input_money.setText(money);
                            et_input_money.setSelection(money.length());
                        }

                    } else {
                        //判断是非0 开头的小数的钱
                        String[] split = money.split("\\.");
                        if (split.length == 2){
                            String twoInfo = split[1];
                            if (twoInfo.length()>2){
                                twoInfo = twoInfo.substring(0,2);
                            }

                            money = split[0]+"." + twoInfo;
                            et_input_money.setText(money);
                            et_input_money.setSelection(money.length());
                            return;
                        }
                        if (money.length() >7){
                            et_input_money.setText(money.substring(0,7));
                            et_input_money.setSelection(money.substring(0,7).length());
                        }else {
                            et_input_money.setText(money);
                            et_input_money.setSelection(money.length());
                        }

                    }
                }
            }
            @Override
            public void delete() {
                //点击了删除
                String currentText = et_input_money.getText().toString().trim();
                if (TextUtils.isEmpty(currentText)) {
                    return;
                }
                currentText = currentText.substring(0, currentText.length() - 1);
                et_input_money.setText(currentText);
                et_input_money.setSelection(currentText.length());


            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CardBean cardBean) {
        Log.i(TAG, "onMessageEvent: " + cardBean);
        // Do something
        if (mCallBack != null) {
            mCallBack.onSuccess(cardBean);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_transfer_back) {
            finish();
        }
    }

    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}
