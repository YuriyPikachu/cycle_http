package com.poll.demo.network;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;

/**
 * 网络状态, 全局就一份，系统启动的时候调用
 *
 * @author Levin Liu
 */
public class NetworkStatus extends BroadcastReceiver implements Closeable{
	/**
	 * @return 0:
	 * 1:WIFI
	 * 2:2G
	 * 3:3G
	 * 4:Four4G
	 */
	public final static int STATUS_NONE = 0;
	public final static int STATUS_WIFI = 1;
	public final static int STATUS_2G = 2;
	public final static int STATUS_3G = 3;
	public final static int STATUS_4G = 4;
	private static NetworkStatus instance;
	private Application application;
	private int status;
	private boolean isMonitorEnabled;

	public static NetworkStatus getInstance() {
		return instance;
	}

	/**
	 * 设置instance， 设置instance的时候必须带application，所以请在Application中调用
	 *
	 * @param status
	 * @param application
	 */
	public static void setInstance(NetworkStatus status, Application application) {
		if (status != null) {
			instance = status;
		}
		if (instance == null) instance = new NetworkStatus();
		if (application != null) instance.initialize(application);
	}

	/**
	 * Get the network status
	 *
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 是否已经启动了网络状态监听
	 *
	 * @return
	 */
	public boolean isMonitorEnabled() {
		return isMonitorEnabled;
	}

	protected void initialize(Application application) {
		this.application = application;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			this.refreshStatus();
		} catch (Exception error) {

		}
	}

	/**
	 * Enable the listener to monitor the network status
	 *
	 * @param enable
	 */
	public void enableNetworkListener(boolean enable) {
		if (this.isMonitorEnabled == enable) return;
		if (enable) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			application.registerReceiver(this, filter);
		} else {
			application.unregisterReceiver(this);
		}
		this.isMonitorEnabled = enable;
	}

	/**
	 * 获取当前网络状态
	 * Refresh the network status
	 */
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void refreshStatus() {
		ConnectivityManager mConnectivity = (ConnectivityManager) this.application.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if(info!=null){
            int netType = info.getType();
            int netSub = info.getSubtype();
            if (TelephonyManager.NETWORK_TYPE_GPRS == netSub ||
                    TelephonyManager.NETWORK_TYPE_EDGE == netSub ||
                    TelephonyManager.NETWORK_TYPE_CDMA == netSub) {
                this.status = STATUS_2G;
            } else if (TelephonyManager.NETWORK_TYPE_EVDO_0 == netSub ||
                    TelephonyManager.NETWORK_TYPE_EVDO_A == netSub ||
                    TelephonyManager.NETWORK_TYPE_EVDO_B == netSub ||
                    TelephonyManager.NETWORK_TYPE_HSDPA == netSub ||
                    TelephonyManager.NETWORK_TYPE_UMTS == netSub) {
                this.status = STATUS_3G;
            } else if (netType == ConnectivityManager.TYPE_WIFI) {
                this.status = STATUS_WIFI;
            } else if (TelephonyManager.NETWORK_TYPE_LTE == netSub) {
                this.status = STATUS_4G;
            } else this.status = STATUS_NONE;
        }else{
            this.status = STATUS_NONE;
        }
	}

	/**
	 * 是否是WIFI网络
	 *
	 * @return
	 */
	public boolean isWifi() {
		return this.status == STATUS_WIFI;
	}

	/**
	 * 2G 3G 4G网络
	 *
	 * @return
	 */
	public boolean isWAN() {
		return this.status == STATUS_2G ||
				this.status == STATUS_3G ||
				this.status == STATUS_4G;
	}

	/**
	 * 没有网络
	 *
	 * @return
	 */
	public boolean isNetworkConnected() {
		return this.status == STATUS_NONE;
	}

	/**
	 * 获取当前网速
	 */
	public int getTotalReceivedBytes() {
		String line;
		String[] segs;
		int received = 0;
		int i;
		int tmp = 0;
		boolean isNum;
		try {
			FileReader fr = new FileReader("/proc/net/dev");
			BufferedReader in = new BufferedReader(fr, 500);
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("rmnet") || line.startsWith("eth") || line.startsWith("wlan")) {
					segs = line.split(":")[1].split(" ");
					for (i = 0; i < segs.length; i++) {
						isNum = true;
						try {
							tmp = Integer.parseInt(segs[i]);
						} catch (Exception e) {
							isNum = false;
						}
						if (isNum == true) {
							received = received + tmp;
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			return -1;
		}
		return received;
	}

	@Override
	public void close() throws IOException {
		this.enableNetworkListener(false);
		this.application=null;
	}
}
