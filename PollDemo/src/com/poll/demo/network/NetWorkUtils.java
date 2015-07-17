package com.poll.demo.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.poll.demo.R;


/**
 * 类 名: NetWorkUtils.java 包 名: com.bangbang.app.ui.utils 描 述: 网络工具类
 * 创建人:zchuanjian@bangbang123.cn 创建时间: Jul 11, 2014 2:49:39 PM 修改记录:
 * zhouchuanjian-001-2014-07-13-创建 ChenBin-002-2014-11-10-添加GPS，WiFi与移动网络状态判断方法
 * ChenBin-003-2014-11-25-添加setGPSStatusListener方法，用于监听GPS的启动与停止
 * ChenBin-004-2014-12-16-修改isMobileNetNormal()方法
 */

public class NetWorkUtils
{
    private static Context mContext;
    public State wifiState = null;
    public State mobileState = null;

    public NetWorkUtils(Context context)
    {
        mContext = context;
    }

    public enum NetWorkState
    {
        WIFI, MOBILE, NONE;
    }

    /**
     * 判断网络是否可用
     * @return boolean
     */
    public static boolean CheckNetwork()
    {
        boolean flag = false;
        ConnectivityManager cwjManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager != null)
        {
            NetworkInfo info = cwjManager.getActiveNetworkInfo();
            if (info != null)
            {
                flag = info.isAvailable();
            }
        }
        return flag;
    }

    /**
     * 获取设备号DeviceId
     * @return String
     */
    public String getDeviceId()
    {
        TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        String sb = tm.getDeviceId();
        if (sb != null)
        {
            return sb;
        } else
        {
            return mContext.getResources().getString(R.string.strUnknown);
        }
    }

    /**
     * 获取运营商类型
     * @return String 返回当前手机使用的运营商
     */
    public String getProvidersName()
    {
        TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        String ProvidersName = null;
        // 返回唯一的用户ID;就是这张卡的编号什么的
        String IMSI = tm.getSubscriberId();
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if (IMSI != null)
        {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002"))
            {
                ProvidersName = mContext.getResources().getString(
                        R.string.strCMCC);
            } else if (IMSI.startsWith("46001"))
            {
                ProvidersName = mContext.getResources().getString(
                        R.string.strChinaUnicom);
            } else if (IMSI.startsWith("46003"))
            {
                ProvidersName = mContext.getResources().getString(
                        R.string.strChinaTelecom);
            } else
            {
                ProvidersName = "";
            }
        } else
        {
            ProvidersName = "";
        }
        return ProvidersName;
    }

    /**
     * 获取手机系统版本号
     * @return String
     */
    public String getAppVersionName()
    {
        String versionName = "";
        try
        {
            // ---get the package info---
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0)
            {
                return "";
            }
        } catch (Exception e)
        {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 获取当前的网络链接状态
     *
     * @return NetWorkState
     */
    public NetWorkState getConnectState()
    {
        ConnectivityManager manager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        manager.getActiveNetworkInfo();
        wifiState = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        mobileState = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState();
        if (wifiState != null && mobileState != null
                && State.CONNECTED != wifiState
                && State.CONNECTED == mobileState)
        {
            return NetWorkState.MOBILE;
        } else if (wifiState != null && mobileState != null
                && State.CONNECTED != wifiState
                && State.CONNECTED != mobileState)
        {
            return NetWorkState.NONE;
        } else if (wifiState != null && State.CONNECTED == wifiState)
        {
            return NetWorkState.WIFI;
        }
        return NetWorkState.NONE;
    }

    /**
     * 判断当前网络类型：-1 无可用网络 ; 0 wifi ; 1 2G网络 ； 2 3G网络
     *
     */
    private static final int NET_WIFI = 0; // wifi
    private static final int NET_2G = 1; // 2G网络
    private static final int NET_3G = 2; // 3G网络

    /**
     * 获取网络类型
     * @return int NET_WIFI = 0; // wifi NET_2G = 1; // 2G网络 NET_3G = 2; // 3G网络
     */
    public int getNetType()
    {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        TelephonyManager telephonyManager = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        int subType = telephonyManager.getNetworkType();

        if (networkInfo == null || !networkInfo.isAvailable())
        {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE)
        {
            switch (subType)
            {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return NET_2G; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return NET_2G; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return NET_2G; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return NET_3G; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return NET_3G; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return NET_2G; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return NET_3G; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return NET_3G; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return NET_3G; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return NET_3G; // ~ 400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return NET_2G;
                default:
                    return NET_2G;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI)
        {
            netType = NET_WIFI;
        }
        return netType;
    }

    /**
     * 判断应用是否处于前台运行
     * @return boolean
     */
    public boolean isAppOnForeground()
    {
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = mContext.getPackageName();

        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (RunningAppProcessInfo appProcess : appProcesses)
        {

            if (appProcess.processName.equals(packageName))
            {

                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断GPS状态
     *
     * @return boolean GPS状态
     */
    public static boolean isGPSNormal()
    {
        LocationManager locationManager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);
        if(locationManager == null){
            return true;
        }else{
            boolean isGPSNormal = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            return isGPSNormal;
        }
    }

    /**
     * 判断WiFi状态
     *
     * @return boolean WiFi状态
     */
    public static boolean isWiFiNormal()
    {
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        int intWifiState = wifiManager.getWifiState();
        if (intWifiState == WifiManager.WIFI_STATE_ENABLED)
        {
            return true;
        }
        return false;
    }

    /**
     * 判断移动网络状态
     *
     * @return boolean 移动网络状态
     */
    public static boolean isMobileNetNormal()
    {
        ConnectivityManager connectivity = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        State state = connectivity.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE).getState();
        if (state != null)
        {
            // 若3G为连接状态，则直接返回true
            if (state == State.CONNECTING || state == State.CONNECTED)
            {
                return true;
            } else
            {
                // 3G为断开状态，则有可能是在WiFi情况下。
                // 若不处于WiFi下，则断定3G为断开；否则，断开当前WiFi再进行3G网络判断。
                if (state == State.DISCONNECTING || state == State.DISCONNECTED)
                {
                    WifiManager manager = (WifiManager) mContext
                            .getSystemService(Context.WIFI_SERVICE);
                    if (manager.isWifiEnabled())
                    {
                        manager.setWifiEnabled(false);
                    }
                }
            }
        }
        return false;
    }

    /**
     * 设置GPS监听，并发出Massage
     *
     * @param handler
     */
    public void setGPSStatusListener(final Handler handler)
    {
        LocationManager locationManager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);
        locationManager.addGpsStatusListener(new Listener()
        {

            @Override
            public void onGpsStatusChanged(int event)
            {
                switch (event)
                {
                    case GpsStatus.GPS_EVENT_STARTED:
                        handler.sendEmptyMessage(GpsStatus.GPS_EVENT_STARTED);
                        break;
                    case GpsStatus.GPS_EVENT_STOPPED:
                        handler.sendEmptyMessage(GpsStatus.GPS_EVENT_STOPPED);
                        break;
                    default:
                        break;
                }

            }
        });
    }
    /**
     * 判断能不能上外网
     * @return
     */
    public static boolean ping(){
        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
//	                Log.d("----result---", "result = " + result); 
        }
        return false;
    }

}
