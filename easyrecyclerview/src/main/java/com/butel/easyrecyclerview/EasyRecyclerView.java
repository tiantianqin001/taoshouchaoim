package com.butel.easyrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.butel.easyrecyclerview.adapter.OtherStateAdapter;
import com.butel.easyrecyclerview.adapter.OtherStateBindListener;
import com.butel.easyrecyclerview.adapter.OtherStateViewHolder;
import com.butel.easyrecyclerview.adapter.RecyclerArrayAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class EasyRecyclerView extends RecyclerView {

    public static final String TAG = "EasyRecyclerView";
    public static boolean DEBUG = true;

    public static final int STATUS_DATA = 1000;
    public static final int STATUS_NO_DATA = 1001;
    public static final int STATUS_ERROR = 1002;
    public static final int STATUS_LODA = 1003;
    private int mShowStatus;

    private int mProgressId;
    private int mEmptyId;
    private int mErrorId;

    private Adapter mOrginalAdapter;
    private LayoutManager mOrginalLayoutManager;
    private OtherStateBindListener mListener;

    public EasyRecyclerView(Context context) {
        this(context, null);
    }

    public EasyRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EasyRecyclerView);
        try {
            mEmptyId = a.getResourceId(R.styleable.EasyRecyclerView_layout_empty, 0);
            mProgressId = a.getResourceId(R.styleable.EasyRecyclerView_layout_progress, 0);
            mErrorId = a.getResourceId(R.styleable.EasyRecyclerView_layout_error, 0);
        } finally {
            a.recycle();
        }
    }

    public void setEmptyView(int emptyView) {
        this.mEmptyId = emptyView;
    }

    public void setProgressView(int progressView) {
        this.mProgressId = progressView;
    }

    public void setErrorView(int errorView) {
        this.mErrorId = errorView;
    }

    /**
     * Set the layout manager to the recycler
     *
     * @param manager
     */
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        this.mOrginalLayoutManager = manager;
    }


    /**
     * 设置适配器，关闭所有副view。展示recyclerView
     * 适配器有更新，自动关闭所有副view。根据条数判断是否展示EmptyView
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        this.mOrginalAdapter = adapter;
        adapter.registerAdapterDataObserver(new EasyDataObserver(this));
        showRecycler();
    }

    /**
     * 设置适配器，关闭所有副view。展示进度条View
     * 适配器有更新，自动关闭所有副view。根据条数判断是否展示EmptyView
     *
     * @param adapter
     */
    public void setAdapterWithProgress(RecyclerView.Adapter adapter) {
        this.mOrginalAdapter = adapter;
        adapter.registerAdapterDataObserver(new EasyDataObserver(this));
        //只有Adapter为空时才显示ProgressView
        if (adapter instanceof RecyclerArrayAdapter) {
            if (((RecyclerArrayAdapter) adapter).getCount() == 0) {
                showProgress();
            } else {
                showRecycler();
            }
        } else {
            if (adapter.getItemCount() == 0) {
                showProgress();
            } else {
                showRecycler();
            }
        }
    }

    /**
     * Remove the adapter from the recycler
     */
    public void clear() {
        setAdapter(null);
    }

    public void showError() {
        log("showError");
        if (mShowStatus == STATUS_ERROR) {
            return;
        }
        if (mErrorId != 0) {
            mShowStatus = STATUS_ERROR;
            initOtherStateAdapter();
        } else {
            showRecycler();
        }
    }

    public void showEmpty() {
        log("showEmpty");
        if (mShowStatus == STATUS_NO_DATA) {
            return;
        }
        if (mEmptyId != 0) {
            mShowStatus = STATUS_NO_DATA;
            initOtherStateAdapter();
        } else {
            showRecycler();
        }
    }

    public void showProgress() {
        log("showProgress");
        if (mShowStatus == STATUS_LODA) {
            return;
        }
        if (mProgressId != 0) {
            mShowStatus = STATUS_LODA;
            initOtherStateAdapter();
        } else {
            showRecycler();
        }
    }

    public void showRecycler() {
        log("showRecycler");
        if (mShowStatus == STATUS_DATA) {
            return;
        }
        if (mOrginalLayoutManager == null || mOrginalAdapter == null) {
            throw new NullPointerException("before showRecycler, you must setLayoutManager and setAdapter");
        }
        mShowStatus = STATUS_DATA;
        super.setLayoutManager(mOrginalLayoutManager);
        super.setAdapter(mOrginalAdapter);
    }

    public void showRecyclerForce() {
        log("showRecyclerForce");
        if (mOrginalLayoutManager == null || mOrginalAdapter == null) {
            throw new NullPointerException("before showRecyclerForce, you must setLayoutManager and setAdapter");
        }
        mShowStatus = STATUS_DATA;
        super.setLayoutManager(mOrginalLayoutManager);
        super.setAdapter(mOrginalAdapter);
    }


    private void initOtherStateAdapter() {
        super.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        super.setAdapter(new DefaultOtherStateAdapter());
    }

    private int getLayoutIdByState() {
        int id;
        switch (mShowStatus) {
            case STATUS_NO_DATA:
                id = mEmptyId;
                break;
            case STATUS_ERROR:
                id = mErrorId;
                break;
            case STATUS_LODA:
                id = mProgressId;
                break;
            default:
                id = mErrorId;
                break;
        }
        return id;
    }

    public int getShowStatus() {
        return mShowStatus;
    }

    public void setOtherStateBindListener(OtherStateBindListener listener) {
        this.mListener = listener;
    }

    /**
     * @return the recycler adapter
     */
    public RecyclerView.Adapter getAdapter() {
        return mOrginalAdapter;
    }

    private static void log(String content) {
        if (DEBUG) {
            Log.i(TAG, content);
        }
    }

    private class DefaultOtherStateAdapter extends OtherStateAdapter {

        @Override
        public void onBindView(OtherStateViewHolder holder, int position) {
            if (mListener != null) {
                mListener.onBindView(holder, mShowStatus);
            }
        }

        @Override
        public int getLayoutID(int position) {
            return getLayoutIdByState();
        }

        @Override
        public boolean clickable() {
            if (mListener != null) {
                return mListener.clickable();
            }
            return false;
        }

        @Override
        public void onItemClick(View v, int position) {
            if (mListener != null) {
                mListener.onItemClick(v, mShowStatus);
            }
        }
    }

}
