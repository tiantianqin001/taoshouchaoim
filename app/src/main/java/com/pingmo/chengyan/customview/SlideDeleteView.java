package com.pingmo.chengyan.customview;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.IntentService;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.pingmo.chengyan.R;


/**
 * Created by MrBoudar on 16/9/11.
 * use in xml
 * use in code
 */
public class SlideDeleteView extends LinearLayout {
    private int mWidth;
    private int mHeight;
    private View contentView;
    private View deleteView;
    //首次触摸
    private int downX;
    private int downY;
    //位移变量
    private int leftX;
    //侧滑打开状态
    private int mCurrentState = SlideState.CLOSE;

    //qq截图 3倍图的大小
    private static int defaultOptionsWidth = 100 / 3;
    private static int defaultOptionsHeight = 80 / 3;

    //内容的宽度
    private int optionViewWidth;
    //option选项的宽度
    private int contentViewWidth;

    //用来处理单击事件是否在内容区域内
    private RectF contentRectF;
    private RectF deleteRectF;
    private IntentService intentService;


    public SlideDeleteView(Context context) {
        this(context, null);
    }

    public SlideDeleteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideDeleteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init type arrays
        setOrientation(LinearLayout.HORIZONTAL);


        initView(context);
    }

    private void initView(Context context) {

        contentRectF = new RectF();
        deleteRectF = new RectF();
        deleteView = LayoutInflater.from(getContext()).inflate(R.layout.item_delete_options, null);
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.item_content, null);


        LayoutParams contentLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.qb_px_70));
        contentView.setLayoutParams(contentLayoutParams);
        LayoutParams deleteLayoutParams = new LayoutParams((int) getResources().getDimension(R.dimen.qb_px_80),
                (int) getResources().getDimension(R.dimen.qb_px_70));
        deleteView.setLayoutParams(deleteLayoutParams);
        optionViewWidth = (int) getResources().getDimension(R.dimen.qb_px_80) ;

        WindowManager wm = (WindowManager)
        context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        contentViewWidth = width;

        this.removeAllViews();
        this.setGravity(Gravity.CENTER_VERTICAL);
        this.addView(contentView);
        this.addView(deleteView);
        Log.e("onSizeChanged", "onSizeChanged");
    }


    public View getContentView(){
        return contentView;
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mHeight = h;
        this.mWidth = w;



    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.e("onAttachedToWindow", "onAttachedToWindow");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        }, 100);
       // requestLayout();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.e("onDetachedFromWindow", "onDetachedFromWindow");
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.e("onWindowFocusChanged", "onWindowFocusChanged");

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e("onMeasure", "onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        contentView.layout(leftX, 0, contentViewWidth + leftX, mHeight);
        deleteView.layout(contentViewWidth + leftX, 0, contentViewWidth + optionViewWidth + leftX, mHeight);
        Log.e("onLayout", "onLayout" + String.valueOf(contentView.getLeft()) + "==" + String.valueOf(deleteView.getLeft()));
        contentRectF.top = contentView.getTop();
        contentRectF.left = contentView.getLeft();
        contentRectF.right = contentView.getRight();
        contentRectF.bottom = contentView.getBottom();
        if (mCurrentState == SlideState.OPEN) {
            //打开状态获取此时内容和删除区域的rectF,用户再次单击时获取是否在内容区域内,如果在,则执行关闭动画,反之,则是删除区域的操作
            deleteRectF.left = deleteView.getLeft();
            deleteRectF.right = deleteView.getRight();
            deleteRectF.bottom = deleteView.getBottom();
            deleteRectF.top = deleteView.getTop();
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY= (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                if (downX - moveX > 10 && mCurrentState == SlideState.CLOSE) {//打开
                    if (downX - moveX >= optionViewWidth) {
                        leftX = -optionViewWidth;
                        callbackOpen();
                    } else {
                        leftX = moveX - downX;
                        requestLayout();
                    }
                } else if (moveX - downX > 10 && mCurrentState == SlideState.OPEN) {//关闭
                    if (moveX - downX >= optionViewWidth) {
                        leftX = 0;
                        callbackClose();
                    } else {
                        leftX = -optionViewWidth + (moveX - downX);
                        requestLayout();
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                int upX = (int) event.getX();
                int upY = (int) event.getY();
                if (mCurrentState == SlideState.OPEN && Math.abs(downX - upX) <= 10 &&
                        contentRectF.contains(upX, event.getY())) {
                    closeAnimation();
                }
                if(mCurrentState == SlideState.CLOSE && Math.abs(downX - upX) <= 10 &&
                        Math.abs(downY - upY) <= 10 &&
                        contentRectF.contains(upX, event.getY())){
                    //响应单击事件
                    if (null != mOnSlideOpenOrCloseListener) {
                        mOnSlideOpenOrCloseListener.option(2);
                    }
                }
                if (mCurrentState == SlideState.OPEN && Math.abs(downX - upX) <= 10 &&
                        deleteRectF.contains(upX, event.getY())) {
//                    if (upX > optionViewWidth / 2 + (mWidth - optionViewWidth)) {//执行删除操作
//                        if (null != mOnSlideOpenOrCloseListener) {
//                            mOnSlideOpenOrCloseListener.option(1);
//                        }
//
//
//
//
//
//                        Toast.makeText(getContext(), "删除", Toast.LENGTH_SHORT).show();
//                    } else {//置顶等其他操作
//                        if (null != mOnSlideOpenOrCloseListener) {
//                            mOnSlideOpenOrCloseListener.option(0);
//                        }
//                        Toast.makeText(getContext(), "置顶", Toast.LENGTH_SHORT).show();
//                    }

                    if (null!=mOnSlideOpenOrCloseListener){
                        mOnSlideOpenOrCloseListener.option(1);
                    }
                  //  Toast.makeText(getContext(), "删除", Toast.LENGTH_SHORT).show();
                    closeAnimation();
                }
                if (leftX <= -optionViewWidth / 2) {
                    leftX = -optionViewWidth;
                    callbackOpen();
                } else {
                    leftX = 0;
                    callbackClose();
                }
                break;
        }
        return true;
    }

    public void callbackOpen() {
        mCurrentState = SlideState.OPEN;
        if (null != mOnSlideCompletionListener) {
            mOnSlideCompletionListener.open();
        }
        requestLayout();
    }

    public void callbackClose() {
        mCurrentState = SlideState.CLOSE;
        if (null != mOnSlideCompletionListener) {
            mOnSlideCompletionListener.close();
        }
        requestLayout();
    }

    public static class SlideState {
        public static final int OPEN = 0;
        public static final int CLOSE = 1;
    }

    private onSlideOpenOrCloseListener mOnSlideOpenOrCloseListener;

    public interface onSlideOpenOrCloseListener {
        //0 置顶 1删除 2 跳转
        public void option(int state);
    }

    public void setOnSlideOpenOrCloseListener(onSlideOpenOrCloseListener onSlideOpenOrCloseListener) {
        this.mOnSlideOpenOrCloseListener = onSlideOpenOrCloseListener;
    }

    public int getState() {
        return mCurrentState;
    }

    private onSlideCompletionListener mOnSlideCompletionListener;

    public interface onSlideCompletionListener {
        public void open();

        public void close();
    }

    public void setOnSlideCompltetionListener(onSlideCompletionListener onSlideCompltetionListener) {
        this.mOnSlideCompletionListener = onSlideCompltetionListener;
    }

    public void closeAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
        valueAnimator.setRepeatCount(0);
        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float atFloat = (Float) valueAnimator.getAnimatedValue();
                leftX = (int) (-optionViewWidth * atFloat);
                requestLayout();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mCurrentState = SlideState.CLOSE;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.start();
    }
}
