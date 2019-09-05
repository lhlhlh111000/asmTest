package com.pig.plugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ASMMethodVisitor extends MethodVisitor {

    public ASMMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM6, mv);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        System.out.println("hello from onCreate visitCode");
    }

    @Override
    public void visitInsn(int opcode) {
        if(opcode == Opcodes.RETURN) {
            System.out.println("Opcodes is return");
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitLdcInsn("hello");
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/widget/Toast", "makeText", "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/widget/Toast", "show", "()V", false);
        }
        super.visitInsn(opcode);
    }
}