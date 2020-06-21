package com.lkl.standaloneplugin;

import com.android.SdkConstants;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Status;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.variant.VariantInfo;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.build.gradle.internal.pipeline.TransformTask;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static jdk.nashorn.internal.objects.Global.print;


public class CustomTransform extends Transform {
    protected final Project project;

    private static final String TRANSFORM = "Transform: ";

    /**
     * Linux/Unix： com/sankuai/waimai/router/generated/service
     * Windows：    com\sankuai\waimai\router\generated\service
     */
    public static final String INIT_SERVICE_DIR = Const.GEN_PKG_SERVICE.replace('.', File.separatorChar);
    /**
     * com/sankuai/waimai/router/generated/service
     */
    public static final String INIT_SERVICE_PATH = Const.GEN_PKG_SERVICE.replace('.', '/');

    private static List<String> maps = new ArrayList<>();

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
        return true;
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

//        TransformTask transformTask = (TransformTask) invocation.getContext();
        //VariantCache 就是保存一些跟当前variant相关的一些缓存，以及在支持增量编译的情况下存储一些信息

        maps.add("ddd");
        CustomLogger.info(TRANSFORM + "maps sizes: %d", maps.size());
        if (invocation.isIncremental()) {
            //TODO 增量
            CustomLogger.info(TRANSFORM + "====================增量编译=================");
            for (TransformInput input : invocation.getInputs()) {
                input.getJarInputs().parallelStream().forEach(jarInput -> {
                    Status status = jarInput.getStatus();
                    CustomLogger.info(TRANSFORM + "change jar status: %s", status.name());
                });

                input.getDirectoryInputs().parallelStream().forEach(directoryInput -> {
                    Map<File, Status> changedFiles = directoryInput.getChangedFiles();
                    for (Map.Entry<File, Status> fileStatusEntry : changedFiles.entrySet()) {
                        CustomLogger.info(TRANSFORM + "change file path: %s, file status: %s",
                                fileStatusEntry.getKey().toString(), fileStatusEntry.getValue().name());
                    }
                });
            }
        } else {
            CustomLogger.info(TRANSFORM + "====================非增量编译=================");
            //非增量,需要删除输出目录
            try {
                invocation.getOutputProvider().deleteAll();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set<String> initClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());

            for (TransformInput input : invocation.getInputs()) {
                input.getJarInputs().parallelStream().forEach(jarInput -> {
                    File src = jarInput.getFile();
                    File dst = invocation.getOutputProvider().getContentLocation(
                            jarInput.getName(), jarInput.getContentTypes(), jarInput.getScopes(),
                            Format.JAR);
                    try {
                        scanJarFile(src, initClasses);
                        FileUtils.copyFile(src, dst);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                input.getDirectoryInputs().parallelStream().forEach(directoryInput -> {
                    File src = directoryInput.getFile();
                    File dst = invocation.getOutputProvider().getContentLocation(
                            directoryInput.getName(), directoryInput.getContentTypes(),
                            directoryInput.getScopes(), Format.DIRECTORY);
                    try {
                        scanDir(src, initClasses);
                        FileUtils.copyDirectory(src, dst);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            File dest = invocation.getOutputProvider().getContentLocation(
                    Const.NAME, TransformManager.CONTENT_CLASS,
                    ImmutableSet.of(QualifiedContent.Scope.PROJECT), Format.DIRECTORY);
//            generateServiceInitClass(dest.getAbsolutePath(), initClasses);
        }

        CustomLogger.info(TRANSFORM + "cost %s ms", System.currentTimeMillis() - ms);
    }

    /**
     * 扫描由注解生成器生成到包 {@link Const#GEN_PKG_SERVICE} 里的初始化类
     */
    private void scanJarFile(File file, Set<String> initClasses) throws IOException {
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.endsWith(SdkConstants.DOT_CLASS) && name.startsWith(INIT_SERVICE_PATH)) {
//                String className = trimName(name, 0).replace('/', '.');
//                initClasses.add(className);
//                CustomLogger.info("    find ServiceInitClass: %s", className);
            }
        }
    }

    /**
     * 扫描由注解生成器生成到包 {@link Const#GEN_PKG_SERVICE} 里的初始化类
     */
    private void scanDir(File dir, Set<String> initClasses) throws IOException {
        File packageDir = new File(dir, INIT_SERVICE_DIR);
        if (packageDir.exists() && packageDir.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(packageDir,
                    new SuffixFileFilter(SdkConstants.DOT_CLASS, IOCase.INSENSITIVE), TrueFileFilter.INSTANCE);
            for (File f : files) {
//                String className = trimName(f.getAbsolutePath(), dir.getAbsolutePath().length() + 1)
//                        .replace(File.separatorChar, '.');
//                initClasses.add(className);
//                CustomLogger.info("    find ServiceInitClass: %s", className);
            }
        }
    }
}
