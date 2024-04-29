package me.about.widget.retrofit2.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Invocation;

import java.io.IOException;

@Slf4j
public class RetryInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        RetryOnFailure retryAnnotation = getRetryAnnotation(originalRequest);
        if (retryAnnotation != null) {
            return retryRequest(chain, originalRequest, retryAnnotation);
        } else {
            return chain.proceed(originalRequest);
        }
    }

    private RetryOnFailure getRetryAnnotation(Request request) {
        if (request == null) {
            return null;
        }
        Invocation invocation = request.tag(Invocation.class);
        if (invocation == null) {
            return null;
        }
        return invocation.method().getAnnotation(RetryOnFailure.class);
    }

    private Response retryRequest(Chain chain, Request request, RetryOnFailure retryAnnotation) throws IOException {
        int maxRetries = retryAnnotation.maxRetries();
        long initialDelayMs = retryAnnotation.initialDelayMs();
        Response response = null;
        for (int i = 0; i <= maxRetries; i++) {
            log.info("Retrying request {}", request.url());
            response = chain.proceed(request);
            if (response.isSuccessful()) {
                return response;
            }

            long delay = (long) (initialDelayMs * Math.pow(2, i));
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted during retry delay", e);
            }
        }
        // 如果所有重试都失败，则返回最后一次尝试的结果
        return response;
    }
}
