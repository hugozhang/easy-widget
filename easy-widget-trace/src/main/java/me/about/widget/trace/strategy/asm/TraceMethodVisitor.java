package me.about.widget.trace.strategy.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hugo.zxh
 * @date: 2022/03/09 14:48
 * @description:
 */
public class TraceMethodVisitor extends AdviceAdapter {

    private String owner;

    private String methodName;

    public TraceMethodVisitor(MethodVisitor mv, int access, String owner, String methodName, String methodDesc) {
        super(Opcodes.ASM5,mv,access,methodName,methodDesc);
        this.owner = owner;
        this.methodName = methodName;
    }

    @Override
    public void onMethodEnter() {
        //trace
        push(this.owner.replace('/', '.') + "." + this.methodName);
        visitMethodInsn(Opcodes.INVOKESTATIC, "me/about/widget/trace/entity/Trace", "enter", "(Ljava/lang/String;)V", false);
    }


    /**
     * 方法退出的时候都会执行它（包括if,exception,正常退出）
     * 如果方法里面有try{if(true){throw new RuntimeException("123");}}catch(Exception e){throw e;} 会执行两次exit
     * @param opcode
     */
    @Override
    public void onMethodExit(int opcode) {
        //不能放在visitEnd()
        if (opcode == Opcodes.ATHROW) {
            dup();
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Throwable", "getMessage", "()Ljava/lang/String;", false);
            visitMethodInsn(Opcodes.INVOKESTATIC, "me/about/widget/trace/entity/Trace", "exit", "(Ljava/lang/String;)V", false);
        } else {
            visitMethodInsn(Opcodes.INVOKESTATIC, "me/about/widget/trace/entity/Trace", "exit", "()V", false);
        }
    }

    public static void main(String[] args) {
        System.out.println(Type.getInternalName(TimeUnit.class));
    }
}
