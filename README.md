# AndroidPluginDemo

#### 介绍
Android plugin开发demo工程

#### 软件架构
软件架构说明


#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx

#### 使用说明

1.  xxxx
2.  xxxx
3.  xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)

Android plugin项目开发demo

## Packaging a plugin

您可以在3个地方放置插件的源代码

1. [buildScript](docs/buildScript.md)

您可以直接在构建脚本中包含插件的源代码。 这样的好处是，无需执行任何操作即可自动编译插件并将其包含在构建脚本的类路径中。 但是，该插件在构建脚本之外不可见，因此您不能在定义该构建脚本的外部重用该插件。

2. [buildSrcProject](docs/buildSrcProject.md)

您可以将插件的源代码放在*rootProjectDir/buildSrc/src/main/groovy*目录（或*rootProjectDir/buildSrc/src/main/java*或*rootProjectDir/buildSrc/src/main/kotlin*中，具体取决于您喜欢的语言）。 Gradle将负责编译和测试插件，并使其在构建脚本的类路径中可用。 该插件对当前构建使用的每个构建脚本都是可见的。 但是，它在构建外部不可见，因此您不能在定义该构建的外部重用该插件。

3. [Standalone project](docs/standaloneProject.md)

您可以为插件创建一个单独的项目。 这个项目产生并发布了一个JAR，您可以在多个版本中使用它并与他人共享。 通常，此JAR可能包含一些插件，或将几个相关的任务类捆绑到一个库中。 或两者的某种组合。


## 参考文献
[Developing Custom Gradle Plugins](https://docs.gradle.org/5.6.4/userguide/custom_plugins.html)