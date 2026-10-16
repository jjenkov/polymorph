# Micro Platforms and Micro Platform Architecture

Micro platform architecture is a new architecture style that has grown naturally out of my 
Polymorph Personal Compute Platform project.

## Embedding a Virtual Machine
You turn an application or service into a micro platform by embedding a small virtual machine (or interpreter)
inside it. This enables the user to pass small scripts to the application or service which are executed by
the VM. 

## Interaction with Embedding Application via External Functions
The embedding application can plug "external functions" into the VM. These functions can be called by the scripts the VM executes.
External functions are the only way the scripts can interact with the world outside the VM. Otherwise, the scripts
run completely sandboxed.

## Sending Output via the Output Listener
The VM has an "output listener". Both scripts and external functions can write output to this output listener.
The output listener can send the output back to a calling client, write it to an output file, print it to
System.out - or do something else entirely with the output. The embedding application plugs in the concrete 
output listener implementation, so it is up to the embedding application where the output is directed.

## From Application to Micro Platform
It is the combination of the application, the embedded VM and the external functions that turns the software
into a "platform". Since it will typically be a very focused platform - I refer to this composition as a 
"micro platform".

The reason I call this composition a platform is that your application essentially becomes a platform that
exposes functionality via the external functions. How this functionality is used - is up to the user of 
your micro platform.

Here is a diagram illustrating the micro platform architecture:

![Micro Platform Architecture](../resources/micro-platform-architecture.png)

## Micro Platform Benefits
The benefits of micro platforms over traditional applications or REST services are:

- Flexibility:
  - Protocol independent interaction mechanism.
  - Greater control over service responses.
  - Multiple responses from a single script.
  - Streaming of service responses is natural.
  - Enables emergent behaviour.
- Simpler software:
  - Decompositional simplification of services. Similar to decompositional contraction in compositional design.

### Protocol Independent Interaction Mechanism
Passing a script to an application or service for execution provides a protocol-independent way to interact with that
application or service. No need to encode parameters to the application or service into URI or the body of an HTTP request.
Just embed the parameters in the script.

Your software can be tested by passing scripts directly to the VM. No need to have a full web server running to test
the software. In fact, each external function could be tested on its own – by calling it directly. It doesn't matter
if the script comes from a local file, or is received over the network.

### Greater Control Over Service Responses
A service response can be generated either by an external function or by the script itself.
This enables the script to control the response sent back over the network. 

### Multiple Responses Streamed From a Single Script
A single script executed by a service could send output to the output listener multiple times during execution.
Each of these responses could be streamed back to the client.

### Enables Emergent Behaviour
Giving your users a script language interface to your software enables your users to come up with new ways
to use your software. This is also known as emergent behaviour: Behaviour you did not anticipate when you designed
your software.

### Decompositional Simplification of Services
Imagine you have a REST service that can:

- Return a customer by ID
- Return a customer's orders
- Return a customer's payments
- Return a customer's shipments
- Etc.

In the basic case, a round-trip would be needed to this service to obtain each part of a customer's information.
One round-trip for the customer information, one round-trip for the orders etc.

If you want the client to be able to obtain more information in a single round-trip, you must provide a way for the
client to choose which parts of the information to obtain. This could be a set of flags 
(customerInfo=true, orders=true, payments=false, shipments=false) passed as input parameters to the service.
Or, it could be a set of service endpoints, each providing a preset combination of information.

In any case, the compositional logic added to your service to handle the multiple information parts in the response
add code overhead to your service. 

This overhead disappears with a micro platform architecture. The service response composition is encoded in the
scripts executed by the service - not by the service itself. The service code thus becomes simpler.


## Similar to Interaction With Databases
Sending a script to a service is similar to sending an SQL query to a database.

Imagine if we could only communicate with databases by calling stored procedures. That situation would be similar
to what we have today – when calling REST services.

If this principle works for databases – it can work for services too.


## Micro Platform Use Cases
The use cases for micro platforms are many. Some of the most common use cases are:

- Interaction mechanism for services. Send scripts instead of HTTP requests.

- Configuration mechanism for desktop apps. Ask a desktop app to execute a script at startup to start the application 
  in a certain desired state.

- Enable users to write plugins for desktop applications.

- Use a scripting language as the interaction mechanism for a CLI application (like PowerShell)


## Micro Platform Summary
Micro platform architecture is an exciting new architectural style providing a smart way to simplify your software while expanding the ways in which
users can interact with your software. Performance improvements are possible too – via round-trip reduction and service response streaming.

## Polymorph VM Enables Micro Platforms
The Polymorph VM developed as part of this Polymorph project is designed to be used exactly for micro platforms.

The Polymorph VM will be used internally by the Polymorph tools, but it can also be embedded by software outside the
Polymorph project.

