# 自定义Transform

## 基础概念

术语|说明
---|---
TransformInput|所谓Transform就是对输入的class文件转变成目标字节码文件，TransformInput就是这些输入文件的抽象。目前它包括两部分：DirectoryInput集合与JarInput集合。
DirectoryInput|它代表着以源码方式参与项目编译的所有目录结构及其目录下的源码文件，可以借助于它来修改输出文件的目录结构、目标字节码文件。
JarInput|它代表着以jar包方式参与项目编译的所有本地jar包或远程jar包，可以借助于它来动态添加jar包。
TransformOutputProvider|它代表的是Transform的输出，例如可以通过它来获取输出路径。

## Transform方法

方法|说明
---|---
String getName()|用于指明本Transform的名字，也是代表该Transform的task的名字。
Set<QualifiedContent.ContentType> getInputTypes()|用于指明Transform的输入类型，可以作为输入过滤的手段。
Set<? super QualifiedContent.Scope> getScopes()|用于指明Transform的作用域。
boolean isIncremental()|用于指明是否是增量构建。
void transform(TransformInvocation invocation)|执行Transform方法，Transform处理逻辑的地方。
boolean applyToVariant(@NonNull VariantInfo variant)|是否应将此Transform应用于给定的variant，可以区分渠道使用Transform。