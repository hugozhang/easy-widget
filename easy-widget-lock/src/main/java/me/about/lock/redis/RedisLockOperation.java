package me.about.lock.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/13 15:35
 * @Description: redis 加锁  释放锁 重置时间
 */
@Slf4j
public class RedisLockOperation {

    private RedisScript<String> LOCK_SCRIPT;

    private RedisScript<Long> UNLOCK_SCRIPT;

    private RedisScript<String> RENEWAL_SCRIPT;

    private StringRedisTemplate stringRedisTemplate;

    public RedisLockOperation (StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.LOCK_SCRIPT = new DefaultRedisScript(RedisLockConfig.LOCK_SCRIPT,String.class);
        this.UNLOCK_SCRIPT = new DefaultRedisScript(RedisLockConfig.UNLOCK_SCRIPT,Long.class);
        this.RENEWAL_SCRIPT = new DefaultRedisScript(RedisLockConfig.RENEWAL_SCRIPT,Long.class);
    }

    public boolean renewal(String key) {
        List<String> keys = new ArrayList<String>();
        keys.add(RedisLockConfig.LOCK_KET_PREFIX + key);
        String ret = stringRedisTemplate.execute(RENEWAL_SCRIPT,keys, Thread.currentThread().getName(),RedisLockConfig.EXPIRE_SEC);
        log.info("key {} renewal,return value is {}. ",key,ret);
        return "OK".equalsIgnoreCase(ret);
    }

    public boolean lock(String key) {
        List<String> keys = new ArrayList<String>();
        keys.add(RedisLockConfig.LOCK_KET_PREFIX + key);
        String ret = stringRedisTemplate.execute(LOCK_SCRIPT,keys, Thread.currentThread().getName(),RedisLockConfig.EXPIRE_SEC);
        log.info("key {} lock,return value is {}. ",key,ret);
        return "OK".equalsIgnoreCase(ret);
    }

    public void unlock(String key) {
        List<String> keys = new ArrayList<String>();
        keys.add(RedisLockConfig.LOCK_KET_PREFIX + key);
        Long ret = stringRedisTemplate.execute(UNLOCK_SCRIPT, keys, Thread.currentThread().getName());
        log.info("unlock key is {},ret is {}.",key,ret);
    }

}
