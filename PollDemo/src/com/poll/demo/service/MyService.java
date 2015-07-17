package com.poll.demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.poll.demo.network.NetWorkUtils;
import com.poll.demo.network.NetworkStatus;
import com.poll.demo.sys.MyApplication;
import com.poll.demo.utils.Constant;

import javax.security.auth.login.LoginException;

/**
 * Created by Administrator on 15-4-7.
 */
public class MyService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化的操作
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        //网络是否可用
        if(new NetWorkUtils(this).CheckNetwork()){
            //
            Log.i("yigeren","来了一次");
            MyApplication.getInstance().setSharedPreferencesValue("yig","来了一次");
            sendBroadcast(new Intent(Constant.ReceiverAction.TONGZHI_ACTION));
        }
    }
}
