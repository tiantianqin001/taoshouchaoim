package com.tencent.qcloud.tuikit.tuichat.setting;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.hjq.toast.ToastUtils;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.ui.page.IntoenvelopeActivity;


/**
 * Created by pengchengfu on 2018/03/12
 */
public class KeyBoardUtil {

    private KeyboardView keyboardView;
    private EditText editText;
    private Keyboard keyboard;// 键盘
    private String chatName;
    private String toUserId;

    public KeyBoardUtil(KeyboardView keyboardView, EditText editText, String chatName,String toUserId ) {
        this.keyboardView = keyboardView;
        this.editText = editText;

        this.keyboard = new Keyboard(editText.getContext(), R.xml.key);
        this.chatName = chatName;
        this.toUserId = toUserId;
        this.editText.setInputType(InputType.TYPE_NULL);
        this.keyboardView.setOnKeyboardActionListener(listener);
        this.keyboardView.setKeyboard(keyboard);
        this.keyboardView.setEnabled(true);
        this.keyboardView.setPreviewEnabled(false);
    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {

        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = editText.getText();
            int start = editText.getSelectionStart();
            switch (primaryCode) {
                case Keyboard.KEYCODE_DELETE:
                    if (editable != null && editable.length() > 0) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                    }
                    break;
                case Keyboard.KEYCODE_DONE:
                    keyboardView.setVisibility(View.GONE);
                    //点击了转账
                    String info = editable.toString().trim();
                    if (TextUtils.isEmpty(info)){
                        ToastUtils.show("请先填写金额");
                        return;
                    }
                    //开始转账
                    Intent intent = new Intent(keyboardView.getContext(), IntoenvelopeActivity.class);
                    intent.putExtra("chatName",chatName);
                    intent.putExtra("mony",info);
                    intent.putExtra("recipientId",toUserId);
                    keyboardView.getContext().startActivity(intent);


                    break;
                default:
                    editable.insert(start, Character.toString((char) primaryCode));
                    break;
            }
        }
    };

    // Activity中获取焦点时调用，显示出键盘
    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }
}
