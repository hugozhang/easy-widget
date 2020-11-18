package me.about.widget.lock.redis;

import lombok.extern.slf4j.Slf4j;
import me.about.widget.lock.LockContext;
import me.about.widget.lock.LockOperation;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.locks.Lock;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/12 20:29
 * @Description:
 */
@Slf4j
public class RedisLockContext implements LockContext {

    private LockOperation redisLockOperation;

    public RedisLockContext(StringRedisTemplate stringRedisTemplate) {
        this.redisLockOperation = new RedisLockOperation(stringRedisTemplate);
    }

    @Override
    public LockOperation getLockOperation() {
        return redisLockOperation;
    }

    public Lock getLock(String lockKey) {
        return new RedisLock(this,lockKey);
    }

}
