package com.example.XinTiaobaoDemo;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 包名：com.example.XinTiaobaoDemo
 * 描述：
 * User 张伟
 * Date 2015/5/22 0022
 * Time 下午 3:35.
 * 修改日期：
 * 修改内容：
 */
public class BBHttpClient {
    private BBHttpUtils mBBClient = null;
    private static BBHttpClient mySingleton;
    public static String TOKEN = "";
    // 开放一个公有方法，判断是否已经存在实例，有返回，没有新建一个在返回
    public static BBHttpClient getInstance()
    {
        if(mySingleton == null){
            mySingleton = new BBHttpClient();
        }
        return mySingleton;
    }
    private BBHttpClient()
    {
        // 私有化构造函数
        mBBClient = new BBHttpUtils();
        mBBClient.configRequestRetryCount(3);// 设置重链次数，这里为3次
        mBBClient.configCurrentHttpCacheExpiry(0);
        mBBClient.configTimeout(30000);// 30秒超时
        mBBClient.configResponseTextCharset("UTF-8");
        // 自动管理 cookie,或后续其他操作
        // mBBClient.configCookieStore(preferencesCookieStore)
    }

    /**
     * 请求路由器方法
     *
     * @param HttpMethodPOSTorGET
     *            请求方式
     * @param currentType
     *            请求标识
     * @param url
     *            接口绝对地址
     * @param params
     *            参数对象或null
     * @param callBack
     *            回调方法
     */
    public synchronized void getRouter(HttpRequest.HttpMethod HttpMethodPOSTorGET,
                                       int currentType, String url, RequestParams params,
                                       RequestCallBack<String> callBack){
        setHttpHeader(url);
        if (HttpMethodPOSTorGET.equals(HttpRequest.HttpMethod.POST))
        {
            mBBClient.send(HttpRequest.HttpMethod.POST, url, params, callBack,
                    currentType);
        } else if (HttpMethodPOSTorGET.equals(HttpRequest.HttpMethod.DELETE))
        {
            mBBClient.send(HttpRequest.HttpMethod.DELETE, url, params,
                    callBack, currentType);
        }else if(HttpMethodPOSTorGET.equals(HttpRequest.HttpMethod.PUT)){
            mBBClient.send(HttpRequest.HttpMethod.PUT, url, params,
                    callBack, currentType);
        }
        else
        {
            mBBClient.send(HttpRequest.HttpMethod.GET, url, callBack,
                    currentType);
        }
    }

    private String paserUrl(String url)
    {
        // String strlocalString="";
        // int end= (url.lastIndexOf('?')==0?url.length():url.lastIndexOf('?'));
        // strlocalString=url.substring(0,end);
        // LogUtil.getLogger().d(
        // "paser:url="+url+".............paserover="+strlocalString);
        // return strlocalString;
        return url.trim();

    }

    private synchronized void setHttpHeader(String url)
    {

        String strwillbeMD5 = paserUrl(url);
        List<Header> headers = new ArrayList<Header>();
        StringBuilder key = new StringBuilder();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
        key.append("SHANGJIA").append("|").append("123").append("|")
                .append(dateformat.format(new Date())).append(strwillbeMD5);

        BasicHeader headerXid = new BasicHeader("xid", "123" + "|"
                + Utility.md5(key.toString()));// MD5 加密
        headers.add(headerXid);

        BasicHeader headerToken = new BasicHeader("BANGBANG_TOKEN", TOKEN);
        headers.add(headerToken);

        BasicHeader headerXdate = new BasicHeader("xdate",
                String.valueOf(new Date().getTime()));
        headers.add(headerXdate);

        BasicHeader headerxPath = new BasicHeader("xpath", url);
        headers.add(headerxPath);

        mBBClient.getHttpClient().getParams()
                .setParameter("http.default-headers", headers);
    }

}
