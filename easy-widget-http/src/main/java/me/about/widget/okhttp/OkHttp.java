package me.about.widget.okhttp;

import com.alibaba.fastjson.JSON;
import okhttp3.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * http 工具类
 *
 * @author: hugo.zxh
 * @date: 2020/11/02 9:24
 * @description:
 */
public class OkHttp {

    private Request.Builder requestBuilder;

    private FormBody.Builder formBuilder;

    private MultipartBody.Builder multipartBodyBuilder;

    private Response response;

    private Map<String,String> parameterMap = new HashMap();

    private Map<String, File> fileMap = new HashMap();

    private RequestBody requestBody;

    private boolean isUploadFile = false;

    private OkHttp() {
        this.requestBuilder = new Request.Builder();
        this.formBuilder = new FormBody.Builder();
        this.multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
    }

    public OkHttp header(String name,String value) {
        this.requestBuilder.addHeader(name,value);
        return this;
    }

    public OkHttp url(String url) {
        this.requestBuilder.url(url);
        return this;
    }

    public OkHttp form(String name,String value) {
        parameterMap.put(name,value);
        return this;
    }

    public OkHttp file(String name, File file) {
        fileMap.put(name,file);
        return this;
    }

    public OkHttp body(Object o) {
        if (o == null) {
            throw new NullPointerException("o == null");
        }
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                JSON.toJSONString(o)
        );
        this.requestBody = body;
        return this;
    }

    private boolean isUpload() {
        return this.fileMap.isEmpty() ? false : true;
    }

    public OkHttp form() throws IOException {
        Iterator<Map.Entry<String, String>> iterator = this.parameterMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (!isUpload()) {
                this.formBuilder.add(entry.getKey(),entry.getValue());
            } else {
                this.multipartBodyBuilder.addFormDataPart(entry.getKey(),entry.getValue());
            }
        }
        Iterator<Map.Entry<String, File>> it = this.fileMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, File> entry = it.next();
            this.multipartBodyBuilder.addFormDataPart(entry.getKey(), "file", RequestBody.create(MediaType.parse("application/octet-stream"),entry.getValue()));
        }
        if (!isUpload()) {
            this.requestBody = this.formBuilder.build();
        } else {
            this.requestBody = this.multipartBodyBuilder.build();
        }
        //发起调用
        execute();
        return this;
    }

    public OkHttp get() throws IOException {
        this.requestBuilder.get();
        return execute();
    }

    public OkHttp post() throws IOException {
        this.requestBuilder.post(this.requestBody);
        return execute();
    }

    public OkHttp put() throws IOException {
        this.requestBuilder.put(this.requestBody);
        return execute();
    }

    private OkHttp execute() throws IOException {
        this.response = OkHttpHolder.INSTANCE.newCall(requestBuilder.build()).execute();
        return this;
    }

    public String asString() throws IOException {
        return this.response.body().string();
    }

    public <T> T asObject(Class<T> class1) throws IOException {
        return JSON.parseObject(asString(),class1);
    }

    public static OkHttp builder() {
        return new OkHttp();
    }

    private static class OkHttpHolder {

        private static OkHttpClient INSTANCE;

        static {
            SSLContext sslContext = null;
            ConnectionSpec spec = null;
            try {
                sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .allEnabledCipherSuites()
                        .build();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

            X509TrustManager x509TrustManager = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            };

            List<ConnectionSpec> connectionSpecs = new ArrayList<>();
            connectionSpecs.add(spec);
            connectionSpecs.add(new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build());

            try {
                INSTANCE = new OkHttpClient().newBuilder()
                        .connectionSpecs(connectionSpecs)
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .writeTimeout(5, TimeUnit.SECONDS)
                        .sslSocketFactory(sslContext.getSocketFactory(), x509TrustManager)
                        .hostnameVerifier(getHostnameVerifier())
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static HostnameVerifier getHostnameVerifier() {
            HostnameVerifier hostnameVerifier = (s, sslSession) -> true;
            return hostnameVerifier;
        }
    }
}
