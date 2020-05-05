package com.lkl.buildsrc.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class BuildSrcJavaPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        System.out.println( "BuildSrcJavaPlugin: " + project.getName());
    }
}
