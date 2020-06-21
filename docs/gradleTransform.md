# 自定义Transform

## 基础概念

术语|说明
---|---
TransformInput|所谓Transform就是对输入的class文件转变成目标字节码文件，TransformInput就是这些输入文件的抽象。目前它包括两部分：DirectoryInput集合与JarInput集合。
DirectoryInput|它代表着以源码方式参与项目编译的所有目录结构及其目录下的源码文件，可以借助于它来修改输出文件的目录结构、目标字节码文件。
JarInput|它代表着以jar包方式参与项目编译的所有本地jar包或远程jar包，可以借助于它来动态添加jar包。
TransformOutputProvider|它代表的是Transform的输出，例如可以通过它来获取输出路径。

## Transform API

使用Transform API主要是写一个类继承Transform，并把该Transform注入到打包过程中。注入Transform很简单，先获取com.android.build.gradle.BaseExtension对象，然后调用它的registerTransform()方法。
```java
public class CustomPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        project.getExtensions().findByType(BaseExtension.class)
                .registerTransform(new CustomTransform(project));
    }
}
```

### Transform常用API：
方法|说明
---|---
String getName()|用于指明本Transform的名字，也是代表该Transform的task的名字。
Set<QualifiedContent.ContentType> getInputTypes()|用于指明Transform的输入类型，可以作为输入过滤的手段。
Set<? super QualifiedContent.Scope> getScopes()|用于指明Transform的作用域。
boolean isIncremental()|用于指明是否是增量构建。
void transform(TransformInvocation invocation)|执行Transform方法，Transform处理逻辑的地方。
boolean applyToVariant(@NonNull VariantInfo variant)|是否应将此Transform应用于给定的variant，可以区分渠道使用Transform。

### ContentType
ContentType是一个接口，默认有一个枚举类型DefaultContentType实现了ContentType，包含有CLASSES和RESOURCES类型。

类型|说明
---|---
CLASSES|表示的是在jar包或者文件夹中的.class文件。
RESOURCES|表示的是标准的Java资源文件。

Android Plugin扩展的ContentType -> ExtendedContentType:

类型|说明
---|---
DEX|The content is dex files.
NATIVE_LIBS|Content is a native library.
CLASSES_ENHANCED|Instant Run '$override' classes, which contain code of new method bodies.此流还包含用于应用HotSwap更改的AbstractPatchesLoaderImpl类。
DATA_BINDING|The content is an artifact exported by the data binding compiler.
JAVA_SOURCES|The content is Java source file. @Deprecated don't use!
DEX_ARCHIVE|The content is a dex archive. It contains a single DEX file per class.

### Scope 作用范围
Scope类型|说明
---|---
PROJECT	|只处理当前的项目（模块）
SUB_PROJECTS	|只处理子项目（模块）
EXTERNAL_LIBRARIES	|只处理外部的依赖库
TESTED_CODE	|只处理测试代码
PROVIDED_ONLY	|只处理provided-only的依赖库
PROJECT_LOCAL_DEPS	|只处理当前项目的本地依赖,例如jar, aar（过期，被EXTERNAL_LIBRARIES替代）
SUB_PROJECTS_LOCAL_DEPS	|只处理子项目的本地依赖,例如jar, aar（过期，被EXTERNAL_LIBRARIES替代）

Transform中的getInputTypes()方法和getScopes() 方法返回的是Set集合，因此这些类型是可以进行组合的。在TransformManager中就包含了多种Set集合。

```java
package com.android.build.gradle.internal.pipeline;

/**
 * Manages the transforms for a variant.
 *
 * <p>The actual execution is handled by Gradle through the tasks.
 * Instead it's a means to more easily configure a series of transforms that consume each other's
 * inputs when several of these transform are optional.
 */
public class TransformManager extends FilterableStreamCollection {

    private static final boolean DEBUG = true;

    private static final String FD_TRANSFORMS = "transforms";

    public static final Set<ScopeType> EMPTY_SCOPES = ImmutableSet.of();

    public static final Set<ContentType> CONTENT_CLASS = ImmutableSet.of(CLASSES);
    public static final Set<ContentType> CONTENT_JARS = ImmutableSet.of(CLASSES, RESOURCES);
    public static final Set<ContentType> CONTENT_RESOURCES = ImmutableSet.of(RESOURCES);
    public static final Set<ContentType> CONTENT_NATIVE_LIBS =
            ImmutableSet.of(NATIVE_LIBS);
    public static final Set<ContentType> CONTENT_DEX = ImmutableSet.of(ExtendedContentType.DEX);
    public static final Set<ContentType> CONTENT_DEX_WITH_RESOURCES =
            ImmutableSet.of(ExtendedContentType.DEX, RESOURCES);
    public static final Set<ScopeType> PROJECT_ONLY = ImmutableSet.of(Scope.PROJECT);
    public static final Set<ScopeType> SCOPE_FULL_PROJECT =
            ImmutableSet.of(Scope.PROJECT, Scope.SUB_PROJECTS, Scope.EXTERNAL_LIBRARIES);
    public static final Set<ScopeType> SCOPE_FULL_WITH_FEATURES =
            new ImmutableSet.Builder<ScopeType>()
                    .addAll(SCOPE_FULL_PROJECT)
                    .add(InternalScope.FEATURES)
                    .build();
    public static final Set<ScopeType> SCOPE_FEATURES = ImmutableSet.of(InternalScope.FEATURES);
    public static final Set<ScopeType> SCOPE_FULL_LIBRARY_WITH_LOCAL_JARS =
            ImmutableSet.of(Scope.PROJECT, InternalScope.LOCAL_DEPS);
    public static final Set<ScopeType> SCOPE_FULL_PROJECT_WITH_LOCAL_JARS =
            new ImmutableSet.Builder<ScopeType>()
                    .addAll(SCOPE_FULL_PROJECT)
                    .add(InternalScope.LOCAL_DEPS)
                    .build();
```

### isIncremental
Transform的isIncremental()方法表示是否支持增量编译，返回true的话表示支持，这个时候可以根据TransformInput来获得**更改、移除或者添加**的文件目录或者jar包。

```java
package com.android.build.api.transform;

import com.android.annotations.NonNull;
import java.util.Collection;

/**
 * The input to a Transform.
 * <p>
 * It is mostly composed of a list of {@link JarInput} and a list of {@link DirectoryInput}.
 */
public interface TransformInput {

    /**
     * Returns a collection of {@link JarInput}.
     */
    @NonNull
    Collection<JarInput> getJarInputs();

    /**
     * Returns a collection of {@link DirectoryInput}.
     */
    @NonNull
    Collection<DirectoryInput> getDirectoryInputs();
}
```

#### JarInput

JarInput有一个方法是getStatus()来获取Status

```java
package com.android.build.api.transform;

import com.android.annotations.NonNull;
import java.util.Collection;

/**
 * A {@link QualifiedContent} of type jar.
 * <p>
 * This means the {@link #getFile()} is the jar file containing the content.
 * <p>
 * This also contains the incremental state of the jar file, if the transform is in incremental
 * mode through {@link #getStatus()}.
 * <p>
 * For a transform to run in incremental mode:
 * <ul>
 *     <li>{@link Transform#isIncremental()} must return <code>true</code></li>
 *     <li>The parameter <var>isIncremental</var> of
 *     {@link Transform#transform(Context, Collection, Collection, TransformOutputProvider, boolean)}
 *     must be <code>true</code>.</li>
 * </ul>
 *
 * If the transform is not in incremental mode, {@link #getStatus()} always returns
 * {@link Status#NOTCHANGED}.
 */
public interface JarInput extends QualifiedContent {

    @NonNull
    Status getStatus();
}
```
Status是一个枚举类，包含了NOTCHANGED、ADDED、CHANGED、REMOVED，所以可以根据JarInput的status来对它进行相应的处理，比如添加或者移除。

```java
package com.android.build.api.transform;

/**
 * The file changed status for incremental execution.
 */
public enum Status {
    /**
     * The file was not changed since the last build.
     */
    NOTCHANGED,
    /**
     * The file was added since the last build.
     */
    ADDED,
    /**
     * The file was modified since the last build.
     */
    CHANGED,
    /**
     * The file was removed since the last build.
     */
    REMOVED;
}
```

#### DirectoryInput

DirectoryInput有一个方法getChangedFiles()开获取一个Map<File, Status>集合，所以可以遍历这个Map集合，然后根据File对应的Status来对File进行处理。

如果不支持增量编译，就在处理.class之前把之前的输出目录中的文件删除。

获取TransformInput对象是根据TransformInvocation:
```java
package com.android.build.api.transform;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import java.util.Collection;

/**
 * An invocation object used to pass of pertinent information for a
 * {@link Transform#transform(TransformInvocation)} call.
 */
public interface TransformInvocation {

    /**
     * Returns the context in which the transform is run.
     * @return the context in which the transform is run.
     */
    @NonNull
    Context getContext();

    /**
     * Returns the inputs/outputs of the transform.
     * @return the inputs/outputs of the transform.
     */
    @NonNull
    Collection<TransformInput> getInputs();

    /**
     * Returns the referenced-only inputs which are not consumed by this transformation.
     * @return the referenced-only inputs.
     */
    @NonNull Collection<TransformInput> getReferencedInputs();
    /**
     * Returns the list of secondary file changes since last. Only secondary files that this
     * transform can handle incrementally will be part of this change set.
     * @return the list of changes impacting a {@link SecondaryInput}
     */
    @NonNull Collection<SecondaryInput> getSecondaryInputs();

    /**
     * Returns the output provider allowing to create content.
     * @return he output provider allowing to create content.
     */
    @Nullable
    TransformOutputProvider getOutputProvider();


    /**
     * Indicates whether the transform execution is incremental.
     * @return true for an incremental invocation, false otherwise.
     */
    boolean isIncremental();
}
```

#### TransformInvocation

TransformInvocation包含了输入、输出相关信息。其输出相关内容是由TransformOutputProvider来做处理。TransformOutputProvider的getContentLocation()方法可以获取文件的输出目录，如果目录存在的话直接返回，如果不存在就会重新创建一个。例如：
```java
// getContentLocation方法相当于创建一个对应名称表示的目录
// 是从0 、1、2开始递增。如果是目录，名称就是对应的数字，如果是jar包就类似0.jar
File outputDir = transformInvocation.outputProvider.getContentLocation("include", 
         dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)

File outputJar = transformInvocation.outputProvider.getContentLocation(jarInput.name
        , jarInput.contentTypes
        , jarInput.scopes
        , Format.JAR)
```

在执行编译过程中会生成对应的目录，例如在app/build/intermediates/transforms目录下生成了一个名为CustomPlugin的目录，这个名称就是根据自定义的Transform类getName()方法返回的字符串来的。

```
transforms
    > CustomPlugin
        > debug
            > 0.jar
            > 1.jar
            ...
            > 39
            > __content__.json
```
CustomPlugin目录下还会有一个名为__content__的.json文件。该文件中展示了CustomPlugin中文件目录下的内容。

```json
[
  {
    "name": "androidx.localbroadcastmanager:localbroadcastmanager:1.0.0",
    "index": 37,
    "scopes": [
      "EXTERNAL_LIBRARIES"
    ],
    "types": [
      "CLASSES"
    ],
    "format": "JAR",
    "present": true
  },
  {
    "name": "cae395e225fd7e1a29b7e372dfac40c8d0d8f1ee",
    "index": 38,
    "scopes": [
      "PROJECT"
    ],
    "types": [
      "CLASSES"
    ],
    "format": "JAR",
    "present": true
  },
  {
    "name": "66d46f518ab0f2d4aa1a29cadd54ee980bbb1cb6",
    "index": 40,
    "scopes": [
      "PROJECT"
    ],
    "types": [
      "CLASSES"
    ],
    "format": "DIRECTORY",
    "present": true
  },
  {
    "name": "CustomPlugin",
    "index": 42,
    "scopes": [
      "PROJECT"
    ],
    "types": [
      "CLASSES"
    ],
    "format": "DIRECTORY",
    "present": false
  }
]
```




