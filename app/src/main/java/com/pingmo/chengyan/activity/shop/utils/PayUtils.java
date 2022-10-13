package com.pingmo.chengyan.activity.shop.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;


import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.shop.dialog.CommonDialog;
import com.pingmo.chengyan.activity.shop.view.CustomerKeyboard;
import com.pingmo.chengyan.activity.shop.view.PasswordEditText;

import java.math.BigDecimal;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentManager;

public class PayUtils {

    /**
     * 支付密码
     *
     * @param context              上下午
     * @param passwordFullListener 密码输入完成监听
     */
    @Deprecated
    public static void payPassword(Context context, PasswordFullListener passwordFullListener) {
        showDialog(context, SpUtils.getInstance().decodeBoolean(SpConstant.isPay), passwordFullListener);
    }

    /**
     * 支付密码
     *
     * @param context              上下午
     * @param passwordFullListener 密码输入完成监听
     */
    public static void payPassword(Context context, String user, String money, PasswordFullListener passwordFullListener) {
        showDialog(context, SpUtils.getInstance().decodeBoolean(SpConstant.isPay), user, money, passwordFullListener);
    }

    /**
     * @param context
     * @param isPay                false 设置支付密码，true 输入支付密码
     * @param passwordFullListener
     */
    private static void showDialog(Context context, boolean isPay, PasswordFullListener passwordFullListener) {
        final CommonDialog.Builder builder = new CommonDialog.Builder(context).fullWidth().fromBottom()
                .setView(R.layout.dialog_customer_keyboard3);
        builder.setOnClickListener(R.id.delete_dialog, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.create().show();
        final CustomerKeyboard mCustomerKeyboard = builder.getView(R.id.custom_key_board);
        final PasswordEditText mPasswordEditText = builder.getView(R.id.password_edit_text);
        final LinearLayoutCompat llc_use_money = builder.getView(R.id.llc_use_money);
        final TextView tv_use = builder.getView(R.id.tv_use);
        final TextView tv_money = builder.getView(R.id.tv_money);
        if (!isPay) {
            final TextView tvTitle = builder.getView(R.id.tv_title);
            tvTitle.setText("设置位数字支付密码");
        }
        mCustomerKeyboard.setOnCustomerKeyboardClickListener(new CustomerKeyboard.CustomerKeyboardClickListener() {
            @Override
            public void click(String number) {
                if ("返回".equals(number)) {
                    builder.dismiss();
                } else if ("忘记密码？".equals(number)) {
//                    startActivity(new Intent(WithdrawActivity.this, ForgotPaymentPasswordActivity.class));
//                    Intent intentResetPay = new Intent(context, ResetPayPasswordActivity.class);
//                    context.startActivity(intentResetPay);
                } else {
                    mPasswordEditText.addPassword(number);
                }
            }

            @Override
            public void delete() {
                mPasswordEditText.deleteLastPassword();
            }
        });

        mPasswordEditText.setOnPasswordFullListener(new PasswordEditText.PasswordFullListener() {
            @Override
            public void passwordFull(String password) {
                builder.dismiss();
                passwordFullListener.passwordFull(password, isPay);
            }
        });
    }

    /**
     * @param context
     * @param isPay                false 设置支付密码，true 输入支付密码
     * @param passwordFullListener
     */
    @SuppressLint("SetTextI18n")
    private static void showDialog(Context context, boolean isPay, String user, String money, PasswordFullListener passwordFullListener) {
        final CommonDialog.Builder builder = new CommonDialog.Builder(context).fullWidth().fromBottom()
                .setView(R.layout.dialog_customer_keyboard3);
        builder.setOnClickListener(R.id.delete_dialog, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.create().show();
        final CustomerKeyboard mCustomerKeyboard = builder.getView(R.id.custom_key_board);
        final PasswordEditText mPasswordEditText = builder.getView(R.id.password_edit_text);
        final LinearLayoutCompat llc_use_money = builder.getView(R.id.llc_use_money);
        final TextView tv_use = builder.getView(R.id.tv_use);
        final TextView tv_money = builder.getView(R.id.tv_money);
        if (!isPay) {
            final TextView tvTitle = builder.getView(R.id.tv_title);
            tvTitle.setText("设置位数字支付密码");
            llc_use_money.setVisibility(View.GONE);
        } else {
            llc_use_money.setVisibility(View.VISIBLE);
            BigDecimal bigDecimal = BigDecimalUtils.getInstance().getBigDecimal(new BigDecimal(money));
            tv_money.setText(bigDecimal.toString());
            tv_use.setText(user);
        }
        mCustomerKeyboard.setOnCustomerKeyboardClickListener(new CustomerKeyboard.CustomerKeyboardClickListener() {
            @Override
            public void click(String number) {
                if ("返回".equals(number)) {
                    builder.dismiss();
                } else if ("忘记密码？".equals(number)) {
//                    startActivity(new Intent(WithdrawActivity.this, ForgotPaymentPasswordActivity.class));
//                    Intent intentResetPay = new Intent(context, ResetPayPasswordActivity.class);
//                    context.startActivity(intentResetPay);
                } else {
                    mPasswordEditText.addPassword(number);
                }
            }

            @Override
            public void delete() {
                mPasswordEditText.deleteLastPassword();
            }
        });

        mPasswordEditText.setOnPasswordFullListener(new PasswordEditText.PasswordFullListener() {
            @Override
            public void passwordFull(String password) {
                builder.dismiss();
                passwordFullListener.passwordFull(password, isPay);
            }
        });
    }


    /**
     * 密码已经全部填满
     */
    public interface PasswordFullListener {
        public void passwordFull(String password, boolean isPay);
    }

    /**
     * 多次输错密码
     *
     * @param fragmentManager
     * @param onDialogButtonClickListener
     */
    public static void showChangePassDialog(FragmentManager fragmentManager, com.pingmo.chengyan.activity.shop.common.CommonDialog.OnDialogButtonClickListener onDialogButtonClickListener) {
        com.pingmo.chengyan.activity.shop.common.CommonDialog.Builder builder = new com.pingmo.chengyan.activity.shop.common.CommonDialog.Builder();
        builder.setContentMessage("您已多次输错密码，是否忘记密码？");
        builder.setDialogButtonClickListener(onDialogButtonClickListener);
        com.pingmo.chengyan.activity.shop.common.CommonDialog dialog = builder.build();
        dialog.show(fragmentManager, "logout_dialog");
    }
}
