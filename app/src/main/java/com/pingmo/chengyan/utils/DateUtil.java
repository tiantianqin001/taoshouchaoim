/***************************************************************************************************
 * 单位：北京红云融通技术有限公司
 * 日期：2016-12-14
 * 版本：1.0.0
 * 版权：All rights reserved.
 **************************************************************************************************/
package com.pingmo.chengyan.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static final String FORMAT_M_D = "M月d日";
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static String getMonthAndDay(String time) {
        return getNewFormatDateString(time, DateUtil.FORMAT_YYYY_MM_DD_HH_MM_SS, DateUtil.FORMAT_M_D);
    }

    public static String getNewFormatDateString(String dateStr, String fromFormat, String toFormat) {
        Date date = formatString2Date(dateStr, fromFormat);
        return formatDate2String(date, toFormat);
    }

    public static Date formatString2Date(String dateStr, String format) {
        if (TextUtils.isEmpty(dateStr)) {
            return null;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

            return sdf.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDate2String(Date date, String format) {
        if (date == null) {
            return "";
        }

        try {
            SimpleDateFormat formatPattern = new SimpleDateFormat(format, Locale.getDefault());
            return formatPattern.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
