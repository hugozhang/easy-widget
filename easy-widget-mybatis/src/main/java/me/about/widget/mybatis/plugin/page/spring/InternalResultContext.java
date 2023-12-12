package me.about.widget.mybatis.plugin.page.spring;

/**
 * 内部使用 分页结果上下文
 *
 * @author: hugo.zxh
 * @date: 2023/11/07 10:26
 */
public class InternalResultContext {

    private static final ThreadLocal<InternalResult<?>> INTERNAL_RESULT = new ThreadLocal<>();

    public static void setResult(InternalResult<?> internalResult) {
        INTERNAL_RESULT.set(internalResult);
    }

    public static InternalResult<?> getResult() {
        return INTERNAL_RESULT.get();
    }

    public static void clear() {
        INTERNAL_RESULT.remove();
    }

}
