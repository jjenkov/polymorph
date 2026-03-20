# Top-down Virtual Machines vs. Bottom-up Virtual Machines

The Polymorph Personal Compute Platform project contains several smaller virtual machines. You could even argue 
that the Polymorph Player is also a kind of virtual machine - although a pretty simple one. 

So far, the VMs in the Polymorph are what I call "top-down virtual machines", or sometimes just "top-down VMs".

The difference between what I call a "top-down virtual machine" and a "bottom-up virtual machine" is explained
in the following sections.


## Bottom-up Virtual Machines

A bottom-up virtual machine emulates hardware. This is what virtual machines such as VM Ware, Virtual Box
and QEMU does. I call these types of virtual machines "bottom-up virtual machines" because they are building a
machine "bottom-up" - focusing on providing the same functionality as physical hardware, which is typically 
located at the "bottom" of our tech stacks.


## Top-down Virtual Machines

A top-down virtual machine emulates the functionality its users most commonly needs. It provides this functionality
via an instruction set, just like a bottom-up virtual machine does. But - the instruction set of a top-down 
virtual machine is much more "high level" than that of a bottom-up machine. 

A bottom-up machine understands instructions such as "push data onto stack", "read data into a CPU register",
"add CPU register A to CPU register B" etc. In other words, the functionality often found at the "bottom" of
our tech stacks. Hence, the name "bottom-up virtual machine".

A top-down machine may understand instructions such as "open window", "draw graphics", "load file" etc. which is 
functionality end users often need. This is the kind of functionality that typically exists at the "top" of
our tech stacks. Hence, the name "top-down virtual machine".

Maybe it would be more correct to call a "top-down virtual machine" an "interpreter". The reason I still call
it a "virtual machine" is that it has an assembly language instruction set, and actually executes a binary
version of that assembly language (bincode).

The purpose of a top-down virtual machine is to provide an instruction set that requires a lot fewer instructions
to do the same thing as with a bottom-up virtual machine. Opening a window in a top-down VM can be just
one instruction. In a bottom-up VM that may require lots of instructions.

The advantage of having a much more concise instruction set is, that your scripts will become much shorter,
both in the assembly language version, and in the compiled version. This is handy if you have to send these 
instructions over the network for execution in a remote VM. This is something the Polymorph project hopes 
to implement in the future.

Another advantage of a more high-level instruction set is, that whenever you need to execute more heavy functionality,
you are actually calling out into native code (Java code in this case). The external functions can execute
faster than instructions executed by the embedded high-level virtual machine.

The disadvantage of the high-level instruction sets of top-down VMs is that the VM becomes less suitable for
general purpose programming than a bottom-up VM. However, that is typically not the purpose of a top-down VM anyways.
The purpose of a top-down VM is typically to provide concise access to a limited set of end user functionality
via a simple instruction set. Not to be general purpose application platform.


## Virtual Machines in Polymorph Personal Compute Platform

Polymorph contains at least two top-down virtual machines:

The first top-down virtual machine is the Polymorph Player. The Polymorph Player is capable of executing a limited
instruction set such as opening windows, drawing graphics, playing sound etc. I consider the Polymorph Player 
a kind of top-down virtual machine, even though it actually uses an embedded virtual machine to execute the instructions.
This embedded virtual machine is able to call out into the Polymorph Player application. Thus, the combination 
of the embedded VM and the Polymorph Player results in a combined top-down virtual machine with a specific instruction set. 

The second top-down virtual machine is the virtual machine that actually executes the instructions inside the Polymorph Player.
This VM can actually be used outside of the Polymorph Player too. You could embed it in your own applications if you wanted to.
This VM can have external functions plugged into it, which it can call via its instruction set. There is an instruction
for calling an external function. Thus, you could turn your own application into a platform, by embedding this VM
inside your application - providing scripted access to your application's functionality.

The instructions of the second top-down VM look more like the instructions of a higher-level scripting language
than the instructions of a lower-level assembly language. That is why I call this a top-down VM too. It is a top-down
virtual machine with no external functionality plugged into it (yet). Embedded in Polymorph Player, the Player app
plugs some external functions into it that expose functionality in the Player app. Embedded in your app, you could
expose whatever functionality you wanted to.






