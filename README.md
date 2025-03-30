# Polymorph Personal Compute Platform
The Polymorph platform is a Personal Compute Platform intended to enable individual users to utilize their
personal computers more fully.

The Polymorph Personal Compute Platform is intended to consist of the following core parts:

1) Polymorph Data Encoding (PDE) + Polymorph Data Language
2) Polymorph Human Interface (PHI) 
3) Polymorph Fabric
4) Polymorph Toolkit(s)


## Polymorph Data Encoding (PDE) + Polymorph Data Language
Polymorph Data Encoding (PDE) and Polymorph Data Language are two data formats that are used to encode data
used internally by the other Polymorph parts.

Polymorph Data Encoding (PDE) is a binary encoding that is compact and fast to read and write.
PDE uses an encoding that is similar to MessagePack and CBOR, but with some changes that
makes it easier and faster to navigate in its binary form. It also contains some data types that
MessagePack and CBOR does not.

Polymorph Data Language (PDL) is a textual version of PDE that is easy to open and read and write in a text editor.
You can convert PDE to PDL and PDL to PDE. The syntax of PDL looks similar to JSON but with some changes to 
make it able to model PDE data structures, and for making PDL easier and faster to tokenize than JSON.

You can find the PDE and PDL specifications in their own GitHub repository here:

(https://github.com/jjenkov/pde-pdl-specification/tree/main)


## Polymorph Human Interface (PHI)
Polymorph Human Interface (PHI) is an application designed to run on desktop computers. 
PHI is intended to be the human interface to the Polymorph platform. 

PHI will come with a lot of functionality built in that is useful to individual users. 
Exactly what that functionality will be - is not fully decided - and it will probably expand over time too. 


## Polymorph Fabric
Polymorph Fabric is a communication fabric that makes it easy for individual users of Polymorph to 
exchange data with each other. 


## Polymorph Toolkits
The Polymorph toolkits enable you to use Polymorph technologies outside of the Polymorph platform - 
in your own systems.

The Polymorph toolkits also provide proof-of-concept implementations of many of the Polymorph technologies
(e.g. standards).

Here is the first toolkit with Java implementations:

(https://github.com/jjenkov/polymorph-tools-java)