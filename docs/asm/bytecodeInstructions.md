# 字节码指令 Bytecode instructions

* [简介](#简介)
* [Local variables](#Localvariables)
* [Stack](#Stack)
* [Constants](#Constants)
* [Arithmetic and logic](#Arithmeticandlogic)
* [Casts](#Casts)
* [Objects, fields and methods](#Objectsfieldsandmethods)
* [Arrays](#Arrays)
* [Jumps](#Jumps)
* [Return](#Return)

## <a name="简介">简介</a>

字节码指令由标识该指令的操作码以及固定数量的参数构成

* 操作码是一个无符号字节值，因此是字节码名称，并由助记符号标识。例如，操作码值0是由助记符NOP标识的，与不执行任何操作的指令相对应。
* 参数是定义精确指令行为的静态值。它们在操作码之后给出。例如，操作码值为167的 `GOTO label `指令将 `label `作为参数，指定下一条要执行的指令的标签。**不要将指令参数与指令操作数混淆：参数值是静态已知的，并存储在编译的代码中，而操作数值来自操作数堆栈，只有在运行时才知道。**

字节码指令可分为两类：
* 一小部分指令用于将值从局部变量传输到操作数堆栈，反之亦然；
* 其他指令仅作用于操作数堆栈：它们从堆栈中弹出一些值，根据这些值计算结果，然后将其推回到堆栈中。

局部变量与操作数堆栈传输指令：
*  `ILOAD `、 `LLOAD `、 `FLOAD `、 `DLOAD `和 `ALOAD `指令读取局部变量并将其值推送到操作数堆栈上。它们将必须读取的局部变量的索引  `i ` 作为参数。
*  `ILOAD `用于加载 `boolean `、 `byte `、 `char `、 `short `或 `int `局部变量。
*  `LLOAD `、 `FLOAD `和 `DLOAD `分别用于加载 `long `、 `float `或 `double `值（ `LLOAD `和 `DLOAD `实际加载两个插槽 `i `和 `i+1 `）。
*  `ALOAD `用于加载任何非基元值，即对象和数组引用。
*  `ISTORE `、 `LSTORE `、 `FSTORE `、 `DSTORE `和 `ASTORE `指令对称地从操作数堆栈中弹出一个值，并将其存储在由其索引 `i `指定的局部变量中。

如您所见，`xLOAD`和`xSTORE`指令都是成对的（实际上，正如您将在下面看到的，几乎所有的指令都是成对的）。这用于确保不进行非法转换。实际上，**在局部变量中存储一个值，然后用不同的类型加载它是非法的。** 例如，`ISTORE 1` `ALOAD 1`序列是非法的。

它允许在本地变量1中存储任意内存地址，并将此地址转换为对象引用！这意味着局部变量的类型，即存储在该局部变量中的值的类型，可以在方法执行期间发生变化。

如上所述，所有其他字节码指令只在操作数堆栈上工作。它们可以分为以下几类：

* **[Stack](#Stack)** 这些指令用于操作堆栈上的值：`POP`弹出堆栈顶部的值，`DUP`将顶部堆栈值的副本压栈，`SWAP`弹出两个值并按相反的顺序推送它们，等等。 
* **[Constants](#Constants)** 这些指令在操作数堆栈上push一个常量值：`ACONST_NULL` pushes null, `ICONST_0` pushes the int value 0, `FCONST_0` pushes 0f, `DCONST_0` pushes 0d, `BIPUSH b` pushes the byte value b, `SIPUSH s` pushes the short value s, `LDC cst` pushes the arbitrary int, float, long, double, String, or class constant cst, etc.
* **[Arithmetic and logic](#Arithmeticandlogic)** 这些指令从操作数堆栈中弹出数值，将它们组合起来并将结果推送到堆栈上。他们没有任何参数。`xADD`、`xSUB`、`xMUL`、`xDIV`和`xREM`对应于+、-、*、/和%运算，其中x是I、L、F或D。类似地，对于int值和long值，还有其他与<<，>>，>|，&和^对应的指令。
* **[Casts](#Casts)** 这些指令从堆栈中弹出一个值，将其转换为另一个类型，并将结果推入。它们对应于Java中的强制转换表达式。`I2F`、`F2D`、`L2D`等。将数值从一种数值类型转换为另一种数值类型。`CHECKCAST t` 将引用值转换为类型t。
* **[Objects](#Objectsfieldsandmethods)** 这些指令用于创建对象、锁定对象、测试其类型等。例如，`NEW type`指令将类型为type的新对象push到堆栈上（其中type是内部名称）。
* **[Fields](#Objectsfieldsandmethods)** 这些指令读取或写入字段的值。`GETFIELD owner name desc`弹出一个对象引用，并推送其name字段的值。`PUTFIELD owner name desc`弹出一个值和一个对象引用，并将该值存储在其name字段中。在这两种情况下，对象必须是owner类型，并且其字段必须是desc类型。`GETSTATIC`和`PUTSTATIC`是类似的指令，但对于静态字段。
* **[Methods](#Objectsfieldsandmethods)** 这些指令调用方法或构造函数。它们弹出与方法参数相同的值，再加上目标对象的一个值，并推送方法调用的结果。`INVOKEVIRTUAL owner name desc`调用类所有者中定义的name方法，其方法描述符为desc。`INVOKESTATIC`用于静态方法，`INVOKESPECIAL`用于私有方法和构造函数，`INVOKEINTERFACE`用于接口中定义的方法。最后，对于Java7类，`INVOKEDYNAMIC`用于新的动态方法调用机制。
* **[Arrays](#Arrays)** 这些指令用于读取和写入数组中的值。`xALOAD`指令弹出一个索引和一个数组，并将数组元素的值推送到该索引处。`xASTORE`指令弹出一个值、一个索引和一个数组，并将该值存储在数组中的该索引处。这里x可以是I、L、F、D或A，也可以是B、C或S。
* **[Jumps](#Jumps)** 如果某些条件为真或无条件，这些指令将跳转到任意指令。它们用于编译if、for、do、while、break和continue指令。例如，`IFEQ label`从堆栈中弹出一个int值，如果该值为0，则跳到`label`设计的指令（否则将正常执行到下一条指令）。存在许多其他跳转指令，例如`IFNE`或`IFGE`。最后，`TABLESWITCH`和`LOOKUPSWITCH`对应于switch Java指令。
* **[Return](#Return)** 使用`xRETURN`和`RETURN`指令终止方法的执行并将其结果返回给调用方。`RETURN`用于返回**void**的方法，`xRETURN`用于其他方法。

## <a name="Localvariables">Local variables</a>

|Instruction |Stack before |Stack after
|---|---|---
|ILOAD, LLOAD, FLOAD, DLOAD var |... |... , a
|ALOAD var |... |... , o
|ISTORE, LSTORE, FSTORE, DSTORE var |... , a |...
|ASTORE var |... , o |...
|IINC var incr |... |...

## <a name="Stack">Stack</a>

<table>
	<tr>
	    <th>Instruction</th>
	    <th>Stack before</th>
	    <th>Stack after</th>  
	</tr >
	<tr>
    	<td >POP</td>
    	<td>... , v</td>
    	<td>...</td>
    </tr>
	<tr >
	    <td rowspan="2">POP2</td>
	    <td>... , v1 , v2</td>
	    <td>...</td>
	</tr>
	<tr>
	    <td>... , w</td>
	    <td>...</td>
	</tr>
	<tr>
        <td >DUP</td>
        <td>... , v </td>
        <td>... , v , <b>v</b></td>
    </tr>
    <tr >
    	<td rowspan="2">DUP2</td>
    	<td>... , v1 , v2</td>
    	<td>... , v1 , v2 , <b>v1 , v2</b></td>
    </tr>
    <tr>
    	<td>... , w</td>
    	<td>... , w, <b>w</b></td>
    </tr>
    <tr>
        <td >SWAP</td>
        <td>... , v1 , v2</td>
        <td>... , v2 , v1</td>
    </tr>
    <tr>
        <td >DUP_X1</td>
        <td>... , v1 , v2</td>
        <td>... , <b>v2</b> , v1 , v2</td>
    </tr>
    <tr >
    	<td rowspan="2">DUP_X2</td>
    	<td>... , v1 , v2 , v3</td>
    	<td>... , <b>v3</b> , v1 , v2 , v3</td>
    </tr>
    <tr>
    	<td>... , w , v</td>
    	<td>... , <b>v</b> , w , v</td>
    </tr>
    <tr >
    	<td rowspan="2">DUP2_X1</td>
    	<td>... , v1 , v2 , v3</td>
    	<td>... , <b>v2 , v3</b> , v1 , v2 , v3</td>
    </tr>
    <tr>
    	<td>... , v , w</td>
    	<td>... , <b>w</b> , v , w</td>
    </tr>
    <tr >
    	<td rowspan="4">DUP2_X2</td>
    	<td>... , v1 , v2 , v3 , v4</td>
    	<td>... , <b>v3 , v4 </b>, v1 , v2 , v3 , v4</td>
    </tr>
    <tr>
    	<td>... , w , v1 , v2</td>
    	<td>... , <b>v1 , v2 </b>, w , v1 , v2</td>
    </tr>
    <tr>
    	<td>.... , v1 , v2 , w</td>
    	<td>... , <b>w </b>, v1 , v2 , w</td>
    </tr>
    <tr>
    	<td>.... , w1 , w2</td>
    	<td>... , <b>w2 </b>, w1 , w2</td>
    </tr>
</table>

## <a name="Constants">Constants</a>

Instruction |Stack before |Stack after
---|---|---
ICONST_n (−1 <= n <= 5) |... |... , n
LCONST_n (0 <= n <= 1) |... |... , nL
FCONST_n (0 <= n <= 2) |... |... , nF
DCONST_n (0 <= n <= 1) |... |... , nD
BIPUSH b, −128 <= b < 127 |... |... , b
SIPUSH s, −32768 <= s < 32767 |... |... , s
LDC cst (int, float, long, double, String or Type) |... |... , cst
ACONST_NULL |... |... , null

## <a name="Arithmeticandlogic">Arithmetic and logic</a>

Instruction |Stack before |Stack after
---|---|---
IADD, LADD, FADD, DADD |... , a , b |... , a + b
ISUB, LSUB, FSUB, DSUB |... , a , b |... , a - b
IMUL, LMUL, FMUL, DMUL |... , a , b |... , a * b
IDIV, LDIV, FDIV, DDIV |... , a , b |... , a / b
IREM, LREM, FREM, DREM |... , a , b |... , a % b
INEG, LNEG, FNEG, DNEG |... , a |... , -a
ISHL, LSHL |... , a , n |... , a << n
ISHR, LSHR |... , a , n |... , a >> n
IUSHR, LUSHR |... , a , n |... , a >>> n
IAND, LAND |... , a , b |... , a & b
IOR, LOR |... , a , b |... , a | b
IXOR, LXOR |... , a , b |... , a ^ b
LCMP |... , a , b |... , a == b ? 0 : (a < b ? -1 : 1)   
FCMPL, FCMPG |... , a , b |... , a == b ? 0 : (a < b ? -1 : 1)
DCMPL, DCMPG |... , a , b |... , a == b ? 0 : (a < b ? -1 : 1)

## <a name="Casts">Casts</a>

Instruction |Stack before |Stack after
---|---|---
I2B |... , i |... , (byte) i
I2C |... , i |... , (char) i
I2S |... , i |... , (short) i
L2I, F2I, D2I |... , a |... , (int) a
I2L, F2L, D2L |... , a |... , (long) a
I2F, L2F, D2F |... , a |... , (float) a
I2D, L2D, F2D |... , a |... , (double) a
CHECKCAST class |... , o |... , (class) o

## <a name="Objectsfieldsandmethods">Objects, fields and methods</a>

Instruction |Stack before |Stack after
---|---|---
NEW class |... |... , new class
GETFIELD c f t |... , o |... , o.f
PUTFIELD c f t |... , o , v |...
GETSTATIC c f t |... |... , c.f
PUTSTATIC c f t |... , v |...
INVOKEVIRTUAL c m t |... , o , v1 , ... , vn |... , o.m(v1, ... vn)
INVOKESPECIAL c m t |... , o , v1 , ... , vn |... , o.m(v1, ... vn)
INVOKESTATIC c m t |... , v1 , ... , vn |... , c.m(v1, ... vn)
INVOKEINTERFACE c m t |... , o , v1 , ... , vn |... , o.m(v1, ... vn)
INVOKEDYNAMIC m t bsm |... , o , v1 , ... , vn |... , o.m(v1, ... vn)
INSTANCEOF class |... , o |... , o instanceof class
MONITORENTER |... , o |...
MONITOREXIT |... , o |...

## <a name="Arrays">Arrays</a>

Instruction |Stack before |Stack after
---|---|---
NEWARRAY type (for any primitive type) |... , n |... , new type[n]
ANEWARRAY class |... , n |... , new class[n]
MULTIANEWARRAY [...[t n |... , i1 , ... , in |... , new t[i1]...[in]...
BALOAD, CALOAD, SALOAD |... , o , i |... , o[i]
IALOAD, LALOAD, FALOAD, DALOAD |... , o , i |... , o[i]
AALOAD |... , o , i |... , o[i]
BASTORE, CASTORE, SASTORE |... , o , i , j |...
IASTORE, LASTORE, FASTORE, DASTORE |... , o , i , a |...
AASTORE |... , o , i , p |...
ARRAYLENGTH |... , o |... , o.length

## <a name="Jumps">Jumps</a>

Instruction |Stack before |Stack after
---|---|---
IFEQ |... , i |... jump if i == 0
IFNE |... , i |... jump if i != 0
IFLT |... , i |... jump if i < 0
IFGE |... , i |... jump if i >= 0
IFGT |... , i |... jump if i > 0
IFLE |... , i |... jump if i <= 0
IF_ICMPEQ |... , i , j |... jump if i == j
IF_ICMPNE |... , i , j |... jump if i != j
IF_ICMPLT |... , i , j |... jump if i < j
IF_ICMPGE |... , i , j |... jump if i >= j
IF_ICMPGT |... , i , j |... jump if i > j
IF_ICMPLE |... , i , j |... jump if i <= j
IF_ACMPEQ |... , o , p |... jump if o == p
IF_ACMPNE |... , o , p |... jump if o != p
IFNULL |... , o |... jump if o == null
IFNONNULL |... , o |... jump if o != null
GOTO |... |... jump always
TABLESWITCH |... , i |... jump always
LOOKUPSWITCH |... , i |... jump always

## <a name="Return">Return</a>

Instruction |Stack before |Stack after
---|---|---
IRETURN, LRETURN, FRETURN, DRETURN |... , a|
ARETURN |... , o|
RETURN |...|
ATHROW |... , o|
