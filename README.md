# Polymorph Personal Compute Platform
The Polymorph platform is a Personal Compute Platform intended to enable individual users to better utilize their
personal computers. This includes supporting common personal computing use cases on the user's local PC - as well as 
communication with other user's PC. 


## Polymorph Use Cases
Below is a list of the personal computing use cases that Polymorph could address.
Exactly which use cases will be addresses and in what sequence - is not yet determined.
The use cases below are just ideas.

- Presentations (slides)
- Documents 
- Recipes (cooking, tech runbooks etc.)
- Books
- Spreadsheets
- Charts
- Diagrams
- Lists (shopping lists, todo lists etc.)
- Notes
- Photos
- Audio books
- Podcasts
- Music
- Video
- Chat
- Mail
- Video conferences
- File sharing / backup
- Etc.

To be able to support the above (and future) use cases, the Polymorph Personal Compute Platform needs a set of 
underlying more general purpose technologies.

## Polymorph Core Parts

The Polymorph Personal Compute Platform is intended to consist of the following core parts:

1) Polymorph Data Encoding (PDE) + Polymorph Data Language
2) Polymorph Human Interface (PHI)
3) Polymorph Fabric
4) Polymorph Toolkit(s)

Each of these major parts are explained further down this document.


## Polymorph Development Plan
These are the nearest development goals for the Polymorph project:

Next:
- PDL to PDE converter.
- Specify Polymorph VM
- Specify Polymorph VM instruction set
- Specify Polymorph VM machine code (binary representation of instruction set)
- Specify Polymorph VM assembly language (textual representation of instruction set)

Later:
- Polymorph Human Interface (PHI) - first functions defined
- Polymorph Human Interface (PHI) - first draft
- Polymorph Fabric - first functions defined.
- Polymorph Fabric - first draft
- Stream operations for appending to a stream
- Stream operations for reading from a stream from a specific offset
- Stream operations for querying a stream (more advanced than from a specific offset)


Done:
- PDE to PDL converter - Java class that can convert PDE to PDL.
- PdeWriter - Java class for writing raw, binary PDE
- PdeReader - Java class for reading raw, binary PDE
- Polymorph Data Language (PDL) specification - version 1.0
- Polymorph Data Encoding (PDE) specification - version 1.0


## Polymorph Innovations
To achieve the vision of the Polymorph project - several innovations have been necessary.
Several of these innovations can be used in small business and enterprise environments too.
The most important innovations are:

- PDE + PDL provide a much more versatile data format pair than e.g. JSON + BSON. 
  You can use PDE as message encoding, document encoding, stream encoding, log file encoding, data file encoding
  configuration file format etc.
  Since PDE is stream-oriented you can use a PDE file similarly to a Kafka topic, appending records to the file,
  scanning through records, doing incremental processing, subscribing to new records being added etc.
 
- The Polymorph VM that will be part of Polymorph Tools will enable you to move from a microservices architecture 
  to a microplatforms architecture.

- Polymorph Human Interface (PHI) will enable you to script the state of itself - giving more control over the
  user experience to the end user.

- The Polymorph Fabric could easily be used for commercial communication too. 


 


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