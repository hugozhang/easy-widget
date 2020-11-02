package me.about.widget.http;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/02 9:50
 * @Description:
 */
public class Main {

    @Test
    public void postForm() throws IOException {
        String asString = OkHttp.builder().url("http://www.baidu.com").form("q", "腾讯").file("asda", new File("pom.xml")).form().asString();
        System.out.println(asString);
    }

    @Test
    public void postJson() throws IOException {
        String asString = OkHttp.builder().url("http://www.baidu.com").body(new Object()).post().asString();
        System.out.println(asString);
    }

    @Test
    public void get() throws IOException {
        String asString = OkHttp.builder().url("http://www.baidu.com").get().asString();
        System.out.println(asString);
    }

}
