# AndroidPluginDemo
Android plugin项目开发demo

## Packaging a plugin

您可以在3个地方放置插件的源代码

1. [buildScript](docs/buildScript.md)

您可以直接在构建脚本中包含插件的源代码。 这样的好处是，无需执行任何操作即可自动编译插件并将其包含在构建脚本的类路径中。 但是，该插件在构建脚本之外不可见，因此您不能在定义该构建脚本的外部重用该插件。

2. [buildSrcProject](docs/buildSrcProject.md)

您可以将插件的源代码放在*rootProjectDir/buildSrc/src/main/groovy*目录（或*rootProjectDir/buildSrc/src/main/java*或*rootProjectDir/buildSrc/src/main/kotlin*中，具体取决于您喜欢的语言）。 Gradle将负责编译和测试插件，并使其在构建脚本的类路径中可用。 该插件对当前构建使用的每个构建脚本都是可见的。 但是，它在构建外部不可见，因此您不能在定义该构建的外部重用该插件。

3. [Standalone project](docs/standaloneProject.md)

您可以为插件创建一个单独的项目。 这个项目产生并发布了一个JAR，您可以在多个版本中使用它并与他人共享。 通常，此JAR可能包含一些插件，或将几个相关的任务类捆绑到一个库中。 或两者的某种组合。

## plugin开发指南

### 使插件可配置

1. 建一个java bean类承载配置元素
```java
/**
 * 自定义扩展配置
 */
public class CustomExtension {
    /**
     * 出错时中断编译
     */
    private boolean abortOnError = true;
    /**
     * 是否允许Log
     */
    private boolean enableLog = true;
    /**
     * 是否开启Debug。Debug模式会输出更详细的Log。
     */
    private boolean enableDebug = false;

    public boolean isAbortOnError() {
        return abortOnError;
    }

    public void setAbortOnError(boolean abortOnError) {
        this.abortOnError = abortOnError;
    }

    public boolean isEnableLog() {
        return enableLog;
    }

    public void setEnableLog(boolean enableLog) {
        this.enableLog = enableLog;
    }

    public boolean isEnableDebug() {
        return enableDebug;
    }

    public void setEnableDebug(boolean enableDebug) {
        this.enableDebug = enableDebug;
    }
}
```
2. 在自定义插件的apply方法里create Extension
```java
public class CustomPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        System.out.println( "CustomPlugin start: " + project.getName());
        CustomExtension extension = project.getExtensions()
                .create("CustomPlugin", CustomExtension.class);
    }
}
```
3. 给自定义plugin配置设置值
```groovy
apply plugin: 'com.lkl.standaloneplugin.custom-plugin'

CustomPlugin {
    enableDebug true // 或者 enableDebug = true
}
```
4. Transform里获取配置 project.getExtensions().findByType(CustomExtension.class)，然后可以正常拿到配置内容从而可以愉快的玩耍了
```java
@Override
public void transform(TransformInvocation invocation) {
    CustomLogger.info(TRANSFORM + " start...");
    long ms = System.currentTimeMillis();
    // project.getExtensions().findByName(Const.NAME);
    CustomExtension customExtension = project.getExtensions().findByType(CustomExtension.class);

    CustomLogger.info(TRANSFORM + "customExtension: %s", customExtension.toString());
}
```

### [自定义Transform](./docs/gradleTransform.md)

Gradle Transform是Android官方提供给开发者在项目构建阶段即由class到dex转换期间修改class文件的一套api。目前比较经典的应用是字节码插桩、代码注入技术。

### [ASM](./docs/asm.md)

`ASM`是一个通用的Java字节码操作和分析框架。它可以直接以二进制形式用于修改现有类或动态生成类。ASM提供了一些常见的字节码转换和分析算法，可以从中构建定制的复杂转换和代码分析工具。ASM提供了与其他Java字节码框架类似的功能，但是侧重于 **性能**。因为它的设计和实现是尽可能的小和尽可能快，所以它非常适合在动态系统中使用（但当然也可以以静态方式使用，例如在编译器中）。

> ASM 可以直接产生二进制 class 文件，也可以在类被加载入 Java 虚拟机之前动态改变类行为。Java class 被存储在严格格式定义的 .class 文件里，这些类文件拥有足够的元数据来解析类中的所有元素：类名称、方法、属性以及 Java 字节码（指令）。ASM 从类文件中读入信息后，能够改变类行为，分析类信息，甚至能够根据用户要求生成新类。说白了asm是直接通过字节码来修改class文件。

## 参考文献
[Developing Custom Gradle Plugins](https://docs.gradle.org/5.6.4/userguide/custom_plugins.html)

[Transform API](https://google.github.io/android-gradle-dsl/javadoc/current/)

[Asm官方文档](https://asm.ow2.io/developer-guide.html)

[ASM Bytecode Outline](https://plugins.jetbrains.com/plugin/5918-asm-bytecode-outline)

[一文让你明白Java字节码](https://www.jianshu.com/p/13d18c631549)

[Android Gradle Plugin打包Apk过程中的Transform API](https://www.jianshu.com/p/811b0d0975ef)

[Android Gradle Plugin 源码解析（上）](https://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649492752&idx=1&sn=1d1ad65c63667d96b72452a492cbde58)

[Android Gradle Plugin 源码解析（下）](https://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649492778&idx=1&sn=bf18cd9b7e4fb5f08b1a698836807304)
