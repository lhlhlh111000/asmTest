package com.pig.plugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ASMClassVisitor extends ClassVisitor {

    private String className;
    private String superName;
    private String[] interfaces;

    public ASMClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM6, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
        this.superName = superName;
        this.interfaces = interfaces;
        super.visit(version, access, name, signature, superName, interfaces);
        System.out.println("AAAAAAA: " + name);
        System.out.println("AAAAAAA: " + superName);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if("com/pig/android/asm/MainActivity".equals(className) && name.equals("onCreate")) {
            return new ASMMethodVisitor(mv);
        }
        if(isInterfaceContainer("android/view/View$OnClickListener") && name.equals("onClick")) {
            return new ClickMethodVisitor(mv);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    private boolean isInterfaceContainer(String interfaceStr) {
        if(null == interfaces || interfaces.length <= 0) {
            return false;
        }

        for(String s : interfaces) {
            if(s.contains(interfaceStr)) {
                return true;
            }
        }

        return false;
    }
}