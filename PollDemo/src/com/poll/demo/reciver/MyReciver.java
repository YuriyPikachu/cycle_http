package com.poll.demo.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.poll.demo.service.MyService;
import com.poll.demo.sys.MyApplication;
import com.poll.demo.utils.Constant;

/**
 * Created by Administrator on 15-4-7.
 */
public class MyReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(Constant.ReceiverAction.MSG_POLL.equals(action)&& MyApplication.isGetMsg){
            Intent i = new Intent();
            i.setClass(context, MyService.class);
            context.startService(i);
        }
    }
}
