
import me.about.widget.trace.entity.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2022/03/09 14:58
 * @Description:
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {


//        ThreadLocalTrace.getView().begin("com.juma.tgm.a");
//
//        ThreadLocalTrace.getView().begin("com.juma.tgm.b");
//        ThreadLocalTrace.getView().begin("com.juma.tgm.c");
//        ThreadLocalTrace.getView().begin("com.juma.tgm.c2");
//        ThreadLocalTrace.getView().end();
//        ThreadLocalTrace.getView().end();
//        ThreadLocalTrace.getView().begin("com.juma.tgm.b");
//        ThreadLocalTrace.getView().end();
//        ThreadLocalTrace.getView().end();
//        ThreadLocalTrace.getView().end();
//        ThreadLocalTrace.getView().begin("com.juma.tgm.b");
//        ThreadLocalTrace.getView().begin("com.juma.tgm.c");
//        ThreadLocalTrace.getView().end();
//        ThreadLocalTrace.getView().end();
//        ThreadLocalTrace.getView().end();
//        ThreadLocalTrace.finish();
//
//        System.out.print(ThreadLocalTrace.getView().draw());
//
//        ThreadLocalTrace.reset();
//        ThreadLocalTrace.getView().draw();
//
//        ThreadLocalTrace.reset();


        Trace.enter("com.juma.a");
        Trace.enter("com.juma.b");
        Trace.exit();
        Trace.exit();


        Enumeration<URL> resources = Main.class.getClassLoader().getResources("me/about/trace"); //这里只是在找资源
        System.out.println(resources);

//        new PathScanProvider().scan("me.about.trace");
//        User user = new User();
//        user.setName("Java");
//        user.setA(123456789);
//        System.out.println(user.getClass().getClassLoader());
//


        //        System.out.println(isMatch("com/juma/*/a","com/juma/b/a"));

//        new PathScanProvider().scan("about/me/**/entity");


    }

}
