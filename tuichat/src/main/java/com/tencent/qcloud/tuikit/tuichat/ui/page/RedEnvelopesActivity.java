package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.bean.CardBean;
import com.tencent.qcloud.tuikit.tuichat.bean.LoginBean;
import com.tencent.qcloud.tuikit.tuichat.dialoge.CommonDialog;
import com.tencent.qcloud.tuikit.tuichat.ui.view.CustomerKeyboard;
import com.tencent.qcloud.tuikit.tuichat.ui.view.CustomerKeyboardPassWord;
import com.tencent.qcloud.tuikit.tuichat.ui.view.CustomerKeyboardRed;
import com.tencent.qcloud.tuikit.tuichat.ui.view.PasswordEditText;
import com.tencent.qcloud.tuikit.tuichat.util.EncryptUtil;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Node;

import java.lang.reflect.Method;

public class RedEnvelopesActivity extends Activity implements View.OnClickListener {
    private EditText et_mony;
    private TextView tv_more_money;
    private TextView tv_set_ok_money;
    private LinearLayout ll_red_back;
    private RelativeLayout rl_red_envelope_cover;
    private TextView tv_into_red_envelope;
    private String mony;
    public static final int currentCode = 123;

    public static IUIKitCallback mCallBack;
    private String recipientId;
    private EditText et_notes;
    private CustomerKeyboardRed custom_key_board;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //???????????????
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //?????????????????????
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        setContentView(R.layout.red_envelope_layout);
        //???????????????id
        recipientId = getIntent().getStringExtra("recipientId");

        et_mony = findViewById(R.id.et_mony);
        tv_more_money = findViewById(R.id.tv_more_money);
        tv_set_ok_money = findViewById(R.id.tv_set_ok_money);
        ll_red_back = findViewById(R.id.ll_red_back);
        et_notes = findViewById(R.id.et_notes);

        //????????????
        rl_red_envelope_cover = findViewById(R.id.rl_red_envelope_cover);
        //????????????
        tv_into_red_envelope = findViewById(R.id.tv_into_red_envelope);



        //???????????????
        hideKeyboard(et_mony);
        //?????????????????????
        if (android.os.Build.VERSION.SDK_INT > 10) {//4.0?????? danielinbiti
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus",
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(et_mony, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //???????????????????????????
        custom_key_board = findViewById(R.id.custom_key_board);
      LinearLayout  ll_red_base = findViewById(R.id.ll_red_base);

        //?????????
        et_mony.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom_key_board.setVisibility(View.VISIBLE);

            }
        });
  /*      ll_red_base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom_key_board.setVisibility(View.GONE);
            }
        });*/


        initListener();
    }

    private void initListener() {
        ll_red_back.setOnClickListener(this);
        rl_red_envelope_cover.setOnClickListener(this);
        tv_into_red_envelope.setOnClickListener(this);
        custom_key_board.setOnCustomerKeyboardClickListener(new CustomerKeyboardRed.CustomerKeyboardClickListener() {
            @Override
            public void click(String number) {
                String money = "";
                money = et_mony.getText().toString().trim() + number;


                //???????????????????????????0
                if (!TextUtils.isEmpty(money) && money.length() == 2) {
                    String one = money.substring(0, 1);
                    String two = money.substring(1, 2);
                    //????????????????????????0
                    if (one.equals("0")) {
                        if (two.equals("0")) {
                            et_mony.setText("0");

                        } else {
                            //???2?????????
                            if (!number.equals("??????")) {
                                if (number.equals(".")){
                                    et_mony.setText("0.");
                                }else {
                                    et_mony.setText(number);
                                }

                            }
                        }

                        et_mony.setSelection(et_mony.getText().length());
                        return;

                    }
                }

                if (number.equals(".")) {
                    //??????????????????.
                    if (money.length() == 1 ) {
                        et_mony.setText("0.");

                    }else {
                        //.??????????????????
                        if (money.startsWith("0.")){
                            et_mony.setText("0.");
                        }else {

                            String[] split = money.split("\\.");
                            if (split.length == 1){
                                money = split[0] + ".";
                                et_mony.setText(money);
                                et_mony.setSelection(money.length());
                            }else {
                                String twoInfo = split[1];
                                if (twoInfo.length()>2){
                                    twoInfo = twoInfo.substring(0,3);
                                }
                                money = split[0]+"." + twoInfo;
                                et_mony.setText(money);
                                et_mony.setSelection(money.length());
                            }
                        }


                    }
                    et_mony.setSelection(et_mony.getText().length());

                } else {
                    //????????????0.????????????????????????3?????????
                    boolean b = money.startsWith("0.");
                    if (b) {
                        if (money.length() > 4) {
                            money = money.substring(0, 4);
                            et_mony.setText(money);
                            et_mony.setSelection(money.length());
                        } else {
                            et_mony.setText(money);
                            et_mony.setSelection(money.length());
                        }

                    } else {
                        //????????????0 ?????????????????????
                        String[] split = money.split("\\.");
                        if (split.length == 2){
                            String twoInfo = split[1];
                            if (twoInfo.length()>2){
                                twoInfo = twoInfo.substring(0,2);
                            }

                            money = split[0]+"." + twoInfo;
                            et_mony.setText(money);
                            et_mony.setSelection(money.length());
                            return;
                        }
                        if (money.length() >7){
                            et_mony.setText(money.substring(0,7));
                            et_mony.setSelection(money.substring(0,7).length());
                        }else {
                            if (!number.equals("??????")){
                                et_mony.setText(money);
                                et_mony.setSelection(money.length());
                            }

                        }

                    }
                }
            }
            @Override
            public void delete() {
                //???????????????
                String currentText = et_mony.getText().toString().trim();
                if (TextUtils.isEmpty(currentText)) {
                    return;
                }
                currentText = currentText.substring(0, currentText.length() - 1);
                et_mony.setText(currentText);
                et_mony.setSelection(currentText.length());


            }
        });
        et_mony.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                mony = et_mony.getText().toString().trim();
//                if (!TextUtils.isEmpty(mony)){
//                    String substring = mony.substring(0, 1);
//                    if (substring.equals("???")){
//                        mony = mony.substring(1);
//                    }
//                }

                if (TextUtils.isEmpty(mony)){
                    tv_set_ok_money.setText("???"+"0.00");
                    return;
                }
                if (Float.valueOf(mony) >200){
                    tv_more_money.setVisibility(View.VISIBLE);
                }else {
                    //???????????????????????????
                    boolean contains = mony.contains(".");
                    if (contains){
                        //?????????????????????
                      String fist =  mony.substring(0, mony.indexOf("."));
                      String two =  mony.substring(mony.indexOf(".")+1);
                      if (TextUtils.isEmpty(two)){
                          two = "00";
                      }
                        if (two.length()>=2){
                            two = two.substring(0,2);
                        }else if (two.length() == 1){
                            two = two +"0";
                        }
                        tv_set_ok_money.setText("???"+fist+"."+two);

                    }else {
                        tv_set_ok_money.setText("???"+ mony +".00");

                        tv_more_money.setVisibility(View.GONE);
                    }
                }

            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_red_back) {
            finish();
        }
        if (v.getId() == R.id.rl_red_envelope_cover){
            //????????????
            Intent intent = new Intent(this,AddRedEnvelopeActivity.class);
            startActivity(intent);

        }
        if (v.getId() == R.id.tv_into_red_envelope){
            //????????????
            //??????????????????
            if (TextUtils.isEmpty(mony)){
                ToastUtils.show("????????????????????????");
                return;
            }
            if (Double.valueOf(mony) > 200){
                return;
            }

            String notes = et_notes.getText().toString();
            if (TextUtils.isEmpty(notes)){
                notes = "???????????????????????????";
            }

             CommonDialog.Builder builder = new CommonDialog.Builder(RedEnvelopesActivity.this).fullWidth().fromBottom()
                    .setView(R.layout.dialog_customer_keyboard_red);
            builder.setOnClickListener(R.id.delete_dialog, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                }
            });
            builder.create().show();
            CustomerKeyboardPassWord mCustomerKeyboard = builder.getView(R.id.custom_key_board);
            PasswordEditText mPasswordEditText = builder.getView(R.id.password_edit_text);
            //????????????
          TextView tv_money =  builder.getView(R.id.tv_money);
          if (!TextUtils.isEmpty(mony)){
              tv_money.setText(mony);
          }
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
            //?????????????????????
            String finalNotes = notes;
            mPasswordEditText.setOnPasswordFullListener(new PasswordEditText.PasswordFullListener() {
                @Override
                public void passwordFull(String password) {

                    if (mCallBack!=null){
                        CardBean cardBean = new CardBean();
                        cardBean.cardPassword = password;
                        cardBean.money = mony;
                        cardBean.note = finalNotes;
                        cardBean.recipientId = recipientId;
                    mCallBack.onSuccess(cardBean);
                        finish();
                    }
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == currentCode){
            //??????
            String cardPassword = data.getStringExtra("cardPassword");
            String money = data.getStringExtra("money");
            String note = data.getStringExtra("note");
            String recipientId = data.getStringExtra("recipientId");
            if (TextUtils.isEmpty(cardPassword)){
                return;
            }
            if(cardPassword.equals("error")){
                return;
            }

            // ????????????????????????????????????????????????
            if (mCallBack!=null){

                Intent intent = getIntent();
                intent.putExtra("cardPassword", cardPassword);
                intent.putExtra("money", money);
                intent.putExtra("note", note);
                intent.putExtra("recipientId", recipientId);

                mCallBack.onSuccess(intent);
            }
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
