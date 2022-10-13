package com.pingmo.chengyan.activity.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.gyf.immersionbar.ImmersionBar;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.customview.PlaceHolderView;
import com.pingmo.chengyan.customview.TitleBar;
import com.pingmo.chengyan.dialoge.LoadDialog;
import com.pingmo.chengyan.utils.BaseHandler;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    protected static boolean isShow = false;
    public static   List<Activity> activities = new ArrayList<Activity>();
    protected PlaceHolderView mPlaceHolderView;

    protected Unbinder bind;

    private TitleBar mTitleBar;
    private LoadDialog mDialog;
    protected BaseHandler mHandler = new BaseHandler(this, new BaseHandler.HandleMsgCallback() {
        @Override
        public void handleMessage(Message msg) {
            handleActMessage(msg);
        }
    });

    protected void handleActMessage(Message msg) {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //保持屏幕常亮，也可以再布局文件顶层：android:keepScreenOn="true"
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AppManager.getAppManager().addActivity(this);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .statusBarDarkFont(true)
                .init();

        //获取权限
        requestPerm();

        String sHA1 = sHA1(this);
        //  Log.i("Amap"+ "sHA1......"+ sHA1,null);
    }

    public TitleBar getTitleBar() {
        if (mTitleBar == null) {
            mTitleBar = new TitleBar(this, ((ViewGroup) findViewById(android.R.id.content))
                    .getChildAt(0));
        }
        return mTitleBar;
    }

    protected void showWaitingDlg() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = new LoadDialog(this);
        mDialog.show();
    }

    protected void dismissWaitingDlg() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }


    /**
     * 添加Activity
     * @param activity 添加的Activity对象
     * */
    public  void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 删除Activity
     * @param activity 删除的Activity对象
     * */
    public  void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    private void requestPerm() {
        XXPermissions.with(this)
                //单个权限
                .permission(Permission.RECORD_AUDIO)
                .permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .permission(Permission.CAMERA)
                // .interceptor(new IPermissionInterceptor() {})
                //  .unchecked()
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            //  ToastUtils.show("权限所有的获取");

                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Log.e(TAG, "onDenied：被永久拒绝授权，请手动授予权限 ");
                            XXPermissions.startPermissionActivity(BaseActivity.this, permissions);
                        } else {
                            Log.e(TAG, "onDenied: 权限获取失败");
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == XXPermissions.REQUEST_CODE) {
            if (XXPermissions.isGranted(this, Permission.ACCESS_FINE_LOCATION) &&
                    XXPermissions.isGranted(this, Permission.ACCESS_COARSE_LOCATION)) {
                ToastUtils.show("权限已获取");

            } else {
                ToastUtils.show("权限获取失败");
            }
        }
    }


    /**
     * 设置占位布局
     *
     * @param placeHolderView 继承了占位布局规范的View
     */
    public void setPlaceHolderView(PlaceHolderView placeHolderView) {
        this.mPlaceHolderView = placeHolderView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }
        removeActivity(this);
    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭所有Activity
     * */
    public static   void finishAll() {
        for (Activity activity : activities) {
            if (activity!=null) {
                activity.finish();
            }
        }
        activities.clear();
    }


}
