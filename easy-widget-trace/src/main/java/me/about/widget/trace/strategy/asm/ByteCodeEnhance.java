package me.about.widget.trace.strategy.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * asm 字节码增强
 *
 * @author: hugo.zxh
 * @date: 2022/03/09 14:50
 * @description:
 */

public class ByteCodeEnhance {

    private static Logger logger = LoggerFactory.getLogger(ByteCodeEnhance.class);

    private static Method findResource;

    private static Method defineClass;

    static {
        try {
            Class<?> ll = ClassLoader.class;
            findResource = ll.getDeclaredMethod("findResource", new Class[] { java.lang.String.class });
            defineClass = ll.getDeclaredMethod("defineClass", new Class[] { java.lang.String.class, byte[].class, int.class, int.class });
            findResource.setAccessible(true);
            defineClass.setAccessible(true);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    public static void inject(String className, ClassLoader classLoader) {
        try {
            URL url = (URL) findResource.invoke(classLoader, new Object[] { className.replace('.','/') + ".class" });
            if (url == null) {
                logger.debug("{} not found.", className);
                return;
            }
            byte[] b = getClassByte(url);
            b = injectByteCode(b);
            if (b == null || b.length <= 0) {
                logger.debug("Inject {} failure.", className);
                return;
            }
            Class<?> clazz = defineClass(className, b, classLoader);
            if (clazz == null) {
                logger.debug("Add injected class({}) to {} failure.", className, classLoader);
                return;
            }
            logger.debug("Added asm enhance function for {}.", className);
        } catch (IOException e) {
            logger.error(className,e);
        } catch (IllegalAccessException e) {
            logger.error(className,e);
        } catch (InvocationTargetException e) {
            logger.error(className,e);
        }
    }

    private static byte[] injectByteCode(byte[] clazzByte) {
        ClassReader reader = new ClassReader(clazzByte);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor visitor = new SuperClassVisitor(writer);
        reader.accept(visitor, ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
    }

    private static byte[] getClassByte(URL url) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = url.openStream();
            byte[] byteChunk = new byte[4096];
            int n;
            while ((n = is.read(byteChunk)) > 0) {
                byteArrayOutputStream.write(byteChunk, 0, n);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static Class<?> defineClass(String className, byte[] clazzByte, ClassLoader classLoader) throws IllegalAccessException, InvocationTargetException {
        return (Class<?>) defineClass.invoke(classLoader, new Object[] { className, clazzByte, 0, clazzByte.length });
    }

}
