package com.pingmo.chengyan.activity.persion;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.bean.UpgradeBean;
import com.pingmo.chengyan.customview.CommonDialog;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.CommonUtil;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_layout);
        initView();
        addActivity(this);
    }

    private void initView() {
        getTitleBar().setTitle("关于我们");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        findViewById(R.id.appraise).setOnClickListener(this);
        findViewById(R.id.upgrade).setOnClickListener(this);
    }

    private void getAppInfo() {
        showWaitingDlg();
        String url = Common.SOFTWARE_PACKAGE;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("deviceType", "android");
        OkHttp3_0Utils.getInstance().asyncGetOkHttp(url, mapParams, new Callback() {
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
                        UpgradeBean bean = JSON.parseObject(content, UpgradeBean.class);
                        Message message = Message.obtain();
                        message.what = Common.Interface_success;
                        message.obj = bean;
                        mHandler.sendMessage(message);

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

    private boolean isDownLoadPackage(String localVersion, String serviceVersion) {
        QZXTools.logD("localVersion:" + localVersion + "|serverVersion:" + serviceVersion);

        if (TextUtils.isEmpty(serviceVersion) || TextUtils.isEmpty(localVersion)) {
            return false;
        }

        String[] localArray = localVersion.split("\\.");
        String[] serviceArray = serviceVersion.split("\\.");

        // 以版本分段长度短的为准，进行比较
        int minLength = 0;
        if (localArray.length <= serviceArray.length) {
            minLength = localArray.length;
        } else {
            minLength = serviceArray.length;
        }

        try {
            for (int i = 0; i < minLength; i++) {
                int localVer = Integer.parseInt(localArray[i]);
                int serverVer = Integer.parseInt(serviceArray[i]);
                if (serverVer > localVer) {
                    return true;
                } else if (serverVer < localVer) {
                    return false;
                }
            }

            if (localArray.length < serviceArray.length) {
                // 经过对位比较，没有return（对位比较全部相等），并且服务端分段长度大于本地分段长度，则认为需要升级
                return true;
            }
        } catch (NumberFormatException e) {
            QZXTools.logE("版本号含非数字字符，发生异常", e);
        }

        return false;
    }

    private void showUpgradeDialog(UpgradeBean.DataDTO bean) {
        CommonDialog commonDialog = new CommonDialog(this);
        commonDialog.setMessage("发现新版本，是否更新？");
        commonDialog.setPositiveButton(new CommonDialog.BtnClickedListener() {
            @Override
            public void onBtnClicked() {
                downloadApk(bean.getDownloadHref());
            }
        });
        commonDialog.setCancelButton(bean.isForceUpdate() ? "退出" : "取消", new CommonDialog.BtnClickedListener() {
            @Override
            public void onBtnClicked() {
                if (bean.isForceUpdate()) {
                    System.exit(0);
                }
            }
        });
        commonDialog.showDialog();
    }

    private void downloadApk(String url) {
        OkHttp3_0Utils.getInstance().downloadSingleFileForOnce(url, "new_apk" + System.currentTimeMillis() + ".apk", new OkHttp3_0Utils.DownloadCallback() {
            @Override
            public void downloadProcess(int value) {
                QZXTools.logE("downloadProcess: " + value, null);
            }

            @Override
            public void downloadComplete(String filePath) {
                QZXTools.logE("downloadComplete: " + filePath, null);
                installApk(filePath);
                dismissWaitingDlg();
            }

            @Override
            public void downloadFailure() {
                QZXTools.logE("downloadFailure: ", null);
                dismissWaitingDlg();
            }
        });
    }

    private void installApk(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                install24(file);
            } else {
                installBelow24(file);
            }
        }
    }

    private void installBelow24(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void install24(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(this, this.getPackageName() + ".fileProvider", file);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
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
                UpgradeBean loginBean = (UpgradeBean) msg.obj;
                UpgradeBean.DataDTO bean = loginBean.getData();
                if (bean != null && !TextUtils.isEmpty(bean.getVersion()) && !TextUtils.isEmpty(bean.getDownloadHref())) {
                    if (isDownLoadPackage(CommonUtil.getPackageInfo().versionName, bean.getVersion())) {
                        showUpgradeDialog(bean);
                    } else {
                        ToastUtils.show("已是最新版本");
                    }
                } else {
                    ToastUtils.show("已是最新版本");
                }

                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appraise:
                break;
            case R.id.upgrade:
                getAppInfo();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
