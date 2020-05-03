## 构建脚本plugin

### 简单的例子

要创建Gradle插件，您需要编写一个实现Plugin接口的类。 将插件应用于项目后，Gradle会创建插件类的实例，并调用该实例的Plugin.apply()方法。 项目对象作为参数传递，插件可以使用该参数配置项目，但需要配置。 下面的示例包含一个Greeting插件，该插件向项目添加了hello任务。

```groovy
class GreetingPlugin implements Plugin<Project> {
    void apply(Project project) {
        // 创建一个task hello       
        project.task('hello') {
            doLast {
                // 打印输出log
                println 'Hello from the GreetingPlugin'
            }
        }
    }
}

// Apply the plugin
apply plugin: GreetingPlugin
```
Output of **./gradlew -q hello**

```shell script
abc:AndroidPluginDemo likunlun$ ./gradlew -q hello
Hello from the GreetingPlugin
```
要注意的一件事是，会为每个应用插件的项目创建一个插件的新实例。
另请注意，Plugin类是泛型类型。 此示例接收 [项目](https://docs.gradle.org/5.6.4/dsl/org.gradle.api.Project.html) 类型作为类型参数。
插件可以改为接收类型为 [Settings](https://docs.gradle.org/5.6.4/dsl/org.gradle.api.initialization.Settings.html) 的参数（在这种情况下，该插件可以应用在设置脚本中）或参数类型为 [Gradle](https://docs.gradle.org/5.6.4/dsl/org.gradle.api.invocation.Gradle.html) 的参数，在这种情况下，可以将插件应用在初始化脚本中。

### 使插件可配置

大多数插件为构建脚本提供了一些配置选项，其他插件则可以自定义插件的工作方式。
插件使用扩展对象执行此操作。
Gradle项目具有关联的ExtensionContainer对象，该对象包含已应用于项目的插件的所有设置和属性。
您可以通过向该容器添加扩展对象来为您的插件提供配置。 扩展对象只需一个简单的Java
Bean属性对象表示。

让我们向项目添加一个简单的扩展对象。 在这里，我们将问候语扩展对象添加到项目中，该对象使您可以配置问候语。

```groovy
class GreetingPluginExtension {
    String message = 'Hello from GreetingPlugin'
}

class GreetingPlugin implements Plugin<Project> {
    void apply(Project project) {
        // Add the 'greeting' extension object
        def extension = project.extensions.create('greeting', GreetingPluginExtension)
        // Add a task that uses configuration from the extension object
        project.task('hello') {
            doLast {
                println extension.message
            }
        }
    }
}

// Apply the plugin
apply plugin: GreetingPlugin

// Configure the extension
greeting.message = 'Hi from Gradle'
```
Output of **./gradlew -q hello**
```shell script
abc:AndroidPluginDemo likunlun$ ./gradlew -q hello
Hi from Gradle
```
在此示例中，GreetingPluginExtension是一个具有名为message的属性的对象。
扩展对象将以greeting名称添加到项目中。
然后，该对象可用作与扩展对象同名的项目属性。

通常，您需要在单个插件上指定多个相关属性。
Gradle为每个扩展对象添加一个配置块，因此您可以将分组设置放在一起。

