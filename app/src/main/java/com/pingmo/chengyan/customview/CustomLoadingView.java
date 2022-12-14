package com.pingmo.chengyan.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.pingmo.chengyan.R;


public class CustomLoadingView extends View {
    //定义画笔
    private Paint paint;
    //倒计时的时间
    private int countDownTimeTotal = 1500;//总的倒计时默认是1.5秒
    //view默认的长度和高度
    private int defaultWidth = 200;
    private int defaultHeight = 300;
    //内外层圆弧的颜色，默认都为黑色
    private int outsideArcColor = Color.BLACK;
    private int insideArcColor = Color.BLACK;
    //内外层圆弧的宽度
    private float outsideArcWidth = 4f;
    private float insideArcWidth = 4f;
    //内外层圆弧转过的角度,
    private float outsideArcAngle = 330f;
    private float insideArcAngle = 60f;
    //默认起始角度
    private float defaultStartAngle = 105;
    //计算改变之后的起始角度
    private float startAngle = defaultStartAngle;



    public CustomLoadingView(Context context) {
        this(context,null);

    }

    public CustomLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //取出自定义的属性并赋值
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomLoadingView);
        countDownTimeTotal = array.getInteger(R.styleable.CustomLoadingView_round_once_time,1500);

        outsideArcColor = array.getColor(R.styleable.CustomLoadingView_round_outside_color,Color.BLACK);
        insideArcColor = array.getColor(R.styleable.CustomLoadingView_round_inside_color,Color.BLACK);

        outsideArcAngle = array.getFloat(R.styleable.CustomLoadingView_round_outside_angle,300f);
        insideArcAngle = array.getFloat(R.styleable.CustomLoadingView_round_inside_angle,60f);

        defaultStartAngle = array.getFloat(R.styleable.CustomLoadingView_round_start_angle,105);


        array.recycle();

        init();
    }

    public CustomLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);

    }

    //定义一个倒计时,用于执行动画效果,每隔10毫秒执行一次
    private CountDownTimer countDownTimer;

    private void init() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer.onFinish();
            countDownTimer = null;
        }

        countDownTimer = new CountDownTimer(countDownTimeTotal, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                //计算当前剩余时间的比例
                float radio = (float) millisUntilFinished / (float) countDownTimeTotal;

                float addAngle = 360 - 360 * radio;
                //根据比较改变开始位置的角度
                startAngle = defaultStartAngle;
                startAngle = defaultStartAngle + addAngle;

                invalidate();
            }

            @Override
            public void onFinish() {
                if(countDownTimer != null){
                    countDownTimer.start();
                }
            }
        };


    }

    public void onStart(){
        countDownTimer.start();
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        //首先绘制最外层的圆
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(outsideArcWidth);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(outsideArcColor);
        Path path = new Path();
        path.addArc(10, 10, defaultWidth - 10, defaultHeight - 10, startAngle, outsideArcAngle);
        canvas.drawPath(path, paint);
        //绘制内层的圆
        paint.reset();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(insideArcWidth);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(insideArcColor);

        canvas.drawArc(30 + outsideArcWidth, 30 + outsideArcWidth, defaultWidth - (30 + outsideArcWidth), defaultHeight - (30 + outsideArcWidth), (360 - startAngle), -insideArcAngle,false,paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        defaultWidth = dealReadSize(widthMeasureSpec, defaultWidth);
        defaultHeight = dealReadSize(heightMeasureSpec, defaultHeight);
        //这两个哪个小就用哪个
        if (defaultHeight > defaultWidth) {
            defaultHeight = defaultWidth;
        } else {
            defaultWidth = defaultHeight;
        }
        setMeasuredDimension(defaultWidth, defaultHeight);
    }

    //根据不同的model处理不同的尺寸
    private int dealReadSize(int measureSpec, int defaultSize) {
        int result = 0;
        int model = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (model) {
            case MeasureSpec.UNSPECIFIED:
                //不限制，使用默认的尺寸
                result = defaultSize;
                break;
            case MeasureSpec.AT_MOST:
                //上限
                result = Math.min(defaultSize, size);
                break;
            case MeasureSpec.EXACTLY:
                result = size;
                break;
        }

        return result;
    }

    //设置结束
    public void setFinish(){
        if(countDownTimer != null){
            countDownTimer.onFinish();
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

}
