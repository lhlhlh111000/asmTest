package com.pig.plugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClickMethodVisitor extends MethodVisitor {

    public ClickMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM6, mv);
    }

    @Override
    public void visitInsn(int opcode) {
        if(opcode == Opcodes.RETURN) {
            System.out.println("找到click方法");
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "com/pig/android/asm/Tracker",
                    "click",
                    "(Landroid/view/View;)V",
                    false);

        }
        super.visitInsn(opcode);
    }
}