package com.pingmo.chengyan;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;

import org.jetbrains.annotations.NotNull;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MyAPP extends Application {

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    private static MyAPP myApplication;

    public static MyAPP getInstance() {
        return myApplication;
    }
    private static final String DATABASE_NAME = "greendao.db";
    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 引入bugly
         * bugly主动上报：
         * CrashReport.postCatchedException(thr);  // bugly会将这个throwable上报
         * */
        myApplication = this;
        CrashReport.initCrashReport(getApplicationContext(), "f135a0669a", true);
        ToastUtils.init(this);
        //关闭日志
        //QZXTools.openLog=false;
        //开启日志
        QZXTools.openLog = true;
        MMKV.initialize(this);
        //初始化数据库
//        Realm.init(this);
//        RealmConfiguration config = new RealmConfiguration.Builder().name("tiantianqin.db").build();
//        Realm.setDefaultConfiguration(config);

    }
    /**
     * 初始化greendao
     * <p>
     * 解释一下：
     * 底层还是用到sqlitehelper获得greendao的helper,然后通过helper获取database,通过database创建DaoMaster
     * DaoMaster获取到DaoSession,通过DaoSession获取到想要的dao操作对象，例如UserDao
     */
    private void initGreenDAO() {
      /*  DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DATABASE_NAME);
        DbOpenHelper helper = new DbOpenHelper(this, DATABASE_NAME);
        Database database = helper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();*/
    }
}
