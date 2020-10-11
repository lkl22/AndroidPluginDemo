# ASM

* [ASM 6 Developer Guide](#ASM6DeveloperGuide)
* [字节码指令](#字节码指令)
* [Methods](#Methods)
* [参考文献](#参考文献)

## <a name="ASM6DeveloperGuide">[ASM 6 Developer Guide](./developer-guide.md)</a>

本指南主要面向希望对ASM代码库做出贡献的ASM用户，但其他用户也可能对此感兴趣。它解释了代码的组织，主要的数据结构和最复杂的算法。本文还解释了用于优化ASM性能以及最小化其代码大小的策略，并通过一个具体的示例说明了这一点。

## <a name="字节码指令">[字节码指令](./bytecodeInstructions.md)</a>

本节简要介绍字节码指令。为了完整描述，请参阅Java虚拟机规范。

约定：a和b表示int、float、long或double值（例如:IADD为int，LADD为long），o和p表示objet引用，v表示任何值（or，对于堆栈指令，是大小为1的值），w表示long或double，i、j和n表示int值。

## <a name="Methods">[Methods](./methods.md)</a>

说明如何使用核心ASM API生成和转换编译的方法。它首先介绍已编译的方法，然后介绍了要生成的相应ASM接口、组件和工具并用许多示例对它们进行转换。

## <a name="参考文献">参考文献</a>

[User guide](https://asm.ow2.io/asm4-guide.pdf)

[Asm官方文档](https://asm.ow2.io)

[https://asm.ow2.io/javadoc/overview-summary.html](https://asm.ow2.io/javadoc/overview-summary.html)
