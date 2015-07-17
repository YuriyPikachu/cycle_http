package com.example.XinTiaobaoDemo;

import android.text.TextUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.client.DefaultSSLSocketFactory;
import com.lidroid.xutils.http.client.RetryHandler;
import com.lidroid.xutils.http.client.entity.GZipDecompressingEntity;
import com.lidroid.xutils.util.OtherUtils;
import org.apache.http.*;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * 包名：com.example.XinTiaobaoDemo
 * 描述：
 * User 张伟
 * Date 2015/5/22 0022
 * Time 下午 3:36.
 * 修改日期：
 * 修改内容：
 */
public class BBHttpUtils extends HttpUtils {
    private final static int DEFAULT_CONN_TIMEOUT = 1000 * 15; // 15s
    private final static int DEFAULT_RETRY_TIMES = 3;
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";
    private final DefaultHttpClient httpClient;
    public BBHttpUtils(){
        this(DEFAULT_CONN_TIMEOUT, null);
    }

    public BBHttpUtils(int connTimeout) {
        this(connTimeout, null);
    }

    public BBHttpUtils(String userAgent) {
        this(BBHttpUtils.DEFAULT_CONN_TIMEOUT, userAgent);
    }

    public BBHttpUtils(int connTimeout, String userAgent) {
        HttpParams params = new BasicHttpParams();

        ConnManagerParams.setTimeout(params, connTimeout);
        HttpConnectionParams.setSoTimeout(params, connTimeout);
        HttpConnectionParams.setConnectionTimeout(params, connTimeout);

        if (TextUtils.isEmpty(userAgent)) {
            userAgent = OtherUtils.getUserAgent(null);
        }
        HttpProtocolParams.setUserAgent(params, userAgent);

        ConnManagerParams.setMaxConnectionsPerRoute(params,
                new ConnPerRouteBean(10));
        ConnManagerParams.setMaxTotalConnections(params, 10);

        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSocketBufferSize(params, 1024 * 8);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", DefaultSSLSocketFactory
                .getSocketFactory(), 443));

        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(
                params, schemeRegistry), params);

        httpClient.setHttpRequestRetryHandler(new RetryHandler(
                DEFAULT_RETRY_TIMES));

        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(org.apache.http.HttpRequest httpRequest,
                                HttpContext httpContext)
                    throws org.apache.http.HttpException, IOException {
                if (!httpRequest.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    httpRequest
                            .addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });

        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse response, HttpContext httpContext)
                    throws org.apache.http.HttpException, IOException {
                final HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase("gzip")) {
                            response.setEntity(new GZipDecompressingEntity(
                                    response.getEntity()));
                            return;
                        }
                    }
                }
            }
        });
    }

}
