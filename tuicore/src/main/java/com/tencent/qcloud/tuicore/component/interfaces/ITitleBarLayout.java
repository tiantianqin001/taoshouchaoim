package com.tencent.qcloud.tuicore.component.interfaces;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 会话列表窗口 {@link ConversationLayout}、聊天窗口 {@link ChatLayout} 等都自带标题栏，<br>
 * 标题栏设计为左中右三部分标题，左边可为图片+文字，中间为文字，右边也可为图片+文字，这些区域返回的都是标准的<br>
 * Android View，可以根据业务需要对这些 View 进行交互响应处理。
 */
public interface ITitleBarLayout {

    /**
     * 设置左边标题的点击事件
     *
     * @param listener
     */
    void setOnLeftClickListener(View.OnClickListener listener);

    /**
     * 设置右边标题的点击事件
     *
     * @param listener
     */
    void setOnRightClickListener(View.OnClickListener listener);


    void setOnRightSearchClickListener(View.OnClickListener listener);

    /**
     * 设置标题
     *
     * @param title    标题内容
     * @param position 标题位置
     */
    void setTitle(String title, Position position);

    /**
     * 返回左边标题区域
     *
     * @return
     */
    LinearLayout getLeftGroup();

    /**
     * 返回右边标题区域
     *
     * @return
     */
    LinearLayout getRightGroup();

    /**
     * 返回左边标题的图片
     *
     * @return
     */
    ImageView getLeftIcon();

    /**
     * 设置左边标题的图片
     *
     * @param resId
     */
    void setLeftIcon(int resId);

    /**
     * 返回右边标题的图片
     *
     * @return
     */
    ImageView getRightIcon();

    /**
     * 设置右边标题的图片
     *
     * @param resId
     */
    void setRightIcon(int resId);

    void setRightSearchIcon(int resId);

    /**
     * 返回左边标题的文字
     *
     * @return
     */
    TextView getLeftTitle();

    /**
     * 返回中间标题的文字
     *
     * @return
     */
    TextView getMiddleTitle();

    /**
     * 返回右边标题的文字
     *
     * @return
     */
    TextView getRightTitle();


    /**
     * 右边的搜索
     */
    ImageView getRightSearchIcon();

    /**
     * 返回根布局
     */

    RelativeLayout getRootView();

    /**
     * 标题区域的枚举值
     */
    enum Position {
        /**
         * 左边标题
         */
        LEFT,
        /**
         * 中间标题
         */
        MIDDLE,
        /**
         * 右边标题
         */
        RIGHT
    }

}