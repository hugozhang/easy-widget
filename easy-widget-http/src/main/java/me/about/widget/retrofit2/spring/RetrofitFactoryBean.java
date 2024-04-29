package me.about.widget.retrofit2.spring;

import lombok.extern.slf4j.Slf4j;
import me.about.widget.retrofit2.annotation.RetrofitClient;
import me.about.widget.retrofit2.converter.FastJsonConverterFactory;
import me.about.widget.retrofit2.converter.JacksonConverterFactory;
import me.about.widget.retrofit2.core.Retrofit2AdapterFactory;
import me.about.widget.retrofit2.interceptor.LoggingInterceptor;
import me.about.widget.retrofit2.interceptor.RetryInterceptor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.util.concurrent.TimeUnit;

/**
 * 工厂 bean
 *
 * @author: hugo.zxh
 * @date: 2023/03/28 16:06
 */
@Slf4j
public class RetrofitFactoryBean<T> implements FactoryBean<T>, EnvironmentAware {

    @Value("${retrofit.connect-timeout-ms:5000}")
    private Integer connectTimeout;

    @Value("${retrofit.write-timeout-ms:5000}")
    private Integer writeTimeout;

    @Value("${retrofit.read-timeout-ms:5000}")
    private Integer readTimeout;

    @Value("${retrofit.call-timeout-ms:5000}")
    private Integer callTimeout;

    private final Class<T> retrofitClientClass;

    private Environment environment;

    public RetrofitFactoryBean(Class<T> retrofitClientClass) {
        this.retrofitClientClass = retrofitClientClass;
    }

    @Override
    public T getObject() throws Exception {

        Assert.isTrue(retrofitClientClass.isInterface(), "RetrofitHttpClient is only interface");

        RetrofitClient retrofitClient = retrofitClientClass.getAnnotation(RetrofitClient.class);

        Class<? extends Converter.Factory> converterFactory = retrofitClient.converterFactory();


//        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
//        StandardEvaluationContext context = new StandardEvaluationContext();

        String baseUrl = environment.resolveRequiredPlaceholders(retrofitClient.baseUrl());

//        String baseUrl = parser.parseExpression(hostUrl).getValue(context, String.class);


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .callTimeout(callTimeout,TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggingInterceptor())
                .addInterceptor(new RetryInterceptor())
//                .retryOnConnectionFailure(true)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(Retrofit2AdapterFactory.create())
                .addConverterFactory(converterFactory == FastJsonConverterFactory.class
                        ? FastJsonConverterFactory.create() : JacksonConverterFactory.create())
                .build();

        return retrofit.create(retrofitClientClass);
    }

    @Override
    public Class<?> getObjectType() {
        return retrofitClientClass;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
