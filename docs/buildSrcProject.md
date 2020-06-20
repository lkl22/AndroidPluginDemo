## buildSrc project

项目根目录下新建一个buildSrc模块，目录名称千万不要修改，这个是默认的plugin源码开发调试的模块，最好也不要作为其他模块使用。

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
**src/main/resources/META-INF/gradle-plugins/buildsrc-groovy-plugin.properties**
```properties
implementation-class=com.lkl.buildsrc.plugin.BuildSrcGroovyPlugin
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

### plugin使用

直接在工程模块的build.gradle文件中引用 apply plugin: 'plugin id'
```groovy
apply plugin: 'buildsrc-java-plugin'
apply plugin: 'buildsrc-groovy-plugin'
```