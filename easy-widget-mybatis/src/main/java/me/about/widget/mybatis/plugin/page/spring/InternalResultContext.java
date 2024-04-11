package me.about.widget.mybatis.plugin.page.spring;

import java.util.HashMap;
import java.util.Map;

/**
 * 内部使用 分页结果上下文
 *
 * @author: hugo.zxh
 * @date: 2023/11/07 10:26
 */
public class InternalResultContext {

    private static final ThreadLocal<Map<String,InternalResult<?>>> INTERNAL_RESULT = ThreadLocal.withInitial(HashMap::new);

    public static void setResult(String method,InternalResult<?> internalResult) {
        INTERNAL_RESULT.get().put(method,internalResult);
    }

    public static InternalResult<?> getResult(String method) {
        return INTERNAL_RESULT.get().get(method);
    }

    public static void clear() {
        INTERNAL_RESULT.remove();
    }

}
