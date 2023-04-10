package me.about.widget.trace.entity;

import com.alibaba.ttl.TransmittableThreadLocal;
import me.about.widget.trace.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ttl 框架解决线程池上下文
 *
 * @Author: hugo.zxh
 * @Date: 2022/03/09 14:38
 * @Description:
 */
public class ThreadLocalTrace {

    private static Logger logger = LoggerFactory.getLogger(ThreadLocalTrace.class);

    protected static final TransmittableThreadLocal<TraceEntity> threadBoundEntity = TransmittableThreadLocal.withInitial(() -> new TraceEntity());

    public static View getView() {
        return ThreadLocalTrace.threadBoundEntity.get().view;
    }

    public static void reset() {
        ThreadLocalTrace.threadBoundEntity.remove();
    }

    public static void finish() {
        if (--threadBoundEntity.get().deep == 0) {
            logger.info(ThreadLocalTrace.getView().draw());
            ThreadLocalTrace.reset();
        }
    }

    public static void main(String[] args) {
        ThreadLocalTrace.getView().begin("com.juma.tgm.a");

        ThreadLocalTrace.getView().begin("com.juma.tgm.b");
        ThreadLocalTrace.getView().begin("com.juma.tgm.c");
        ThreadLocalTrace.getView().begin("com.juma.tgm.c2");
        ThreadLocalTrace.getView().end();
        ThreadLocalTrace.getView().end();
        ThreadLocalTrace.getView().begin("com.juma.tgm.b");
        ThreadLocalTrace.getView().end();
        ThreadLocalTrace.getView().end();
        ThreadLocalTrace.getView().end();
        ThreadLocalTrace.getView().begin("com.juma.tgm.b");
        ThreadLocalTrace.getView().begin("com.juma.tgm.c");
        ThreadLocalTrace.getView().end();
        ThreadLocalTrace.getView().end();
        ThreadLocalTrace.getView().end();
        ThreadLocalTrace.finish();

        System.out.print(ThreadLocalTrace.getView().draw());

        ThreadLocalTrace.reset();
        ThreadLocalTrace.getView().draw();

        ThreadLocalTrace.reset();


    }

}
