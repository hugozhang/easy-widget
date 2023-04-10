package me.about.widget.lock;

import java.util.concurrent.locks.Lock;

/**
 * 锁上下文
 *
 * @author: hugo.zxh
 * @date: 2020/11/14 10:26
 * @description:
 */
public interface LockContext {

    /**
     * 内部实现
     * @return
     */
    LockOperation getLockOperation();

    /**
     * 外部接口
     * @param key
     * @return
     */
    Lock getLock(String key);

}
