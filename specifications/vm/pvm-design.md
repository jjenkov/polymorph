# Polymorph VM Design

The Polymorph VM (PVM) is a minimalistic virtual machine designed to be embedded inside an 
application or service, so you can interact with the application or service via scripts executed by the VM.
This, in effect, turns the application or service into a micro platform.

The scripts interact with the embedding application (or service) via functions that the application plugs into the VM.
The scripts can call these external functions to interact with the embedding application.

The external functions plugged into the VM are the only way that scripts executed by the VM can execute code 
outside the VM. Apart from these external functions, the VM is fully sandboxed code-wise.

The PVM enables you to plugin an output listener. This output listener provides a standardized way to 
send data out of the PVM, so you don't have to invent your own output mechanisms (e.g. via external functions).


![Polymorph VM](resources/polymorph-vm-1.png)


## An Interaction Technology – Not a General Purpose Programming Technology
The Polymorph VM is explicitly designed for interaction with an embedding application, service, or system of some kind.
The VM is not intended nor designed for implementing full applications running inside the VM.

The instructions, the language, and the internal VM design are designed for giving the user lightweight scripting
capabilities for an application-side interaction with an application or service. If you need heavy processing,
that processing should be done inside the application itself. This heavy processing can be triggered by scripts
via the external functions plugged into the VM by the embedding application.

![Interaction Technology - Not General Purpose Technology](resources/polymorph-vm-2.png)


## Polymorph VM Use Cases
The Polymorph VM is designed to be embedded all kinds of applications, including:

- Desktop applications
- CLI applications
- Services
- Server platforms
- etc.



## The Polymorph VM in Polymorph Player

The Polymorph VM is embedded inside the Polymorph Player. This means that the Player functionality 
can be activated via scripts. 

Scripts are a useful way to open up the Player in a certain state. For instance, with certain windows opened,
certain documents open, or data visualized etc. Thus, you can use scripts to quickly open up the Player 
for a specific purpose (use case). For instance, a script opens up some data views. Another script might open
up a news feed view etc.

Scripts may not be the only way you can interact with the Player. Exactly which interaction mechanisms we need
for the Player - we will figure out as we go along.

![Polymorph VM in Polymorph Player](resources/polymorph-vm-3.png)



## Internal VM Structure
The Polymorph VM is a multi-stack VM, meaning it contains multiple stacks internally.
More specifically, the VM contains the following stacks:

- Current code block
- Instruction pointer named ip

- Parameter stack
- Expression stack
- Variable stack
- Instruction stack

- Return location stack
  - Return code block
  - Return IP location within the return code block
- Return value field named returnValue

Furthermore, the VM contains:

- A map of external functions (function name => external function implementation).
- A map of internal functions (function name => byte array with copy of function code).
- An output listener that provides a standardized way to send data out of the VM.


### Current Code Block
The current code block is a reference to a byte array containing binary Polymorph VM instructions.

When you create a PVM and pass a byte array with instructions for the VM to execute, that
byte array is stored in the current code block reference.

If you pass a second byte array with instructions for the VM to execute, the second byte array
will overwrite the curren code block reference (and the instruction pointer will be reset to 0).


### Instruction Pointer (IP)
The instruction pointer (named IP) contains the byte index of the instruction currently being executed.
When that instruction has been executed, the instruction pointer is advanced to point to the next
instruction to execute.


### Parameter Stack
The parameter stack is used to push parameters onto, so they can be passed to external or internal functions.

The parameter stack can have stack scopes (or just scopes) pushed onto them. When pushing a value to the
parameter stack, it is pushed into the top scope on the parameter stack. When a stack scope is removed from
the stack (popped off the stack), all values within that stack scope are removed. No references are kept
to them.


### Expression Stack
The expression stack is used to push intermediate values onto, so you can perform operations on them.

The expression stack can have stack scopes (or just scopes) pushed onto them. When pushing a value to the
expression stack, it is pushed into the top scope on the expression stack. When a stack scope is removed from
the stack (popped off the stack), all values within that stack scope are removed. No references are kept
to them.


### Variable Stack
The variable stack is used to store variable values so you can read and write them.

Variables are named, so they are stored in a map on the stack. One map per variable stack scope.

The variable stack can have stack scopes (or just scopes) pushed onto them. Beginning a new 
variable scope pushes a new map onto the top of the variable stack. This map holds the
new variable scope. 

When creating a new variable, it is stored within the top variable scope on the variable stack. 
When a variable scope is removed from the stack (popped off the stack), all values within that 
variable scope are removed. No references are kept to them.


### Return Location Stack
The return location stack holds the return address and return code block to return to when returning
from an internal function call to the call location. 

When calling a function, a reference to the code block containing the function call instruction plus
the location of the next instruction after the call instruction are stored on return location stack,
as a pair (code block reference + return location).

### Return Value
The return value field (named returnValue) in the VM is used to store the return value from an external or internal function call.
From the return value field the return value can be pushed onto the expression stack or parameter stack.

### External Function Map
The external function map contains a map of external function names mapped to external functions 
that can be called from the executed instructions.

### Internal Function Map
The internal function map contains a map of internal function names mapped to code blocks (byte arrays)
that can be called from the executed instructions.

### Output Listener
The Polymorph VM enables you to register an output listener. Scripts executed by the VM can send data out
to the output listener via the different output instructions. See the PVM ISA document for more information
about the different output instructions available.

The external functions should probably have access to write data to the output listener too, so they
can send output directly without sending it back into the PVM first. This output writing shortcut is
a performance optimization.

What the output listener does with the output data is up to the output listener. For instance, the output listener
could write the output to System.out, write it to a log file, or send it over the network to a client or remote service.

If the VM is embedded inside a service, the output listener could send data back to the client.

If the VM is embedded inside a CLI application, the output listener could write output back to the command line interface.

If the VM is embedded inside a desktop application, the output listener could write the output to System out,
or to a desktop log file somewhere, or show it in a UI control, a popup dialog, or just ignore it.



## Polymorph VM Instruction Set
The Polymorph VM (PVM) instruction set is described in its own document in: 

[pvm-isa.md](pvm-isa.md)

## External Functions
External functions are simply objects that implement a specific interface (in Java, C#, etc.)
or a specific trait (e.g. in Scala or Rust). These external functions should be plugged into the PVM before before you
start executing any scripts.


