package me.about.widget.lock;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/14 18:13
 * @Description:
 */
public class LockException extends RuntimeException {

    public LockException(String message,String lockKey,String owner) {
        super(message + ",Lock Key:" + lockKey + ",Owner:" + owner);
    }

}
