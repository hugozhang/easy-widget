package me.about.widget.retrofit2.interceptor;

import okhttp3.*;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 日志拦截器
 *
 * @author: hugo.zxh
 * @date: 2023/04/02 23:45
 */
public class LoggingInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    private void printLog(Request request, Response response) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nMethod:").append(request.method());
        sb.append("\nURL:").append(request.url());
        sb.append("\nRequest Body:");
        try {
            sb.append(bodyToString(request.body()));
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        sb.append("\nResponse Body:");
        try {
            ResponseBody responseBody = response.peekBody(1024 * 1024);
            sb.append(responseBody.string());
            log.info(sb.toString());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    private String bodyToString(RequestBody request) throws IOException {
        if (request == null) {
            return "";
        }
        Buffer buffer = new Buffer();
        request.writeTo(buffer);
        return buffer.readUtf8();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        printLog(request, response);
        return response;
    }
}
