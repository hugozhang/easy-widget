package me.about.widget.retrofit2.core;

import okhttp3.Call;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * ResponseCallAdapterFactory
 *
 * @author: hugo.zxh
 * @date: 2023/04/01 22:13
 */
public class Retrofit2AdapterFactory extends CallAdapter.Factory {

    private static final Logger log = LoggerFactory.getLogger(Retrofit2AdapterFactory.class);

    public static Retrofit2AdapterFactory create() {
        return new Retrofit2AdapterFactory();
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (Call.class.isAssignableFrom(getRawType(returnType))) {
            return null;
        }
        if (CompletableFuture.class.isAssignableFrom(getRawType(returnType))) {
            return null;
        }
        if (Response.class.isAssignableFrom(getRawType(returnType))) {
            return null;
        }
        return new ResponseCallAdapter<>(returnType,annotations,retrofit);
    }

    public class ResponseCallAdapter<R> implements CallAdapter<R,R> {

        private final Type returnType;

        private final Retrofit retrofit;

        private final Annotation[] annotations;

        ResponseCallAdapter(Type returnType, Annotation[] annotations, Retrofit retrofit) {
            this.returnType = returnType;
            this.retrofit = retrofit;
            this.annotations = annotations;
        }

        @Override
        public Type responseType() {
            return returnType;
        }

        @Override
        public R adapt(retrofit2.Call<R> call) {
            try {
                Response<R>  response = call.execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                ResponseBody errorBody = response.errorBody();
                if (errorBody == null) {
                    return null;
                }
                Converter<ResponseBody, R> converter = retrofit.responseBodyConverter(responseType(), annotations);
                return converter.convert(Objects.requireNonNull(errorBody));
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
            return null;
        }
    }
}
