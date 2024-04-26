package me.about.widget.trace.util;

/**
 * 线程工具类
 *
 * @author: hugo.zxh
 * @date: 2022/03/09 14:46
 * @description:
 */
public class ThreadUtils {

    public static String getThreadTitle(Thread currentThread) {
        return "thread_name=" + currentThread.getName() +
                ";id=" + Long.toHexString(currentThread.getId()) +
                ";is_daemon=" + currentThread.isDaemon() +
                ";priority=" + currentThread.getPriority() +
                ";TCCL=" + getTCCL(currentThread);
    }

    private static String getTCCL(Thread currentThread) {
        if (null == currentThread.getContextClassLoader()) {
            return "null";
        }
        return currentThread.getContextClassLoader().getClass().getName() + "@" + Integer.toHexString(currentThread.getContextClassLoader().hashCode());
    }

}
