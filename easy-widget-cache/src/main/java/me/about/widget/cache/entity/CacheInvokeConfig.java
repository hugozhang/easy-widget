package me.about.widget.cache.entity;

import lombok.Data;
import me.about.widget.cache.enums.CacheType;

import java.util.concurrent.TimeUnit;


@Data
public class CacheInvokeConfig {

    private String cacheKey;

    private CacheType type;

    private long expire;

    private TimeUnit timeUnit;

    private long emptyExpire;

    private TimeUnit emptyTimeUnit;

    private ManyToManyParam manyToManyParam;

}
