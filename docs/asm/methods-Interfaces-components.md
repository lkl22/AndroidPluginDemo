# Interfaces and components

* [Presentation](#Presentation)

## <a name="Presentation">Presentation</a>

用于生成和转换已编译方法的ASM API基于`MethodVisitor`抽象类，后者由`ClassVisitor`的`visitMethod`方法返回。除了一些与注解和调试信息相关的方法，此类根据这些指令的参数数量和类型为每个字节码指令类别定义一个方法。必须按以下顺序调用这些方法（在MethodVisitor接口的Javadoc中指定一些附加约束）：

```asm
visitAnnotationDefault?
( visitAnnotation | visitParameterAnnotation | visitAttribute )*
( visitCode
    ( visitTryCatchBlock | visitLabel | visitFrame | visitXxxInsn |
        visitLocalVariable | visitLineNumber )*
    visitMaxs )?
visitEnd
```

这意味着对于非抽象方法，必须首先访问注解和属性，然后访问方法的字节码。对于这些方法，必须按顺序访问代码，在一次调用`visitCode`和一次调用`visitmax`之间。

The MethodVisitor class:

```asm
abstract class MethodVisitor { // public accessors ommited
    MethodVisitor(int api);
    MethodVisitor(int api, MethodVisitor mv);
    AnnotationVisitor visitAnnotationDefault();
    AnnotationVisitor visitAnnotation(String desc, boolean visible);
    AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible);
    void visitAttribute(Attribute attr);
    void visitCode();
    void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack);
    void visitInsn(int opcode);
    void visitIntInsn(int opcode, int operand);
    void visitVarInsn(int opcode, int var);
    void visitTypeInsn(int opcode, String desc);
    void visitFieldInsn(int opc, String owner, String name, String desc);
    void visitMethodInsn(int opc, String owner, String name, String desc);
    void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs);
    void visitJumpInsn(int opcode, Label label);
    void visitLabel(Label label);
    void visitLdcInsn(Object cst);
    void visitIincInsn(int var, int increment);
    void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels);
    void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels);
    void visitMultiANewArrayInsn(String desc, int dims);
    void visitTryCatchBlock(Label start, Label end, Label handler, String type);
    void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index);
    void visitLineNumber(int line, Label start);
    void visitMaxs(int maxStack, int maxLocals);
    void visitEnd();
}
```

`visitCode`和`VisitMax`方法可以用于在一系列事件中检测方法字节码的开始和结束。与类一样，`visitEnd`方法必须最后调用，并用于检测事件序列中方法的结尾。

可以组合ClassVisitor和MethodVisitor类，以生成完整的类：

```asm
ClassVisitor cv = ...;
cv.visit(...);
MethodVisitor mv1 = cv.visitMethod(..., "m1", ...);
mv1.visitCode();
mv1.visitInsn(...);
...
mv1.visitMaxs(...);
mv1.visitEnd();
MethodVisitor mv2 = cv.visitMethod(..., "m2", ...);
mv2.visitCode();
mv2.visitInsn(...);
...
mv2.visitMaxs(...);
mv2.visitEnd();
cv.visitEnd();
```

注意，不需要完成一个方法才可以开始访问另一个方法。事实上，`MethodVisitor`实例是完全独立的，可以按任何顺序使用（只要`visitEnd()`未被调用）：

```asm
ClassVisitor cv = ...;
cv.visit(...);
MethodVisitor mv1 = cv.visitMethod(..., "m1", ...);
mv1.visitCode();
mv1.visitInsn(...);
...
MethodVisitor mv2 = cv.visitMethod(..., "m2", ...);
mv2.visitCode();
mv2.visitInsn(...);
...
mv1.visitMaxs(...);
mv1.visitEnd();
...
mv2.visitMaxs(...);
mv2.visitEnd();
cv.visitEnd();
```

ASM基于`MethodVisitor` API提供三个核心组件，用于生成和转换方法：

* **`ClassReader`**类解析已编译方法的内容，并调用由`ClassVisitor`作为参数传递给其accept方法的`MethodVisitor`对象上的相应方法。
* **`ClassWriter`**的`visitMethod`方法返回`MethodVisitor`接口的实现，该接口直接以二进制形式构建已编译的方法。
* **`MethodVisitor`**类将它接收到的所有方法调用委托给另一个`MethodVisitor`实例。它可以看作是一个事件过滤器。

**ClassWriter options**

计算一个方法的堆栈映射帧不是很容易的：你必须计算所有帧，找到与跳转目标相对应或遵循无条件跳转的帧，最后压缩这些剩余的帧。同样，计算方法的局部变量和操作数堆栈部分的大小更容易，但仍然不是很容易。

希望ASM可以为您计算。创建类编写器时，可以指定必须自动计算的内容：

* 使用`new ClassWriter(0)`，不会自动计算任何内容。你必须自己计算帧、局部变量和操作数堆栈大小。
* 使用`new ClassWriter(ClassWriter.COMPUTE_MAXS)`将为您计算局部变量和操作数堆栈部分的大小。**您仍然必须调用`visitmax`，但可以使用任何参数：它们将被忽略并重新计算。使用此选项，您仍然需要自己计算帧。**
* 使用`new ClassWriter(ClassWriter.COMPUTE_FRAMES)`一切都是自动计算。你不必调用`visitFrame`，但你必须仍然调用`visitmax`（参数将被忽略并重新计算）。

使用这些选项很方便，但也有代价：`COMPUTE_MAXS`选项会使类编写器慢10%，而使用`COMPUTE_FRAMES`选项会使它慢两倍。必须将其与自己计算所需的时间进行比较：在特定情况下，与ASM中使用的算法相比，通常有更简单、更快的算法来计算此值，后者必须处理所有情况。

请注意，如果您选择自己计算帧，可以让ClassWriter类为您执行压缩步骤。为此，您只需使用`visitFrame(F_NEW, nLocals, locals, nStack, stack)`访问未压缩的帧，其中`nLocals`和`nStack`是局部变量的数量和操作数堆栈的大小，`locals`和`stack`是包含相应类型的数组（有关更多详细信息，请参阅Javadoc）。

还要注意，为了自动计算帧，有时需要计算两个给定类的公共超类。默认情况下，`ClassWriter`类在`getCommonSuperClass`方法中通过将两个类加载到JVM中并使用反射API来计算。如果要生成多个互相引用的类，这可能是个问题，因为被引用的类可能还不存在。在这种情况下，您可以重写`getCommonSuperClass`方法来解决这个问题。


