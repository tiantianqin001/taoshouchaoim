package com.pingmo.chengyan.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.pingmo.chengyan.MyAPP;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 600) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 手机号码检查
     */
    public static boolean phonenumberCheck(String phoneNumber) {

        // 表达式对象
        Pattern p = Pattern.compile("^(1)[0-9]{10}$");

        // 创建 Matcher 对象
        Matcher m = p.matcher(phoneNumber);

        return m.matches();
    }

    public static PackageInfo getPackageInfo() {
        PackageInfo info = new PackageInfo();
        try {
            info = MyAPP.getInstance()
                    .getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(
                            MyAPP.getInstance().getPackageName(),
                            0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }

}
