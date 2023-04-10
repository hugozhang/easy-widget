package me.about.widget.lock;

import io.netty.util.HashedWheelTimer;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/23 15:04
 * @Description:
 */
public class Main {

    HashedWheelTimer timer = new HashedWheelTimer();

    int i = 0;

    public void print(String key) {
        System.out.println(key);
        timer.newTimeout(timeout -> {
            print("Hello test");
            i++;
        }, 3, TimeUnit.SECONDS);

        System.out.print(timer.pendingTimeouts());
    }

    @Test
    public void test() throws InterruptedException {

        timer.newTimeout(timeout -> print("Hello test"), 3, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(100);
    }
}
