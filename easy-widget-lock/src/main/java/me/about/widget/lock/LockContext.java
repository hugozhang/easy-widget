package me.about.widget.lock;

import java.util.concurrent.locks.Lock;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/14 10:26
 * @Description:
 */
public interface LockContext {

    LockOperation getLockOperation();

    Lock getLock(String key);

}
