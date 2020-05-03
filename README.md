# AndroidPluginDemo
Android plugin项目开发demo

## Packaging a plugin

您可以在3个地方放置插件的源代码

1. [buildScript](docs/buildScript.md)

您可以直接在构建脚本中包含插件的源代码。 这样的好处是，无需执行任何操作即可自动编译插件并将其包含在构建脚本的类路径中。 但是，该插件在构建脚本之外不可见，因此您不能在定义该构建脚本的外部重用该插件。

2. buildSrc project

您可以将插件的源代码放在*rootProjectDir/buildSrc/src/main/groovy*目录（或*rootProjectDir/buildSrc/src/main/java*或*rootProjectDir/buildSrc/src/main/kotlin*中，具体取决于您喜欢的语言）。 Gradle将负责编译和测试插件，并使其在构建脚本的类路径中可用。 该插件对当前构建使用的每个构建脚本都是可见的。 但是，它在构建外部不可见，因此您不能在定义该构建的外部重用该插件。

3. Standalone project

您可以为插件创建一个单独的项目。 这个项目产生并发布了一个JAR，您可以在多个版本中使用它并与他人共享。 通常，此JAR可能包含一些插件，或将几个相关的任务类捆绑到一个库中。 或两者的某种组合。


## 参考文献
[Developing Custom Gradle Plugins](https://docs.gradle.org/5.6.4/userguide/custom_plugins.html)