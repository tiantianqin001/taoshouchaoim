package com.tencent.qcloud.tuikit.tuichat.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

public class NoteContentEditText extends androidx.appcompat.widget.AppCompatEditText {

    private SelectEndPositionListener listener;

    public NoteContentEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        // TODO Auto-generated method stub
        super.onSelectionChanged(selStart, selEnd);
        Log.i("",""+selStart+" selEnd "+selEnd);

        if (listener!=null){
            listener.lastPosition();
        }
    }


    public interface SelectEndPositionListener{
        void lastPosition();
    }

    public void setOnSelectEndPositionListener(SelectEndPositionListener listener){

        this.listener = listener;
    }

}

