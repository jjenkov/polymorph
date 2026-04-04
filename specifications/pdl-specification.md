# Polymorph Data Language (PDL) Specification

Polymorph Data Language (PDL) is a textual data encoding that is capable of representing the same data types as
the Polymorph Data Encoding (PDE). You can convert from the binary PDE to PDL, and from PDL to PDE. 

You can read about the Polymorph Data Encoding here:

[Polymorph Data Encoding](pde-specification.md)

Polymorph Data Language can also be used by itself as an alternative to CSV, JSON, XML, and YAML.


## Table of Contents

- [Polymorph Data Language Use Cases](#Polymorph-Data-Language-Use-Cases)
- [Syntax Variations and Performance](#Syntax-Variations-and-Performance)
- [Tokens and Fields](#Tokens-and-Fields)
- [Token Structure](#Token-Structure)
  - [Single Character Tokens](#Single-Character-Tokens)
  - [Multi-character Tokens](#Multi-character-Tokens)
  - [Positive Integer Tokens](#Positive-Integer-Tokens)
  - [Named Tokens](#Named-Tokens)
- [Token Type Characters](#Token-Type-Characters)
- [Token Types](#Token-Types)
  - [Null Values](#Null-Values)
  - [Comments](#Comments)
  - [Boolean](#Boolean)
  - [Integers](#Integers)
  - [Floating Point Numbers](#Floating-Point-Numbers)
  - [Bytes](#Bytes)
  - [ASCII Text](#ASCII-Text)
  - [UTF-8 Text](#UTF-8-Text)
  - [Text Escape Characters](#Text-Escape-Characters) 
  - [UTC Date and Time](#UTC-Date-and-Time)
  - [ID](#ID)
  - [Copy](#Copy)
  - [Reference](#Reference)
  - [Key](#Key)
  - [Object](#Object)
  - [Table](#Table)
  - [Metadata](#Metadata)
  - [Named Tokens](#Named-Tokens)
- [Whitespace](#Whitespace)
- [ASCII With UTF-8 Zones](#ASCII-With-UTF-8-Zones)
- [Minification](#Minification)
- [Parallel Tokenization](#Parallel-Tokenization)
- [Full PDL Example](#Full-PDL-Example)


## Polymorph Data Language Use Cases

PDL is useful for debug purposes, where data in PDE can be converted to PDL to make the data easier to read.

PDL can also be useful for hand-editing smaller documents such as configuration files or smaller data sets.

You could also use PDL as an alternative to JSON, YAML, CSV, or XML, if you prefer to stay with a fully textual data format.
PDL has more features than JSON, YAML, CSV, and XML, so you would get more options with PDL than with any of these formats.


## Syntax Variations and Performance

When I started designing the syntax for PDL I started out with a syntax that was similar to the syntax of JSON.
However, as I started optimizing the tokenizer for this syntax, I realized that one of the biggest performance killers was
branch mis-predictions. Every time I either removed a branch, or made it more predictable, performance increased.
Of course there were other optimizations too – but the branch mis-predictions seemed to be the biggest issue.

Having realized that, I started looking into re-designing the PDL syntax to make it easier to parse. 
In other words, to make it parseable with fewer branches in the code. I explain this design process and the
resulting syntaxes in this document: [Polymorph Data Language (PDL) Design](../docs/pdl-syntax-design.md). I would recommend that 
you familiarize yourself with the PDL syntax before reading this document because it only focuses on the syntax of tokens 
and does not explain what the tokens illustrated represent. 

This document will only describe the final PDL syntax.


## Tokens and Fields
A PDL script consists of tokens, whereas a PDE file (stream) consists of fields. 
Sometimes a PDE field can be represented by a single PDL token. Other times you need multiple PDL tokens to represents
a PDE field.


## Token Structure
PDL tokens follow one of these formats:

- Single character tokens
  - The token consists of a single character, such as any of these characters: { } [ ] ( )
  - These are often used to mark the beginning and end of composite fields.

- Multi-character tokens
  - Starts with a token type character and ends with a token end character, such as "hello;
  - These are often used to represent an atomic field (a single-value field).

- Positive integer tokens
  - Starts with a digit character and ends with a token end character, such as 12345;
  - These are used to represent positive integer fields.

- Named tokens 
  - Consists of multiple smaller tokens, such as value( 12345; )   (3 separate tokens)
  - Starts with a token that starts with an alphabetic character and ends with a parenthesis, such as value(
  - Contains one or more PDL tokens after the ( 
  - Ends with another ) - which is a single character token.
  - For instance, the named token value( 12345; ) consists of these 3 smaller tokens:
    - value(
    - 12345;
    - )

### Single Character Tokens

Single character tokens are tokens that uses only one character to represent the token. Currently, PDL recognizes
the following single character tokens:

    {
    }
    [
    ]
    <
    >
    (
    )
    ;

These tokens are used to mark the beginning and end of composite fields, such as objects, tables, arrays, and named tokens.

Note: Only if any of these characters is the first character of a token - will they be perceived as single character tokens.
The ) character is also used as a token end character for named tokens - but being located at the end of a token, it will
not be confused with a single character token.

Note: The single character tokens ( and ; are not currently used, but the PDL tokenizer still recognizes and accepts them
as a valid tokens. Both of these single character tokens are used in Polymorph Assembly, by the way.


### Multi-character Tokens

Multi-character tokens are tokens that consist of multiple characters. They begin with a token type character 
and end with a token end character. There is a list of used token type characters later in this document.

Let's look at a few multi-character tokens. First, here is a boolean token. It starts with the ! token type character,
followed by a single character that tells its value, and ends with a semicolon character:

    !0;
    !1; 

Here are some UTF-8 tokens:

    "This is text;
    "This is also UTF-8 text;

The token type character here is the quote character " and the token end character is the semicolon character.
It looks a bit weird when we are used to seeing strings enclosed in quotes at both ends, e.g. "This is text", I know, 
but there are some good reasons for this choice. And - you get used to reading strings like this over time.


### Positive Integer Tokens

Positive integer tokens are multi-character tokens that represent positive integers. Positive integer tokens 
have the + character as token type character and semicolon as token end character. Here is a positive integer token example:

    +123; 

For positive integers you can leave out the + character. In other words, the characters 0 to 9 in the beginning of a
token are also interpreted to be positive integer token type characters. Here are a few such examples:

    123; 
    456; 
    789; 


### Named Tokens
Named tokens are used to represent fields that cannot be represented using single character tokens,
multi-character tokens, or positive integer tokens. There are no built-in named tokens in PDL at this time.
Thus, named tokens are mostly used for extensibility. You could embed your own named tokens with your
own semantic meaning in your PDL scripts. For instance,

    phoneNumber( 12345678; )
    name( "John; "Doe; )
    address( "Main St; 123; )

The reason it is called a "name token" is, that a named token does not have an explicit 
token type character telling what type of token it is. Instead, it is the name of a named token that
tells what type of token it is. 

This is different from other PDL tokens. For instance, a UTF-8 token looks like this:

    "UTF-8 text;

It consists of a token type character " plus some value characters, and the token end character ;

But, a named token looks like this:

    myNamedToken( "blablabla; )

To know how to interpret this named token, you first need to identify that it is a named token.
You do so by seeing that its first character is alphabetic. 

Second, you look at the name of the token. In this case, "myNamedToken". That name is used to represent the
actual, semantic token type. It's a named token, of semantic type "myNamedToken". 

Third, to see what value this named token has, you look at the characters following the ( and ) .
That is considered the value of the named token.

Finally, the named token ends with the single character token ) .

Note, that the first part of a named token ends with the ( character, as in myNamedToken( . 
Thus, the ( is not considered a single character token when it is not the very first character of a token.
Instead, it is the token end character of the name part of a named token.

As stated earlier, there are no built-in named tokens in PDL at this time, so you will not be using them
unless you create your own. However, Polymorph Assembly uses a syntax that is similar to PDL's named tokens.
Thus, once you understand how named tokens work in PDL, the syntax of Polymorph Assembly will make more sense.


## Token Type Characters
Here is a shorthand list of the token type characters used for different token types.
The token type character is the first character of a token.

| Character | Token Type                    | Token Examples                   |
|-----------|-------------------------------|----------------------------------|
| _         | Null                          | _"; _!; _+;                      |
| #         | Single-token comments         | #This is a single-token comment; |
| *         | Multi-token comments          | *This is a multi-token comment~  |
| !         | Booleans                      | !0; !1; !2;                      |
| +         | Positive integers             | +123;                            |
| -         | Negative integers             | -123;                            |
| %         | 32-bit floating point numbers | %123.45; %-123.45;               |
| /         | 64-bit floating point numbers | /567.89; /-567.89;               |
| $         | Binary data in hexadecimal    | $123A 44E3;                      |
| \|        | Binary data in base64         | \|QmFzZTY0IGRhdGE=;              |
| ^         | Binary data in UTF-8          | ^Binary data in UTF-8;           |
| '         | ASCII text                    | 'ASCII chars;                    |
| "         | UTF-8 text                    | "UTF-8 chars;                    |
| @         | UTC date and time             | @2030-12-31T23:59:59.999;        |
| :         | ID                            | :obj1;                           |
| =         | Copy of another field         | =obj1;                           |
| &         | Reference to another field    | &obj1;                           |
| .         | Keys                          | .key1; .column2; .property3;     |
| {         | Object begin                  | {                                |
| }         | Object end                    | }                                |
| [         | Table begin                   | [                                |
| ]         | Table end                     | ]                                |
| <         | Metadata begin                | <                                |
| \>        | Metadata end                  | \>                               |
| a...Z     | Named tokens                  | id(123;)  ref(123;)              |

Note:
The "Metadata end" token type character is only one character - the > character.
However, if you read this document as raw text instead of rendered markdown, the table above lists two characters, namely
\ and > . This combination is necessary to make markdown render a single > character in the table above. In your
PDL scripts you should only use the single > character.


## Token Types

Polymorph Data Language has a set of token types that it should be able to represent. This section explains these token types
and shows examples of how they look.

Note: As PDL is designed for streaming of data - there is no "root" token or root "object" – like you would normally
have in JSON or XML.


### Null Values
A PDL null token represents a null value. PDL null tokens use the _ character as token type character and the ; character
as token end character. 

Between the _ and the ; characters you put the type character of the type you want the null
to represent. In other words, PDL uses typed nulls. 

Here is an example PDL null token of the UTF-8 field type:

    _";

For tokens that have more than one valid token type character, such as positive integer tokens and named tokens,
you can use any of the token type characters within a null token. Here are a few examples:

   _+;
   _0;

   _a;
   -x;

Composite PDE fields (fields with other fields nested inside) are represented with a "field begin" single character token,
an ended with a "field end" single character token. For instance, here is how a PDE object field would be represented:

    {
        .property1; "hey;
        .property3; 123;
    }

To represent a null field of a composite field type, use the "field begin" character as type null type character.
Here are some examples:

    _{;
    _[;

These examples represent a null object field and a null table field.

There are some PDL tokens (fields) it does not make sense to have null values for. These are:

- Nulls
- Comments
- Keys
- Metadata

It does not make sense to have a null token of null type (in my opinion). But you could still represent it like this:

    __;

It also does not make sense to have a null comment token, but you could still represent it like this:

    _#;

Key tokens represent a key in a map, or a property name in an object. It does not really make sense to have
a null token of key type. Why would you use null as key or property name ? You can still represent it though, like this:

    _.;

Metadata fields consists of multiple PDL tokens, but are translated into a single PDE field with nested fields inside.
It does not really make sense to have a null metadata field. Why not just leave it out? But - you could still represent it like this:

    _<;



### Comments
Comment tokens are intended for inserting comments into PDL scripts. YAML and XML have comments, but JSON 
and CSV do not have a standard comment format. Comments are useful in data documents, test data documents, 
and in configuration files.

PDL has two types of comments:

- Single-token comments.
- Multi-token comments.

Single-token comments use the # character as token type character and semicolon as token end character.

Multi-token comments use the * character as token type character and the tilde character ~ as token end character.

A PDL single token comment would look like this:

    #This is a comment;

Single-token comments can be inserted anywhere between other PDL tokens – but not inside another token.
Thus, you cannot comment out part of another token.

Single-token comments make it easy to comment out a single PDL token too. Here is an example:

     "This is a text;  
    #"This is also a text;  
     "This is one more text;

Notice how the middle UTF-8 token is commented out by placing a # character in front of that token.
This is possible because the single-comment token and the UTF-8 token both end with a semicolon.

If the token you want to comment out does not end with a semicolon, you also need to put a semicolon 
after the token. Here is an example:

    #{; 

A PDL single-token comment can actually span multiple lines. Here is an example:

    #This is a 
     single-token comment
     that spans multiple lines;


In case you need to comment out multiple PDL tokens it can be annoying to have to explicitly comment out
every one of them individually - as shown in this example:

    #This is a single-token comment;

    #123; 
    #/789.00;
    #"This is a text;

Notice how each of the tokens below the first comment token must be commented out individually, since each of them
ends with a ; which marks the end of a comment token too. This is annoying.

To solve this problem PDL also has multi-token comments. A multi-token comment uses the * as type character, 
and the ~ character as token end character. Here is an example of a multi-token comment:

    *This is a multi-token comment~

This multi-token comment actually only consists of a single token. In fact, multi-token comments are all
only one token. The name comes from their ability to comment out multiple tokens more easily. 
Here is a multi-token comment example:

    *This is a multi-token comment
    #This is a single-token comment inside a multi-token comment;
    123; 
    /789.00;
    "This is a text;
    ~

While the above is actually still only a single PDL token - it comments out multiple tokens. It can do so because
it does not use the same token end character as any other token in PDL.


### Boolean
A PDL boolean token represents a boolean PDL field which can be either true, or false. Here are
two examples showing a false and true boolean PDL token (field):

    !0;    
    !1;

The ! character is the token type character for boolean tokens (fields). The value 0 represents a boolean field
with the value false. The value 1 represents a boolean field with the value true. 


### Integers
A PDL integer token represents an integer PDL field. Integers can have either positive or negative integer values.

Positive integers use the + character as token type character and a semicolon as token end character. 
Here is a positive integer token example:

    +123;

It is allowed to omit the + character for positive integer tokens, as the characters 0 to 9 as token type characters
are also interpreted as positive integer token type characters. Thus, the positive integer token example above 
could be represented like this:

    123;

A negative integer token uses the - character as type character, like this:

    -123;


### Floating Point Numbers
A PDL floating point token represents a floating point number. PDL enables you to represent both 32 bit and 64 bit
floating point numbers. The 32 bit floating point tokens use the % character as type character. 
The 64-bit floating point tokens use the / character as type character. Both 32 bit and 64 bit floating point tokens
use a semicolon as token end character.

Here are some example 32 bit floating point tokens:

    %123.45;
    %-123.45;

Here are some example 64 bit floating point tokens:

    /456.789;
    /-456.789;


### Bytes
A PDL bytes token represents binary data encoded using a textual representation. PDL supports 3 different
textual representations for binary data:

1) Hexadecimal encoding
2) Base64 encoding
3) UTF-8 encoding

Each of these are represented using a different token type character. Here are some example PDL bytes field
token examples:

    $45A6 34E3;

    |VGhpcyBpcyBiYXNlNjQgZW5jb2RlZA==;

    ^This is binary data in UTF-8 encoding;

The $ character is the token type character for binary data encoded using hexadecimal encoding (white spaces allowed between digits).

The | character is the token type character for binary data encoded using base64 encoding.

The ^ character is the token type character for binary data encoded using UTF-8 encoding.

All three bytes token types use a semicolon as token end character.


### ASCII Text
A PDL ASCII text token represents an ASCII text field. Sometimes it is useful to tell a consumer of data
that a string of text is only ASCII characters (exactly 1 byte per character) so they do not need to 
parse each character as a UTF-8 character.

The ASCII token uses the ' character as token type character and a semicolon as token end character.
Representing a PDL ASCII text token looks like this:

    'This is an ASCII text;


### UTF-8 Text
A PDL UTF-8 text token represents a UTF-8 text field. Sometimes it is useful to tell a consumer of data
that a string of text is UTF-8 characters (possibly more than 1 byte per character) so they need to
parse each character as a UTF-8 character.

The UTF-8 token uses the " character as token type character and a semicolon as token end character.
Representing a UTF-8 text token looks like this:

    "This is a UTF-8 text;


### Text Escape Characters

PDL contains three different fields / token types that can contain textual data: 

- Bytes - in UTF-8 encoding.
- ASCII
- UTF-8

All of these fields / token types use a semicolon as token end character. That means, that you cannot use a 
semicolon inside the value of that field / token, because it would be interpreted as the token end character.

To represent a semicolon in a text token you will have to escape it. Escaping a character means representing it
using an escape character sequence. 

PDL uses the backslash character \ as escape character. The character after the \ determines what this 2-character
sequence is to be interpreted as. Here are the built-in escape character sequences that PDL supports:

- \0   : Represents a backslash character 
- \1   : Represents the token end character (semicolon)
- \t   : Represents a tab character
- \r   : Represents a carriage return
- \n   : Represents a newline

These escape character sequences should be interpreted before converting PDL to PDE. Thus, the escape characters
should not appear in PDE. 

The reason PDL uses \0 and \1 instead of \\ and \; that other languages would have used, is this:

By representing the backslash character itself using \0 instead of \\ - you know that when you encounter a \ character
in a PDL text token, it is always an escape character. It is never the second character of a 2-character escape sequence
to represent a backslash character.

By representing the semicolon character using the \0 sequence instead of \; - you know that when you encounter a ;
character anywhere in a PDL script - it is always a token end character. It is never the second character of a 2-character
escape sequence to represent a semicolon character. Knowing this is advantageous when tokenizing PDL in parallel.
See [Parallel Tokenization](#Parallel-Tokenization) for more information.

Here is an example UTF-8 token with embedded escape characters to illustrate how it looks in practice:

    "This is a UTF-8 text with embedded escaped characters: \0 \1 \t \r \n ;


### UTC Date and Time
The PDL UTC token represents a date and time in UTC format (no time zones allowed). 
The UTC PDL token uses the @ character as type character and a semicolon as token end character.

The UTC token can contain year, month, date, hour, minute, seconds, milliseconds. Not all of these parts
are needed. Here are all the valid UTC token variations:

    @2030;
    @2030-12;
    @2030-12-31;
    @2030-12-31T23;
    @2030-12-31T23:59;
    @2030-12-31T23:59:59;
    @2030-12-31T23:59:59.999;

Note: A PDE UTC field also has a variation that can hold nanoseconds. PDL does not yet support this variation.
Maybe I will get around to supporting nanoseconds in PDL in the future.


### ID
The PDL ID token represents an identifier you can reference via copy or reference field. 
There is no corresponding ID field in PDE. 

The PDL ID token represents an "address" or "location" in the PDL script which can be referenced by
a copy or reference field. In reality, a reference to an ID token is actually a reference to the 
token following immediately after the ID token. In a sense, an ID token thus is similar to a 
label in an assembly language program.

The PDL ID token uses the : character as token type character and semicolon as token end character.

Here is how a PDL ID token looks on its own:

    :obj1;

As mentioned, an ID token points to the PDL field immediately following the ID token. 
Here is a PDL ID token example followed by a series of tokens representing an object:

    :obj1;
    { .name; "John Doe; }

In the above example the :obj1; field points to the following PDL object field.

See more info in the Copy and Reference fields for how to reference an ID field.


### Copy
The PDL copy token references an ID token earlier in the PDL script. 
The PDL copy token represents a "copy" of the PDL field immediately following the referenced ID field.

Note: A copy token always references an ID token that is located EARLIER in the PDL script than the copy token itself.
Forward references are not allowed. This is because PDL is designed for streamed processing. In a stream you can
reference back into the part of the stream you have already read, but not forward into the part of the stream you 
have not yet read. This is to ensure that the PDL script can be processed in a single pass without needing to 
store parts or all of the stream in memory.

The PDL copy token uses the = character as token type character and semicolon as token end character.
Here is a PDL copy token example:

    :obj1;
    { .name; "John Doe; }

    =obj1;

The copy field is useful if you have to repeat the same field multiple times in a PDL stream. Instead of
repeating the field – you can include it once and include copy fields pointing to the original.


### Reference
The PDL reference token references an ID token earlier in the PDL script.
The PDL reference token represents a "reference" to the PDL field immediately following the referenced ID field.

Note: A reference token always references an ID token that is located EARLIER in the PDL script than the reference token itself.
Forward references are not allowed. This is because PDL is designed for streamed processing. In a stream you can
reference back into the part of the stream you have already read, but not forward into the part of the stream you
have not yet read. This is to ensure that the PDL script can be processed in a single pass without needing to
store parts or all of the stream in memory.

The PDL reference token uses the & character as token type character and semicolon as token end character.
Here is a PDL reference token example:

    :obj1;
    { .name; "John Doe; }

    { .name; "Jane Doe; .spouse;  &obj1; }

This example represents a reference from the "spouse" property of the second object to the first object.

The PDL reference field is useful when you need to represent object graphs where one object references another.

PDL reference fields can also be used to represent cyclic object graphs. 
Here is an example of a cyclic object graph:

    :obj1;
    { .name; "John Doe; 
      .children; [
          { .name; "Phillis Doe; .parent;  &obj1; }
          { .name; "Aron Doe;    .parent;  &obj1; }
      ]
    }

Notice how the parent object references the two child objects via a nested table.
Notice also how both of the child objects reference the parent object via reference fields. 
This results in a cyclic object graph.


### Key
The PDL key token is used to represent a property name inside a PDL object field (e.g. "property1"),
a column name inside a PDL table field (e.g. "column1"), or a property name inside a metadata field. 

The PDL key token uses the . character as token type character and semicolon as token end character. 

Here are some examples of PDL key tokens:

    .key1; .key2; .key3; 
    .col1; .col2; .col3; 
    .property1; .property2 .property3;


### Object
PDL object fields are demarcated by 2 tokens. An object begin token and an object end token.
The token type character for the object begin token is the { character. 
The token type character for the object end token is the } character. 

Here are some PDL object fields with nested fields inside:

    { .firstName; "Jane; .lastName; "Doe; }

    { .name; "John Doe; .birthday; @1999-01-16; }

The fields inside these object fields are key and value tokens representing property names
and values.

#### Compact Objects

There are no strict rules about what PDL tokens / fields you can nest inside an object.
The only principle is that a Key field is interpreted as the property name of the following value field / token.

However, you are not required to nest any key fields inside a PDL object. You could leave out the 
key fields and only include the property values, like this:

    { "John Doe; @1999-01-16; }

You are also allowed to only include the Key tokens if you feel that makes sense. You are also allowed
to include Key tokens for some properties and not others. 

Such compact object representations require that you know out of the PDL file which properties mean what.
You could use the sequence of the property values to deduce what property they represent. 

In case of mixing named and unnamed properties (with and without key fields), you might want to 
put the unnamed properties at the beginning of the object where you can control the sequence and
then put the named properties after the unnamed properties. That would enable you to make the unnamed properties
required (so you can rely on their sequence) and the named properties optional (because you have the 
property names to tell what they represent). For instance:

    { "John Doe; @1999-01-16; .city; "Copenhagen; .country; "Denmark; }



### Table
PDL table fields are demarcated by 2 tokens. A table begin token and a table end token.
The token type character for the table begin token is the [ character.
The token type character for the table end token is the ] character.

A table field represents a tabular data structure consisting of rows and columns. 
A table only contains the column names once, at the beginning of the table.
After the columns follow sequences of row fields. 

If a table contains N column fields in the beginning, then each row is considered to consist of N fields too.

If a table contains no column fields in the beginning, it is considered to be an array - meaning each 
row consists of exactly one field.

Here are some PDL table fields with nested fields inside:

    [ .firstName; .lastName;
      "Jane;      "Doe;
      "John;      "Doe;
      "Joe;       "Blocks;
    ]

    [ .name;       .birthday;
      "Jane Doe;   @1999-01-16;
      "John Doe;   @1995-09-18;
      "Joe Blocks; @1993-03-23; 
    ]

    [ "Jane Doe; "John Doe; "Joe Blocks; ]

It is possible to nest tables inside tables. This can be used to represent tree structures more compactly.
Here is a nested PDL table example:

    [
      .name; .children;
      "Gretchen; [
        .name; .children;
        "Rami; []
        "Fana; []
      ]
      "Hansel; [
        .name; .children;
        "Gordia; []
        "Victor; []
      ]
    ]

If you use reference tokens inside some of the nested row columns, you can actually represent a cyclic object
graph of similar objects more compactly via tables.


### Metadata
A PDL metadata field contains metadata about the data in the PDL stream. What the metadata means semantically
is up to you to decide.  

A PDL metadata field is structurally similar to a PDL object. 
A PDL metadata field are demarcated using the metadata start token < and the metadata end token > . 

Here is an example PDL metadata field:

    < .type; "Customer; >


A PDL metadata field typically precedes the PDL fields it belongs to.
Here is an example of a PDL metadata field belonging to the following PDL object field:

    < .class; "com.company.project.product.Customer; >

    { .name; "John Doe";
      .customerId; 12345;
    }

In the above example the PDL metadata field contains the Java class name of the PDL object following it.
In other words, the metadata field signals that the object field was serialized from an object 
of the class com.company.project.product.Customer. 

Note:
A PDL metadata field does not have to belong to just one PDL field. You can interpret a metadata field to
be applicable to all fields following it.  

Note:
There are currently no predefined semantic meanings of metadata. It is up to you to decide what they mean.
Predefined semantic meanings of some metadata fields might be added in the future  or within the context of 
certain use cases. 


### Named Tokens
A PDL named token is used to represent fields that do not have their own special token type character.
Instead the name of a named token tells what kind of token it is. Thus, named tokens are more verbose than tokens
that use token type characters.

As explained earlier in this PDL specification, a named token actually consists of 3 or or more individual tokens.
Here is an example named token:

    tokenNamePart(
    "nested field part;
    )

The first subtoken of a named token uses any alphabetic character as token type character.
This first subtoken used the ( character as subtoken end character.

Any nested fields are just regular PDL tokens / fields.

The last subtoken of a named token is the single character token ) .

Here are some example PDL named tokens using different syntax suggestions:

    id(123;)
    ref(123;)

Note:
There are currently no predefined named tokens in Polymorph Data Language (PDL). 
You could construct your own named tokens. For instance, you could create a 
concatenation named token that could look like this:

    concat( "John and Jane went for a walk.; 
            "They then had coffee.;
            "Then they walked home.;
    )

This construct could be interpreted by your processor to mean that the 3 nested UTF-8 PDL fields
should be concatenated into a single UTF-8 PDE field.


## Whitespace
Polymorph Data Language considers all ASCII characters with a decimal value of 32 or lower to be whitespace.
That includes carriage return, new line and several other non-visible characters.

Note: For now, the ASCII character DEL with the decimal value 127 is not considered whitespace.

Whitespace characters can be located between PDL tokens. Inside a PDL token, whitespace characters are considered
part of the token.


## ASCII With UTF-8 Zones
A PDL file / stream is considered an ASCII file with UTF-8 zones. All tokens use ASCII characters 
except UTF-8 tokens and bytes tokens encoded using UTF-8. But even bytes tokens with UTF-8 encoded values,
and UTF-8 tokens use ASCII token type characters (begin characters) and token end characters.

Knowing this is a big help during implementation of a tokenizer.


## Minification
Minification is the process of removing all unnecessary characters from a PDL script. Typically, what is removed
are all the whitespace characters between the PDL tokens. 

Here is an example of a PDL script that is not yet minified:

    { .field1; "value; .field2; 123; .field3; /123.45; .field4; !1; }

Here is the same example minified:

    {.field1;"value;.field2;123;.field3;/123.45;.field4;!1;}

Tokenizing minified PDL can be faster than tokenizing non-minified PDL, for two reasons.
First, minified PDL simply has fewer characters to process. Second, you don't need to scan for
whitespace characters in between the PDL tokens. This removes the loop checking for whitespace characters
from the tokenization process, making it simpler and faster.


## Parallel Tokenization

Due to PDl's stringent syntax it is possible to parallelize the tokenization of PDL scripts. This is most likely 
only beneficial for larger PDL scripts - but nevertheless it is possible.

If you need to tokenize a PDL script of e.g. 10 MB you can split those 10 MB up into 10 blocks of 1 MB each.
Each of these blocks can be tokenized in parallel.

The boundaries of these blocks may fall within a token and not exactly between tokens. Therefore it is necessary
to search from a block boundary forward until the next token starts, and then tokenize within that byte range. 
E.g.: 

 - 0 to 1 MB + 2 bytes
 - 1 MB + 3 bytes to 2 MB + 5 bytes
 - 2 MB + 6 bytes to 3 MB + 1 byte
 - 3 MB + 2 bytes to 4 MB + 4 bytes
 - etc.

The challenge of parallel tokenization is, that if you jump into any script (PDL, JSON, CSV etc.) at a certain byte index, 
how do you  find out where the next token starts? In JSON, if you find a comma, but it was actually a comma inside a quoted
string, then it is not actually a token boundary comma. 

In PDL, semicolon is the most commonly used token end character and semicolons cannot exist anywhere else than
as a token end character. If you need to represent a semicolon in an ASCII or UTF-8 sequence it must be
escaped. See the escaping section in the UTF-8 field description.

Because of this special characteristic of the semicolon character in PDL, you can search for the next semicolon
from a block boundary. When found, you know for sure that a new token starts after that semicolon, possibly after
some whitespace characters. Thus, semicolons can be used effectively to find out where to start tokenizing from,
and where to tokenize to, for any given PDL block.


## FULL PDL Example

Here is a full PDL example showing all of the built-in PDL token and field types:

    # single-token comment;
    *"text1; "text2; "text3; multi-token comment~
    
    !; !0; !1;
    123;
    %123.45;
    /123.45;
    "Hello world;
    @2030-12-31T23:59:59.999;
    
    {.f1; 123; .f2; "val2;}
    {123; "val2;}
    
    [.c1; .c2; .c3;
     123; "val2; @2023;
     456; "val5; @2024; ]
    
    { .name; "Aya;
      .children; [
        .name; .children;
        "Gretchen; [
          .name; .children;
          "Rami; []
          "Fana; []
        ]
        "Hansel; [
          .name;   .children;
          "Gordia; []
          "Victor; []
        ]
      ]
    }
    
    :0;
    { .name; "Parent;
      .child; {
        .parent; &0;
      }
    }
    
    :1;
    { .name; "mother;
      .parent; !{;
      .children; [
        .name;     .parent;  .children;
        "child1;   &1;       []
        "child2;   &1;       []
      ]
    }