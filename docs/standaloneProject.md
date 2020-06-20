## A standalone project

我们将插件移至独立项目，以便我们可以发布它并与他人共享。 这个项目只是一个Groovy项目，它产生一个包含插件类的JAR。 这是该项目的简单构建脚本。 它应用了Groovy插件，并将Gradle API添加为编译时依赖项。

自定义插件的构建:
``` groovy
apply plugin: 'groovy'

dependencies {
    implementation gradleApi()
    implementation localGroovy()
}
```

那么Gradle如何找到插件实现？ 答案是您需要在jar的 **META-INF/gradle-plugins**
目录中提供与插件ID相匹配的属性文件。

### Wiring for a custom plugin:
**src/main/resources/META-INF/gradle-plugins/com.lkl.standaloneplugin.custom-plugin.properties**
```properties
implementation-class=implementation-class=com.lkl.standaloneplugin.CustomPlugin
```
请注意，属性文件名与插件ID匹配，并放置在资源文件夹中，并且实现类属性标识插件实现类。该目录下可以放置多个文件，同时实现多个plugin。

### Creating a plugin id
插件ID以类似于Java软件包的方式（即反向域名）。 这有助于避免冲突，并提供了一种将具有相似所有权的插件分组的方法。

您的插件ID应该是反映命名空间（合理指向您或您的组织）的组件及其提供的插件名称的组合。
例如，如果您有一个名为“ foo”的Github帐户，而您的插件名为“
bar”，则合适的插件ID可能是com.github.foo.bar。
同样，如果插件是由baz组织开发的，则插件ID可能是org.baz.bar。

插件ID应符合以下条件：

* 可以包含任何字母数字字符“.”和“-”
* 必须至少包含一个“.”, 分隔命名空间和插件名称的字符
* 按照惯例，对命名空间使用小写的反向域名约定
* 通常，名称中仅使用小写字符
* 不能使用org.gradle和com.gradleware命名空间
* 不能以“.”字符开头或结尾
* 不能包含连续的“.” 字符（即“..”）

尽管插件ID与程序包名称之间存在常规的相似之处，但通常包名称比插件ID所需的名称更为详细。
例如，在您的插件ID中添加“gradle”作为组件似乎很合理，但是由于插件ID仅用于Gradle插件，因此这是多余的。
通常，一个良好的插件ID仅需要一个用于标识所有权和名称的命名空间。

### Publishing your plugin

如果要在内部发布插件供组织内部使用，则可以像其他任何代码工件一样发布。

如果您有兴趣发布您的插件以供更广泛的Gradle社区使用，则可以将其发布到[Gradle插件门户](https://plugins.gradle.org/)。
该站点提供了搜索和收集有关Gradle社区贡献的插件的信息的功能。
请参阅相应的指南，以了解如何在此站点上使用您的插件。

### Using your plugin in another project

要在构建脚本中使用插件，您需要将插件类添加到构建脚本的类路径中。 为此，请使用“
buildscript
{}”块，请参见[使用buildscript块应用插件](https://docs.gradle.org/5.6.4/userguide/plugins.html#sec:applying_plugins_buildscript)中所述。
以下示例显示了包含插件的JAR已发布到本地存储库时如何执行此操作：

```groovy
buildscript {
    repositories {
        maven {
            url = uri(repoLocation)
        }
    }
    dependencies {
        classpath 'org.gradle:customPlugin:1.0-SNAPSHOT'
    }
}

apply plugin: 'org.samples.greeting'
```

### Writing tests for your plugin

您可以使用[ProjectBuilder](https://docs.gradle.org/5.6.4/javadoc/org/gradle/testfixtures/ProjectBuilder.html)类创建在测试插件实现时要使用的[Project](https://docs.gradle.org/5.6.4/dsl/org.gradle.api.Project.html)实例。

Testing a custom plugin:

src/test/java/com/lkl/standaloneplugin/ExampleUnitTest.java

```java
public class ExampleUnitTest {
    @Test
    public void customPluginTest() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("com.lkl.standaloneplugin.custom-plugin");

        System.out.println(project.getDisplayName());
        assertEquals("root project 'test'", project.getDisplayName());
    }
}
```

### 开发调试技巧

建一个buildSrc plugin默认开发模块，在buildSrc模块的build.gradle里指定源码路径为该模块的源码位置。从而达到直接调试standaloneplugin源码的目的。
```groovy
if (isDebugCustomPlugin) {
    java.srcDirs += "${project.rootDir.parent}/standaloneplugin/src/main/java"
    resources.srcDirs += "${project.rootDir.parent}/standaloneplugin/src/main/resources"
    println "引用CustomPlugin模块源码"
}
```

plugin开发调试代码，可以参考：[编译时注解调试](https://github.com/lkl22/AndroidAnnotationDemo/blob/master/docs/CompileAnnotationDebug.md)，有一点需要注意的是第二步选择编译task的时候可以灵活的选择，可以根据我们plugin作用于编译时的哪个阶段来选择task，如果不清楚的话，可以直接选择全量最大化的编译task，比如：assemble task。