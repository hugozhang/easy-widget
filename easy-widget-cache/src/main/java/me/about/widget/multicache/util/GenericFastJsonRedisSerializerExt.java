package me.about.widget.multicache.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.IOUtils;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.ObjectUtils;

/**
 * spring redis
 *
 * @author: hugo.zxh
 * @date: 2022/06/30 22:20
 * @description:
 */
public class GenericFastJsonRedisSerializerExt implements RedisSerializer<Object> {

    private static final byte[] BINARY_NULL_VALUE = JSON.toJSONBytes(NullValue.INSTANCE, new SerializerFeature[]{SerializerFeature.WriteClassName});

    private ParserConfig defaultRedisConfig = new ParserConfig();

    public GenericFastJsonRedisSerializerExt() {
        defaultRedisConfig.setAutoTypeSupport(true);
    }

    @Override
    public byte[] serialize(Object object) throws SerializationException {
        if (object == null) {
            return new byte[0];
        } else {
            try {
                return JSON.toJSONBytes(object, new SerializerFeature[]{SerializerFeature.WriteClassName});
            } catch (Exception var3) {
                throw new SerializationException("Could not serialize: " + var3.getMessage(), var3);
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes != null && bytes.length != 0) {
            if (ObjectUtils.nullSafeEquals(bytes, BINARY_NULL_VALUE)) {
                return NullValue.INSTANCE;
            }
            try {
                return JSON.parseObject(new String(bytes, IOUtils.UTF8), Object.class, defaultRedisConfig, new Feature[0]);
            } catch (Exception var3) {
                throw new SerializationException("Could not deserialize: " + var3.getMessage(), var3);
            }
        } else {
            return null;
        }
    }

}