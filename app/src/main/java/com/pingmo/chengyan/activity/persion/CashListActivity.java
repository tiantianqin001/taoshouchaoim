package com.pingmo.chengyan.activity.persion;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.butel.easyrecyclerview.EasyRecyclerView;
import com.butel.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.adapter.CashListAdapter;
import com.pingmo.chengyan.bean.CashItemBean;
import com.pingmo.chengyan.bean.CashListBean;
import com.pingmo.chengyan.customview.WrapContentLinearLayoutManager;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CashListActivity extends BaseActivity implements OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener, RecyclerArrayAdapter.OnErrorListener {

    public static final int QUERY_TYPE_REFRESH = 1;
    public static final int QUERY_TYPE_LOADMORE = 2;
    private int curType = QUERY_TYPE_REFRESH;
    private int curPage = 0;
    private int queryPage = 1;
    private int pageSize = 30;
    private double totalMoney;

    private boolean mHasSetLoadMore = false;

    private SwipeToLoadLayout swipeToLoadLayout = null;
    private EasyRecyclerView mRecyclerView = null;
    private CashListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_list_layout);
        initView();
        initData();
        addActivity(this);
    }

    private void initView() {
        getTitleBar().setTitle("账单明细");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        swipeToLoadLayout = findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(this);
        mRecyclerView = swipeToLoadLayout.findViewById(R.id.swipe_target);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this));
    }

    private void initData() {
        totalMoney = getIntent().getDoubleExtra("total_money", 0.0);
        mAdapter = new CashListAdapter(this, totalMoney);
        mAdapter.setNoMore(R.layout.easyrecycle_load_no_more_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        onRefresh();
    }

    private void requestCashList() {
        String url = Common.WALLET_RECORD;
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
                        CashListBean listBean = JSON.parseObject(content, CashListBean.class);
                        Message message = Message.obtain();
                        message.what = Common.Interface_success;
                        message.obj = listBean;
                        mHandler.sendMessage(message);

                    } else if (code == 403) {
                        Intent intent = new Intent();
                        intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                        intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                        sendBroadcast(intent);
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
        queryPage = 1;
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

    @Override
    protected void handleActMessage(Message msg) {
        super.handleActMessage(msg);
        switch (msg.what) {
            case Common.Interface_err:
                String info = (String) msg.obj;
                ToastUtils.show(info);
                dismissWaitingDlg();
                break;
            case Common.Interface_success:
                dismissWaitingDlg();
                CashListBean listBean = (CashListBean) msg.obj;
                List<CashItemBean> list = listBean.getData().getRows();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
