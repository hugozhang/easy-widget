package me.about.widget.retrofit2.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Retrofit2FastJsonConverterFactory
 *
 * @author: hugo.zxh
 * @date: 2023/04/02 0:48
 */
public class FastJsonConverterFactory extends Converter.Factory {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    private FastJsonConfig fastJsonConfig;

    public FastJsonConverterFactory(FastJsonConfig fastJsonConfig) {
        this.fastJsonConfig = fastJsonConfig;
    }

    public static FastJsonConverterFactory create() {
        return create(new FastJsonConfig());
    }

    public static FastJsonConverterFactory create(FastJsonConfig fastJsonConfig) {
        if (fastJsonConfig == null) {
            throw new NullPointerException("fastJsonConfig == null");
        }
        return new FastJsonConverterFactory(fastJsonConfig);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                            Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new FastJsonConverterFactory.ResponseBodyConverter<>(type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations,
                                                          Retrofit retrofit) {
        return new FastJsonConverterFactory.RequestBodyConverter<>();
    }

    final class ResponseBodyConverter<T> implements Converter<ResponseBody, T> {

        private Type type;

        ResponseBodyConverter(Type type) {
            this.type = type;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            try {
                if (type == String.class) {
                    return (T) value.string();
                }
                return JSON.parseObject(value.bytes()
                        , fastJsonConfig.getCharset()
                        , type
                        , fastJsonConfig.getParserConfig()
                        , fastJsonConfig.getParseProcess()
                        , JSON.DEFAULT_PARSER_FEATURE
                        , fastJsonConfig.getFeatures()
                );
            } catch (Exception e) {
                throw new IOException("JSON parse error: " + e.getMessage(), e);
            } finally {
                value.close();
            }
        }
    }

    final class RequestBodyConverter<T> implements Converter<T, RequestBody> {

        RequestBodyConverter() {
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            try {
                byte[] content = JSON.toJSONBytesWithFastJsonConfig(fastJsonConfig.getCharset()
                        , value
                        , fastJsonConfig.getSerializeConfig()
                        , fastJsonConfig.getSerializeFilters()
                        , fastJsonConfig.getDateFormat()
                        , JSON.DEFAULT_GENERATE_FEATURE
                        , fastJsonConfig.getSerializerFeatures()
                );
                return RequestBody.create(MEDIA_TYPE, content);
            } catch (Exception e) {
                throw new IOException("Could not write JSON: " + e.getMessage(), e);
            }
        }
    }
}
