package com.zhihuianxin.xyaxf.app;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import io.realm.RealmObject;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by zcrpro on 16/9/19.
 */
public class RetrofitFactory {

    private static Retrofit retrofit;
    private static String baseUrl;
    public static final String tag = "CustomTrust";
    public static OkHttpClient client;
    private static final int DEFAULT_TIMEOUT = 600;

    public static void setBaseUrl(String url) {
        baseUrl = url;
    }

    /**
     * 获取配置好的retrofit对象来生产Manager对象
     */
    public static <T extends RealmObject> Retrofit getRetrofit() {
        if (retrofit == null) {
            if (baseUrl == null || baseUrl.length() <= 0)
                throw new IllegalStateException("请在调用getFactory之前先调用setBaseUrl");

            X509TrustManager trustManager;
            SSLSocketFactory sslSocketFactory=null;

            trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            try {
                SSLContext sslContext;
                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null,new X509TrustManager[]{trustManager},null);
                sslSocketFactory = sslContext.getSocketFactory();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }

            client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory).hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .build();

            Retrofit.Builder builder = new Retrofit.Builder();

            builder.baseUrl(baseUrl)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 参考RxJava
                    .addConverterFactory(new CustomGsonConverterFactory<T>())
                    .client(client);   // 参考与GSON的结合

            retrofit = builder.build();
        }

        return retrofit;
    }
}
