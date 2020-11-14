package me.about.widget.lock;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/14 10:27
 * @Description:
 */
public interface LockOperation {

    boolean reset(String key);

    boolean tryLock(String key);

    void releaseLock(String key);

}
