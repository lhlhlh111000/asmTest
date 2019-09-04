package com.pig.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class ASMTest implements Plugin<Project> {

    @Override
    void apply(Project project) {
        System.out.println("========================")
        System.out.println("hello gradle plugin!")
        System.out.println("========================")

        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new ASMTransform())

        System.out.println("custom transform end")
    }
}