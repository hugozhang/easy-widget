package me.about.widget.lock.redis;

import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;
import me.about.widget.lock.LockException;
import me.about.widget.lock.LockOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/13 15:35
 * @Description: 操作类： 加锁、释放锁、重置锁时间（续期）  这个类是单例的，随着LockContext初始化
 */
@Slf4j
class RedisLockOperator implements LockOperation {

    private final Map<String, AtomicInteger> KEYS_CONTAINER = new ConcurrentHashMap<>();

    private final Map<String, Boolean> TIMEOUT_STATUS_CONTAINER = new ConcurrentHashMap<>();

    private final HashedWheelTimer TIMER_TASK = new HashedWheelTimer();

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
    public boolean resetLock(String key,String value) {
        List<String> keys = new ArrayList<String>();
        String lockKey = RedisLockConfig.LOCK_KET_PREFIX + key;
        keys.add(lockKey);

        //每重置一次就计数1
        int increment = KEYS_CONTAINER.computeIfAbsent(key, k -> new AtomicInteger()).getAndIncrement();
        //记录并检查锁续期次数
        if (increment >= RedisLockConfig.RESET_MAX_COUNT) {
            throw new LockException("锁续期超过最大次数:" + RedisLockConfig.RESET_MAX_COUNT,key,value);
        }

        String ret = stringRedisTemplate.execute(RESET_SCRIPT,keys, value,RedisLockConfig.EXPIRE_SEC + "");
        boolean isOk = "OK".equalsIgnoreCase(ret);
        if (isOk) {
            log.info("Reset Lock key is {} reset,value is {},return value is {},reset count is {}. ",new Object[]{lockKey,value,ret,increment});
            newTimeout(key,value);
        }
        return isOk;
    }

    private void newTimeout(String key,String value) {
        //添加续期任务
        TIMER_TASK.newTimeout(timeout -> {
            //不停的重置时间，什么时候退出
            if (!TIMEOUT_STATUS_CONTAINER.containsKey(key)) {
                return;
            }
            //重置时间
            resetLock(key,value);
        }, RedisLockConfig.EXPIRE_SEC*2/3, TimeUnit.SECONDS);
    }

    @Override
    public boolean tryLock(String key,String value) {
        List<String> keys = new ArrayList<String>();
        String lockKey = RedisLockConfig.LOCK_KET_PREFIX + key;
        keys.add(lockKey);

        String ret = stringRedisTemplate.execute(LOCK_SCRIPT,keys, value,RedisLockConfig.EXPIRE_SEC + "");
        log.info("TryLock Lock key is {},value is {},return value is {}. ",new Object[]{lockKey,value,ret});
        boolean isOk = "OK".equalsIgnoreCase(ret);
        //拿到锁，添加续期任务
        if (isOk) {
            //先设置上下文
            TIMEOUT_STATUS_CONTAINER.putIfAbsent(key,true);
            newTimeout(key,value);
        }
        return isOk;
    }

    public void releaseLock(String key,String value) {
        List<String> keys = new ArrayList<String>();
        String lockKey = RedisLockConfig.LOCK_KET_PREFIX + key;
        keys.add(lockKey);
        Long ret = stringRedisTemplate.execute(UNLOCK_SCRIPT, keys, value);
        log.info("ReleaseLock Lock key is {},value is {},return value is {}. ",new Object[]{lockKey,value,ret});
        //清理上下文
        clear(key);
    }

    private void clear(String key) {
        TIMEOUT_STATUS_CONTAINER.remove(key);
        KEYS_CONTAINER.remove(key);
    }
}
