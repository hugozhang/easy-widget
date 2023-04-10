package me.about.widget.trace.entity;

/**
 * Trace
 *
 * @author: hugo.zxh
 * @date: 2022/03/09 14:38
 * @description:
 */
public class Trace {

    public static void enter(String message) {
        ThreadLocalTrace.getView().begin(message);
        ThreadLocalTrace.threadBoundEntity.get().deep++;
    }

    public static void exit() {
        ThreadLocalTrace.getView().end();
        ThreadLocalTrace.finish();
    }

    public static void exit(String message) {
        ThreadLocalTrace.getView().end(message);
        ThreadLocalTrace.finish();
    }

}
