# 字节码指令 Bytecode instructions

* [Local variables](#Localvariables)
* [Stack](#Stack)


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
</table>
