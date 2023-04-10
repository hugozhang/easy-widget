package me.about.widget.lock;

/**
 * 锁操作
 *
 * @author: hugo.zxh
 * @date: 2020/11/14 10:27
 * @description:
 */
public interface LockOperation {

    /**
     * 重置锁
     * @param key
     * @param value
     * @return
     */
    boolean resetLock(String key,String value);

    /**
     * 获取锁
     * @param key
     * @param value
     * @return
     */
    boolean tryLock(String key,String value);

    /**
     * 释放锁
     * @param key
     * @param value
     */
    void releaseLock(String key,String value);

}
