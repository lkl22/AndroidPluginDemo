package com.lkl.standaloneplugin;

import com.android.build.gradle.BaseExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class CustomPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        System.out.println("CustomPlugin start: " + project.getName());
        CustomExtension extension = project.getExtensions()
                .create(Const.NAME, CustomExtension.class);

        project.getExtensions().findByType(BaseExtension.class)
                .registerTransform(new CustomTransform(project));

        project.afterEvaluate(p -> {
            System.out.println("CustomPlugin project.afterEvaluate ");
            CustomLogger.setConfig(extension);
        });

        System.out.println("CustomPlugin apply end!");
    }
}
