package com.tencent.qcloud.tuikit.tuicontact.util;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;


import com.tencent.imsdk.BuildConfig;

import java.io.File;

public class PicUtil {
    public static String getDir(Context context,String folderName) {
        String path = getBaseDir(context) + folderName + "/";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    public static String getBaseDir(Context context) {
        return context.getExternalFilesDir("").getAbsolutePath() + File.separator
                + BuildConfig.APPLICATION_ID + File.separator;

    }

    public static String getPublicDirByType(String type, String folderName) {
        String path = Environment.getExternalStoragePublicDirectory(type).getAbsolutePath()
                + File.separator + BuildConfig.APPLICATION_ID + File.separator + folderName;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    private static Uri getPicPublicDir(Context context, String folderName, String fileName) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM
                + File.separator + BuildConfig.APPLICATION_ID
                + File.separator + folderName);
        return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static Uri getPicUri(Context context, String folderName, String fileName) {
        Uri uri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            String path = getPublicDirByType(Environment.DIRECTORY_DCIM, folderName);
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(path, fileName);
            uri = DocumentsHelper.getImgCaptureUrl(context, file);
        } else {
            uri = getPicPublicDir(context, folderName, fileName);
        }
        return uri;
    }


    public static Point getDeviceSize(Context context) {
        Point deviceSize = new Point(0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            ((WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getSize(deviceSize);
        } else {
            Display display = ((WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay();
            deviceSize.x = display.getWidth();
            deviceSize.y = display.getHeight();
            display = null;
        }
        return deviceSize;
    }

    public static int getImageRotationByPath(Context ctx, String path) {
        int rotation = 0;
        if (TextUtils.isEmpty(path)) {
            return rotation;
        }

        Cursor cursor = null;
        try {
            cursor = ctx.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media.ORIENTATION},
                    MediaStore.Images.Media.DATA + " = ?",
                    new String[]{"" + path}, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                rotation = cursor.getInt(0);
            } else {
                rotation = getImageRotationFromUrl(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return rotation;
    }

    public static int getImageRotationFromUrl(String path) {
        int orientation = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;
                    break;
                default:
                    orientation = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            orientation = 0;
        }
        return orientation;
    }

}
