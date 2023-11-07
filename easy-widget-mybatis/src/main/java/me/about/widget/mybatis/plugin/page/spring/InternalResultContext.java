package me.about.widget.mybatis.plugin.page.spring;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hugo.zxh
 * @date: 2023/11/07 10:26
 */
public class InternalResultContext {

    private static final ThreadLocal<InternalResult> INTERNAL_RESULT = new ThreadLocal<>();

    public static void setResult(InternalResult internalResult) {
        INTERNAL_RESULT.set(internalResult);
    }

    public static InternalResult getResult() {
        return INTERNAL_RESULT.get();
    }

    public static void clear() {
        INTERNAL_RESULT.remove();
    }

}
