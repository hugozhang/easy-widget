package me.about.widget.lock.redis;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/12 20:25
 * @Description: 常量
 */
abstract class RedisLockConfig {

    /**
     * 设置锁的过期时间10min 单位:秒
     */
    public static final long EXPIRE_SEC = 600;

    /**
     * 锁前缀
     */
    public static final String LOCK_KET_PREFIX = "LOCK:";

    /**
     * EX seconds  PX milliseconds
     * 成功返回OK 失败返回null
     */
    public static final String LOCK_SCRIPT = "return redis.call('SET', KEYS[1], ARGV[1], 'NX', 'EX', ARGV[2])";

    /**
     * 成功返回1 失败返回0
     */
    public static final String UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then\n" +
            "    return redis.call('del', KEYS[1])\n" +
            "else\n" +
            "    return 0\n" +
            "end";

    /**
     * 重置过期时间，相当于续期
     */
    public static final String RESET_SCRIPT = "return redis.call('SET', KEYS[1], ARGV[1], 'EX', ARGV[2])";

}
