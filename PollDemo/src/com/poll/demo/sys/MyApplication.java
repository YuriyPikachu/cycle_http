package com.poll.demo.sys;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import com.poll.demo.reciver.MyReciver;
import com.poll.demo.utils.Constant;

/**
 * Created by Administrator on 15-4-7.
 */
public class MyApplication extends Application {
    private static final String BANGBANG_SETTING = "zw";// 要改，屏蔽不同版本等信息?
    private static MyApplication application=null;

    public static MyApplication getInstance(){
        return application;
    }

    public static boolean isGetMsg = false;//是否需要用到轮询 在登陆成功就开始使用 或者 开始http聊天时使用
    private AlarmManager am;//全局定时器

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        initPoll();
    }

    //轮询
    private void initPoll(){
        Intent intent = new Intent(this, MyReciver.class);
        intent.setAction(Constant.ReceiverAction.MSG_POLL);
        //送达
        PendingIntent sender = PendingIntent.getBroadcast(this,0,intent,0);
        long firstime = SystemClock.elapsedRealtime();//最初时间
        am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        //10秒一个周期 不停的发送广播
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,firstime,10*1000,sender);
    }

    public String getSharedPreferencesValue(String key) {
        SharedPreferences settings = this.getSharedPreferences(
                BANGBANG_SETTING, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public void setSharedPreferencesValue(String key, String value) {
        SharedPreferences settings = this.getSharedPreferences(
                BANGBANG_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void setSharedPreferencesBooleanValue(String key, boolean value) {
        SharedPreferences settings = this.getSharedPreferences(
                BANGBANG_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getSharedPreferencesBooleanValue(String key, boolean value) {
        SharedPreferences settings = this.getSharedPreferences(
                BANGBANG_SETTING, Context.MODE_PRIVATE);
        return settings.getBoolean(key, value);
    }

    public void clearSharepreference() {
        SharedPreferences settings = this.getSharedPreferences(
                BANGBANG_SETTING, Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }
}
