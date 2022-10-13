package com.pingmo.chengyan.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeRefreshHeaderLayout;
import com.pingmo.chengyan.R;

public class TwitterRefreshHeaderView extends SwipeRefreshHeaderLayout {

    private ImageView ivArrow;

    private ImageView ivSuccess;

    private TextView tvRefresh;

    private ProgressBar progressBar;

    private int mHeaderHeight;

    private Animation rotateUp;

    private Animation rotateDown;

    private ImageView mHeaderPic;
    private TextView mSuccessToast;
    private RelativeLayout mNormalParent;
    private RelativeLayout mHeaderParent;

    private boolean rotated = false;

    public TwitterRefreshHeaderView(Context context) {
        this(context, null);
    }

    public TwitterRefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwitterRefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHeaderHeight = getResources().getDimensionPixelOffset(R.dimen.qb_px_30);
        rotateUp = AnimationUtils.loadAnimation(context, R.anim.rotate_up);
        rotateDown = AnimationUtils.loadAnimation(context, R.anim.rotate_down);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tvRefresh = findViewById(R.id.tvRefresh);
        ivArrow = findViewById(R.id.ivArrow);
        ivSuccess = findViewById(R.id.ivSuccess);
        progressBar = findViewById(R.id.progressbar);
        mHeaderPic = findViewById(R.id.header_pic);
        mSuccessToast = findViewById(R.id.header_complete_text);
        mNormalParent = findViewById(R.id.normal_parent);
        mHeaderParent = findViewById(R.id.header_parent);
    }

    private void initViewParam() {
        ViewGroup.LayoutParams headerLp = mHeaderParent.getLayoutParams();
        ViewGroup.LayoutParams lp = mSuccessToast.getLayoutParams();
        MarginLayoutParams marginParams = null;
        //获取view的margin设置参数
        if (lp instanceof MarginLayoutParams) {
            marginParams = (MarginLayoutParams) lp;
        } else {
            //不存在时创建一个新的参数
            //基于View本身原有的布局参数对象
            marginParams = new MarginLayoutParams(lp);
        }
        headerLp.height = getResources().getDimensionPixelOffset(R.dimen.qb_px_30);
        marginParams.setMargins(0, 0, 0, 0);
        headerLp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mHeaderParent.setLayoutParams(headerLp);
        mSuccessToast.setLayoutParams(marginParams);
        initView();
    }

    private void initView() {
        mSuccessToast.setVisibility(GONE);
        mNormalParent.setVisibility(VISIBLE);
        mHeaderPic.setVisibility(GONE);
    }

    @Override
    public void onRefresh() {
        Log.d("TwitterRefreshHeader", "onRefresh()");
        ivSuccess.setVisibility(GONE);
        ivArrow.clearAnimation();
        ivArrow.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        tvRefresh.setText("正在刷新");
    }

    @Override
    public void onPrepare() {
        Log.d("TwitterRefreshHeader", "onPrepare()");
        initView();
    }

    @Override
    public void onMove(int y, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            ivArrow.setVisibility(VISIBLE);
            progressBar.setVisibility(GONE);
            ivSuccess.setVisibility(GONE);
            if (y > mHeaderHeight) {
                tvRefresh.setText("松开刷新");
                if (!rotated) {
                    ivArrow.clearAnimation();
                    ivArrow.startAnimation(rotateUp);
                    rotated = true;
                }
            } else if (y < mHeaderHeight) {
                if (rotated) {
                    ivArrow.clearAnimation();
                    ivArrow.startAnimation(rotateDown);
                    rotated = false;
                }

                tvRefresh.setText("下拉刷新");
            }
        }
    }

    @Override
    public void onRelease() {
        Log.d("TwitterRefreshHeader", "onRelease()");
    }

    @Override
    public void onComplete(int dataSize, String toast) {
        Log.d("TwitterRefreshHeader", "onComplete()");
        rotated = false;
        ivSuccess.setVisibility(VISIBLE);
        ivArrow.clearAnimation();
        ivArrow.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        tvRefresh.setText("完成");
    }

    @Override
    public void onReset() {
        Log.d("TwitterRefreshHeader", "onReset()");
        rotated = false;
        ivSuccess.setVisibility(GONE);
        ivArrow.clearAnimation();
        ivArrow.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        mNormalParent.setVisibility(GONE);
        mSuccessToast.setVisibility(GONE);
    }

}
