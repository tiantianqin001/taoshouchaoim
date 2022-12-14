package com.tencent.qcloud.tuikit.tuichat.setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class AndroidBug5497Workaround {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content



    public static void assistActivity (Activity activity) {
        new AndroidBug5497Workaround(activity);
    }
    private Activity activity;
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private AndroidBug5497Workaround(Activity activity) {
        this.activity = activity;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new

                                                                                ViewTreeObserver.OnGlobalLayoutListener() {
                                                                                    public void onGlobalLayout() {
                                                                                        possiblyResizeChildOfContent();
                                                                                    }
                                                                                });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    @SuppressLint("LongLogTag")
    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        Log.e("possiblyResizeChildOfContent","usableHeightNow:"+usableHeightNow);
        Log.e("possiblyResizeChildOfContent","usableHeightPrevious:"+usableHeightPrevious);
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();

            //???????????????????????????19??????????????????????????????????????????????????????????????????????????????
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
                Rect frame = new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                int statusBarHeight = frame.top;
                usableHeightSansKeyboard -= statusBarHeight;
            }
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference>(usableHeightSansKeyboard/4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);

        //???????????????????????????19?????????????????????????????????????????????????????????????????????adjustResize????????????????????????


        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return (r.bottom - r.top)+statusBarHeight;
        }

        return (r.bottom - r.top);
    }

}
