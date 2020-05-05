package com.lkl.buildsrc.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildSrcGroovyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("BuildSrcGroovyPlugin ${project.path}")
    }
}