package me.about.widget.lock.redis;

/**
 * redis 锁操作脚本
 *
 * @author: hugo.zxh
 * @date: 2020/11/12 20:25
 * @description: 常量
 */
public class RedisLockConfig {

    /**
     * 单位:秒
     */
    public static final long EXPIRE_SEC = 30;

    /**
     * 锁前缀
     */
    public static final String LOCK_KET_PREFIX = "LOCK:";

    /**
     * EX seconds  PX milliseconds
     * 成功返回OK 失败返回null
     * Key不存在的时候才设置过期时间
     */
    public static final String LOCK_SCRIPT = "return redis.call('SET', KEYS[1], ARGV[1], 'NX', 'EX', ARGV[2])";

    /**
     * Key存在的时候才重置过期时间
     */
    public static final String RESET_SCRIPT = "return redis.call('SET', KEYS[1], ARGV[1], 'XX', 'EX', ARGV[2])";

    /**
     * 成功返回1 失败返回0
     */
    public static final String UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then\n" +
            "    return redis.call('del', KEYS[1])\n" +
            "else\n" +
            "    return 0\n" +
            "end";

    /**
     * 重置过期时间最大次数
     */
    public static final int RESET_MAX_COUNT = 10;

}
