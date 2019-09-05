package com.pig.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class ASMTransform extends Transform {

    @Override
    public String getName() {
        return "ASMTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        if(null != outputProvider) {
            outputProvider.deleteAll();
        }
        for(TransformInput input : inputs) {
            if(null != input.getDirectoryInputs()) {
                for(DirectoryInput directoryInput : input.getDirectoryInputs()) {
                    handleDirectoryInput(directoryInput, outputProvider);
                }
            }

            if(null != input.getJarInputs()) {
                for(JarInput jarInput : input.getJarInputs()) {
                    handleJarInput(jarInput, outputProvider);
                }
            }
        }
    }

    private void handleDirectoryInput(DirectoryInput input,
                                      TransformOutputProvider outputProvider) {
        System.out.println("handle dir input");
        System.out.println(input.getFile().getName());
        modifyFile(input.getFile(), input, outputProvider);
        File dest = outputProvider.getContentLocation(input.getName(),
                input.getContentTypes(), input.getScopes(),
                Format.DIRECTORY);
        try {
            FileUtils.copyDirectory(input.getFile(), dest);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void modifyFile(File f, DirectoryInput input,
                            TransformOutputProvider outputProvider) {
        if(!f.isDirectory()) {
            String name = f.getName();
            System.out.println(name);
            if (name.endsWith(".class") && f.getAbsolutePath().contains("com/pig/android/asm") && (!f.getAbsolutePath().contains("R$"))) {
                try {
                    ClassReader classReader = new ClassReader(FileUtils.readFileToByteArray(f));
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                    ClassVisitor cv = new ASMClassVisitor(classWriter);
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES);
                    byte[] code = classWriter.toByteArray();
                    FileOutputStream fos = new FileOutputStream(
                            f.getParentFile().getAbsoluteFile() + File.separator + name);
                    fos.write(code);
                    fos.close();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {
            for(File file : f.listFiles()) {
                modifyFile(file, input, outputProvider);
            }
        }
    }

    private void handleJarInput(JarInput input,
                                TransformOutputProvider outputProvider) {
        System.out.println("handle jar input");
        System.out.println(input.getFile().getName());
        try {
            if(input.getFile().getAbsolutePath().endsWith(".jar")) {
                String jarName = input.getName();
                String md5Name = DigestUtils.md5Hex(input.getFile().getAbsolutePath());
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4);
                }

//                JarFile jarFile = new JarFile(input.getFile());
//                Enumeration enumeration = jarFile.entries();
//                File tmpFile = new File(input.getFile().getParent() + File.separator + "classes_temp.jar");
//                //避免上次的缓存被重复插入
//                if (tmpFile.exists()) {
//                    tmpFile.delete();
//                }
//                JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile));

                File dest = outputProvider.getContentLocation(jarName + md5Name,
                        input.getContentTypes(), input.getScopes(), Format.JAR);
                FileUtils.copyFile(input.getFile(), dest);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
