# Web Technology Deficiencies

Despite having undergone work since 1989 - 1990, web technologies as they look today (2026) are 
still inherently deficient. 

The Polymorph Personal Compute Platform project attempts to address
some of these deficiencies. That is why the Polymorph project insists on reimagining so much of its tech stack.

The following sections explains the most annoying deficiencies of today's web technology, 
which Polymorph attempts to address.

[Technical Deficiencies](#Technical-Web-Tech-Deficiencies)


## Technical Web Tech Deficiencies 
The most central of the web tech deficiencies are the technical deficiencies.
These technical deficiencies result in some of the derived deficiencies e.g. in versatility (possible use cases)
and ownership.

### Streams vs. Files and Documents
The core data abstraction in web technology is a file, or a document. For instance, an HTML file,
a CSS file, or a JavaScript file.

A more efficient and versatile core data abstraction would have been a stream of records. 
A single file could be represented as a stream with a single record in (the file).

Using streams of records, all the files for a given web page could be merged into a single
record stream, server side, and sent to the client for display.

A client can subscribe to a stream, an get updates to whatever resource the stream represents, whether
it is a single file, or a data set of multiple records.

Streams can be cached locally, so only updates to the stream are downloaded.

Streams can be queried, so only the records you are interested in are returned, and only the fields of each
record you are interested in, are returned. This saves network bandwidth and improves the user experience.

### UDP vs. TCP

- UDP vs. TCP.
- P2P vs. client-server.

## Use cases Deficiencies

- Composable media vs. siloed media.
- Built-in end user functionality vs. no built-in end user functionality.

- Local computing vs. remote computing.
- Decentralized computing vs. centralized computing.

## Ownership Deficiencies

- Autonomy vs. dependency
- Collaborative computing vs. consumer / producer consuming


Major deficiency categories (suggested at least)

- Efficiency
- Versatility
- Autonomy

    