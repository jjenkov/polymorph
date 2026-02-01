# Web Technology Deficiencies

Despite having undergone work since 1989 - 1990, web technologies as they look today (2026) are 
still inherently deficient. 

The Polymorph Personal Compute Platform project attempts to address
some of these deficiencies. That is why the Polymorph project insists on reimagining so much of its tech stack.

The following sections explains the most annoying deficiencies of today's web technology, 
which Polymorph attempts to address.

- [Technical Deficiencies](#Technical-Deficiencies)
  - [Streams vs. Files and Documents](#Streams-vs-Files-and-Documents)
  - [UDP vs TCP](#UDP-vs-TCP)
  - [P2P vs Client-Server](#P2P-vs-Client-Server)
  - [Poor Integration With Local Device](#Poor-Integration-With-Local-Device)
  - [The DOM is Inflexible and Inefficient](#The-DOM-is-Inflexible-and-Inefficient)
- [Use Case Deficiencies](#Use-Case-Deficiencies)
  - [Composable vs Siloed Media](#Composable-vs-Siloed-Media)
  - [Built-in Functionality-vs-No-Built-in-Functionality](#Built-in-Functionality-vs-No-Built-in-Functionality)
  - [Local Computing vs Remote Computing](#Local-Computing-vs-Remote-Computing)
  - [Decentralized-Computing-vs-Centralized-Computing]
- [Ownership Deficiencies](#Ownership-Deficiencies)

## Technical Deficiencies
The most central of the web tech deficiencies are the technical deficiencies.
These technical deficiencies result in some of the derived deficiencies e.g. in versatility (possible use cases)
and ownership.


### Streams vs Files and Documents
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


### UDP vs TCP
The early versions of HTTP had round-trip problems. You could only send 1 HTTP request to a server, and then
had to wait for a response, before you could send the next HTTP request to that server. This has been addressed
in newer versions of HTTP. Unfortunately this has also made the protocol more messy.

HTTP runs ontop of TCP - and TCP has its own head-of-line blocking problem. If you are sending 2 unrelated files over the
same TCP connection via HTTP, e.g. two different images files, or a JavaScript and a CSS file, and a packet for 
the first file gets lost and needs to be resent, that blocks the second file from arriving - because it appears 
later in the TCP stream. This too seems to have been attempted addresses in HTTP 3 / QUIC, but this has made the 
protocol even more messy.

It would have been better to base the communication protocol on UDP. Each file could be transmitted in its own
"conversation" (a series of UDP packets), so the loss of a UDP packet for one file, did not block the arrival
of any other files.

Additionally, on the server you would always know what the largest possible message you could receive would be -
namely the maximum size of a normal UDP packet (leaving aside Jumbo packets). This makes it easier to allocate
memory for incoming messages. It also forces a breakup of transmitted data into smaller packets, so they can be sent and
received + processed bit by bit (stream processing), rather than waiting for the whole file to arrive.

UDP would also allow for lossy transmissions, e.g. of a live transmission, where it is more important to be
up-to-date than to receive the entire transmission perfectly, error free. 

Polymorph will use UDP based communication protocols. In addition, local communication will be possible over 
Unix Domain Sockets - not just UDP.


### P2P vs Client-Server
The Web is inherently designed around client-server communication. A browser connects to a server, and that's it.

It would be far more flexible if an Internet client was designed around P2P communication. Thus, an application
could be running locally, and communicate with one of your friend's applications running on his PC. No server
involved.

This is probably more true for personal computing, than for commercial computing. It can work fine for a
company to have all their applications be available from a server, like mainframes in the old days. 
But for personal computing, on your own device, this model is not as attractive.

Polymorph will enable P2P style communication between Polymorph clients (Polymorph Player and potential related applications).


### Poor Integration With Local Device
The web browser has poor integration with the local device. Some web technologies, such as local storage or cookies, 
do not work if you load a web page from the local disk. Also, loading files from the local disk can only be done
with the user's consent (via a file dialog). You cannot allow a web page to just read and write files, 
e.g. from a certain directory and down - without that file dialog.


## The DOM is Inflexible and Inefficient
Web browsers use a DOM UI model to represent the state of the UI displayed in the browser. This model is in many
ways inflexible and inefficient.

Polymorph uses a different kind of application state model.  


## Use Case Deficiencies
The technical deficiencies of web technologies results in a set of use case deficiencies.
In other words, use cases that web technology does not support well. 
These use case deficiencies make web technology less versatile.


### Composable vs Siloed Media
Web tech does not properly support composable media. You can get around it if you are a programmer,
but the web browser does not itself allow the loading of two web pages or data sets within the same tab.
This results in a siloed media model.

Polymorph will be designed around a composable media model.


### Built-in Functionality vs No Built-in Functionality
Web tech does not have any built-in functionality that the end user can use, except opening
windows and tabs and load one web page in each tab. Oh, and you can store bookmarks for web pages.
That's about it. Some browsers might have a few extra add-ons, but it's not standard tech.

Polymorph Player will have built-in functionality that the end user can use as is - without having
to first load a remote application (e.g. web page) to make the Player do anything.

### Local Computing vs Remote Computing
Web tech does not enable much local computing. It's possible, but clumsy, and the end user cannot do it
without a web application that enables it for them. There is no built-in local computing functionality
in a web browser. 

Polymorph will be centered around local computing. Network communication is considered an extension of
the local computing functionality - not a central tenet of it.

### Decentralized vs Centralized Computing
Web tech does not inherently support decentralized computing. This means, that there are many
types of use cases that are harder to implement using web tech.

Polymorph will be centered around P2P style communication, so both centralized and decentralized
computing will be possible.


## Ownership Deficiencies
The fact that the Web uses a client-server computing model means, that the end user most often does not own the
applications they use. The applications can change or disappear without their consent.

This ownership model leads to dependency for the end user, instead of autonomy.

The whole personal computing (PC) revolution was centered around autonomy - that you now had access
to your own computing device, and could choose what to compute on it yourself.

Client-server computing also leads to a consumer-producer style of computing, rather than a more 
collaborative style of computing that would be possible with P2P style communication instead.

    