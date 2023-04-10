package me.about.widget.trace.util;

import me.about.widget.trace.entity.ClassField;
import me.about.widget.trace.entity.MethodArg;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hugo.zxh
 * @date: 2022/03/09 14:34
 * @description:
 */
public class AsmUtils {

    private static Logger logger = LoggerFactory.getLogger(AsmUtils.class);

    public static Map<String, MethodArg> readMethodArg(String owner) {
        try {
            return readArg(owner);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return new HashMap(16);
    }

    public static Map<String, ClassField> readClassField(String owner) {
        try {
            return readField(owner);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return new HashMap(16);
    }

    private static Map<String, ClassField> readField(String owner) throws IOException {
        final Map<String,ClassField> classFieldMap = new HashMap(16);
        ClassReader cr = new ClassReader(owner);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cr.accept(new ClassVisitor(Opcodes.ASM5,cw) {
            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                ClassField classField = new ClassField();
                classField.name = name;
                classField.desc = descriptor;
                classFieldMap.put(name,classField);
                return super.visitField(access, name, descriptor, signature, value);
            }
        }, 0);
        return classFieldMap;
    }

    private static Map<String, MethodArg> readArg(String owner) throws IOException {
        final Map<String, MethodArg> argMap = new HashMap(16);
        ClassReader cr = new ClassReader(owner);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cr.accept(new ClassVisitor(Opcodes.ASM5,cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM5,mv) {
                    @Override
                    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
                        MethodArg arg = new MethodArg();
                        arg.name = name;
                        arg.desc = desc;
                        arg.index = index;
                        argMap.put(name,arg);
                        super.visitLocalVariable(name, desc, signature, start, end, index);
                    }
                };
            }
        }, 0);
        return argMap;
    }
}
