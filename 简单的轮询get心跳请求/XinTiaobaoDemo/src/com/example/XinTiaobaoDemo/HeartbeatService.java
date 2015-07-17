package com.example.XinTiaobaoDemo;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.List;

/**
 * 包名：com.example.XinTiaobaoDemo
 * 描述：
 * User 张伟
 * Date 2015/5/22 0022
 * Time 下午 2:42.
 * 修改日期：
 * 修改内容：
 */
public class HeartbeatService extends Service implements Runnable {
    private Thread mThread;
    public int count = 0;
    private static String postUrl = "http://192.168.43.30:2060/cgi-bin/getADkey/getkey";//url


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mThread = new Thread(this);
        mThread.start();
        count = 0;
    }

    @Override
    public void run() {
        while (MyActivity.isTrue) {
            try {
//                if (count > 1) {
//                    count = 1;
//                    if (isTip) {
//                        //判断应用是否在运行
//                        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(3);
//                        for (ActivityManager.RunningTaskInfo info : list) {
//                            if (info.topActivity.getPackageName().equals("com.example.XinTiaobaoDemo")) {
//                                //通知应用，显示提示“连接不到服务器”
//                                Intent intent = new Intent(MyReciver.ENDNENDNN);
//                                intent.putExtra("msg", true);
//                                sendBroadcast(intent);
//                                break;
//                            }
//                        }
//                        isTip = false;
//                    }
//                }
                count++;
                Log.i("xintiao", "开始了"+count);

                //向服务器发送心跳包
                httprequestRouter(HttpRequest.HttpMethod.GET,0,null,postUrl);
                Thread.sleep(3000);

            } catch (InterruptedException e) {
                if(e!=null)
                e.printStackTrace();
            }
        }
    }

    private void sendHeartbeatPackage() {
        HttpGet httpGet = new HttpGet(postUrl);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        // 发送请求
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
        } catch (Exception e) {
            if(e!=null)
            e.printStackTrace();
        }
        if (httpResponse == null) {
            return;
        }

        // 处理返回结果
        final int responseCode = httpResponse.getStatusLine().getStatusCode();
        if (responseCode == HttpStatus.SC_OK) {
            //只要服务器有回应就OK
            Log.i("xintiao", "结束了————成功了");
        } else {
            Log.i("xintiao", "结束了————失败了");
        }
    }


    // 路由器请求。跟帮帮路由器进行身份确认
    private void httprequestRouter(HttpRequest.HttpMethod mtype, int type, RequestParams params, String url) {
        // 网络请求
        BBHttpClient.getInstance().getRouter(mtype, type, url, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        Log.i("xintiao", "结束了————失败了"+arg1);
                    }
                    @Override
                    public void onSuccess(ResponseInfo<String> arg0, int arg1, String arg2, String arg3, int arg4) {
                        if (arg1 == 0) {
                            Log.i("xintiao", "结束了————成功了"+arg3);
                        }
                    }
                });
    }



}
