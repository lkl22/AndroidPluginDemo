package com.lkl.buildsrc.plugin

import com.android.build.gradle.internal.api.DefaultAndroidSourceSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.plugins.JavaPluginConvention

class BuildSrcGroovyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("BuildSrcGroovyPlugin ${project.path}")

//        // 获取java模块源文件
//        JavaPluginConvention javaPluginConvention = project.getConvention().getPlugin(JavaPluginConvention.class)
//        javaPluginConvention.getSourceSets().getByName("main").java {
//            DefaultSourceDirectorySet directorySet ->
//                directorySet.srcDirs.forEach {
//                    println("src dir ${it.canonicalPath}")
//                }
//        }
//
//        // 获取Android模块源文件
//        project.extensions.getByName("android").sourceSets.forEach { DefaultAndroidSourceSet sourceSet ->
//            sourceSet.java.srcDirs.forEach {
//                println("src dir ${it.canonicalPath}")
//            }
//        }
    }
}