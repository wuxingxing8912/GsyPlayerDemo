package cn.com.cunw.gsyplayerdemo;

import android.app.Application;

import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by wuxingxing on 2022/11/24 15:38
 * desc:
 */
public class GsyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ProxyCacheManager.instance().setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };
        ProxyCacheManager.instance().setTrustAllCerts(trustAllCerts);
    }
}
