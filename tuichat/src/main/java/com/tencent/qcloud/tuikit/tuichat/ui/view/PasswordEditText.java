package com.tencent.qcloud.tuikit.tuichat.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.inputmethod.EditorInfo;
import com.tencent.qcloud.tuikit.tuichat.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

/**
 * 密码输入框
 */
public class PasswordEditText extends AppCompatEditText {
    private Paint mPaint;
    /**
     * 一个密码所占的宽度
     */
    private int mPasswordItemWidth;
    /**
     * 密码的个数默认为6位数
     */
    private int mNumberOfPasswords = 6;
    /**
     * 背景边框颜色
     */
    private int mBgColor = Color.parseColor("#d1d2d6");
    /**
     * 矩形背景
     */
    private int mBgColor2 = Color.parseColor("#EDEDED");
    /**
     * 背景边框大小
     */
    private int mBgSize = 1;
    /**
     * 背景边框圆角大小
     */
    private int mBgCorner = 0;
    /**
     * 分割线的颜色
     */
    private int mDivisionLineColor = mBgColor;
    /**
     * 分割线的大小
     */
    private int mDivisionLineSize = 1;
    /**
     * 密码圆点的颜色
     */
    private int mPasswordColor = Color.parseColor("#000000");
    /**
     * 密码圆点的半径大小
     */
    private int mPasswordRadius = 4;
    /**
     * 方块间隔
     */
    private int mBlockSpacing = 10;
    /**
     * 方块宽度
     */
    private int mBlockWidth = 0;
    private PasswordFullListener mListener;

    private PasswordFullListener mPasswordFullListener;

    public PasswordEditText(@NonNull Context context) {
        super(context);
    }

    public PasswordEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        initAttributeSet(context, attrs);
        // 设置输入模式是密码
        setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        // 不显示光标
        setCursorVisible(false);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordEditText);
        mDivisionLineColor = typedArray.getColor(R.styleable.PasswordEditText_divisionLineColor, mDivisionLineColor);
        mBgColor = typedArray.getColor(R.styleable.PasswordEditText_bgColor, mBgColor);
        mPasswordColor = typedArray.getColor(R.styleable.PasswordEditText_passwordColor, mPasswordColor);

        mDivisionLineSize = (int) typedArray.getDimension(R.styleable.PasswordEditText_divisionLineSize, dip2px(mDivisionLineSize));
        mPasswordRadius = (int) typedArray.getDimension(R.styleable.PasswordEditText_passwordRadius, dip2px(mPasswordRadius));
        mBgSize = (int) typedArray.getDimension(R.styleable.PasswordEditText_bgSize, dip2px(mBgSize));
        mBgCorner = (int) typedArray.getDimension(R.styleable.PasswordEditText_bgCorner, 0);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    /**
     * dip 转 px
     */
    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dip, getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int passwordWidth = getWidth() - (mNumberOfPasswords - 1) * mDivisionLineSize;
        mPasswordItemWidth = passwordWidth / mNumberOfPasswords;
        mBlockWidth = (getWidth() - ((mNumberOfPasswords - 1) * mBlockSpacing)) / mNumberOfPasswords;
        //绘制背景
//        drawBg(canvas);
        //绘制分割线
//        drawDivisionLine(canvas);
        drawRectangle(canvas);
        //绘制密码
        drawHidePassword(canvas);
    }

    /**
     * 绘制隐藏的密码
     *
     * @param canvas
     */
    private void drawHidePassword(Canvas canvas) {
        int passwordLength = Objects.requireNonNull(getText()).length();
        mPaint.setColor(mPasswordColor);
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < passwordLength; i++) {
            int cx = i * mDivisionLineSize + i * mPasswordItemWidth + mPasswordItemWidth / 2 + mBgSize;
            canvas.drawCircle(cx, getHeight() / 2, mPasswordRadius, mPaint);
        }
        if (mPasswordFullListener != null) {
            if (passwordLength >= mNumberOfPasswords) {
                mPasswordFullListener.passwordFull(getText().toString().trim());
            }
        }
    }

    /**
     * 绘制分割线
     *
     * @param canvas
     */
    private void drawDivisionLine(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mDivisionLineColor);
        mPaint.setStrokeWidth(mDivisionLineSize);
        for (int i = 1; i < mNumberOfPasswords; i++) {
            int startX = i * mDivisionLineSize + i * mPasswordItemWidth + mBgSize;
            canvas.drawLine(startX, mBgSize, startX, getHeight() - mBgSize, mPaint);
        }
    }

    /**
     * 绘制边框背景
     *
     * @param canvas
     */
    private void drawBg(Canvas canvas) {
        mPaint.setColor(mBgColor);
        //设置画笔为空心
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBgSize);
        RectF rectF = new RectF(mBgSize, mBgSize, getWidth() - mBgSize, getHeight() - mBgSize);
        //如果没有圆角就画矩形
        if (mBgCorner == 0) {
            canvas.drawRect(rectF, mPaint);
        } else {
            canvas.drawRoundRect(rectF, mBgCorner, mBgCorner, mPaint);
        }
    }

    /**
     * 绘制矩形方块
     */
    private void drawRectangle(Canvas canvas) {
        mPaint.setColor(mBgColor2);
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 1; i <= mNumberOfPasswords; i++) {
            int left = (i - 1) * mBlockWidth + (i - 1) * mBlockSpacing;
            RectF rectF = new RectF(left, 0, left + mBlockWidth, getHeight());
            //如果没有圆角就画矩形
            if (mBgCorner == 0) {
                canvas.drawRect(rectF, mPaint);
            } else {
                canvas.drawRoundRect(rectF, mBgCorner, mBgCorner, mPaint);
            }
        }
    }

    /**
     * 添加密码
     */
    public void addPassword(String number) {
        number = getText().toString().trim() + number;
        if (number.length() > mNumberOfPasswords) {
            return;
        }
        setText(number);
    }

    /**
     * 删除最后一位密码
     */
    public void deleteLastPassword() {
        String currentText = getText().toString().trim();
        if (TextUtils.isEmpty(currentText)) {
            return;
        }
        currentText = currentText.substring(0, currentText.length() - 1);
        setText(currentText);
    }

    /**
     * 设置密码填充满的监听
     */
    public void setOnPasswordFullListener(PasswordFullListener listener) {
        this.mPasswordFullListener = listener;
    }

    /**
     * 密码已经全部填满
     */
    public interface PasswordFullListener {
        public void passwordFull(String password);
    }
}
