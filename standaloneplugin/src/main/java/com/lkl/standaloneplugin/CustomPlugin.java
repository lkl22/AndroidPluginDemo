package com.lkl.standaloneplugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class CustomPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        System.out.println( "CustomPlugin: " + project.getName());
    }
}
