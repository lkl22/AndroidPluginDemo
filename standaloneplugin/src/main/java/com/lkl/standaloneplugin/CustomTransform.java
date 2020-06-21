package com.lkl.standaloneplugin;

import com.android.SdkConstants;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.variant.VariantInfo;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.google.common.collect.ImmutableSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.gradle.api.Project;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class CustomTransform extends Transform {
    protected final Project project;

    private static final String TRANSFORM = "Transform: ";

    public CustomTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return Const.NAME;
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
    public boolean applyToVariant(VariantInfo variant) {
        CustomLogger.info(TRANSFORM + " applyToVariant FullVariantName: %s, BuildTypeName: %s, Debuggable: %s, Test: %s",
                variant.getFullVariantName(),
                variant.getBuildTypeName(),
                variant.isDebuggable(),
                variant.isTest());
        return variant.isDebuggable();
    }

    @Override
    public void transform(TransformInvocation invocation) {
        CustomLogger.info(TRANSFORM + "start...");
        long ms = System.currentTimeMillis();
//        project.getExtensions().findByName(Const.NAME);
        CustomExtension customExtension = project.getExtensions().findByType(CustomExtension.class);

        CustomLogger.info(TRANSFORM + "customExtension: %s", customExtension.toString());

        CustomLogger.info(TRANSFORM + "cost %s ms", System.currentTimeMillis() - ms);
    }


}
