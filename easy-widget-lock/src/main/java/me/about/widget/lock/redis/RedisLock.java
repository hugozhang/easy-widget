package me.about.widget.lock.redis;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/12 14:36
 * @Description: 基于JDK @java.util.concurrent.locks.Lock 的实现
 */
@Slf4j
class RedisLock implements Lock {

    private String lockKey;

    private RedisLockContext redisLockContext;

    public RedisLock(RedisLockContext redisLockContext,String lockKey) {
        this.redisLockContext = redisLockContext;
        this.lockKey = lockKey;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        checkInterruption();
        if (tryLock()) {
            return;
        }
        for (; ; ) {
            if (tryLock()) {
                return;
            }
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(50));
            checkInterruption();
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        checkInterruption();
        if (tryLock()) {
            return true;
        }
        long deadline = System.nanoTime() + unit.toNanos(time);
        for (; ; ) {
            if (tryLock()) {
                return true;
            }
            if (deadline <= System.nanoTime()) {// 超时
                return false;
            }
            checkInterruption();
        }
    }

    @Override
    public void lock() {
        if (tryLock()) {
            return;
        }
        for (; ; ) {
            if (tryLock()) {
                return;
            }
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(50));
        }
    }

    @Override
    public boolean tryLock() {
        checkNull();
        return redisLockContext.getRedisLockOperation().lock(lockKey);
    }

    @Override
    public void unlock() {
        checkNull();
        redisLockContext.getRedisLockOperation().unlock(lockKey);
    }

    private void checkNull() {
        assert lockKey != null;
        assert redisLockContext.getRedisLockOperation() != null;
    }

    private void checkInterruption() throws InterruptedException {
        if(Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
