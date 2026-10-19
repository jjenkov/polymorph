# PVM ISA - Binary Encoding
This document describes a concrete binary encoding of the abstract PVM ISA described in the [pvm-isa.md](pvm-isa.md) .
This specific binary encoding is called "Polymorph VM ISA Binary Encoding" (PVM ISA Bin). 

File extensions should probably be ".pbin" or just ".pin". However, a lot of Pasm will be compiled
to internal byte buffers and then executed, and then be thrown away. That code will never be stored in a
file with a file extension.

This document will not explain what each instruction does. This is described in the PVM ISA document.


## General Encoding Format
Each instruction of the PVM ISA will be encoded using one of the following forms:

    [opcode byte]
   
    [opcode byte] [argument count byte] [arguments]

An instruction that does not take any arguments will be encoded as just a single opcode byte.
An example of such an instruction is the nop instruction.

An instruction that takes arguments will be encoded as an opcode byte, followed by an argument count byte,
followed by the argument values encoded using PDE. This encoding puts a limit of 255 arguments for any instruction.

In theory, it would be possible to allow individual instructions to use 2 (or more) argument bytes if they
really needed more than 255 arguments - but now, let's see if we can do with max 255 arguments.

An argument count and a stack index are not typed values. Each is a single byte holding a number from 0 to 255.
They are not encoded with PDE. PDE is used for typed literal values.


## nop
The nop instruction is encoded using only an opcode byte with the binary value 0.
Here is an example, using hexadecimal encoding:

    00

The purpose of the nop instruction is to make it a "no operation" (noop or nop) in case a VM accidentally
executes beyond the instructions stored in a code block (byte array). This is done to avoid this situation
to result in errors. Now it just results in nothing happening.

For instance, if you have a byte array with space for 1024 bytes, but have only filled up the first 57 bytes 
of that byte array with instructions, the rest of that byte array will most likely be filled with zeroes 
(if nothing else was stored in it before).


## callearg
The callearg instruction is encoded using:

- 1 opcode byte with the binary value 1.
- 1 argument count byte
- 1 PDE key argument representing the name of the external function to call
- n arguments PDE encoded

Here is an example, using hexadecimal + PDL encoding:

    01 03
    .externalFunctionName;
    123;
    "param2;


## out
The out instruction is encoded using:

- 1 opcode byte with the binary value 2.
- 1 argument count byte
- n PDE encoded literal arguments

Here is an example, using hexadecimal + PDL encoding:

    02 02
    123;
    "param2;


## outr
The outr instruction is encoded using only an opcode byte with the binary value 3.
Here is an example, using hexadecimal encoding:

    03



## par
The par instruction is encoded using:

- 1 opcode byte with the binary value 4.
- 1 PDE encoded literal argument

Here is an example, using hexadecimal + PDL encoding:

    04
    123;


## psb
The psb instruction is encoded using only an opcode byte with the binary value 5.
Here is an example, using hexadecimal encoding:

    05


## pse
The pse instruction is encoded using only an opcode byte with the binary value 6.
Here is an example, using hexadecimal encoding:

    06


## calle
The calle instruction is encoded using:

- 1 opcode byte with the binary value 7.
- 1 PDE key argument representing the name of the external function to call

Here is an example, using hexadecimal + PDL encoding:

    07
    .externalFunctionName;


## ex
The ex instruction is encoded using:

- 1 opcode byte with the binary value 8.
- 1 PDE encoded literal argument

Here is an example, using hexadecimal + PDL encoding:

    08
    123;


## exret
The exret instruction is encoded using only an opcode byte with the binary value 9.
Here is an example, using hexadecimal encoding:

    09


## expar
The expar instruction is encoded using:

- 1 opcode byte with the binary value 10.
- 1 index byte

In PDL the index is written as `0;`. The binary encoding stores that index as one byte – not as a PDE encoded positive integer.

Here is an example, using hexadecimal encoding:

    0A
    00


## parex
The parex instruction is encoded using only an opcode byte with the binary value 11.
Here is an example, using hexadecimal encoding:

    0B


## outex
The outex instruction is encoded using only an opcode byte with the binary value 12.
Here is an example, using hexadecimal encoding:

    0C


## esb
The esb instruction is encoded using only an opcode byte with the binary value 13.
Here is an example, using hexadecimal encoding:

    0D


## esc
The esc instruction is encoded using only an opcode byte with the binary value 14.
Here is an example, using hexadecimal encoding:

    0E


## ese
The ese instruction is encoded using only an opcode byte with the binary value 15.
Here is an example, using hexadecimal encoding:

    0F


## var
The var instruction is encoded using:

- 1 opcode byte with the binary value 16.
- 1 PDE key argument representing the name of the variable

Here is an example, using hexadecimal + PDL encoding:

    10
    .variableName;


## get
The get instruction is encoded using:

- 1 opcode byte with the binary value 17.
- 1 PDE key argument representing the name of the variable

Here is an example, using hexadecimal + PDL encoding:

    11
    .variableName;


## set
The set instruction is encoded using:

- 1 opcode byte with the binary value 18.
- 1 PDE key argument representing the name of the variable
- 1 PDE literal argument representing the value

Here is an example, using hexadecimal + PDL encoding:

    12
    .variableName;
    123;


## setex
The setex instruction is encoded using:

- 1 opcode byte with the binary value 19.
- 1 PDE key argument representing the name of the variable

Here is an example, using hexadecimal + PDL encoding:

    13
    .variableName;


## vsb
The vsb instruction is encoded using only an opcode byte with the binary value 20.
Here is an example, using hexadecimal encoding:

    14


## vse
The vse instruction is encoded using only an opcode byte with the binary value 21.
Here is an example, using hexadecimal encoding:

    15


## isb
The isb instruction is encoded using:

- 1 opcode byte with the binary value 22.
- 2 bytes holding the relative offset of the instruction that ends the scope.

The offset is little endian. It is not encoded with PDE.
It is the number of bytes from the first instruction after isb to the matching ise instruction.
Those 2 bytes are reserved by isb and filled when that ise is written.

Here is an example of an isb, a nop, and the matching ise, using hexadecimal encoding:

    16
    01 00
    00
    17

The relative offset is 1.


## ise
The ise instruction is encoded using only an opcode byte with the binary value 23.
Here is an example, using hexadecimal encoding:

    17


## tsb
The tsb instruction is encoded using:

- 1 opcode byte with the binary value 24.
- 2 bytes holding the relative offset of the instruction that ends the scope.

The offset is little endian. It is not encoded with PDE.
It is the number of bytes from the first instruction after tsb to the matching tse instruction.
Those 2 bytes are reserved by tsb and filled when that tse is written.

Here is an example of a tsb, a nop, and the matching tse, using hexadecimal encoding:

    18
    01 00
    00
    19

The relative offset is 1.


## tse
The tse instruction is encoded using only an opcode byte with the binary value 25.
Here is an example, using hexadecimal encoding:

    19


## leave
The leave instruction is encoded using only an opcode byte with the binary value 26.
Here is an example, using hexadecimal encoding:

    1A


## leaveif
The leaveif instruction is encoded using only an opcode byte with the binary value 27.
Here is an example, using hexadecimal encoding:

    1B


## repeat
The repeat instruction is encoded using only an opcode byte with the binary value 28.
Here is an example, using hexadecimal encoding:

    1C


## repeatif
The repeatif instruction is encoded using only an opcode byte with the binary value 29.
Here is an example, using hexadecimal encoding:

    1D


## if
The if instruction is encoded using only an opcode byte with the binary value 30.
Here is an example, using hexadecimal encoding:

    1E


## ife
In Pasm an ife can be written like this:

    (
        ife(
            nop()
            else()
            nop()
        )
    )

else() is not an ISA instruction. It compiles to a leave instruction and fills the else offset
of the parent ife.

That form is compiled into the following actual instructions:

    tsb()
        ife()
            nop()
            leave()
            nop()
    tse()

The entire if-construct is nested inside a triple scope.
The leave instruction is emitted by else(). It skips the else block when the condition is true
and the then instructions were executed.


The ife instruction is encoded using:

- 1 opcode byte with the binary value 31.
- 2 bytes holding the relative offset of the else block.

The offset is little endian. It is not encoded with PDE.
It is the number of bytes from the first instruction after ife to the first instruction of the else block.
Those 2 bytes are reserved by ife and filled when else() is compiled.

Here is the Pasm above encoded, using hexadecimal encoding:

    18          # parent tsb
    06 00       # relative offset 6, to the parent tse
    1F          # ife
    02 00       # relative offset 2, to the first else instruction
    00          # nop, then block
    1A          # leave, emitted by else()
    00          # nop, else block
    19          # parent tse

The relative offset of the else block is 2.
The parent tsb stores relative offset 6.

