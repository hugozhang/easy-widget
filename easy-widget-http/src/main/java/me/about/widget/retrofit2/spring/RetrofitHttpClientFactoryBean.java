package me.about.widget.retrofit2.spring;

import lombok.extern.slf4j.Slf4j;
import me.about.widget.retrofit2.annotation.RetrofitHttpClient;
import me.about.widget.retrofit2.converter.JacksonConverterFactory;
import me.about.widget.retrofit2.core.Retrofit2AdapterFactory;
import me.about.widget.retrofit2.converter.FastJsonConverterFactory;
import me.about.widget.retrofit2.interceptor.LoggingInterceptor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
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
public class RetrofitHttpClientFactoryBean<T> implements FactoryBean<T>, EnvironmentAware {

    @Value("${retrofit.http.connect-timeout:5}")
    private Integer connectTimeout;

    @Value("${retrofit.http.write-timeout:5}")
    private Integer writeTimeout;

    @Value("${retrofit.http.read-timeout:5}")
    private Integer readTimeout;

    @Value("${retrofit.http.call-timeout:5}")
    private Integer callTimeout;

    private Class<T> retrofitHttpClientClass;

    private Environment environment;

    public RetrofitHttpClientFactoryBean(Class<T> retrofitHttpClientClass) {
        this.retrofitHttpClientClass = retrofitHttpClientClass;
    }

    @Override
    public T getObject() throws Exception {
        Assert.isTrue(retrofitHttpClientClass.isInterface(), "RetrofitHttpClient is only interface");

        RetrofitHttpClient retrofitHttpClient = retrofitHttpClientClass.getAnnotation(RetrofitHttpClient.class);

        Class<? extends Converter.Factory> converterFactory = retrofitHttpClient.converterFactory();


        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();

        String hostUrl = environment.resolveRequiredPlaceholders(retrofitHttpClient.baseUrl());

        String baseUrl = parser.parseExpression(hostUrl).getValue(context, String.class);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .callTimeout(callTimeout,TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .retryOnConnectionFailure(true)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(Retrofit2AdapterFactory.create())
                .addConverterFactory(converterFactory == FastJsonConverterFactory.class
                        ? FastJsonConverterFactory.create() : JacksonConverterFactory.create())
                .build();

        return retrofit.create(retrofitHttpClientClass);
    }

    @Override
    public Class<?> getObjectType() {
        return retrofitHttpClientClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
