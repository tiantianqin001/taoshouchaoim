/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.butel.easyrecyclerview.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.butel.easyrecyclerview.EasyRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * A concrete BaseAdapter that is backed by an array of arbitrary
 * objects.  By default this class expects that the provided resource id references
 * a single TextView.  If you want to use a more complex layout, use the constructors that
 * also takes a field id.  That field id should reference a TextView in the larger layout
 * resource.
 * <p>
 * <p>However the TextView is referenced, it will be filled with the toString() of each object in
 * the array. You can add lists or arrays of custom objects. Override the toString() method
 * of your objects to determine what text will be displayed for the item in the list.
 * <p>
 * <p>To use something other than TextViews for the array display, for instance, ImageViews,
 * or to have some of data besides toString() results fill the views,
 */
abstract public class RecyclerArrayAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    public static final String TAG = "RecyclerArrayAdapter";

    /**
     * Contains the list of objects that represent the data of this ArrayAdapter.
     * The content of this list is referred to as "the array" in the documentation.
     */
    protected List<T> mObjects;
    protected EventDelegate mEventDelegate;
    protected ArrayList<ItemView> headers = new ArrayList<>();
    protected ArrayList<ItemView> footers = new ArrayList<>();

    protected OnItemClickListener mItemClickListener;
    protected OnItemLongClickListener mItemLongClickListener;

    protected RecyclerView mRecyclerView;

    public interface ItemView {
        View onCreateView(ViewGroup parent);

        void onBindView(View headerView);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnMoreListener {
        void onMoreShow();

        void onMoreClick();
    }

    public interface OnNoMoreListener {
        void onNoMoreShow();

        void onNoMoreClick();
    }

    public interface OnErrorListener {
        void onErrorShow();

        void onErrorClick();
    }

    public class GridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        private int mMaxCount;

        public GridSpanSizeLookup(int maxCount) {
            this.mMaxCount = maxCount;
        }

        @Override
        public int getSpanSize(int position) {
            if (headers.size() != 0) {
                if (position < headers.size()) return mMaxCount;
            }
            if (footers.size() != 0) {
                int i = position - headers.size() - getCount();
                if (i >= 0) {
                    return mMaxCount;
                }
            }
            return 1;
        }
    }

    public GridSpanSizeLookup obtainGridSpanSizeLookUp(int maxCount) {
        return new GridSpanSizeLookup(maxCount);
    }

    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock.
     */
    private final Object mLock = new Object();


    /**
     * Indicates whether or not {@link #notifyDataSetChanged()} must be called whenever
     * {@link #mObjects} is modified.
     */
    private boolean mNotifyOnChange = true;

    private Context mContext;


    /**
     * Constructor
     *
     * @param context The current context.
     */
    public RecyclerArrayAdapter(Context context) {
        init(context, new ArrayList<T>());
    }


    /**
     * Constructor
     *
     * @param context The current context.
     * @param objects The objects to represent in the ListView.
     */
    public RecyclerArrayAdapter(Context context, T[] objects) {
        init(context, Arrays.asList(objects));
    }

    /**
     * Constructor
     *
     * @param context The current context.
     * @param objects The objects to represent in the ListView.
     */
    public RecyclerArrayAdapter(Context context, List<T> objects) {
        init(context, objects);
    }


    private void init(Context context, List<T> objects) {
        mContext = context;
        mObjects = new ArrayList<>(objects);
    }


    public void stopMore() {
        if (mEventDelegate == null)
            throw new NullPointerException("You should invoking setLoadMore() first");
        mEventDelegate.stopLoadMore();
    }

    public void pauseMore(String msg) {
        if (mEventDelegate == null)
            throw new NullPointerException("You should invoking setLoadMore() first");
        mEventDelegate.pauseLoadMore(msg);
    }

    public void resumeMore() {
        if (mEventDelegate == null)
            throw new NullPointerException("You should invoking setLoadMore() first");
        mEventDelegate.resumeLoadMore();
    }

    public void clearFooter() {
        if (mEventDelegate == null)
            throw new NullPointerException("You should invoking setLoadMore() first");
        mEventDelegate.clear();
    }

    public void addHeader(ItemView view) {
        if (view == null) throw new NullPointerException("ItemView can't be null");
        headers.add(view);
        notifyDataSetChanged();
    }

    public void insertHeader(ItemView view, int index) {
        if (view == null) throw new NullPointerException("ItemView can't be null");
        headers.add(index, view);
        Log.d("test", "insertHeader index is " + (headers.size() - index));
        notifyItemInserted(headers.size() - index);
    }


    public void addFooter(ItemView view) {
        if (view == null) throw new NullPointerException("ItemView can't be null");
        footers.add(view);
        notifyItemInserted(headers.size() + getCount() + footers.size() - 1);
    }

    public void removeAllHeader() {
        int count = headers.size();
        headers.clear();
        notifyItemRangeRemoved(0, count);
    }

    public void removeAllFooter() {
        int count = footers.size();
        footers.clear();
        notifyItemRangeRemoved(headers.size() + getCount(), count);
    }

    public ItemView getHeader(int index) {
        return headers.get(index);
    }

    public boolean containsHeader(ItemView header) {
        return headers.contains(header);
    }

    public ItemView getFooter(int index) {
        return footers.get(index);
    }

    public int getHeaderCount() {
        return headers.size();
    }

    public int getFooterCount() {
        return footers.size();
    }

    public void removeHeader(ItemView view) {
        int position = headers.indexOf(view);
        headers.remove(view);
        notifyItemRemoved(position);
    }

    public void removeFooter(ItemView view) {
        int position = headers.size() + getCount() + footers.indexOf(view);
        footers.remove(view);
        notifyItemRemoved(position);
    }


    EventDelegate getEventDelegate() {
        if (mEventDelegate == null) mEventDelegate = new DefaultEventDelegate(this);
        return mEventDelegate;
    }

    public void setEnableShowFooter(boolean enable) {
        if (mEventDelegate != null) {
            mEventDelegate.setEnableShow(enable);
        }
    }

    public boolean isLoadingMore() {
        return getEventDelegate().isLoadingMore();
    }

    /**
     * @deprecated Use {@link #setMore(int, OnLoadMoreListener)} instead.
     */
    @Deprecated
    public void setMore(final int res, final OnLoadMoreListener listener) {
        getEventDelegate().setMore(res, new OnMoreListener() {
            @Override
            public void onMoreShow() {
                listener.onLoadMore();
            }

            @Override
            public void onMoreClick() {

            }
        });
    }

    /**
     * @deprecated Use {@link #setMore(View, OnLoadMoreListener)} instead.
     */
    public void setMore(final View view, final OnLoadMoreListener listener) {
        getEventDelegate().setMore(view, new OnMoreListener() {
            @Override
            public void onMoreShow() {
                listener.onLoadMore();
            }

            @Override
            public void onMoreClick() {

            }
        });
    }

    public void setMore(final int res, final OnMoreListener listener) {
        getEventDelegate().setMore(res, listener);
    }

    public void setMore(final View view, OnMoreListener listener) {
        getEventDelegate().setMore(view, listener);
    }

    public void setNoMore(final int res) {
        getEventDelegate().setNoMore(res, null);
    }

    public void setNoMore(final View view) {
        getEventDelegate().setNoMore(view, null);
    }

    public void setNoMore(final View view, OnNoMoreListener listener) {
        getEventDelegate().setNoMore(view, listener);
    }

    public void setNoMore(final int res, OnNoMoreListener listener) {
        getEventDelegate().setNoMore(res, listener);
    }


    public void setError(final int res) {
        getEventDelegate().setErrorMore(res, null);
    }

    public void setError(final View view) {
        getEventDelegate().setErrorMore(view, null);
    }

    public void setError(final int res, OnErrorListener listener) {
        getEventDelegate().setErrorMore(res, listener);
    }

    public void setError(final View view, OnErrorListener listener) {
        getEventDelegate().setErrorMore(view, listener);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(obtainGridSpanSizeLookUp(gridLayoutManager.getSpanCount()));
        }
        //?????????RecyclerArrayAdapter???????????????????????????
        registerAdapterDataObserver(new FixDataObserver(mRecyclerView));
    }

    public boolean hasEventFooter() {
        return mEventDelegate != null;
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(T object) {
        if (mEventDelegate != null) mEventDelegate.addData(object == null ? 0 : 1);
        if (object != null) {
            synchronized (mLock) {
                mObjects.add(object);
            }
        }
        if (mNotifyOnChange) notifyItemInserted(headers.size() + getCount());
        log("add notifyItemInserted " + (headers.size() + getCount()));
    }

    public void setData(Collection<? extends T> collection) {
        if (mEventDelegate != null) {
            mEventDelegate.addData(collection == null ? 0 : collection.size());
        }
        synchronized (mLock) {
            mObjects.clear();
            mObjects.addAll(collection);
        }
        if (mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    public void addAll(Collection<? extends T> collection) {
        if (mEventDelegate != null)
            mEventDelegate.addData(collection == null ? 0 : collection.size());
        if (collection != null && collection.size() != 0) {
            synchronized (mLock) {
                mObjects.addAll(collection);
            }
        }
        int dataCount = collection == null ? 0 : collection.size();
        if (mNotifyOnChange)
            notifyItemRangeInserted(headers.size() + getCount() - dataCount, dataCount);
        log("addAll notifyItemRangeInserted " + (headers.size() + getCount() - dataCount) + "," + (dataCount));

    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    public void addAll(T[] items) {
        if (mEventDelegate != null) mEventDelegate.addData(items == null ? 0 : items.length);
        if (items != null && items.length != 0) {
            synchronized (mLock) {
                Collections.addAll(mObjects, items);
            }
        }
        int dataCount = items == null ? 0 : items.length;
        if (mNotifyOnChange)
            notifyItemRangeInserted(headers.size() + getCount() - dataCount, dataCount);
        log("addAll notifyItemRangeInserted " + ((headers.size() + getCount() - dataCount) + "," + (dataCount)));
    }

    /**
     * ?????????????????????????????????
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insert(T object, int index) {
        synchronized (mLock) {
            mObjects.add(index, object);
        }
        if (mNotifyOnChange) notifyItemInserted(headers.size() + index);
        log("insert notifyItemRangeInserted " + (headers.size() + index));
    }

    /**
     * ???????????????????????????????????????
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insertAll(T[] object, int index) {
        synchronized (mLock) {
            mObjects.addAll(index, Arrays.asList(object));
        }
        int dataCount = object == null ? 0 : object.length;
        if (mNotifyOnChange) notifyItemRangeInserted(headers.size() + index, dataCount);
        log("insertAll notifyItemRangeInserted " + ((headers.size() + index) + "," + (dataCount)));
    }

    /**
     * ???????????????????????????????????????
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insertAll(Collection<? extends T> object, int index) {
        if (object != null && object.size() != 0) {
            synchronized (mLock) {
                mObjects.addAll(index, object);
            }
            int dataCount = object == null ? 0 : object.size();
//            if (mNotifyOnChange) notifyItemRangeInserted(headers.size() + index, dataCount);
            if (mNotifyOnChange) notifyDataSetChanged();
            log("insertAll notifyItemRangeInserted " + ((headers.size() + index) + "," + (dataCount)));
        }

    }


    public void update(T object, int pos) {
        synchronized (mLock) {
            mObjects.set(pos, object);
        }
        if (mNotifyOnChange) notifyItemChanged(pos);
        log("insertAll notifyItemChanged " + pos);
    }

    public void updateObject(T object, int pos) {
        synchronized (mLock) {
            mObjects.set(pos, object);
        }
    }


    /**
     * ?????????????????????????????????
     *
     * @param object The object to remove.
     */
    public void remove(T object) {
        int position = mObjects.indexOf(object);
        synchronized (mLock) {
            if (mObjects.remove(object)) {
                if (mNotifyOnChange) notifyItemRemoved(headers.size() + position);
                log("remove notifyItemRemoved " + (headers.size() + position));
            }
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param position The position of the object to remove.
     */
    public void remove(int position) {
        synchronized (mLock) {
            mObjects.remove(position);
        }
        if (mNotifyOnChange) {
            int start = headers.size() + position;
            notifyItemRemoved(start);
            notifyItemRangeChanged(start, mObjects.size() - position);
        }
        log("remove notifyItemRemoved " + (headers.size() + position));
    }

    /**
     * ??????
     *
     * @param position The position of the object to remove.
     */
    public void removeNotify(int position) {
        synchronized (mLock) {
            mObjects.remove(position);
        }
        if (mNotifyOnChange) {
            notifyDataSetChanged();
        }

        log("remove notifyItemRemoved " + (headers.size() + position));
    }

    /**
     * ????????????
     * ???{@link #clear()}??????????????????????????????notifyItemRangeRemoved.
     * ?????????????????????add?????????????????????????????????"Scrapped or attached views may not be recycled"???Crash.
     * ??????????????????{@link #clear()}
     */
    public void removeAll() {
        int count = mObjects.size();
        if (mEventDelegate != null) mEventDelegate.clear();
        synchronized (mLock) {
            mObjects.clear();
        }
        if (mNotifyOnChange) notifyItemRangeRemoved(headers.size(), count);
        log("clear notifyItemRangeRemoved " + (headers.size()) + "," + (count));
    }

    /**
     * ????????????
     */
    public void clear() {
        int count = mObjects.size();
        if (mEventDelegate != null) mEventDelegate.clear();
        synchronized (mLock) {
            mObjects.clear();
        }
        if (mNotifyOnChange) notifyDataSetChanged();
        log("clear notifyItemRangeRemoved " + (headers.size()) + "," + (count));
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     *                   in this adapter.
     */
    public void sort(Comparator<? super T> comparator) {
        synchronized (mLock) {
            Collections.sort(mObjects, comparator);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }


    /**
     * Control whether methods that change the list ({@link #add},
     * {@link #insert}, {@link #remove}, {@link #clear}) automatically call
     * {@link #notifyDataSetChanged}.  If set to false, caller must
     * manually call notifyDataSetChanged() to have the changes
     * reflected in the attached view.
     * <p>
     * The default is true, and calling notifyDataSetChanged()
     * resets the flag to true.
     *
     * @param notifyOnChange if true, modifications to the list will
     *                       automatically call {@link
     *                       #notifyDataSetChanged}
     */
    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }


    /**
     * Returns the context associated with this array adapter. The context is used
     * to create views from the resource passed to the constructor.
     *
     * @return The Context associated with this adapter.
     */
    public Context getContext() {
        return mContext;
    }

    public void setContext(Context ctx) {
        mContext = ctx;
    }

    /**
     * ????????????????????????????????????view???????????????????????????item?????????
     *
     * @return
     */
    @Deprecated
    @Override
    public final int getItemCount() {
        return getCount() + headers.size() + footers.size();
    }

    /**
     * ????????????????????????item??????
     *
     * @return
     */
    public int getCount() {
        return mObjects.size();
    }

    private StateViewHolder createSpViewByType(ViewGroup parent, int viewType) {
        for (ItemView headerView : headers) {
            if (headerView.hashCode() == viewType) {
                View view = headerView.onCreateView(parent);
                StaggeredGridLayoutManager.LayoutParams layoutParams;
                if (view.getLayoutParams() != null)
                    layoutParams = new StaggeredGridLayoutManager.LayoutParams(view.getLayoutParams());
                else
                    layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setFullSpan(true);
                view.setLayoutParams(layoutParams);
                return new StateViewHolder(view, headerView);
            }
        }
        for (ItemView footerview : footers) {
            if (footerview.hashCode() == viewType) {
                View view = footerview.onCreateView(parent);
                StaggeredGridLayoutManager.LayoutParams layoutParams;
                if (view.getLayoutParams() != null)
                    layoutParams = new StaggeredGridLayoutManager.LayoutParams(view.getLayoutParams());
                else
                    layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setFullSpan(true);
                view.setLayoutParams(layoutParams);
                return new StateViewHolder(view, footerview);
            }
        }
        return null;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof StateViewHolder) {
            ItemView item = ((StateViewHolder) holder).getItemVIew();
            OnHeaderOrFooterAttachedToWindow(item);
        } else {
            OnViewAttachedToWindow(holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof StateViewHolder) {
            ItemView item = ((StateViewHolder) holder).getItemVIew();
            OnHeaderOrFooterDetachedFromWindow(item);
        } else {
            OnViewDetachedFromWindow(holder);
        }
    }

    public void OnHeaderOrFooterAttachedToWindow(ItemView itemView) {
    }

    public void OnHeaderOrFooterDetachedFromWindow(ItemView itemView) {
    }


    public void OnViewAttachedToWindow(BaseViewHolder holder) {
    }

    public void OnViewDetachedFromWindow(BaseViewHolder holder) {
    }

    @Override
    public final BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StateViewHolder stateViewHolder = createSpViewByType(parent, viewType);
        if (stateViewHolder != null) {
            return stateViewHolder;
        }

        final BaseViewHolder viewHolder = OnCreateViewHolder(parent, viewType);

        //itemView ???????????????
        if (mItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(viewHolder.getAdapterPosition() - headers.size());
                }
            });
        }

        if (mItemLongClickListener != null) {
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mItemLongClickListener.onItemLongClick(viewHolder.getAdapterPosition() - headers.size());
                }
            });
        }
        return viewHolder;
    }

    abstract public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType);


    @Override
    public final void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.itemView.setId(position);
        if (headers.size() != 0 && position < headers.size()) {
            headers.get(position).onBindView(holder.itemView);
            return;
        }

        int i = position - headers.size() - getCount();
        if (footers.size() != 0 && i >= 0) {
            footers.get(i).onBindView(holder.itemView);
            return;
        }
        OnBindViewHolder(holder, position - headers.size());
    }


    public void OnBindViewHolder(BaseViewHolder holder, final int position) {
        holder.setData(getItem(position));
    }


    @Deprecated
    @Override
    public final int getItemViewType(int position) {
        if (headers.size() != 0) {
            if (position < headers.size()) return headers.get(position).hashCode();
        }
        if (footers.size() != 0) {
            /*
            eg:
            0:header1
            1:header2   2
            2:object1
            3:object2
            4:object3
            5:object4
            6:footer1   6(position) - 2 - 4 = 0
            7:footer2
             */
            int i = position - headers.size() - getCount();
            if (i >= 0) {
                return footers.get(i).hashCode();
            }
        }
        return getViewType(position - headers.size());
    }

    public int getViewType(int position) {
        return 0;
    }


    public List<T> getAllData() {
        return new ArrayList<>(mObjects);
    }

    /**
     * {@inheritDoc}
     */
    public T getItem(int position) {
        return mObjects.get(position);
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    public int getPosition(T item) {
        return mObjects.indexOf(item);
    }

    /**
     * {@inheritDoc}
     */
    public long getItemId(int position) {
        return position;
    }

    private static class StateViewHolder extends BaseViewHolder {

        private ItemView mView;

        public StateViewHolder(View itemView, ItemView view) {
            super(itemView);
            this.mView = view;
        }

        public ItemView getItemVIew() {
            return mView;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    private static void log(String content) {
        if (EasyRecyclerView.DEBUG) {
            Log.i(EasyRecyclerView.TAG, content);
        }
    }
}
