package com.tencent.qcloud.tuikit.tuichat.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CenterTextView  extends androidx.appcompat.widget.AppCompatTextView {

    private StaticLayout myStaticLayout;

    public CenterTextView(Context context) {
        super(context);
    }

    public CenterTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CenterTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override

    protected void onSizeChanged(int w, int h, int oldw, int oldh)

    {

        super.onSizeChanged(w, h, oldw, oldh);

        initView();

    }

    private void initView()

    {

        TextPaint  tp = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        tp.setTextSize(getTextSize());

        tp.setColor(getCurrentTextColor());

        myStaticLayout = new StaticLayout(getText(), tp, getWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        myStaticLayout.draw(canvas);
    }
}
