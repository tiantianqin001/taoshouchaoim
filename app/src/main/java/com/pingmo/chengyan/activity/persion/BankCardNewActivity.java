package com.pingmo.chengyan.activity.persion;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.adapter.BankListAdapter;
import com.pingmo.chengyan.bean.BankItemBean;
import com.pingmo.chengyan.customview.WrapContentLinearLayoutManager;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;
import com.pingmo.chengyan.utils.WindowUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BankCardNewActivity extends BaseActivity implements View.OnClickListener, BankListAdapter.OnSelectedBank {

    public static final String IS_SELECTED_TYPE = "isSelectType";
    public static final String SELECTED_BEAN = "selectedBean";

    public static final int BANK_LIST_SUCCESS = 0x0001;
    public static final int BANK_LIST_FAILED = 0x0002;
    public static final int BANK_ADD_SUCCESS = 0x0003;
    public static final int BANK_ADD_FAILED = 0x0004;
    public static final int BANK_DELETE_SUCCESS = 0x0005;
    public static final int BANK_DELETE_FAILED = 0x0006;

    private RecyclerView mRecyclerView;
    private TextView mBtnIcon, mBtn;
    private BankListAdapter mAdapter;
    private int ScreenHeight;
    private int otherheight;

    private boolean isSelectType = false;//是否是选择银行卡模式

    private boolean isEdit = false;
    private boolean isWeiget = false;

    private List list = new ArrayList();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankcard_layout);
        isSelectType = getIntent().getBooleanExtra(IS_SELECTED_TYPE, false);
        initView();
        initData();
        addActivity(this);
    }

    private void initView() {
        getTitleBar().setTitle("银行卡");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        mRecyclerView = findViewById(R.id.list);
        mBtnIcon = findViewById(R.id.btn_icon);
        mBtn = findViewById(R.id.btn_txt);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this));
        findViewById(R.id.btn).setOnClickListener(this);
        edit();
    }

    private void initData() {
        mAdapter = new BankListAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        ScreenHeight = WindowUtil.getScreenHeight(this, false);
        otherheight = getResources().getDimensionPixelSize(R.dimen.qb_px_120)
                + getResources().getDimensionPixelSize(R.dimen.qb_px_44)
                + WindowUtil.getStatusBarHeight(this);
        ViewTreeObserver viewTreeObserver = mRecyclerView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                refreshList(mRecyclerView.getHeight());
            }
        });

        setTest();
        requestBankList();
    }

    private void setTest() {
        List<BankItemBean> testList = new ArrayList<>();
        BankItemBean bean1 = new BankItemBean();
        bean1.setBackCode("1");
        bean1.setBackName("测试银行1");
        bean1.setCardNumber("1234123412341234");
        testList.add(bean1);
        BankItemBean bean2 = new BankItemBean();
        bean2.setBackCode("2");
        bean2.setBackName("测试银行2");
        bean2.setCardNumber("1234123412341234");
        testList.add(bean2);
        BankItemBean bean3 = new BankItemBean();
        bean3.setBackCode("3");
        bean3.setBackName("测试银行3");
        bean3.setCardNumber("1234123412341234");
        testList.add(bean3);
        mAdapter.setData(testList);
    }

    private void edit() {
        if (isEdit) {
            getTitleBar().enableRightBtn("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doEdit();
                }
            });
        } else {
            getTitleBar().enableRightBtn("删除", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doEdit();
                }
            });
        }
    }

    private void refreshList(int recycleheight) {
        boolean weiget = ScreenHeight < recycleheight + otherheight;
        if (isWeiget != weiget) {
            isWeiget = weiget;
            if (weiget) {
                mRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
            } else {
                mRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    private void doEdit() {
        isEdit = !isEdit;
        edit();
        mAdapter.setEdit(isEdit);
        if (isEdit) {
            mBtnIcon.setText("");
            mBtnIcon.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.trash, 0, 0, 0);
            mBtn.setText("删除银行卡");
        } else {
            mBtnIcon.setText("+");
            mBtnIcon.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mBtn.setText("添加银行卡");
        }
    }

    private void add() {
        Intent intent = new Intent(this, BankAddActivity.class);
        startActivity(intent);
    }

    private void delete() {
        if (mAdapter != null) {
            mAdapter.getSelectedList();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                if (isEdit) {
                    delete();
                } else {
                    add();
                }
                break;
        }
    }

    @Override
    protected void handleActMessage(Message msg) {
        super.handleActMessage(msg);
        switch (msg.what) {
            case Common.Interface_success:
                break;
        }
    }

    private void requestBankList() {
        String url = Common.BANK_LIST;
        Map<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncGetOkHttpHadHeader(url, headerParams, new Callback() {
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
//                        WalletStailBean walletStailBean = JSON.parseObject(content, WalletStailBean.class);
//                        Message message = Message.obtain();
//                        message.what = Common.Interface_success;
//                        message.obj = walletStailBean;
//                        mHandler.sendMessage(message);

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
                    message.obj = "当前服务登录失败";
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    private void addCard() {
        showWaitingDlg();
        String deviceName = QZXTools.getSystemModel();
        String deviceId = QZXTools.getDeviceId(this);
        String url = Common.ADD_BANK;
        Map<String, String> mapParams = new LinkedHashMap<>();
//        mapParams.put("nickName", mName.getText().toString());
        Map<String, String> headParams = new LinkedHashMap<>();
        headParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, headParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = BANK_ADD_FAILED;
                message.obj = "添加银行卡失败";
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
                        Message message = Message.obtain();
                        message.what = BANK_ADD_SUCCESS;
                        message.obj = msg;
                        mHandler.sendMessage(message);

                    } else if (code == 403) {
                        Intent intent = new Intent();
                        intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                        intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                        sendBroadcast(intent);
                    } else {
                        Message message = Message.obtain();
                        message.what = BANK_ADD_FAILED;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = BANK_ADD_FAILED;
                    message.obj = "添加银行卡失败";
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    private void deleteCard() {
        showWaitingDlg();
        String deviceName = QZXTools.getSystemModel();
        String deviceId = QZXTools.getDeviceId(this);
        String url = Common.DELETE_BANK;
        Map<String, String> mapParams = new LinkedHashMap<>();
//        mapParams.put("nickName", mName.getText().toString());
        Map<String, String> headParams = new LinkedHashMap<>();
        headParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncBodyDeleteOkHttp(url, mapParams, headParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = BANK_ADD_FAILED;
                message.obj = "添加银行卡失败";
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
                        Message message = Message.obtain();
                        message.what = BANK_ADD_SUCCESS;
                        message.obj = msg;
                        mHandler.sendMessage(message);

                    } else if (code == 403) {
                        Intent intent = new Intent();
                        intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                        intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                        sendBroadcast(intent);
                    } else {
                        Message message = Message.obtain();
                        message.what = BANK_ADD_FAILED;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = BANK_ADD_FAILED;
                    message.obj = "添加银行卡失败";
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }

    @Override
    public void onSelected(BankItemBean bean) {
        if (isSelectType) {
            Intent intent = new Intent();
            intent.putExtra(SELECTED_BEAN, bean);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}
