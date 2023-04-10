package me.about.widget.trace.strategy.asm;

import me.about.widget.trace.util.PkgUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加载器
 *
 * @author: hugo.zxh
 * @date: 2022/03/09 17:50
 * @description:
 */
public class ModuleClassLoader extends ClassLoader {

    private static final String CLASS_FILE_SUFFIX = ".class";


    /** Packages that are excluded by default. */
    public static final String[] DEFAULT_EXCLUDED_PACKAGES = new String[]
            {"java.", "javax.", "sun.", "oracle.", "javassist.", "org.aspectj.", "net.sf.cglib."};

    /**
     * 需要排除的包
     */
    private final Set<String> excludedPackages = Collections.newSetFromMap(new ConcurrentHashMap<>(8));

    private final Set<String> excludedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(8));

    private final Set<String> overridePackages = Collections.newSetFromMap(new ConcurrentHashMap<>(8));


    public ModuleClassLoader(){
        for (String packageName : DEFAULT_EXCLUDED_PACKAGES) {
            excludePackage(packageName);
        }
    }

    public void excludePackage(String packageName) {
        this.excludedPackages.add(packageName);
    }

    public void overridePackage(String packageName) {
        this.overridePackages.add(packageName);
    }

    public void load() throws ClassNotFoundException {
        for (String pkg : overridePackages ) {
            Set<String> classes = PkgUtils.getClzFromPkg(pkg);
            for ( String clz : classes ) {
                loadClass(clz);
            }
        }
    }

    protected InputStream openStreamForClass(String name) {
        String internalName = name.replace('.', '/') + CLASS_FILE_SUFFIX;
        return getParent().getResourceAsStream(internalName);
    }

    protected Class<?> loadClassForOverriding(String name) throws ClassNotFoundException {
        Class<?> result = findLoadedClass(name);
        if (result == null) {
            byte[] bytes = enhanceTraceCode(loadBytesForClass(name));
            if (bytes != null) {
                result = defineClass(name, bytes, 0, bytes.length);
            }
        }
        return result;
    }

    protected byte[] loadBytesForClass(String name) throws ClassNotFoundException {
        InputStream is = openStreamForClass(name);
        if (is == null) {
            return null;
        }
        try {
            // Load the raw bytes.
            return FileCopyUtils.copyToByteArray(is);
        }
        catch (IOException ex) {
            throw new ClassNotFoundException("Cannot load resource for class [" + name + "]", ex);
        }
    }

    protected static byte[] enhanceTraceCode(byte[] clazzByte) {
        ClassReader reader = new ClassReader(clazzByte);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor visitor = new SuperClassVisitor(writer);
        reader.accept(visitor, ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
    }

    /**
     * 覆盖双亲委派机制
     *
     * @see ClassLoader#loadClass(String, boolean)
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (isEligibleForOverriding(name)) {
            Class<?> result = loadClassForOverriding(name);
            if (result != null) {
                return result;
            }
        }
        return super.loadClass(name);
    }

    /**
     * 判断该名字是否是需要覆盖的 class
     *
     * @param className
     * @return
     */
    protected boolean isEligibleForOverriding(String className) {
        return isIncluded(className);
    }

    /**
     * 判断class是否排除
     *
     * @param className
     * @return
     */
    protected boolean isIncluded(String className) {
//        if (this.excludedClasses.contains(className)) {
//            return true;
//        }
//        for (String packageName : this.excludedPackages) {
//            if (className.startsWith(packageName)) {
//                return true;
//            }
//        }
        for (String packageName : this.overridePackages) {
            if (className.startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }

}
