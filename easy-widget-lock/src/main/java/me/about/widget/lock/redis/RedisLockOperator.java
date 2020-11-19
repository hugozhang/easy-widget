package me.about.widget.lock.redis;

import lombok.extern.slf4j.Slf4j;
import me.about.widget.lock.LockOperation;
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
 * @Description: 操作类： 加锁、释放锁、重置锁时间（续期）
 */
@Slf4j
class RedisLockOperator implements LockOperation {

    private RedisScript<String> LOCK_SCRIPT;

    private RedisScript<Long> UNLOCK_SCRIPT;

    private RedisScript<String> RESET_SCRIPT;

    private StringRedisTemplate stringRedisTemplate;

    public RedisLockOperator(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.LOCK_SCRIPT = new DefaultRedisScript(RedisLockConfig.LOCK_SCRIPT,String.class);
        this.UNLOCK_SCRIPT = new DefaultRedisScript(RedisLockConfig.UNLOCK_SCRIPT,Long.class);
        this.RESET_SCRIPT = new DefaultRedisScript(RedisLockConfig.RESET_SCRIPT,String.class);
    }

    @Override
    public boolean resetLock(String key) {
        List<String> keys = new ArrayList<String>();
        keys.add(RedisLockConfig.LOCK_KET_PREFIX + key);
        String ret = stringRedisTemplate.execute(RESET_SCRIPT,keys, Thread.currentThread().getName(),RedisLockConfig.EXPIRE_SEC + "");
        log.info("Lock key is {} reset,return value is {}. ",key,ret);
        return "OK".equalsIgnoreCase(ret);
    }

    @Override
    public boolean tryLock(String key) {
        List<String> keys = new ArrayList<String>();
        keys.add(RedisLockConfig.LOCK_KET_PREFIX + key);
        String ret = stringRedisTemplate.execute(LOCK_SCRIPT,keys, Thread.currentThread().getName(),RedisLockConfig.EXPIRE_SEC + "");
        log.info("Lock key is {},return value is {}. ",key,ret);
        return "OK".equalsIgnoreCase(ret);
    }

    public void releaseLock(String key) {
        List<String> keys = new ArrayList<String>();
        keys.add(RedisLockConfig.LOCK_KET_PREFIX + key);
        Long ret = stringRedisTemplate.execute(UNLOCK_SCRIPT, keys, Thread.currentThread().getName());
        log.info("Lock key is {},return value is {}. ",key,ret);
    }
}
