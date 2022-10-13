package com.pingmo.chengyan.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.pingmo.chengyan.R;

public class CommonDialog {

    private View contentView;
    private Dialog dialog;

    // 确定按钮之后是否关闭对话框
    private boolean stillShow = false;
    private boolean canceledOnTouchOutside = false;

    private Context context;

    private OnDismissListener dismissListener = null;

    public CommonDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.CustomDialog);
        contentView = LayoutInflater.from(context).inflate(R.layout.common_dialog_layout, null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.addContentView(contentView, params);
        dialog.setCanceledOnTouchOutside(false);
        dialog.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_BACK));
        dialog.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_BACK));
        dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (dismissListener != null) {
                    dismissListener.onDismiss(dialog);
                }
            }
        });
    }

    public Dialog getDialog() {
        return dialog;
    }

    public View getContentView() {
        return contentView;
    }

    public void setCancelable(boolean cancelable) {
        if (dialog != null) {
            dialog.setCancelable(cancelable);
        }
    }

    public void setOnDismissListener(OnDismissListener _linstener) {
        dismissListener = _linstener;
    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        } else {
            return false;
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void setMessage(String msg) {
        TextView titleTv = contentView.findViewById(R.id.title);
        titleTv.setText(msg);
    }

    public void setPositiveButton(final BtnClickedListener btnOk) {
        View view = contentView.findViewById(R.id.positive_btn);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnOk != null) {
                    btnOk.onBtnClicked();
                }
                if (dialog != null && !stillShow) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
    }

    public void setPositiveButton(String txt, final BtnClickedListener btnOk) {
        View view = contentView.findViewById(R.id.positive_btn);
        view.setVisibility(View.VISIBLE);
        TextView tv = contentView.findViewById(R.id.positive_txt);
        tv.setText(txt);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnOk != null) {
                    btnOk.onBtnClicked();
                }
                if (dialog != null && !stillShow) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
    }

    public void setCancelButton(final BtnClickedListener cancleOk) {
        View view = contentView.findViewById(R.id.cancel_btn);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancleOk != null) {
                    cancleOk.onBtnClicked();
                }
                if (dialog != null && !stillShow) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
    }

    public void setCancelButton(String txt, final BtnClickedListener cancleOk) {
        View view = contentView.findViewById(R.id.cancel_btn);
        view.setVisibility(View.VISIBLE);
        TextView tv = contentView.findViewById(R.id.cancel_txt);
        tv.setText(txt);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancleOk != null) {
                    cancleOk.onBtnClicked();
                }
                if (dialog != null && !stillShow) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
    }

    public void showDialog() {
        getDialog().setCanceledOnTouchOutside(canceledOnTouchOutside);
        getDialog().show();

    }

    public interface BtnClickedListener {
        void onBtnClicked();
    }

    public interface BtnClickedRespListener {
        boolean onBtnClicked();
    }

    public void setOnCancelListener(OnCancelListener listener) {
        if (dialog != null) {
            dialog.setOnCancelListener(listener);
        }
    }

    public boolean isStillShow() {
        return stillShow;
    }

    public void setStillShow(boolean stillShow) {
        this.stillShow = stillShow;
    }

    /**
     * Description:是否允许点击窗口外区域，隐藏弹出框
     *
     * @param tag ture:允许
     */
    public void setCanceledOnTouchOutside(boolean tag) {
        this.canceledOnTouchOutside = tag;
    }
}
