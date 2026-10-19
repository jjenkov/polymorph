# Polymorph VM Instruction Set Architecture (ISA)

This document describes the instruction set for the Polymorph VM (PVM).


## Instruction Set Layers

The instruction set is "layered" – meaning the instructions are divided into different functional layers.
Each layer is designed to be usable without the layers that follow it. Layers following another layer
are also sometimes referred to as being "layered on top" of the previous layers, like layers in a stack.

Within each layer there is one or more instruction groups. Each instruction group is typically independent
of the other instruction groups within the same layer - but may depend on functionality in earlier instruction layers.

Some instructions in later (higher / upper) layers may have a partial, functional overlap with instructions
in the lower layer. Such instructions from later layers are typically optimizations – meaning they 
combine behaviour from other, lower layers into a single instruction. This overlap (repeated functionality) 
is not ideal, but it is probably unavoidable if the instruction layers are to be independent of each other.

The following list contains the instruction set layers and groups

- Layer 0:
  - Nop
  - External function call
  - Output
- Layer 1:
  - Parameters
- Layer 2:
  - Expressions
- Layer 3:
  - Variables
- Layer 4:
  - Loops
  - Branches
- Layer 5
  - Internal functions
- Layer 6
  - Objects
  - Tables
- Layer 7 
  - Optimized instructions


## Instruction Naming Convention
Whenever an instruction operates on both a destination and a source value, the destination
part is listed first in the instruction name, and the source part is listed second.

For instance:

    expar  # pushes to expression stack (destination) from parameter stack (source)
    parex  # pushes to parameter stack (destination) from expression stack (source)

Some instructions have a default destination or source. In such instruction names the destination or
source may not be explicitly mentioned. In variations of such instructions that use a non-default 
destination or source, the non-default destination or source will be explicitly mentioned.

For instance:

    # Sets the value of a variable from a literal value argument. The argument is default source.
        set      

    # Sets the value of a variable from the top of the expression stack. 
    # The expression stack is a non-default source and thus named explicitly.
        setex    

    # Calls an external function with literal arguments.
    # Literal arguments are a non-default source and thus named explicitly.
        callearg

    # Calls an external function. Parameters are taken from the parameter stack.
    # The parameter stack is the default source and is thus not named.
    # The return value is stored in the returnValue field, the default destination.
        calle

    # Stores the external function call return value at the top of the expression stack.
    # The expression stack is a non-default destination, and thus named explicitly.
        excalle

    # Sets a variable to the external function call return value.
    # The variable is a non-default destination, and thus named explicitly.
        setcalle



## Instruction List

| Instruction | Layer | Description |
|---|---|---|
| nop | 0 | No operation |
| callearg | 0 | Calls an external function with literal arguments. Stores the return value in the returnValue field |
| out | 0 | Sends one or more arguments to the PVM output listener |
| outr | 0 | Sends the returnValue field to the output listener |
| par | 1 | Push a literal value to parameter stack |
| psb | 1 | Parameter stack scope begin |
| pse | 1 | Parameter stack scope end |
| calle | 1 | Calls an external function. Parameters come from the parameter stack. When the function returns, stores the return value in the returnValue field and ends the top parameter scope |
| ex | 2 | Push a literal value to expression stack |
| exret | 2 | Push returnValue field onto expression stack |
| expar | 2 | Push an indexed parameter of the top parameter scope onto the expression stack |
| parex | 2 | Push the top of the expression stack onto the parameter stack |
| outex | 2 | Sends the top of the expression stack to the output listener |
| esb | 2 | Expression stack scope begin |
| esc | 2 | Expression stack scope clear |
| ese | 2 | Expression stack scope end |
| var | 3 | Defines a variable |
| get | 3 | Gets a variable value and pushes it to the expression stack |
| set | 3 | Sets a literal value for a variable |
| setex | 3 | Sets the top of the expression stack as value for a variable |
| vsb | 3 | Variable scope begin |
| vse | 3 | Variable scope end |
| isb | 4 | Instruction scope begin |
| ise | 4 | Instruction scope end |
| tsb | 4 | Triple scope begin |
| tse | 4 | Triple scope end |
| leave | 4 | Leaves an instruction scope |
| leaveif | 4 | Leaves an instruction scope if condition is true |
| repeat | 4 | Repeats an instruction scope |
| repeatif | 4 | Repeats an instruction scope if a condition is true |
| if | 4 | Executes a block of code if a condition is true |
| ife | 4 | Executes a block of code if a condition is true. If the condition is false, another block is executed |
| switchi | 4 | Executes a block of code by using a value as an index into a set of values |
| switchv | 4 | Executes a block of code if a value matches one of a set of values |
| switchr | 4 | Executes a block of code if a value falls inside one of a set of ranges |
| rgl | 4 | Range greater than the start bound and less than the end bound |
| rel | 4 | Range from an included start bound, less than the end bound |
| rge | 4 | Range greater than the start bound, through an included end bound |
| ree | 4 | Range from an included start bound through an included end bound |
| fn | 5 | Defines an internal function |
| calli | 5 | Calls an internal function |
| return | 5 | Returns from an internal function |
| obj | 6 | Define object and pushes it to expression stack |
| getf | 6 | Get a field from an object |
| setf | 6 | Set a field on an object |
| met | 6 | Defines a method on an object |
| callm | 6 | Calls a method on an object |
| tab | 6 | Defines a table and pushes it to expression stack |
| getc | 6 | Gets a column from a row in a table |
| setc | 6 | Sets a column from a row in a table |
| excallearg | 7 | Calls an external function with literal arguments. <br/>When the function returns, pushes the return value onto the expression stack |
| excalle | 7 | Calls an external function. Parameters come from the parameter stack. <br/>When the function returns, pushes the return value onto the expression stack and ends the top parameter scope |
| excalli | 7 | Calls an internal function. Parameters come from the parameter stack. <br/>When the function returns, pushes the return value onto the expression stack and ends the parameter scope |
| excallm | 7 | Calls an object method. Parameters come from the parameter stack. <br/>When the method returns, pushes the return value onto the expression stack and ends the parameter scope |
| parcallearg | 7 | Calls an external function with literal arguments. <br/>When the function returns, pushes the return value onto the parameter stack |
| parcalle | 7 | Calls an external function. Parameters come from the parameter stack. <br/>When the function returns, ends the top parameter scope and pushes the return value onto the parameter stack |
| parcalli | 7 | Calls an internal function. Parameters come from the parameter stack. <br/>When the function returns, ends the parameter scope and pushes the return value onto the parameter stack |
| parcallm | 7 | Calls an object method. Parameters come from the parameter stack. <br/>When the method returns, ends the parameter scope and pushes the return value onto the parameter stack |
| setcallearg | 7 | Calls an external function with literal arguments. <br/>When the function returns, sets a variable to the return value |
| setcalle | 7 | Calls an external function. Parameters come from the parameter stack. <br/>When the function returns, sets a variable to the return value and ends the top parameter scope |
| setcalli | 7 | Calls an internal function. Parameters come from the parameter stack. <br/>When the function returns, sets a variable to the return value and ends the parameter scope |
| setcallm | 7 | Calls an object method. Parameters come from the parameter stack. <br/>When the method returns, sets a variable to the return value and ends the parameter scope |
| outv | 7 | Sends a variable value directly to the output listener. |


  
## ISA Layer 0
Layer 0 of the PVM ISA contains the minimal set of instructions necessary for the PVM
to be able to perform its intended function as an interface to an embedding host application.
In other words, instructions for calling external functions and send output out of the VM,
and the nop instruction as a safety mechanism.

### nop
The nop instruction does nothing. nop (NOP) is short for No Operation.

The nop instruction is mapped to the binary instruction code 0 – so bytes with 0-values are interpreted as NOP
instructions. This prevents the VM from crashing, or performing unspecified behaviour if the VM
should by accident execute beyond its given instruction array – 
into a part of an instruction byte array that contains zeroes (no instructions).

### callearg
The callearg instruction calls an external function with literal arguments.
After the external function returns, callearg stores the return value in the returnValue field.

### calle
The calle instruction calls an external function.
The parameters come from the top parameter scope.
After the external function returns, calle stores the return value in the returnValue field and ends the top parameter scope.

### out
The out instruction sends one or more literal arguments to the PVM output listener.

### outr
Sends the returnValue field to the output listener. The returnValue field is otherwise unchanged.


## ISA Layer 1
Layer 1 of the PVM ISA adds instructions for passing parameters to functions or methods. 


### par
Pushes a literal value onto the parameter stack. The value it stored in the top parameter stack scope.

### psb
Begins a scope on the parameter stack. 

### pse
Ends a parameter scope by removing the top parameter stack scope from the parameter stack. This also removes all parameters
stored within the removed parameter stack scope.


## ISA Layer 2
Layer 2 of the PVM ISA contains functionality for working with the expression stack.

The expression stack is used to store intermediate values, such as variable values, 
function return values, etc. 

The expression stack is not directly accessible for external functions.
Internal functions will have access to their own scope and subscopes on the expression stack.

### ex
Pushes a literal value onto the expression stack. The value is stored in the top expression stack scope.

### exret
Pushes the returnValue field onto the expression stack. The returnValue field is otherwise unchanged.

### expar
Pushes an indexed parameter from the top parameter scope onto the expression stack. The parameter stack is otherwise unchanged.

The index selects a parameter in the top parameter scope. Index 0 is the first parameter in that scope.

    expar(0;)

### parex
Pushes the top of the expression stack onto the parameter stack. The expression stack is otherwise unchanged.

    parex()

### outex
Sends the top of the expression stack to the output listener. The expression stack is otherwise unchanged.
This is an expression stack peek operation.

### esb
The esb instruction begins an expression stack scope.

### esc
The esc instruction clears the current expression stack scope without ending it.
Clearing an expression stack scope removes all values pushed onto the expression stack
since the beginning of that expression stack scope.

### ese
The ese instruction ends an expression stack scope. All values on the expression stack within the
ended scope are removed from the stack (the cells are set to null). No references are kept to the
removed values.


## ISA Layer 3
Layer 3 of the PVM ISA contains instructions for working with variables and variable scopes on the
variable scope stack.

When getting or setting a named variable, these instructions will first look for the named variable
in the top variable scope on the variable scope stack. If no variable is found with the given name
in that variable scope, the instructions continue searching in the variable scopes further down
the variable scope stack for a variable with the given name.

If no variable with the given name is found, the instructions fail.

Note: What "failing" means is still undefined.

Note: The variable scope stack may need some "boundaries" beyond which the
instruction does not search. Such boundaries will stop an instruction executed
inside an internal function from accessing named variables outside its own variable scope boundary.
Scope boundaries are not yet defined nor described.


### var
Defines a new, named variable in the top variable stack scope.
The variable name is the instruction's argument. The variable is stored with a null value. 

### get
Reads the value of a named variable from the top variable stack scope or below, 
and pushes the variable value onto the expression stack.
The variable name is the instruction's argument.

### set
Sets the value of a named variable in the top variable scope or below.
The variable name is the instruction's argument.
The value is passed as a literal value argument to the set instruction.

### setex
Sets a named variable to the value stored at the top of the expression stack.
The variable name is the instruction's argument.
The expression stack is left unchanged.

### vsb
Begins a new variable scope on the variable scope stack.

### vse
Ends the top variable scope on the variable scope stack. All variables will be cleared
from this scope. No references are kept to any of the variables or their values.



## ISA Layer 4
Layer 4 of the PVM ISA contains instructions for execution flow control. 
More precisely, layer 4 contains instructions for loops and branches.


### isb
The isb instruction begins an instruction scope and marks its start.
The scope knows both the location of the instruction that begins and ends the scope.

### ise
The ise instruction ends an instruction scope.
Executing ise ends the scope. This happens both when execution reaches ise
and when leave or leaveif jumps to it.

### tsb
The tsb instruction begins a triple scope.
A triple scope begins an expression scope, then a variable scope, then an instruction scope.
The instruction scope knows the location of the tse instruction that ends the triple scope.
In PDL, ( is a shortcut for tsb.

### tse
The tse instruction ends the triple scope begun by the matching tsb.
The matching tsb is the nearest unmatched tsb, matched as a stack.
Ending the triple scope ends the instruction scope, then the variable scope, then the expression scope.
In PDL, ) is a shortcut for tse.

    tsb      # begins triple scope A
        tsb  # begins triple scope B
        tse  # ends triple scope B
    tse      # ends triple scope A

### leave
Jumps to the instruction that ends the top instruction scope.

### leaveif
Jumps to the instruction that ends the top instruction scope if the value at the top of the expression stack is true.
The expression stack is left unchanged.

### repeat
Jumps to the first instruction after the instruction that began the top instruction scope.
The jump does not execute that instruction again.

### repeatif
Jumps to the first instruction after the instruction that began the top instruction scope
if the value at the top of the expression stack is true.
The jump does not execute that instruction again.
The expression stack is left unchanged.

### if
The if instruction executes an internally nested block of code if the value at the top
of the expression stack is true.
The expression stack is left unchanged.

The if instruction syntax follows this structure:

    if( then( ... ) )

However, we do not explicitly write "then", so the real syntax is like this:

    if( ( ... ) )



### ife
The ife instruction executes the instructions that follow it when the value at the top
of the expression stack is true.
When that value is false, execution jumps to the else block.
The expression stack is left unchanged.

A parent triple scope encloses the ife instruction, the then instructions, and the else instructions.

In Pasm, else() marks the start of the else block. else() is not an ISA instruction.
It compiles to a leave instruction, and it fills the else offset stored in the parent ife.
That leave jumps to the tse that ends the parent triple scope, so a true condition skips the else block.

    (
        ife(
            nop()
            else()
            nop()
        )
    )

The ife instruction stores the relative offset of the first instruction of the else block.
When the condition is false, ife jumps directly to that instruction.


### switchi
The switchi instruction uses the value at the top of the expression stack as an index
into a lookup table of value and block pointer pairs. If that index selects a code block,
that code block is executed.
The expression stack is left unchanged.

The switchi instruction begins a triple scope internally and ends it when the switchi
instruction ends.

The switchi instruction follows this syntax structure:

    switchi(
        0; ( ... )
        1; ( ... ) leave()
        2; ( ... )
           ( ... )  # default block;
    )

The binary encoding of the switchi instruction will look similar to this:

    switchi(
        0; [pointer to block 1]
        1; [pointer to block 2]
        2; [pointer to block 3]
           [pointer to default block ]

        ( block 1 )           # falls through to block 2
        ( block 2 ) leave()   # skips any following blocks
        ( block 3 )           # falls through to default block
        ( default block )
    )


### switchv
The switchv instruction matches the value at the top of the expression stack against values
for internally nested code blocks. The value is compared against each value until a match
is found. If that value matches the value for a code block, that code block is executed.
The expression stack is left unchanged.

The switchv instruction begins a triple scope internally and ends it when the switchv
instruction ends.

The switchv instruction follows this syntax structure:

    switchv(
        "value1; ( ... )
        "value2; ( ... ) leave()
        "value3; ( ... )
                 ( ... )  # default block;
    )

The binary encoding of the switchv instruction will look similar to this:

    switchv(
        "value1; [pointer to block 1]
        "value2; [pointer to block 2]
        "value3; [pointer to block 3]
                 [pointer to default block ]

        ( block 1 )           # falls through to block 2
        ( block 2 ) leave()   # skips any following blocks
        ( block 3 )           # falls through to default block
        ( default block )
    )


### switchr
The switchr instruction matches the value at the top of the expression stack against ranges
for internally nested code blocks. The value is compared against each range until a match
is found, and the switchr instruction then jumps to that code block.
Overlapping ranges are valid. The earliest range that contains the value is the range
that is selected.
The expression stack is left unchanged.

The switchr instruction begins a triple scope internally and ends it when the switchr
instruction ends.

Each range is written with a range instruction. The range instructions are rgl, rel, rge, and ree.
The first literal is the start bound. The second literal is the end bound.
The code block follows the range instruction.

The switchr instruction follows this syntax structure:

    switchr(
        rel( %0.0; %10.0; ) ( ... )
        rel( %10.0; %20.0; ) ( ... ) leave()
        rgl( %20.0; %30.0; ) ( ... )
        rge( %30.0; %40.0; ) ( ... )
        ree( %40.0; %50.0; ) ( ... )
                                ( ... )  # default block;
    )

The binary encoding of the switchr instruction will look similar to this:

    switchr(
        rel( %0.0; %10.0; ) [pointer to block 1]
        rel( %10.0; %20.0; ) [pointer to block 2]
        rgl( %20.0; %30.0; ) [pointer to block 3]
        rge( %30.0; %40.0; ) [pointer to block 4]
        ree( %40.0; %50.0; ) [pointer to block 5]
                             [pointer to default block ]

        ( block 1 )           # falls through to block 2
        ( block 2 ) leave()   # skips any following blocks
        ( block 3 )           # falls through to block 4
        ( block 4 )           # falls through to block 5
        ( block 5 )           # falls through to default block
        ( default block )
    )


### rgl
The rgl instruction marks a switchr range.
The letter g means greater than, so the start bound is excluded.
The letter l means less than, so the end bound is excluded.
The value matches when it is greater than the start bound and less than the end bound.
The first literal is the start bound. The second literal is the end bound.

### rel
The rel instruction marks a switchr range.
The letter e (equals) means the start bound is included.
The letter l means less than, so the end bound is excluded.
The value matches when it is greater than or equal to the start bound and less than the end bound.
The first literal is the start bound. The second literal is the end bound.

### rge
The rge instruction marks a switchr range.
The letter g means greater than, so the start bound is excluded.
The letter e (equals) means the end bound is included.
The value matches when it is greater than the start bound and less than or equal to the end bound.
The first literal is the start bound. The second literal is the end bound.

### ree
The ree instruction marks a switchr range.
Both letters are e (equals), so the start bound and the end bound are included.
The value matches when it is greater than or equal to the start bound
and less than or equal to the end bound.
The first literal is the start bound. The second literal is the end bound.


## ISA Layer 5
Layer 5 of the PVM ISA contains instructions for defining and calling internal functions.
An internal function is stored in the PVM's internal function map.

### fn 
The fn instruction defines an internal function. The fn syntax looks like this:

    fn( functionName( ( function body ) ) )    

For now, a function does not declare any parameters explicitly. Parameters are stored on 
the parameter stack and must be accessed via parameter stack scope index - not by name.

Here is an example of a function named returnLast that accesses 2 numbers from the parameter stack and
returns the last of them:

    fn( returnLast( (
      expar(0)
      expar(1)
      return()
    )))

In this example, the return instruction is interpreted to return the top of the expression stack.

The fn instruction will copy the binary code of the function body to a separate byte array,
and store it in the internal function map using the function name as key.


### calli 
The calli instruction (call internal function instruction) calls an internal function.

The calli instruction begins a new expression stack scope, variable scope, and instruction stack scope
and then jumps to the internal function code block.

When a later return instruction wants to return from the function call, it can do so by looking at the
instruction scope and see what location to return to.


### return
The return instruction returns from an internal function call.
It stores the return value in the returnValue field.

The return instruction ends the function's expression stack scope, variable stack scope, and instruction stack scope.
It also ends the parameter scope that was the top parameter scope when the function was called.


## ISA Layer 6
### obj
### getf
### setf
### met
### callm
The callm instruction (call object method instruction) calls an object method.
The callm instruction jumps to the method code block and then
begins a new expression stack scope, variable stack scope, and instruction stack scope.

### tab
### getc
### setc

## ISA Layer 7
### excallearg
### excalle
### excalli
### excallm

### parcallearg
### parcalle
### parcalli
### parcallm

### setcallearg
### setcalle
### setcalli
### setcallm

### outv
Sends a variable value directly to the output listener without first pushing the variable value
to the expression stack.



  