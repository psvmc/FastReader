package com.codingbingo.fastreader;

import android.app.Application;
import android.app.Service;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.codingbingo.fastreader.dao.DaoMaster;
import com.codingbingo.fastreader.dao.DaoSession;
import com.codingbingo.fastreader.utils.SharedPreferenceUtils;
import com.facebook.stetho.Stetho;

import org.greenrobot.greendao.database.Database;

import java.util.HashMap;

/**
 * Created by bingo on 2016/12/19.
 */

public class FRApplication extends Application {

    private static FRApplication instance;

    private HashMap<String, Service> serviceList;

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        initCloudService();
        initDatabase();
        init();

        Stetho.initializeWithDefaults(this);
    }

    public static FRApplication getInstance(){
        return instance;
    }

    private void init() {
        SharedPreferenceUtils.init(this, "FastReader");
    }

    /**
     * 初始化LeanCloud服务
     */
    private void initCloudService() {
        AVAnalytics.setAnalyticsEnabled(false);
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this, Constants.APP_ID, Constants.APP_KEY);
//        AVAnalytics.enableCrashReport(this, true);
        //调试过程中，出现debug的log
        AVOSCloud.setDebugLogEnabled(true);
    }

    /**
     * 初始化数据库相关
     */
    private void initDatabase() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, Constants.DB_NAME);
        Database database = devOpenHelper.getWritableDb();
        daoSession = new DaoMaster(database).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * 通过service的名称获取Service对象
     *
     * @param name
     * @return
     */
    public Service findServiceByInterface(String name) {
        return serviceList.get(name);
    }

    public void registerService(String name, Service service) {
        if (serviceList == null) {
            serviceList = new HashMap<>();
        }

        if (serviceList.containsKey(name)) {
            serviceList.put(name, service);
        }
    }
}