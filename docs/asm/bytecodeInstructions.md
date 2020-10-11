# 字节码指令 Bytecode instructions

* [Local variables](#Localvariables)
* [Stack](#Stack)
* [Constants](#Constants)
* [Arithmetic and logic](#Arithmeticandlogic)
* [Casts](#Casts)
* [Objects, fields and methods](#Objectsfieldsandmethods)
* [Arrays](#Arrays)
* [Jumps](#Jumps)
* [Return](#Return)


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
    	<td rowspan="2">DUP2_X2</td>
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
ICONST_n (−1<= n <=  5) |... |... , n
LCONST_n (0 <= n <=  1) |... |... , nL
FCONST_n (0 <= n <= 2) |... |... , nF
DCONST_n (0 <= n <= 1) |... |... , nD
BIPUSH b, −128 <= b < 127 |... |... , b
SIPUSH s, −32768 <= s < 32767 |... |... , s
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
