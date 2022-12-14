package com.pingmo.chengyan.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.core.content.FileProvider;


import com.pingmo.chengyan.MyAPP;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/5/16.
 * qzx
 * ?????????
 */

public class QZXTools {

    /**
     private AppInfo getAppInfoFromPackageName(String packageName) {
     AppInfo appInfo = new AppInfo();
     AppInfo appInfo = new AppInfo();
     PackageManager pm = getPackageManager();
     try {
     PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
     if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
     appInfo.setIsSystemApp(true);
     } else {
     appInfo.setIsSystemApp(false);
     }
     appInfo.setName(packageInfo.applicationInfo.loadLabel(pm).toString());
     appInfo.setPackageName(packageName);
     } catch (PackageManager.NameNotFoundException e) {
     e.printStackTrace();
     }
     return appInfo;
     }

     private List<AppInfo> getAllApp() {
     List<AppInfo> appInfoList = new ArrayList<>();
     PackageManager pm = getPackageManager();
     Intent intent = new Intent();
     intent.setAction(Intent.ACTION_MAIN);
     intent.addCategory(Intent.CATEGORY_LAUNCHER);
     List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
     for (int i = 0; i < resolveInfos.size(); i++) {
     ResolveInfo resolveInfo = resolveInfos.get(i);
     String packageName = resolveInfo.activityInfo.packageName;
     if (packageName.equals(getPackageName())) {
     //??????????????????
     continue;
     }

     AppInfo appInfo = new AppInfo();

     if ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
     //?????????????????????
     appInfo.setIsSystemApp(true);
     //??????resolveInfo.activityInfo.applicationInfo.name????????????null
     //                QZXTools.logD("System AppInfo=" + appInfo + ";class name=" + resolveInfo.activityInfo.name);
     } else {
     appInfo.setIsSystemApp(false);
     }
     appInfo.setName(resolveInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
     appInfo.setPackageName(resolveInfo.activityInfo.packageName);
     appInfo.setOrderNum(i);
     //??????resolveInfo.activityInfo.applicationInfo.name????????????null
     //            QZXTools.logD("Third AppInfo=" + appInfo + ";class name=" + resolveInfo.activityInfo.name);
     appInfoList.add(appInfo);

     //??????GreenDao?????????
     AppInfoDao appInfoDao = MyApplication.getInstance().getDaoSession().getAppInfoDao();
     appInfoDao.insertOrReplaceInTx(appInfoList);
     }
     return appInfoList;
     }*/

    /**
     * ????????????????????????----Activity
     * <p>
     * //    private int clickCount = 0;
     * //    private long firstTime;
     * //
     * //    //????????????
     * //    @Override
     * //    public void onBackPressed() {
     * //        clickCount++;//????????????
     * //        if (clickCount == 2 && (SystemClock.elapsedRealtime() - firstTime) < 2000) {
     * //            super.onBackPressed();
     * //        } else {
     * //            clickCount = 1;
     * //            firstTime = SystemClock.elapsedRealtime();
     * //            QZXTools.popToast(this, "???????????????????????????", false);
     * //        }
     * //    }
     */


    /**
     * ?????????????????????????????????
     * <p>
     * protected void hideBottomUIMenu() {
     * //?????????????????????????????????
     * if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
     * View v = this.getWindow().getDecorView();
     * v.setSystemUiVisibility(View.GONE);
     * } else if (Build.VERSION.SDK_INT >= 19) {
     * //for new api versions.
     * View decorView = getWindow().getDecorView();
     * int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
     * | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
     * decorView.setSystemUiVisibility(uiOptions);
     * }
     * }
     */

    /**
     * ??????????????????????????????
     * <p>
     * ?????????????????????????????????????????????padding
     */
//    boolean isExistNavigation = checkExistNavigationBar(this);
//        logE("isExistNavigation=" + isExistNavigation
//                + ";density=" + getResources().getDisplayMetrics().densityDpi, null);
//
//        if (isExistNavigation) {
//        int navigationHeight = getNavigationHeight(this);
//        logE("navigationHeight=" + navigationHeight, null);
//        mainLayout.setPadding(0, 0, 0, navigationHeight);
//    }
//
//    //??????????????????NavigationBar,EasyUi???????????????????????????????????????????????????
//    private boolean checkExistNavigationBar(Context context) {
//        boolean hasNavigationBar = false;
//        Resources rs = context.getResources();
//        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
//        if (id > 0) {
//            hasNavigationBar = rs.getBoolean(id);
//        }
//        try {
//            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
//            Method m = systemPropertiesClass.getMethod("get", String.class);
//            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
//            if ("1".equals(navBarOverride)) {
//                hasNavigationBar = false;
//            } else if ("0".equals(navBarOverride)) {
//                hasNavigationBar = true;
//            }
//        } catch (Exception e) {
//
//        }
//        return hasNavigationBar;
//    }


    //----------------------------Android???????????????????????????---------------------------
    private static long firstTime;
    private static boolean isStart = false;

    /**
     * ???????????????????????????
     */
    public static boolean canClick() {
        long curTime = System.currentTimeMillis();
        if (curTime - firstTime >= 1000) {
            isStart = true;
        }

        if (isStart) {
            isStart = false;
            firstTime = System.currentTimeMillis();
            return true;
        }

        return false;
    }


    //-----------------------------???????????????--------------------------

    /**
     * @describe ???????????????
     * @author luxun
     * create at 2017/4/12 0012 10:53
     */
    public static void saveObject(File file, Serializable value) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        FileOutputStream out = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            out = new FileOutputStream(file);
            oos.writeObject(value);
            out.write(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                    oos.close();
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static Object readObject(File file) {
        if (file == null || !file.exists() || file.length() <= 0) {
            return null;
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            Object reObject = ois.readObject();
            return reObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (ois != null)
                    ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * ?????????InputStream?????????????????????
     *
     * @param is
     * @return
     */
    public static String toConvertString(InputStream is) throws UnsupportedEncodingException {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is, "utf-8");
        BufferedReader read = new BufferedReader(isr);
        String sTempOneLine;
        try {
            while ((sTempOneLine = read.readLine()) != null) {
                res.append(sTempOneLine);
            }
//            String line;
//            line = read.readLine();
//            while (line != null) {
//                res.append(line);
//                line = read.readLine();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != isr) {
                    isr.close();
                    isr.close();
                }
                if (null != read) {
                    read.close();
                    read = null;
                }
                if (null != is) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
            }
        }
        return res.toString();
    }

    //-----------------------------???????????????--------------------------

    /**
     * ??????????????????
     */
    public static void logDeviceInfo(Context context) {
        logE("densityDpi=" + context.getResources().getDisplayMetrics().densityDpi
                + ";widthpixel=" + context.getResources().getDisplayMetrics().widthPixels
                + ";heightpixel=" + context.getResources().getDisplayMetrics().heightPixels
                + ";scaleDensity=" + context.getResources().getDisplayMetrics().scaledDensity, null);
    }


    /**
     * ??????????????????App???ICON
     */
    public static Drawable getIconFromPackageName(Context context, String packageName) {
        Drawable drawable = null;
        try {
            drawable = context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    /**
     * ??????????????????????????????
     */
    public static Intent getLauncherIntent(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return null;
        }
        return MyAPP.getInstance().getApplicationContext()
                .getPackageManager().getLaunchIntentForPackage(pkgName);
    }

    /**
     * ???????????????????????????
     */
    public static int getVirtualBarHeigh(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }

    //------------------------Android Setting
    //???????????????????????????
    public static void appDetailInfo(Activity activity, int RequestCode) {
        Intent localIntent = new Intent();
        //???????????????????????????onActivityResuolt???????????????
//        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
        }
        activity.startActivityForResult(localIntent, RequestCode);
    }

    public static Intent defaultApi(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }

    //---------------------------??????????????????????????????
    public static Intent huaweiApi(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }

    public static Intent xiaomiApi(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }

    public static Intent vivoApi(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packagename", context.getPackageName());
        intent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity");
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }

    private static Intent oppoApi(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }

    public static Intent meizuApi(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", context.getPackageName());
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }
    //---------------------------??????????????????????????????

    /**
     * ??????????????????????????????intent???Activity
     */
    public static boolean hasActivity(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    /**
     * ????????????????????????WIFI????????????
     */
    public static void enterWifiSetting(Context context) {
        /**
         //????????????button bar,????????????true???????????????
         private static final String EXTRA_PREFS_SHOW_BUTTON_BAR = "extra_prefs_show_button_bar";
         //???????????????????????????????????????????????????????????????
         private static final String EXTRA_PREFS_SET_NEXT_TEXT = "extra_prefs_set_next_text";
         //???????????????????????????????????????????????????????????????
         private static final String EXTRA_PREFS_SET_BACK_TEXT = "extra_prefs_set_back_text";
         //???????????????????????????????????????????????????wifi????????????????????????????????????
         private static final String EXTRA_ENABLE_NEXT_ON_CONNECT = "wifi_enable_next_on_connect";
         * */
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        intent.putExtra("extra_prefs_show_button_bar", true);
        intent.putExtra("extra_prefs_set_next_text", "??????");
        intent.putExtra("extra_prefs_set_back_text", "");
        intent.putExtra("wifi_enable_next_on_connect", true);
        context.startActivity(intent);


        //todo  ?????????????????????????????????
      /*  Intent intent = new Intent();
        intent.setClassName("com.hat.settings", "com.hat.settings.SettingsActivity");
        context.startActivity(intent);*/
    }

    //------------------------Android Setting

    //---------------------------Toast----------------------

    private static Field sField_TN;
    private static Field sField_TN_Handler;

    static {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            try {
                sField_TN = Toast.class.getDeclaredField("mTN");
                sField_TN.setAccessible(true);

                sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
                sField_TN_Handler.setAccessible(true);
            } catch (Exception e) {
            }
        }
    }

    private static void hook(Toast toast) {
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafelyHandlerWarpper(preHandler));
        } catch (Exception e) {
        }
    }

    private static class SafelyHandlerWarpper extends Handler {

        private Handler impl;

        public SafelyHandlerWarpper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                impl.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static Toast mToast;

    /**
     * ??????????????????????????????Context??????
     */
    public static void setmToastNull() {
        mToast = null;
    }

    /**
     * ????????????toast,???????????????????????????
     */
    public static void popToast(Context context, String content, boolean isShowLong) {
        //???????????????????????????????????????mToast?????????????????????????????????context???????????????????????????????????????
//        if (mToast == null) {
//            mToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
//        }

        //????????????????????????????????????????????????Context.getResources()??????
        if (context == null) {
            return;
        }

        //?????????????????????????????????
        Toast mToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        if (isShowLong) {
            //7000ms
            mToast.setDuration(Toast.LENGTH_LONG);
        } else {
            //4000ms
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
//        mToast.setText(content);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            hook(mToast);
        }

        mToast.show();
    }

    /**
     * ??????????????????
     */
    public static void popToastTwo(Context context, String content, boolean isShowLong) {
        //?????????
        WeakReference<Context> weakContext = new WeakReference<Context>(context);
        if (mToast == null) {
            mToast = Toast.makeText(weakContext.get(), content, Toast.LENGTH_SHORT);
        }
        if (isShowLong) {
            //7000ms
            mToast.setDuration(Toast.LENGTH_LONG);
        } else {
            //4000ms
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.setText(content);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            hook(mToast);
        }

        mToast.show();
    }

    public static void popCommonToast(Context context, String content, boolean isShowLong) {
        Toast toast = null;
        if (isShowLong) {
            //7000ms
            toast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        } else {
            //4000ms
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            hook(toast);
        }

        toast.show();
    }


    //---------------------------Toast----------------------

    //????????????????????????????????????
    private static final String TAG = "zbv";

    //--------------------------DIMENSION---------------------

    public static int px2dp(Context context, int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int dp2px(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static int sp2px(Context context, int sp) {
        float scaleDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scaleDensity + 0.5f);
    }
    //--------------------------DIMENSION---------------------

    //------------------------LOGCAT----------------------------
    public static boolean openLog = true;

    /**
     * ???????????????Exception?????????????????????????????????????????????????????????log??????
     */
    public static void logE(String content, Exception e) {
        if (openLog) {
            if (e == null) {
                Log.e(TAG, content);
            } else {
                Log.e(TAG, content, e);
            }
        }
    }

    public static void logD(String content) {
        if (openLog) {
            Log.d(TAG, content);
        }
    }


    public static void logD(String content, Exception e) {
        if (openLog) {
            Log.d(TAG, content);
            if (e == null) {
                Log.i(TAG, content);
            } else {
                Log.i(TAG, content, e);
            }
        }
    }

    public static void logDFromBytes(byte[] bytes, String tips) {
        if (openLog) {
            //??????bytes
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                //??????byte:00000000~11111111
                String single = Integer.toHexString(bytes[i] & 0xFF);
                if (single.length() == 1) {
                    sb.append("0").append(single);
                } else {
                    sb.append(single);
                }
                if (i != (bytes.length - 1))
                    sb.append(" ");
            }
            logD(tips + "=" + sb.toString());
        }
    }
    //------------------------LOGCAT----------------------------

//	//------------------------???????????????????????????------------------------
//	//???????????????
//	public static void showKeyboard(){
//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.showSoftInput(editText, 0);
//    }
//
//	//???????????????
//	public static void closeKeyboard(Activity activity) {
//        View view =activity.getWindow().peekDecorView();
//        if (view != null) {
//            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }
//	//------------------------???????????????????????????------------------------

    //------------------------SIGN-------------------------
    public static String getSignInfoFromSelf(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);

        logD("local package name=" + context.getPackageName());

        for (PackageInfo packageInfo : packageInfos) {
            if (packageInfo.packageName.equals(context.getPackageName())) {
                return packageInfo.signatures[0].toCharsString();
            }
        }
        return null;
    }

    public static String getSpecifyPackageName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return packageInfo.signatures[0].toCharsString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * ?????????type???????????????????????????MD5???SHA1???SHA256
     * */
    public static String getSignMessageDigestValue(byte[] digest, String type) {
        StringBuffer sb = new StringBuffer();
        try {
            //MD5???SHA1???SHA256
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = messageDigest.digest(digest);

            logDFromBytes(md5Bytes, "md5????????????");

            //?????????????????????????????????????????????
            for (int i = 0; i < md5Bytes.length; i++) {
                String string = Integer.toHexString(0xFF & md5Bytes[i]);
                if (string.length() == 1) {
                    sb.append("0").append(string);
                } else {
                    sb.append(string);
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * ??????????????????md5??????
     */
    public static String generateCodeFromMD5(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        StringBuffer buffer = new StringBuffer();
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(url.getBytes());
            byte[] cipher = digest.digest();
            for (byte b : cipher) {
                String hexStr = Integer.toHexString(b & 0xff);
                buffer.append(hexStr.length() == 1 ? "0" + hexStr : hexStr);
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }

    //------------------------SIGN-------------------------

    //------------------------FILE-------------------------
    /*
     * ?????????????????????????????????
     * ??????Path????????????????????????????????????????????????
     * */
    public static boolean deleteFileOrDirectory(String path) {
        if (path == null) {
            return false;
        }

        boolean isDelSuccess = false;
        File file = new File(path);
        if (file.exists()) {
            logD("??????Path??????=" + path);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFileOrDirectory(files[i].getAbsolutePath());
                }
            } else {
                isDelSuccess = file.delete();
            }
        } else {
            logD("?????????Path?????????");
        }

        return isDelSuccess;
    }

    /*
     *??????path???????????????????????????????????????
     * ??????????????????delete?????????????????????????????????
     * ??????????????????????????????????????????????????????path????????????????????????????????????
     *
     * ????????????????????????????????????while????????????Path?????????????????????????????????????????????
     * ????????????/data/data/xxx??????????????????????????????????????????????????????????????????/data/data/xxx????????????
     * ?????????????????????????????????0?????????????????????????????????????????????
     * */
    public static void deleteAllFileOrDirectory(String path) {
        File file = new File(path);
        if (file.exists()) {
            logD("??????Path??????=" + path);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                logD("filesLength=" + files.length + ";files==null=" + (files == null));
                //???????????????????????????
                if (files.length == 0) {
                    String delPath = file.getAbsolutePath();
                    String[] strs = delPath.split("/");
                    file.delete();
                    //???????????????????????????path???
                    String subString = delPath.substring(0, delPath.lastIndexOf("/") + 1);
                    logD("subString=" + subString);
                    //????????????
                    deleteAllFileOrDirectory(subString);
                }
                //?????????????????????
                for (int i = 0; i < files.length; i++) {
                    deleteAllFileOrDirectory(files[i].getAbsolutePath());
                }
            } else {
                //??????????????????
                String delPath = file.getAbsolutePath();
                String[] strs = delPath.split("/");
                file.delete();
                String subString = delPath.substring(0, delPath.lastIndexOf("/") + 1);
                logD("subString=" + subString);
                deleteAllFileOrDirectory(subString);
            }
        } else {
            logD("?????????Path?????????");
        }
    }

    /**
     * ???????????? dirctory??????txtName???Null?????????????????????????????????????????????????????????
     * <br/><br/>
     * ???????????????<br/>
     * android.permission.WRITE_EXTERNAL_STORAGE
     * android.permission.READ_EXTERNAL_STORAGE
     * android.permission.MOUNT_UNMOUNT_FILESYSTEMS(??????????????????????????????--SDCard)
     */
    public static String createSDCardDirectory(String directory, String txtName) {
        String desPath = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            logD("SDCard????????? path=" + Environment.getExternalStorageDirectory());
            String base_path = "/sdcard/" + directory;
            File file = new File(base_path);

            if (!file.exists()) {
                logD("???????????????");
                boolean success = false;
                if (directory != null && !directory.equals("")) {
                    success = file.mkdirs();
                }

                logD("sdcard---???????????????????????????=" + success);

                if (success) {

                    if (txtName != null && !txtName.equals("")) {
                        desPath = base_path + "/" + txtName;
                    } else {
                        desPath = base_path + "/";
                    }

                } else {
                    if (txtName != null && !txtName.equals("")) {
                        desPath = "/sdcard/" + txtName;
                    } else {
                        desPath = "/sdcard/";
                    }

                }
            } else {
                logD("?????????");
                if (txtName != null && !txtName.equals("")) {
                    desPath = base_path + "/" + txtName;
                } else {
                    desPath = base_path + "/";
                }
            }
        } else {
            logD("SDCard????????????");
        }
        return desPath;
    }

    /***
     * ?????????directory?????????????????????????????????
     * <br/><br/>
     * ?????????context???????????????????????????????????????
     * <br/><br/>
     * ???????????? dirctory??????txtName???Null?????????????????????????????????????????????????????????
     */
    public static String createLocalDirectory(Context context, String directory, String txtName) {
        String desPath = null;
        //?????????????????????zbv???????????????
        String base_path = "/data/data/" + context.getPackageName() + "/" + directory;
        File file = new File(base_path);
        if (!file.exists()) {
            logD("???????????????");

            boolean success = false;
            if (directory != null && !directory.equals("")) {
                success = file.mkdirs();
            }

            logD("local---???????????????????????????=" + success);

            if (success) {

                if (txtName != null && !txtName.equals("")) {
                    desPath = base_path + "/" + txtName;
                } else {
                    desPath = base_path + "/";
                }

            } else {
                if (txtName != null && !txtName.equals("")) {
                    desPath = "/data/data/" + context.getPackageName() + "/" + txtName;
                } else {
                    desPath = "/data/data/" + context.getPackageName() + "/";
                }
            }
        } else {
            logD("?????????");
            if (txtName != null && !txtName.equals("")) {
                desPath = base_path + "/" + txtName;
            } else {
                desPath = base_path + "/";
            }
        }

        return desPath;
    }

    /**
     * assetsPath?????????Assets??????????????????????????????????????????desPath
     * <br/><br/>
     * ??????desPath????????????????????????(local??????sdcard)
     * <br/><br/>
     * ?????????????????????Assets????????????????????????test_file.ecwx??????<br/><br/>
     * QZXTools.writeDataToPath(this,"/data/data/"+getPackageName()+"/test_file.ecwx","test_file.ecwx");
     * <br/><br/>
     * ???????????????desPath?????????????????????????????????????????????????????????????????????
     */
    public static void writeAssetsDataToPath(Context context, String desPath, String assetsPath) {
        logD("desPath=" + desPath);
        try {
            File file = new File(desPath);
            if (file == null || !file.exists()) {
                boolean isSuccess = file.createNewFile();
                if (!isSuccess) {
                    return;
                }

                logD("???????????????????????????????????????" + "   isSuccess=" + isSuccess);
            }

            InputStream is = context.getResources().getAssets().open(assetsPath);

            if (is == null) {
                logD("??????Assets?????????");
                return;
            }

            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            fos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //??????Path?????????????????????????????????
    public static String readDataFromPath(String desPath) {
        String text = null;
        File file = new File(desPath);
        if (file == null || !file.exists()) {
            logD("?????????????????????????????????????????????");
        } else {
            try {
                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = fis.read(bytes)) != -1) {
                    baos.write(bytes, 0, len);
                }
                text = baos.toString();
                logD("???????????????????????????=" + text);
                baos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return text;
    }

    //????????????????????????path???????????????
    public static void writeStringToPath(String content, String path) {
        logD("path=" + path + ";content=" + content);
        try {
            File file = new File(path);
            if (file == null || !file.exists()) {
                boolean isSuccess = file.createNewFile();
                if (!isSuccess) {
                    return;
                }

                logD("???????????????????????????????????????" + "   isSuccess=" + isSuccess);
            }

            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);

            //????????????writeUTF(content)????????????
            dos.write(content.getBytes("utf-8"));

            dos.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //------------------------FILE-------------------------

    //---------???????????????????????????-??????????????????APK???????????????????????????----------------------
    ///data/user/0/com.ahtelit.zbv.myapplication/cache
    public static String getInternalStorageForCache(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    //??????SDCARD??????????????? /storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/cache
    public static String getExternalStorageForCache(Context context) {
        return context.getExternalCacheDir().getAbsolutePath();
    }

    ///data/user/0/com.ahtelit.zbv.myapplication/files
    public static String getInternalStorageForFiles(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    /*
    type??????????????????????????? /storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files
    type=Environment.DIRECTORY_PICTURES???
    ===??? /storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files/Pictures
     */
    public static String getExternalStorageForFiles(Context context, String type) {
        return context.getExternalFilesDir(type).getAbsolutePath();
    }
    //-----------------------???????????????????????????----------------------

    //-----------------------????????????????????????---------------------------
    public static String formatByteUnit(String size) {
        float byteSize = Float.parseFloat(size);
        //2M??????
        if (byteSize < 1024) {
            //??????
            return String.format("%.2f", byteSize) + "B";
        } else if (byteSize < (1024 * 1024)) {
            return String.format("%.2f", byteSize / 1024) + "K";
        } else {
            return String.format("%.2f", byteSize / 1024 / 1024) + "M";
        }

    }

    /**
     * ????????????
     */
    public static String transformBytes(long bytes) {
//        Log.e("zbv", "integer max value=" + Integer.MAX_VALUE + ";integer mini value=" + Integer.MIN_VALUE
//                + ";Long max value=" + Long.MAX_VALUE + ";Long min value=" + Long.MIN_VALUE);
        if (bytes > Long.MAX_VALUE || bytes < 0) {
            return "?????????????????????";
        }

        if (bytes < 1024) {
            return bytes + "B";
        } else if (bytes < (1024 * 1024)) {
            if (bytes % 1024 != 0) {
                double result = bytes / 1024.0;
                return String.format("%.1fKB", result);
            } else {
                int result = (int) (bytes / 1024);
                return result + "KB";
            }

        } else if (bytes < (1024 * 1024 * 1024)) {
            if (bytes % (1024 * 1024) != 0) {
                double result = bytes / 1024 / 1024.0;
                return String.format("%.1fMB", result);
            } else {
                int result = (int) (bytes / 1024 / 1024);
                return result + "MB";
            }

        } else {
            if (bytes % (1024 * 1024 * 1024) != 0) {
                double result = bytes / 1024 / 1024 / 1024.0;
                return String.format("%.1fGB", result);
            } else {
                long result = bytes / 1024 / 1024 / 1024;
                return result + "GB";
            }
        }
    }


    //----------------------------NETWORK------------------------

    //----------------------------Time/Date----------------------

    //?????????????????????
    public static String getFormatCurrentDateToSecond() {
        //???-y ???-M ???-d ???-h(1-12) H(0-23) ???-m ???-s ??????-S
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        return date;
    }

    /**
     * ??????????????????????????????????????????yyyy-MM-dd hh:mm:ss????????????????????????
     */
    public static String getFormatDateFromTimeStamp(String timeStamp) {
        try {
            //???ms???????????????Date
            Date date = DateFormat.getDateInstance().parse(timeStamp);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            logE("e=" + e, null);
        }
        return "";
    }

    /**
     * 12:30:12
     */
    public static String getFormatTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * ??????Date?????????Long
     *
     * @param content ????????????????????????
     * @param format  ???????????????
     */
    public static long getDateValue(String content, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(content);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            logE("e=" + e, null);
        }
        return -1;
    }

    /**
     * ?????????????????????????????????????????????????????????---???1~7
     */
    public static int judgeWeekFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == 1) {
            week = 7;
        } else {
            week--;
        }
        return week;
    }


    /**
     * ??????????????????????????????
     *
     * @param month 1~12
     */
    public static int calculate(int year, int month) {
        boolean yearleap = judge(year);
        int day;
        if (yearleap && month == 2) {
            day = 29;
        } else if (!yearleap && month == 2) {
            day = 28;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            day = 30;
        } else {
            day = 31;
        }
        return day;
    }

    /**
     * ?????????????????????
     */
    private static boolean judge(int year) {
        boolean yearleap = (year % 400 == 0) || (year % 4 == 0)
                && (year % 100 != 0);// ?????????????????????????????????????????????
        return yearleap;
    }

    /**
     * ????????????
     * 00:00:07
     * ????????????????????????????????????
     * {@link #getFormatTime}
     */
    public static String getTransmitTime(long count) {
        if (count > Long.MAX_VALUE || count < 0) {
            return "?????????Count??????";
        }

        if (count < 60) {
            //???
            String result;
            if (count < 10) {
                result = "00:0" + count;
            } else {
                result = "00:" + count;
            }
            return result;
        } else if (count < 60 * 60) {
            //??????
            long minute = count / (60);
            String minuteResult;
            if (minute < 10) {
                minuteResult = "0" + minute;
            } else {
                minuteResult = minute + "";
            }
            long second = count % (60);
            String secondResult;
            if (second < 10) {
                secondResult = "0" + second;
            } else {
                secondResult = second + "";
            }
            return minuteResult + ":" + secondResult;
        } else {
            long hour = count / (60 * 60);
            String hourResult;
            if (hour < 10) {
                hourResult = "0" + hour;
            } else {
                hourResult = hour + "";
            }

            long minute = count % (60 * 60) / (60);
            String minuteResult;
            if (minute < 10) {
                minuteResult = "0" + minute;
            } else {
                minuteResult = minute + "";
            }

            long second = count % (60 * 60) % (60);
            String secondResult;
            if (second < 10) {
                secondResult = "0" + second;
            } else {
                secondResult = second + "";
            }

            return hourResult + ":" + minuteResult + ":" + secondResult;
        }
    }


    //-------------------------????????????Properties---------------------
    public static Properties getConfigProperties(String path) {
        Properties properties = new Properties();
//        InputStream inputStream = QZXTools.class.getResourceAsStream(path);//????????????assets???????????????
        try {
            FileInputStream fis = new FileInputStream(path);
            //????????????
            properties.load(new InputStreamReader(fis, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void addDataToProperties(String packageName, String fileName, String
            key, String value) {
        String path = "/data/data/" + packageName + "/" + fileName;
        Properties properties = new Properties();

        properties.setProperty(key, value);

        try {
            FileOutputStream fos = new FileOutputStream(path);
            properties.store(new OutputStreamWriter(fos, "UTF-8"),
                    "??????key=" + key + ";value=" + value + "?????????");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //-------------------------Properties---------------------

    //-----------------------????????????????????????????????????------------------------

    /**
     * ???Uri?????????path
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{
                    MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * MediaStore.Images.Media.insertImage(InteractiveActivity.this.getContentResolver(), bitmap, "", "");
     * InteractiveActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
     * Uri.parse("file://" + destFile.getAbsolutePath())));
     * <p>
     * ???????????????????????????Pictures??????
     */
    public static void savePictureToSystemDCIM(Context context, File fileImage, String
            nameImage) {
        // ????????????????????????????????????
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    fileImage.getAbsolutePath(), nameImage, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // ????????????????????????
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(fileImage);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    //??????????????????---Camera??????SD?????????--->??????6.0/7.0??????
    public static final int CODE_TAKE_PHOTO = 1;//??????RequestCode

    public static void accessSystemCamera(Activity activity, Uri photoUri) {
        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //?????????????????????????????????????????????????????????onActivityResult????????????Intent??????
        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        activity.startActivityForResult(takeIntent, CODE_TAKE_PHOTO);
    }


    //??????????????????
    public static final int CODE_SELECT_IMAGE = 2;//??????RequestCode

    public static void accessSystemAlbum(Activity activity) {
        Intent albumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        albumIntent.setType("image/*");
        activity.startActivityForResult(albumIntent, CODE_SELECT_IMAGE);
    }

    public static final int CROP_REQUEST_CODE = 3;//??????RequestCode

    public static void cropPhoto(Uri uri, Activity activity) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    //????????????????????????
    public static void galleryAddPic(Activity activity, Uri contentUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }


    /**
     * ??????????????????
     */
    public static Bitmap compressBitmap(Bitmap image, float userWidth, float userHeight) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//????????????????????????1M,????????????????????????????????????BitmapFactory.decodeStream????????????
            baos.reset();//??????baos?????????baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//????????????50%?????????????????????????????????baos???
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //??????????????????????????????options.inJustDecodeBounds ??????true???
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        float hh = userWidth;//?????????????????????100f
        float ww = userHeight;//?????????????????????100f
        int be = 1;//be=1???????????????
        if (w > h && w > ww) {//???????????????????????????????????????????????????
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//???????????????????????????????????????????????????
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;

        logD("w=" + w + ";h=" + h + ";be=" + be);

        newOpts.inSampleSize = be;//??????????????????
        //??????????????????????????????????????????options.inJustDecodeBounds ??????false???
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//?????????????????????????????????????????????
    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//???????????????????????????100????????????????????????????????????????????????baos???
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {    //?????????????????????????????????????????????100kb,??????????????????
            baos.reset();//??????baos?????????baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//????????????options%?????????????????????????????????baos???
            options -= 10;//???????????????10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//?????????????????????baos?????????ByteArrayInputStream???
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//???ByteArrayInputStream??????????????????
        return bitmap;
    }

    /**
     * ??????Matrix
     *
     * @param bitmap ?????????Bitmap
     * @param width  ????????????
     * @param height ????????????
     * @return ????????????Bitmap
     */
    public static Bitmap scaleMatrix(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float scaleW = width * 1.0f / w;
        float scaleH = height * 1.0f / h;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH); // ??????????????????????????????
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * ??????Canvas
     *
     * @param bitmap ?????????Bitmap
     * @param rect   Bitmap??????????????????Rect
     * @return ????????????Bitmap
     */
    public static Bitmap scaleCanvas(Bitmap bitmap, Rect rect) {
        Bitmap newBitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);//?????????????????????????????????Bitmap
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        Bitmap temp = bitmap;

        //????????????bitmap???????????????
        PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setFilterBitmap(true); //???Bitmap??????????????????
        paint.setAntiAlias(true);//???????????????
        canvas.setDrawFilter(pfd);
        canvas.drawBitmap(temp, null, rect, paint);

        return newBitmap;
    }

    //-----------------------????????????????????????????????????------------------------

    //------------------------------???????????????????????????

    /**
     * ???????????????????????????
     * <p>
     * ?????????Android 7.0??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ???????????? file://xxx ???????????????URI????????????????????????????????????????????????????????????????????????
     * ???????????? FileUriExposedException ???????????????????????????????????????????????FileProvider???
     * ???????????????content://xxx ?????????URI??????????????? URI ??????????????????
     *
     * @param file
     * @param context
     */
    public static void openFile(File file, Context context) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // ??????intent???Action??????
            intent.setAction(Intent.ACTION_VIEW);
            // ????????????file???MIME??????
            String type = getMIMEType(file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        "com.telit.smartclass.desktop.fileprovider",
                        file);//file??????????????????????????????file
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//???????????????????????????
                intent.setDataAndType(photoURI, type);
            } else {
                // ??????intent???data???Type?????????
                intent.setDataAndType(Uri.fromFile(file), type);
            }
            // ??????
            context.startActivity(intent);
            Intent.createChooser(intent, "??????????????????????????????????????????");
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????????????????????????????MIME?????????
     *
     * @param file
     */
    public static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        // ??????????????????????????????"."???fName???????????????
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* ???????????????????????? */
        String end = fName.substring(dotIndex + 1, fName.length())
                .toLowerCase();
        if (end == "")
            return type;
        // ???MIME?????????????????????????????????????????????MIME?????????
        for (int i = 0; i < MIME_Table.length; i++) {
            if (end.equals(MIME_Table[i][0]))
                type = MIME_Table[i][1];
        }
        return type;
    }

    /**
     * ??????MIME??????(????????????????????????????????????????????????????????????????????????MIME??????)
     */
    private static final String[][] MIME_Table = {
            // {????????????MIME??????}
            {"aab", "application/x-authoware-bin"},
            {"aam", "application/x-authoware-map"},
            {"aas", "application/x-authoware-seg"},
            {"amc", "application/x-mpeg"},
            {"ani", "application/octet-stream"},
            {"apk", "application/vnd.android.package-archive"},
            {"asd", "application/astound"}, {"asn", "application/astound"},
            {"asp", "application/x-asap"},
            {"ai", "application/postscript"},
            {"avb", "application/octet-stream"},
            {"bcpio", "application/x-bcpio"},
            {"bin", "application/octet-stream"},
            {"bld", "application/bld"}, {"bld2", "application/bld2"},
            {"aif", "audio/x-aiff"}, {"aifc", "audio/x-aiff"},
            {"aiff", "audio/x-aiff"}, {"als", "audio/X-Alpha5"},
            {"au", "audio/basic"}, {"awb", "audio/amr-wb"},
            {"3gp", "video/3gpp"}, {"asf", "video/x-ms-asf"},
            {"asx", "video/x-ms-asf"}, {"avi", "video/x-msvideo"},
            {"asc", "text/plain"}, {"bmp", "image/bmp"},
            {"bpk", "application/octet-stream"},
            {"bz2", "application/x-bzip2"}, {"c", "text/x-csrc"},
            {"cpp", "text/x-c++src"}, {"cal", "image/x-cals"},
            {"ccn", "application/x-cnc"}, {"cco", "application/x-cocoa"},
            {"cdf", "application/x-netcdf"},
            {"cgi", "magnus-internal/cgi"}, {"chat", "application/x-chat"},
            {"class", "application/octet-stream"},
            {"clp", "application/x-msclip"}, {"cmx", "application/x-cmx"},
            {"co", "application/x-cult3d-object"},
            {"cod", "image/cis-cod"}, {"csh", "application/x-csh"},
            {"csm", "chemical/x-csml"}, {"csml", "chemical/x-csml"},
            {"css", "text/css"}, {"dcm", "x-lml/x-evm"},
            {"cpio", "application/x-cpio"},
            {"cpt", "application/mac-compactpro"},
            {"crd", "application/x-mscardfile"},
            {"cur", "application/octet-stream"},
            {"dcr", "application/x-director"},
            {"dir", "application/x-director"},
            {"dll", "application/octet-stream"},
            {"dmg", "application/octet-stream"},
            {"dms", "application/octet-stream"},
            {"doc", "application/msword"}, {"dot", "application/x-dot"},
            {"dvi", "application/x-dvi"}, {"dwg", "application/x-autocad"},
            {"dxf", "application/x-autocad"},
            {"dxr", "application/x-director"},
            {"ebk", "application/x-expandedbook"},
            {"etc", "application/x-earthtime"}, {"dcx", "image/x-dcx"},
            {"dhtml", "text/html"}, {"dwf", "drawing/x-dwf"},
            {"emb", "chemical/x-embl-dl-nucleotide"},
            {"embl", "chemical/x-embl-dl-nucleotide"},
            {"eps", "application/postscript"}, {"eri", "image/x-eri"},
            {"es", "audio/echospeech"}, {"esl", "audio/echospeech"},
            {"etx", "text/x-setext"}, {"evm", "x-lml/x-evm"},
            {"evy", "application/x-envoy"},
            {"exe", "application/octet-stream"},
            {"fh4", "image/x-freehand"}, {"fh5", "image/x-freehand"},
            {"fhc", "image/x-freehand"}, {"fif", "image/fif"},
            {"fm", "application/x-maker"}, {"fpx", "image/x-fpx"},
            {"fvi", "video/isivideo"},
            {"gau", "chemical/x-gaussian-input"},
            {"gca", "application/x-gca-compressed"},
            {"gdb", "x-lml/x-gdb"}, {"gif", "image/gif"},
            {"gps", "application/x-gps"}, {"gtar", "application/x-gtar"},
            {"gz", "application/x-gzip"}, {"h", "text/x-chdr"},
            {"hdf", "application/x-hdf"}, {"hdm", "text/x-hdml"},
            {"hdml", "text/x-hdml"}, {"hlp", "application/winhlp"},
            {"hqx", "application/mac-binhex40"}, {"htm", "text/html"},
            {"html", "text/html"}, {"hts", "text/html"},
            {"ice", "x-conference/x-cooltalk"},
            {"ico", "application/octet-stream"}, {"ief", "image/ief"},
            {"ifm", "image/gif"}, {"ifs", "image/ifs"},
            {"imy", "audio/melody"}, {"ins", "application/x-NET-Install"},
            {"ips", "application/x-ipscript"},
            {"ipx", "application/x-ipix"}, {"it", "audio/x-mod"},
            {"itz", "audio/x-mod"}, {"ivr", "i-world/i-vrml"},
            {"j2k", "image/j2k"},
            {"jad", "text/vnd.sun.j2me.app-descriptor"},
            {"jam", "application/x-jam"}, {"java", "application/x-java"},
            {"jar", "application/java-archive"},
            {"jnlp", "application/x-java-jnlp-file"},
            {"jpe", "image/jpeg"}, {"jpeg", "image/jpeg"},
            {"jpg", "image/jpeg"}, {"jpz", "image/jpeg"},
            {"js", "application/x-javascript"}, {"jwc", "application/jwc"},
            {"kjx", "application/x-kjx"}, {"lak", "x-lml/x-lak"},
            {"latex", "application/x-latex"},
            {"lcc", "application/fastman"},
            {"lcl", "application/x-digitalloca"},
            {"lcr", "application/x-digitalloca"},
            {"lgh", "application/lgh"},
            {"lha", "application/octet-stream"}, {"lml", "x-lml/x-lml"},
            {"lmlpack", "x-lml/x-lmlpack"}, {"lsf", "video/x-ms-asf"},
            {"lsx", "video/x-ms-asf"}, {"lzh", "application/x-lzh"},
            {"m13", "application/x-msmediaview"},
            {"m14", "application/x-msmediaview"}, {"m15", "audio/x-mod"},
            {"m3u", "audio/x-mpegurl"}, {"m3url", "audio/x-mpegurl"},
            {"ma1", "audio/ma1"}, {"ma2", "audio/ma2"},
            {"ma3", "audio/ma3"}, {"ma5", "audio/ma5"},
            {"man", "application/x-troff-man"},
            {"map", "magnus-internal/imagemap"},
            {"mbd", "application/mbedlet"},
            {"mct", "application/x-mascot"},
            {"mdb", "application/x-msaccess"}, {"mdz", "audio/x-mod"},
            {"me", "application/x-troff-me"}, {"mel", "text/x-vmel"},
            {"mi", "application/x-mif"}, {"mid", "audio/midi"},
            {"midi", "audio/midi"}, {"mif", "application/x-mif"},
            {"mil", "image/x-cals"}, {"mio", "audio/x-mio"},
            {"mmf", "application/x-skt-lbs"}, {"mng", "video/x-mng"},
            {"mny", "application/x-msmoney"},
            {"moc", "application/x-mocha"},
            {"mocha", "application/x-mocha"}, {"mod", "audio/x-mod"},
            {"mof", "application/x-yumekara"},
            {"mol", "chemical/x-mdl-molfile"},
            {"mop", "chemical/x-mopac-input"}, {"mov", "video/quicktime"},
            {"movie", "video/x-sgi-movie"}, {"mp2", "audio/x-mpeg"},
            {"mp3", "audio/x-mpeg"}, {"mp4", "video/mp4"},
            {"mpc", "application/vnd.mpohun.certificate"},
            {"mpe", "video/mpeg"}, {"mpeg", "video/mpeg"},
            {"mpg", "video/mpeg"}, {"mpg4", "video/mp4"},
            {"mpga", "audio/mpeg"},
            {"mpn", "application/vnd.mophun.application"},
            {"mpp", "application/vnd.ms-project"},
            {"mps", "application/x-mapserver"}, {"mrl", "text/x-mrml"},
            {"mrm", "application/x-mrm"}, {"ms", "application/x-troff-ms"},
            {"mts", "application/metastream"},
            {"mtx", "application/metastream"},
            {"mtz", "application/metastream"},
            {"mzv", "application/metastream"}, {"nar", "application/zip"},
            {"nbmp", "image/nbmp"}, {"nc", "application/x-netcdf"},
            {"ndb", "x-lml/x-ndb"}, {"ndwn", "application/ndwn"},
            {"nif", "application/x-nif"}, {"nmz", "application/x-scream"},
            {"nokia-op-logo", "image/vnd.nok-oplogo-color"},
            {"npx", "application/x-netfpx"}, {"nsnd", "audio/nsnd"},
            {"nva", "application/x-neva1"}, {"oda", "application/oda"},
            {"oom", "application/x-AtlasMate-Plugin"},
            {"pac", "audio/x-pac"}, {"pae", "audio/x-epac"},
            {"pan", "application/x-pan"},
            {"pbm", "image/x-portable-bitmap"}, {"pcx", "image/x-pcx"},
            {"pda", "image/x-pda"}, {"pdb", "chemical/x-pdb"},
            {"pdf", "application/pdf"}, {"pfr", "application/font-tdpfr"},
            {"pgm", "image/x-portable-graymap"}, {"pict", "image/x-pict"},
            {"pm", "application/x-perl"}, {"pmd", "application/x-pmd"},
            {"png", "image/png"}, {"pnm", "image/x-portable-anymap"},
            {"pnz", "image/png"}, {"pot", "application/vnd.ms-powerpoint"},
            {"ppm", "image/x-portable-pixmap"},
            {"pps", "application/vnd.ms-powerpoint"},
            {"ppt", "application/vnd.ms-powerpoint"},
            {"pqf", "application/x-cprplayer"},
            {"pqi", "application/cprplayer"}, {"prc", "application/x-prc"},
            {"proxy", "application/x-ns-proxy-autoconfig"},
            {"ps", "application/postscript"},
            {"ptlk", "application/listenup"},
            {"pub", "application/x-mspublisher"},
            {"pvx", "video/x-pv-pvx"}, {"qcp", "audio/vnd.qcelp"},
            {"qt", "video/quicktime"}, {"qti", "image/x-quicktime"},
            {"qtif", "image/x-quicktime"},
            {"r3t", "text/vnd.rn-realtext3d"},
            {"ra", "audio/x-pn-realaudio"},
            {"ram", "audio/x-pn-realaudio"},
            {"rar", "application/x-rar-compressed"},
            {"ras", "image/x-cmu-raster"}, {"rdf", "application/rdf+xml"},
            {"rf", "image/vnd.rn-realflash"}, {"rgb", "image/x-rgb"},
            {"rlf", "application/x-richlink"},
            {"rm", "audio/x-pn-realaudio"}, {"rmf", "audio/x-rmf"},
            {"rmm", "audio/x-pn-realaudio"},
            {"rmvb", "audio/x-pn-realaudio"},
            {"rnx", "application/vnd.rn-realplayer"},
            {"roff", "application/x-troff"},
            {"rp", "image/vnd.rn-realpix"},
            {"rpm", "audio/x-pn-realaudio-plugin"},
            {"rt", "text/vnd.rn-realtext"}, {"rte", "x-lml/x-gps"},
            {"rtf", "application/rtf"}, {"rtg", "application/metastream"},
            {"rtx", "text/richtext"}, {"rv", "video/vnd.rn-realvideo"},
            {"rwc", "application/x-rogerwilco"}, {"s3m", "audio/x-mod"},
            {"s3z", "audio/x-mod"}, {"sca", "application/x-supercard"},
            {"scd", "application/x-msschedule"},
            {"sdf", "application/e-score"},
            {"sea", "application/x-stuffit"}, {"sgm", "text/x-sgml"},
            {"sgml", "text/x-sgml"}, {"sh", "application/x-sh"},
            {"shar", "application/x-shar"},
            {"shtml", "magnus-internal/parsed-html"},
            {"shw", "application/presentations"}, {"si6", "image/si6"},
            {"si7", "image/vnd.stiwap.sis"},
            {"si9", "image/vnd.lgtwap.sis"},
            {"sis", "application/vnd.symbian.install"},
            {"sit", "application/x-stuffit"},
            {"skd", "application/x-Koan"}, {"skm", "application/x-Koan"},
            {"skp", "application/x-Koan"}, {"skt", "application/x-Koan"},
            {"slc", "application/x-salsa"}, {"smd", "audio/x-smd"},
            {"smi", "application/smil"}, {"smil", "application/smil"},
            {"smp", "application/studiom"}, {"smz", "audio/x-smd"},
            {"snd", "audio/basic"}, {"spc", "text/x-speech"},
            {"spl", "application/futuresplash"},
            {"spr", "application/x-sprite"},
            {"sprite", "application/x-sprite"},
            {"spt", "application/x-spt"},
            {"src", "application/x-wais-source"},
            {"stk", "application/hyperstudio"}, {"stm", "audio/x-mod"},
            {"sv4cpio", "application/x-sv4cpio"},
            {"sv4crc", "application/x-sv4crc"}, {"svf", "image/vnd"},
            {"svg", "image/svg-xml"}, {"svh", "image/svh"},
            {"svr", "x-world/x-svr"},
            {"swf", "application/x-shockwave-flash"},
            {"swfl", "application/x-shockwave-flash"},
            {"t", "application/x-troff"},
            {"tad", "application/octet-stream"}, {"talk", "text/x-speech"},
            {"tar", "application/x-tar"}, {"taz", "application/x-tar"},
            {"tbp", "application/x-timbuktu"},
            {"tbt", "application/x-timbuktu"},
            {"tcl", "application/x-tcl"}, {"tex", "application/x-tex"},
            {"texi", "application/x-texinfo"},
            {"texinfo", "application/x-texinfo"},
            {"tgz", "application/x-tar"},
            {"thm", "application/vnd.eri.thm"}, {"tif", "image/tiff"},
            {"tiff", "image/tiff"}, {"tki", "application/x-tkined"},
            {"tkined", "application/x-tkined"}, {"toc", "application/toc"},
            {"toy", "image/toy"}, {"tr", "application/x-troff"},
            {"trk", "x-lml/x-gps"}, {"trm", "application/x-msterminal"},
            {"tsi", "audio/tsplayer"}, {"tsp", "application/dsptype"},
            {"tsv", "text/tab-separated-values"},
            {"tsv", "text/tab-separated-values"},
            {"ttf", "application/octet-stream"},
            {"ttz", "application/t-time"}, {"txt", "text/plain"},
            {"ult", "audio/x-mod"}, {"ustar", "application/x-ustar"},
            {"uu", "application/x-uuencode"},
            {"uue", "application/x-uuencode"},
            {"vcd", "application/x-cdlink"}, {"vcf", "text/x-vcard"},
            {"vdo", "video/vdo"}, {"vib", "audio/vib"},
            {"viv", "video/vivo"}, {"vivo", "video/vivo"},
            {"vmd", "application/vocaltec-media-desc"},
            {"vmf", "application/vocaltec-media-file"},
            {"vmi", "application/x-dreamcast-vms-info"},
            {"vms", "application/x-dreamcast-vms"},
            {"vox", "audio/voxware"}, {"vqe", "audio/x-twinvq-plugin"},
            {"vqf", "audio/x-twinvq"}, {"vql", "audio/x-twinvq"},
            {"vre", "x-world/x-vream"}, {"vrml", "x-world/x-vrml"},
            {"vrt", "x-world/x-vrt"}, {"vrw", "x-world/x-vream"},
            {"vts", "workbook/formulaone"}, {"wav", "audio/x-wav"},
            {"wax", "audio/x-ms-wax"}, {"wbmp", "image/vnd.wap.wbmp"},
            {"web", "application/vnd.xara"}, {"wi", "image/wavelet"},
            {"wis", "application/x-InstallShield"},
            {"wm", "video/x-ms-wm"}, {"wma", "audio/x-ms-wma"},
            {"wmd", "application/x-ms-wmd"},
            {"wmf", "application/x-msmetafile"},
            {"wml", "text/vnd.wap.wml"},
            {"wmlc", "application/vnd.wap.wmlc"},
            {"wmls", "text/vnd.wap.wmlscript"},
            {"wmlsc", "application/vnd.wap.wmlscriptc"},
            {"wmlscript", "text/vnd.wap.wmlscript"},
            {"wmv", "audio/x-ms-wmv"}, {"wmx", "video/x-ms-wmx"},
            {"wmz", "application/x-ms-wmz"}, {"wpng", "image/x-up-wpng"},
            {"wpt", "x-lml/x-gps"}, {"wri", "application/x-mswrite"},
            {"wrl", "x-world/x-vrml"}, {"wrz", "x-world/x-vrml"},
            {"ws", "text/vnd.wap.wmlscript"},
            {"wsc", "application/vnd.wap.wmlscriptc"},
            {"wv", "video/wavelet"}, {"wvx", "video/x-ms-wvx"},
            {"wxl", "application/x-wxl"}, {"x-gzip", "application/x-gzip"},
            {"xar", "application/vnd.xara"}, {"xbm", "image/x-xbitmap"},
            {"xdm", "application/x-xdma"}, {"xdma", "application/x-xdma"},
            {"xdw", "application/vnd.fujixerox.docuworks"},
            {"xht", "application/xhtml+xml"},
            {"xhtm", "application/xhtml+xml"},
            {"xhtml", "application/xhtml+xml"},
            {"xla", "application/vnd.ms-excel"},
            {"xlc", "application/vnd.ms-excel"},
            {"xll", "application/x-excel"},
            {"xlm", "application/vnd.ms-excel"},
            {"xls", "application/vnd.ms-excel"},
            {"xlt", "application/vnd.ms-excel"},
            {"xlw", "application/vnd.ms-excel"}, {"xm", "audio/x-mod"},
            {"xml", "text/xml"}, {"xmz", "audio/x-mod"},
            {"xpi", "application/x-xpinstall"}, {"xpm", "image/x-xpixmap"},
            {"xsit", "text/xml"}, {"xsl", "text/xml"},
            {"xul", "text/xul"}, {"xwd", "image/x-xwindowdump"},
            {"xyz", "chemical/x-pdb"}, {"yz1", "application/x-yz1"},
            {"z", "application/x-compress"},
            {"zac", "application/x-zaurus-zac"},
            {"zip", "application/zip"},};
    //------------------------------???????????????????????????

    //------------------------????????????
    public static Drawable bitmapToDrawable(Bitmap bmp) {
        return new BitmapDrawable(bmp);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        System.out.println("Drawable???Bitmap");
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //????????????????????????????????????????????????View??????SurfaceView??????canvas.drawBitmap???????????????
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }
    //------------------------????????????

    //------------------------little tools

    /**
     * ?????????????????????
     */
    public static int getStatusBarHeight(Context context) {
        int resourcesId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourcesId);
    }

    /**
     * ??????ActionBar??????
     */
    public static int getActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    context.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    /**
     * ????????????????????????????????????????????????????????????
     */
    public static int getNavigationHeight(Context context) {
        //???????????????????????????????????????
        int rid = context.getResources().getIdentifier("config_showNavigationBar",
                "bool", "android");
        //??????rid??????
        int resourceId = context.getResources().getIdentifier("navigation_bar_height",
                "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * ????????????????????????---???????????????
     * api19[??????4.4.2]???????????????????????? api21[??????5.0.1]????????????????????????????????????
     */
    public static void unifyStatusColor(Activity activity, @ColorInt int color,
                                        boolean lightStatusColor) {
        Window window = activity.getWindow();
        //??????5.0???????????????????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //???????????????????????????????????????statusbar?????????
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //????????????????????????????????????????????????---???????????????fitSystemWindow
            ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
            View statusView = new View(activity);
            int statusHeight = getStatusBarHeight(activity);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, statusHeight);
            layoutParams.gravity = Gravity.TOP;
            statusView.setLayoutParams(layoutParams);
            statusView.setBackgroundColor(color);
            decorViewGroup.addView(statusView, layoutParams);
        } else {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = window.getDecorView();
            int flag = decorView.getSystemUiVisibility();
            if (lightStatusColor) {
                flag |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                flag &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(flag);
        }

        /*??????fitSystemWindow="true/false"??????????????????
        ???????????????statusbar??????????????????????????????????????????????????????View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN flag)??????
            fitsSystemWindows????????????????????????statusbar?????????????????????????????????????????????ContentView?????????????????????
            ?????????HierarchyView ??????????????????????????????ContentView???????????????paddingTop??????????????????

        ??????????????????view???????????????fitsSystemWindows????????????????????????????????????????????????????????????????????????????????????

        ??????CoordinatorLayout???fitsSystemWindows???????????????API 21 ????????????????????????View???
            setOnApplyWindowInsetsListener(OnApplyWindowInsetsListener)???????????????fitsSystemWindows??????????????????
            ???OnApplyWindowInsetsListener???onApplyWindowInsets?????????????????????????????????statusbar????????????
        */

//        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
//        View mChildView = mContentView.getChildAt(0);
//        if (mChildView != null) {
//            //?????????????????? ContentView ??? FitsSystemWindows, ???????????? ContentView ??????????????? View .
//            // ??????????????? View ?????????.
//            mChildView.setFitsSystemWindows(true);
//        }

    }

    /**
     * ???????????????StatusBar??????????????????
     */
    public static void addStatusColor(Activity activity, @ColorInt int color) {
        //???????????????????????????????????????statusbar?????????
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //????????????????????????????????????????????????---???????????????fitSystemWindow
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        View statusView = new View(activity);
        int statusHeight = getStatusBarHeight(activity);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, statusHeight);
        layoutParams.gravity = Gravity.TOP;
        statusView.setLayoutParams(layoutParams);
        statusView.setBackgroundColor(color);
        decorViewGroup.addView(statusView, layoutParams);
    }


    //------------------------little tools


    //------------------------Android

    /**
     * ??????????????????apk?????????
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //??????????????????????????????AndroidManifest.xml???android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * ?????????????????????
     *
     * @param context ?????????
     * @return
     */
    public static String getVerionName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    //??????5.0?????????????????????????????????
    public static boolean judgeAppOnForgound(String packageName, Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {

            int curProcessPid = runningAppProcessInfo.pid;

            Log.e("zbv", "processName=" + runningAppProcessInfo.processName
                    + ";curProcessPid=" + curProcessPid);

            if (runningAppProcessInfo.processName.equals(packageName)) {

                if (runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        || runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND_SERVICE
                        || runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_TOP_SLEEPING) {

                    return true;

                }

            }
        }
        return false;
    }

    /**
     * ?????????????????????????????????
     *
     * @param pid ?????????
     * @return ?????????
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    //------------------------Android Install And UnInstall
    /* ??????apk
     * android???????????????????????????---/system/app/PackageInstaller.apk.??????????????????????????????????????????????????????????????????apk?????????
     */
    public static void installApk(Activity context, String fileName) {
        // /storage/emulated/0/Android/data/com.telit.smartclass.desktop/files/wisdomclass-v3.0.apk

        try {

            QZXTools.logE("installApk fileName=" + fileName, null);
            if (TextUtils.isEmpty(fileName)) return;
            //?????????
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri = FileProvider.getUriForFile(context, context.getPackageName()
                        + ".fileprovider", new File(fileName));
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.parse("file://" + fileName), "application/vnd.android.package-archive");
            }
            context.startActivity(intent);

            //???????????????????????????????????????????????????
            android.os.Process.killProcess(android.os.Process.myPid());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * ????????????
     *
     * @param file
     */
    public static void installApk(Context context, File file) {
        Log.i(TAG, "????????????");

        try {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".fileprovider", file);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            intent.setAction(Intent.ACTION_VIEW);
            context.startActivity(intent);

        } catch (Exception e) {
            Log.i("OkGo ", e.toString());
        }
    }


    /* ??????apk */
    public static void uninstallApk(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }
	
	/*-----------------px???????????????????????????------------------------
    import java.io.File;
    import java.io.FileNotFoundException;
    import java.io.FileOutputStream;
    import java.io.PrintWriter;

    public class MakeXml {

        private final static String rootPath = "C:\\Users\\Administrator\\Desktop\\layoutroot\\values-{0}x{1}\\";

        private final static float dw = 320f;
        private final static float dh = 480f;

        private final static String WTemplate = "<dimen name=\"x{0}\">{1}px</dimen>\n";
        private final static String HTemplate = "<dimen name=\"y{0}\">{1}px</dimen>\n";

        public static void main(String[] args) {
            makeString(320, 480);
            makeString(480,800);
            makeString(480, 854);
            makeString(540, 960);
            makeString(600, 1024);
            makeString(720, 1184);
            makeString(720, 1196);
            makeString(720, 1280);
            makeString(768, 1024);
            makeString(800, 1280);
            makeString(1080, 1812);
            makeString(1080, 1920);
            makeString(1440, 2560);
        }

        public static void makeString(int w, int h) {

            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            sb.append("<resources>");
            float cellw = w / dw;
            for (int i = 1; i < 320; i++) {
                sb.append(WTemplate.replace("{0}", i + "").replace("{1}",
                        change(cellw * i) + ""));
            }
            sb.append(WTemplate.replace("{0}", "320").replace("{1}", w + ""));
            sb.append("</resources>");

            StringBuffer sb2 = new StringBuffer();
            sb2.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            sb2.append("<resources>");
            float cellh = h / dh;
            for (int i = 1; i < 480; i++) {
                sb2.append(HTemplate.replace("{0}", i + "").replace("{1}",
                        change(cellh * i) + ""));
            }
            sb2.append(HTemplate.replace("{0}", "480").replace("{1}", h + ""));
            sb2.append("</resources>");

            String path = rootPath.replace("{0}", h + "").replace("{1}", w + "");
            File rootFile = new File(path);
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }
            File layxFile = new File(path + "lay_x.xml");
            File layyFile = new File(path + "lay_y.xml");
            try {
                PrintWriter pw = new PrintWriter(new FileOutputStream(layxFile));
                pw.print(sb.toString());
                pw.close();
                pw = new PrintWriter(new FileOutputStream(layyFile));
                pw.print(sb2.toString());
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        public static float change(float a) {
            int temp = (int) (a * 100);
            return temp / 100f;
        }
    }
    *-----------------px???????????????????????????------------------------
    */


    public static String getDeviceSN() {
        String serialNumber = Build.SERIAL;
        return serialNumber;
    }

    /* @author suncat
  2  * @category ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
  3  * @return
  4  */
    public static final boolean ping() {

        String result = null;
        try {
            String ip = "www.baidu.com";// ping ???????????????????????????????????????????????????
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping??????3???
            // ??????ping????????????????????????
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping?????????
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }

    //??????Activity????????????
    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
            return true;
        }
        return false;
    }

    /**
     * ?????????????????????????????????????????????????????? Google I/O App for Android
     *
     * @param context
     * @return ???????????? True??????????????? False
     * <p>
     * ????????????????????????????????????6???
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    //??????????????????true???????????????false
    public static boolean isMobile(String mobile) {

        String str = mobile;
        String pattern = "^(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57]|19[89]|166)[0-9]{8}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);

        return m.matches();
    }

    public static String getMD5Str(String str) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest = md5.digest(str.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //16??????????????????16?????????
        String md5Str = new BigInteger(1, digest).toString(16);
        return md5Str;
    }

    //????????????????????????
    public static boolean patternValues(String info) {
        Pattern pattern = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");
        boolean matches = pattern.matcher(info).matches();
        return matches;
    }

    //?????????????????????
    public static void forceInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);

        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    //??????????????????????????? ??????????????????
    /**
     * ??????????????????
     */
    //????????????
    private static List<File> data = new ArrayList<>();

    public static List<File> getFileName(File[] files) {
        data.clear();
        // ???????????????????????????????????????????????????
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getFileName(file.listFiles());
                } else {
                    //????????????
                    String fileName = file.getName();
                    //???????????? ??? file.getPath()
                    if (fileName.endsWith(".docx")
                            || fileName.endsWith(".doc")
                            || fileName.endsWith(".xlsx")
                            || fileName.endsWith(".jpg")
                            || fileName.endsWith(".png")
                            || fileName.endsWith(".jpeg")
                            || fileName.endsWith(".mp4")
                            || fileName.endsWith(".flv")
                            || fileName.endsWith(".wmv")
                            || fileName.endsWith(".ppt")
                            || fileName.endsWith(".pptx")
                            || fileName.endsWith(".xls")
                            || fileName.endsWith(".pdf")) {
                        data.add(file);
                    }
                }
            }
            return data;
        }
        return null;
    }


    //???????????????????????????
    public static boolean isEmpty(CharSequence str) {

        return str == null || str.length() == 0 || str.equals("null");

    }


    /**
     * hide keyboard
     */
    public static void hideKeyboard(Activity context, View et_name) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_name.getWindowToken(), 0); // ??????????????????
    }

    /**
     * ??????????????????
     *
     * @return ????????????
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;

    }

    public static String getDeviceId(Context context) {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        return ANDROID_ID;
    }

}
