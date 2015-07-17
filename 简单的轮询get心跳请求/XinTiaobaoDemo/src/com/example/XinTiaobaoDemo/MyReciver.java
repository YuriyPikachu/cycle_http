package com.example.XinTiaobaoDemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 包名：com.example.XinTiaobaoDemo
 * 描述：
 * User 张伟
 * Date 2015/5/22 0022
 * Time 下午 2:57.
 * 修改日期：
 * 修改内容：
 */
public class MyReciver extends BroadcastReceiver {
    public static final String ENDNENDNN = "com.example.XinTiaobaoDemo.tongzhi";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(ENDNENDNN.equals(action)){
            boolean msg = intent.getBooleanExtra("msg",false);
            if(msg){
                Toast.makeText(context,"无法连接到服务器",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
