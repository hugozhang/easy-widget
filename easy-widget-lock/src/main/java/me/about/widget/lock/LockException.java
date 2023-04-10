package me.about.widget.lock;

/**
 * 锁异常通知
 *
 * @author: hugo.zxh
 * @date: 2020/11/14 18:13
 * @description:
 */
public class LockException extends RuntimeException {

    public LockException(String message,String lockKey,String owner) {
        super(message + ",Lock Key:" + lockKey + ",Owner:" + owner);
    }

}
