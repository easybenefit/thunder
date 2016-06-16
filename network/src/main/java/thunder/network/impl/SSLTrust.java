package thunder.network.impl;

import okhttp3.OkHttpClient;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by HandGunBreak on 2015/11/6 - 18:04.
 * Mail: HandGunBreak@gmail.com
 * Copyright: 杭州医本健康科技有限公司(2014-2015)
 * Description: Https SSL认证配置
 */
public class SSLTrust {

    public static OkHttpClient configureHttps(OkHttpClient okHttpClient) {

        final TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {

                return new X509Certificate[0];
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain,
                                           final String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(final X509Certificate[] chain,
                                           final String authType) throws CertificateException {
            }
        }};

        try {

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            OkHttpClient.Builder builder = okHttpClient.newBuilder();
            builder.sslSocketFactory(sslContext.getSocketFactory());
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {

                    return true;
                }
            };
            builder.hostnameVerifier(hostnameVerifier);
            okHttpClient = builder.build();

        } catch (Exception exception) {

            exception.printStackTrace();
        }

        return okHttpClient;
    }

}
