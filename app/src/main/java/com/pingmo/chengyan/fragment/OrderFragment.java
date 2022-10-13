package com.pingmo.chengyan.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.butel.easyrecyclerview.EasyRecyclerView;
import com.butel.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.adapter.OrderListAdapter;
import com.pingmo.chengyan.bean.OrderItemBean;
import com.pingmo.chengyan.bean.OrderListBean;
import com.pingmo.chengyan.customview.WrapContentLinearLayoutManager;
import com.pingmo.chengyan.dialoge.LoadDialog;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.BaseHandler;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OrderFragment extends Fragment implements OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener, RecyclerArrayAdapter.OnErrorListener {
    public static final String ORDER_TYPE = "ORDER_TYPE";
    public static final int ORDER_TYPE_SEND = 1;
    public static final int ORDER_TYPE_RECEIVED = 2;

    public static final int QUERY_TYPE_REFRESH = 1;
    public static final int QUERY_TYPE_LOADMORE = 2;
    private int curType = QUERY_TYPE_REFRESH;
    private int curPage = 0;
    private int queryPage = 0;
    private int pageSize = 20;

    private boolean mHasSetLoadMore = false;

    private LoadDialog mDialog;
    private BaseHandler mHandler = new BaseHandler(this, new BaseHandler.HandleMsgCallback() {
        @Override
        public void handleMessage(Message msg) {
            handleActMessage(msg);
        }
    });

    private SwipeToLoadLayout swipeToLoadLayout = null;
    private EasyRecyclerView mRecyclerView = null;
    private OrderListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.easyrecycler_content_layout, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        swipeToLoadLayout = view.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(this);
        mRecyclerView = swipeToLoadLayout.findViewById(R.id.swipe_target);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity()));
    }

    private void initData() {
        mAdapter = new OrderListAdapter(getContext());
        mAdapter.setNoMore(R.layout.easyrecycle_load_no_more_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        onRefresh();
    }

    private void showWaitingDlg() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = new LoadDialog(getContext());
        mDialog.show();
    }

    private void dismissWaitingDlg() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private void requestCashList() {
        String url = Common.SHOP_ORDER;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("pageNum", queryPage + "");
        mapParams.put("pageSize", pageSize + "");
        Map<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncGetOkHttp(url, mapParams, headerParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = Common.Interface_err;
                message.obj = "获取数据失败";
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                    int code = jsonObject.optInt("code");
                    String msg = jsonObject.optString("msg");
                    if (code == 200) {
                        OrderListBean listBean = JSON.parseObject(content, OrderListBean.class);
                        Message message = Message.obtain();
                        message.what = Common.Interface_success;
                        message.obj = listBean;
                        mHandler.sendMessage(message);

                    }else if (code == 403){
                        Intent intent = new Intent();
                        intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                        intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                        getContext().sendBroadcast(intent);
                    } else {
                        Message message = Message.obtain();
                        message.what = Common.Interface_err;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = Common.Interface_err;
                    message.obj = "获取数据失败";
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        QZXTools.logD("onRefresh");
        queryPage = 0;
        curType = QUERY_TYPE_REFRESH;
        requestCashList();
    }

    @Override
    public void onLoadMore() {
        QZXTools.logD("onLoadMore");
        queryPage = curPage + 1;
        curType = QUERY_TYPE_LOADMORE;
        requestCashList();
    }

    @Override
    public void onErrorShow() {

    }

    @Override
    public void onErrorClick() {
        if (mAdapter != null) {
            mAdapter.resumeMore();
        }
    }

    private void handleActMessage(Message msg) {
        switch (msg.what) {
            case Common.Interface_err:
                String info = (String) msg.obj;
                ToastUtils.show(info);
                dismissWaitingDlg();
                break;
            case Common.Interface_success:
                dismissWaitingDlg();
                OrderListBean listBean = (OrderListBean) msg.obj;
                List<OrderItemBean> list = listBean.getData().getRows();
                if (curType == QUERY_TYPE_REFRESH) {
                    curPage = queryPage;
                    if (swipeToLoadLayout.isRefreshing()) {
                        swipeToLoadLayout.setRefreshing(false);
                    }
                    mAdapter.setData(list);
                    if (list != null && list.size() >= pageSize) {
                        if (!mHasSetLoadMore) {
                            mHasSetLoadMore = true;
                            mAdapter.setNoMore(R.layout.easyrecycle_load_no_more_view);
                            mAdapter.setMore(R.layout.easyrecycle_load_more_view, this);
                            mAdapter.setError(R.layout.easyrecycle_load_error_view, this);
                        }
                    } else {
                        mAdapter.stopMore();
                    }
                } else {
                    if (list != null && list.size() > 0) {
                        curPage = queryPage;
                    }
                    mAdapter.addAll(list);
                }
                break;
        }
    }

}
