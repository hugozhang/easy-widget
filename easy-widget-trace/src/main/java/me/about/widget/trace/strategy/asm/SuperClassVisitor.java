package me.about.widget.trace.strategy.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;

/**
 * 类方法改写
 *
 * @author: hugo.zxh
 * @date: 2022/03/09 14:53
 * @description:
 */
public class SuperClassVisitor extends ClassVisitor {

    private String owner;

    private boolean isInterface;

    public SuperClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5,cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        isInterface = Modifier.isInterface(access);
        this.owner = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (isInterface || "<init>".equals(name) || "<clinit>".equals(name)
                || Modifier.isNative(access) || Modifier.isAbstract(access) || Modifier.isStatic(access) || !Modifier.isPublic(access)) {
            return mv;
        }

        /**
         * java.lang.IllegalArgumentException:
         *  LocalVariablesSorter only accepts expanded frames (see ClassReader.EXPAND_FRAMES)
         *  也可以用继承来实现 责任链式调用*
         */
        TraceMethodVisitor v1 = new TraceMethodVisitor(mv,access,owner,name, desc);
        return v1;
    }
}
