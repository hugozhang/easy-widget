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
        StringBuilder sb = new StringBuilder("thread_name=");
        sb.append(currentThread.getName())
                .append(";id=").append(Long.toHexString(currentThread.getId()))
                .append(";is_daemon=").append(currentThread.isDaemon())
                .append(";priority=").append(currentThread.getPriority())
                .append(";TCCL=").append(getTCCL(currentThread));
        return sb.toString();
    }

    private static String getTCCL(Thread currentThread) {
        if (null == currentThread.getContextClassLoader()) {
            return "null";
        }
        return currentThread.getContextClassLoader().getClass().getName() + "@" + Integer.toHexString(currentThread.getContextClassLoader().hashCode());
    }

}
