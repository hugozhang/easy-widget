package me.about.widget.id.redis;

/**
 * redis 分布式id操作
 *
 * @author: hugo.zxh
 * @date: 2020/11/18 17:46
 * @description:
 */
public class RedisIdConfig {

    public static final String ID_SCRIPT = "-- need redis 3.2+\n" +
            "redis.replicate_commands();\n" +
            "\n" +
            "local prefix = 'IdGen:';\n" +
            "local partitionCount = 4096;\n" +
            "local step = 2;\n" +
            "local startStep = 0;\n" +
            "\n" +
            "local tag = KEYS[1];\n" +
            "-- if user do not pass shardId, default partition is 0.\n" +
            "local partition\n" +
            "if KEYS[2] == nil then\n" +
            "  partition = 0;\n" +
            "else\n" +
            "  partition = KEYS[2] % partitionCount;\n" +
            "end\n" +
            "\n" +
            "local now = redis.call('TIME');\n" +
            "\n" +
            "local miliSecondKey = prefix .. tag ..':' .. partition .. ':' .. now[1] .. ':' .. math.floor(now[2]/1000);\n" +
            "\n" +
            "local count;\n" +
            "repeat\n" +
            "  count = tonumber(redis.call('INCRBY', miliSecondKey, step));\n" +
            "  if count > (1024 - step) then\n" +
            "      now = redis.call('TIME');\n" +
            "      miliSecondKey = prefix .. tag ..':' .. partition .. ':' .. now[1] .. ':' .. math.floor(now[2]/1000);\n" +
            "  end\n" +
            "until count <= (1024 - step)\n" +
            "\n" +
            "if count == step then\n" +
            "  redis.call('PEXPIRE', miliSecondKey, 5);\n" +
            "end\n" +
            "\n" +
            "-- second, microSecond, partition, seq\n" +
            "return {tonumber(now[1]), tonumber(now[2]), partition, count + startStep}";

}
