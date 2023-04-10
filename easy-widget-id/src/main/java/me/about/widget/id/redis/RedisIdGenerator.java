package me.about.widget.id.redis;

import me.about.widget.id.IdGenerator;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.List;

/**
 * 分布式id生成  redis实现
 *
 * @author: hugo.zxh
 * @date: 2020/11/18 17:30
 * @description:
 */
public class RedisIdGenerator implements IdGenerator {

    private RedisScript<List<Long>> idScript;

    private StringRedisTemplate stringRedisTemplate;

    public RedisIdGenerator(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.idScript = new DefaultRedisScript(RedisIdConfig.ID_SCRIPT, List.class);
    }

    @Override
    public long nextId(String tag) {
        List<String> keys = new ArrayList<>();
        keys.add(tag);
        List<Long> ids = stringRedisTemplate.execute(idScript, keys);
        return buildId(ids.get(0),ids.get(1),ids.get(2),ids.get(3));
    }

    private long buildId(long second, long microSecond, long shardId,long seq) {
        long milliSecond = (second * 1000 + microSecond / 1000);
        return (milliSecond << (12 + 10)) + (shardId << 10) + seq;
    }
}