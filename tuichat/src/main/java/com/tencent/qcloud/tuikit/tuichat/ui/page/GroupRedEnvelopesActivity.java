package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.bean.CardBean;
import com.tencent.qcloud.tuikit.tuichat.dialoge.CommonDialog;
import com.tencent.qcloud.tuikit.tuichat.ui.view.CustomerKeyboardPassWord;
import com.tencent.qcloud.tuikit.tuichat.ui.view.PasswordEditText;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;

public class GroupRedEnvelopesActivity extends Activity implements View.OnClickListener {
    private EditText et_mony;
    private TextView tv_more_money;
    private TextView tv_set_ok_money;
    private LinearLayout ll_red_back;
    private RelativeLayout rl_red_envelope_cover;
    public static IUIKitCallback mCallBack;
    private EditText et_all_money;

    public static final int currentCode = 123;
    private String mony;
    private EditText et_notes;
    private String count;
    private TextView tv_luck;
    private LinearLayout ll_show_luck;
    private RelativeLayout rl_orientation;
    private String groupId;
    private TextView tv_name;
    private ImageView iv_avter;

    //????????????????????????????????????
    private boolean isExclusiveGroup = true;
    private String userId;
    private String ninkName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //???????????????
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //?????????????????????
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        setContentView(R.layout.group_red_envelope_layout);

        et_mony = findViewById(R.id.et_mony);
        et_notes = findViewById(R.id.et_notes);
        tv_more_money = findViewById(R.id.tv_more_money);
        TextView tv_envelop_count = findViewById(R.id.tv_envelop_count);
        tv_set_ok_money = findViewById(R.id.tv_set_ok_money);
        ll_red_back = findViewById(R.id.ll_red_back);
        //?????????
        et_all_money = findViewById(R.id.et_all_money);
        //????????????
        rl_red_envelope_cover = findViewById(R.id.rl_red_envelope_cover);
        //???????????????
        TextView tv_red_envelope = findViewById(R.id.tv_red_envelope);
        tv_red_envelope.setOnClickListener(this);
        //??????????????????????????????
        tv_luck = findViewById(R.id.tv_luck);
        tv_luck.setOnClickListener(this);

        //?????????????????? ??????????????????????????????view
        ll_show_luck = findViewById(R.id.ll_show_luck);
        //????????????
        rl_orientation = findViewById(R.id.rl_orientation);
        //?????????
        tv_name = findViewById(R.id.tv_name);
      //??????????????????
        iv_avter = findViewById(R.id.iv_avter);
        //????????????????????????
        rl_orientation.setOnClickListener(this);


        int groupCount = getIntent().getIntExtra("groupCount", 0);
        groupId = getIntent().getStringExtra("groupId");
        tv_envelop_count.setText("?????????" + groupCount + "???");
        initListener();

    }

    private void initListener() {
        ll_red_back.setOnClickListener(this);
        rl_red_envelope_cover.setOnClickListener(this);
        //????????????????????????????????????????????????????????????
        et_mony.setOnClickListener(this);
        et_all_money.setOnClickListener(this);

        et_all_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                count = et_mony.getText().toString().trim();
                mony = et_all_money.getText().toString().trim();
                if (TextUtils.isEmpty(mony)) {
                    return;
                }
                if (isExclusiveGroup){
                    //????????????????????????????????????
                    if (TextUtils.isEmpty(count)) {
                        ToastUtils.show("????????????????????????");
                        return;
                    }
                }else {
                    count = "1";
                }


                if (Float.valueOf(mony) > (200 * Float.valueOf(count))) {
                    tv_more_money.setVisibility(View.VISIBLE);
                } else {
                    tv_set_ok_money.setText("???" + mony + "");
                    tv_more_money.setVisibility(View.GONE);
                }

            }
        });
    }

    int position = 0;

    @Override
    public void onClick(View v) {
        //????????????????????????
        if (v.getId() == R.id.rl_orientation){
            Intent intent = new Intent(this,OrigintationActivity.class);
            intent.putExtra(TUIContactConstants.Group.GROUP_ID,groupId);
           startActivityForResult(intent,100);


        }
        //??????????????????????????????
        if (v.getId() == R.id.tv_luck) {
            showDialoge();
        }
        if (v.getId() == R.id.ll_red_back) {
            hideKeyboard(ll_red_back);
            finish();
        }
        if (v.getId() == R.id.rl_red_envelope_cover) {
            //????????????
            Intent intent = new Intent(this, AddRedEnvelopeActivity.class);
            startActivity(intent);

        }
        if (v.getId() == R.id.tv_red_envelope) {
            //???????????????
            //??????????????????
            if (TextUtils.isEmpty(mony)) {
                ToastUtils.show("????????????????????????");
                return;
            }
            //??????????????? ??????????????????
            if (isExclusiveGroup){
                if (TextUtils.isEmpty(count)) {
                    ToastUtils.show("????????????????????????");
                    return;
                }
            }else {
                //?????????????????????????????????
                if (TextUtils.isEmpty(userId)){
                    ToastUtils.show("?????????????????????");
                    return;
                }

            }

            String notes = et_notes.getText().toString();
            if (TextUtils.isEmpty(notes)) {
                notes = "???????????????????????????";
            }
            CommonDialog.Builder builder = new CommonDialog.Builder(GroupRedEnvelopesActivity.this).fullWidth().fromBottom()
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
            TextView tv_money = builder.getView(R.id.tv_money);
            if (!TextUtils.isEmpty(mony)) {
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
                    if (mCallBack != null) {
                        //?????????????????? ??????????????????
                        if (isExclusiveGroup){
                            CardBean cardBean = new CardBean();
                            cardBean.cardPassword = password;
                            cardBean.money = mony;
                            cardBean.note = finalNotes;
                            cardBean.number = count;
                            cardBean.isExclusiveGroup = isExclusiveGroup;
                            mCallBack.onSuccess(cardBean);
                        }else {
                            //???????????????????????????
                            CardBean cardBean = new CardBean();
                            cardBean.cardPassword = password;
                            cardBean.money = mony;
                            cardBean.note = "????????????"+","+ninkName+","+userId;
                            cardBean.number = "1";
                            cardBean.isExclusiveGroup = isExclusiveGroup;
                            mCallBack.onSuccess(cardBean);
                        }


                        finish();
                    }


                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            String photo = data.getStringExtra("photo");
            ninkName = data.getStringExtra("ninkName");
            userId = data.getStringExtra("userId");
            Log.i("", "onActivityResult: "+ ninkName);
            tv_name.setText(ninkName);
            Glide.with(this)
                    .asBitmap()
                    .load(photo)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .error(R.drawable.image_index)
                    .into(iv_avter);


        }
    }

    private void showDialoge() {
        Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialoge_content_normal_red, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
        //???????????????
        TextView tv_dialoge_photo = bottomDialog.findViewById(R.id.tv_dialoge_photo);
        tv_dialoge_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_luck.setText("???????????????");
                bottomDialog.dismiss();
                ll_show_luck.setVisibility(View.VISIBLE);
                rl_orientation.setVisibility(View.GONE);
                isExclusiveGroup = true;
            }
        });


        //????????????
        TextView tv_save_image = (TextView) bottomDialog.findViewById(R.id.tv_save_image);
        tv_save_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_luck.setText("????????????");
                bottomDialog.dismiss();
                ll_show_luck.setVisibility(View.GONE);
                rl_orientation.setVisibility(View.VISIBLE);
                isExclusiveGroup = false;
            }
        });
        //????????????
        TextView tv_quess = bottomDialog.findViewById(R.id.tv_quess);
        tv_quess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });
    }


    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
