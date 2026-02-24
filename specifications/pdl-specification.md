# Polymorph Data Language (PDL) Specification

Polymorph Data Language (PDL) is a textual data encoding that is capable of representing the same data types as
the Polymorph Data Encoding (PDE). You can convert from the binary PDE to PDL, and from PDL to PDE. 

See more about the purpose of PDE in the README file of this repository, here:

[README.md](README.md)

Polymorph Data Language can also be used by itself as an alternative to CSV, JSON, XML and YAML.
See more about how PDL compares to other data formats in the README file here:

[README.md](README.md)


## Table of Contents

- Polymorph Data Language Use Cases
- Syntax Variations and Performance
- Syntax Designs
- Token Type Characters
- Token Types


## Polymorph Data Language Use Cases 

PDL is useful for debug purposes, where data in PDE can be converted to PDL to make the data easier to read.

PDL can also be useful for hand-editing smaller documents such as configuration files or smaller data sets.

You could also use PDL as an alternative to JSON, YAML, CSV or XML, if you prefer to stay with a fully textual data format.
PDL has more features than JSON, YAML, CSV and XML, so you would get more options with PDL than with any of these formats.


## Syntax Variations and Performance

When I started designing the syntax for PDL I started out with a syntax that was similar to the syntax of JSON.
However, as I started optimizing the tokenizer for this syntax, I realized that one of the biggest performance killers was
branch mis-predictions. Every time I either removed a branch, or made it more predictable, performance went up.
Of course there were other optimizations too - but the branch mis-predictions seemed to be the biggest issue.

Having realized that, I started looking into re-designing the PDL syntax to make it easier to parse. 
In other words, to make it parseable with fewer branches in the code. I explain this design process and the
resulting syntaxes in the following 


## Syntax Designs

So far I have experimented with 3 different syntax designs for PDL. I call them:

- POS0
- POS1
- POS2

POS is short for "Parser Optimized Syntax".


### Tokenizer Challenges

The 2 biggest challenges I have come across in tokenizer designs are:

 - Finding the end of a token.
 - Determining the type of the token.

Finding the beginning of a token is typically just scanning over any (if any) white spaces at the beginning
of the input text - or since the end of the previous token. The next non-white space character is the beginning
of the next token.

Finding the end of that token, however, is not always easy. Where a token ends depends on the syntax. In JSON,
a token can end at a white space, a colon (:), a comma (,) , a quote character, a bracket ({}) etc. This means,
that you will have to check each character found during tokenization of a token against more than one possible
end-of-token delimiting character - to determine if the given character is part of the token or not.

There are several ways to do this more or less cleverly. However, the smartest way I found was to make a small
decision about the syntax - which resulted in Parser Optimized Syntax 0:

- The first character of a token specifies the token type.
- All tokens always end with the same character (I chose semicolon (;) ).

Thus, a token could look like this:

    +123;

The + tells that this is a positive integer token. The ; marks the end of that token.

I will get into a bit more detail about POS0, POS1 and POS2 in the following sections.


### Parser Optimized Syntax 0

As mentioned above, the first parser optimized syntax I experimented with is the syntax I call 0 - 
or Parser Optimized Syntax 0 (POS0).

In POS0 the following is true for all tokens in the language:

- The first character of a token specifies the token type.
- All tokens end with a semicolon character (;) .
- String tokens that would normally contain semicolon character within the token value - will need to escape the semicolon, so no semicolons occur anywhere else than as token end markers.

Using these three rules, I came up with the following syntax for POS0:

    # single-line comment;
    
    !; !0; !1;
    +123;
    %123.45;
    /123.45;
    "Hello world;
    @2030-12-31T23:59:59.999;
    
    {;.f1; +123; .f2; "val2;};
    {;+123; "val2;};
    
    [;.c1; .c2; .c3;
     +123; "val2; @2023;
     +456; "val5; @2024; ];
    
    {; .name; "Aya;
      .children; [;
        .name; .children;
        "Gretchen; [;
          .name; .children;
          "Rami; [;];
          "Fana; [;];
        ];
        "Hansel; [;
          .name; .children;
          "Gordia; [;];
          "Victor; [;];
        ];
      ];
    };
    
    *id;(;+0;);
    {; .name; "Parent;
      .child; {;
        .parent; *ref;(;+0;);
      };
    };
    
    *id;(;+0;);
    {; .name; "mother;
      .parent; o;
      .children; [;
        .name;     .parent;  .children;
        "child1;  *ref;(;0;);   [;];
        "child2;  *ref;(;0;);   [;];
      ];
    };


As you can see, it is in some ways similar to JSON - but with a few differences. First of all, the first token type
character is different. But - also the fact that all tokens are at least 2 characters long is a difference. Thus,
objects and arrays are demarcated like this:

    {;
    };

    [;
    ];

This is not a super beautiful syntax, nor as compact as it could be, but it has the advantage of being very easy and
fast to tokenize. This is all the code needed tokenize the above script:

    public void tokenizeMinified(Utf8Buffer buffer, TokenizerListener listener){
        buffer.skipWhiteSpace();

        while(buffer.hasMoreBytes()){
            int tokenStartOffset = buffer.tempOffset;
            buffer.tempOffset++;
     
            while(buffer.hasMoreBytes()){
                if(buffer.nextCodepointAscii() == ';'){
                    break;
                }
            }
     
            listener.token(tokenStartOffset, buffer.tempOffset, buffer.buffer[tokenStartOffset]);
            buffer.skipWhiteSpace();
        }
    }

This is only around 16 lines of code ! 

Not only is this code easy to write - it also executes quite fast! Since there are not that many branches in this
code, and the branches that are - are reasonably predictable especially for longer tokens - the execution speed of
this tokenizer is pretty good. 



### Parser Optimized Syntax 1

The second parser optimized syntax I experimented with for PDL is what I call Parser Optimized Syntax 1 (POS1).

The purpose of the POS1 syntax variation was to get object and array demarcations down to one character, like this:

    {
    }

    [
    ]

Additionally, the named token syntax should also be abbreviated from

    *id;(;+0;);
    *ref;(;0;);

to

    *id(+0;)
    *ref(+0;)

Notice how the ( character is now used as the named token end marker - e.g. to mark the end of the *id(  token .
Also notice how the ) character is now a single character token.

This syntax variation looks more like what we are used to from JSON and JavaScript etc. 

To tokenize this syntax variation reasonably easy and fast, I had to introduce a lookup table. 
I use the token type character to lookup what character is used to mark the end of that token.

Here is what the code looks like to tokenize the above syntax variation:

    public class PdlPfv2Tokenizer {
    
        private static final byte[] tokenEndCharacters = new byte[128];
    
        {
            for(int i=0; i<tokenEndCharacters.length; i++) {
                tokenEndCharacters[i] = ';';
            }
            tokenEndCharacters['{'] = '{';
            tokenEndCharacters['}'] = '}';
            tokenEndCharacters['['] = '[';
            tokenEndCharacters[']'] = ']';
            tokenEndCharacters['<'] = '<';
            tokenEndCharacters['>'] = '>';
            tokenEndCharacters['('] = '(';
            tokenEndCharacters[')'] = ')';
            tokenEndCharacters['*'] = '(';  
    
        }
    
        public void tokenize(Utf8Buffer buffer, TokenizerListener listener) {
            TokenIndex64Bit tokenIndex = (TokenIndex64Bit) listener;
    
            buffer.skipWhiteSpace();
            while(buffer.hasMoreBytes()){
                int tokenStartOffset = buffer.tempOffset;
                int endCharacter = tokenEndCharacters[buffer.buffer[tokenStartOffset]];
    
                while(buffer.hasMoreBytes()){
                    if(buffer.nextCodepointAscii() == endCharacter){
                        break;
                    }
                }
    
                listener.token(tokenStartOffset, buffer.tempOffset, buffer.buffer[tokenStartOffset]);
                buffer.skipWhiteSpace();
            }
        }
    }

Notice how the token type character is now also examined to see if it is the token end marker character. 
This means that for each token - one more character is checked. 

The token type character double check is only a performance penalty for tokens that actually both have a token type character 
and a separate end marker character. For these kinds of tokens the token type character gets checked 2 times and the
token end marker character gets checked 1 time. That is 3 boundary checks per token.

For the tokens where the token type character is also the token end marker character - 2 characters checks are performed
(against the same character) just as if that token had consisted of 2 separate characters.
That is only 2 boundary checks per token.

This syntax is a bit slower to tokenize than POS0 - but the syntax is a bit easier to read and write.
However, since some of the tokens require less characters to represent - you gain a little speed from 
having fewer bytes to store, load or transport. Even with the saved characters this tokenizer is slower
than the POS0 tokenizer.


### Parser Optimized Syntax 2

The third syntax variation I am currently experimenting with - I call Parser Optimized Syntax 2 (POS2).

The purpose of the POS2 syntax variation is to get rid of the * type character in front of named tokens.
Thus, the named token

    *ref(+0;)

would become

    ref(+0;)

Additionally, this syntax variation could be used to get rid of the + type character in front of positive integers,
so the above named token parameter would be encoded like this:

    ref(0;)

This is one step closer to what we are used to from programming languages.

To be able to tokenize this syntax and still determine the token type reasonably fast, one more
lookup table lookup would need to be added. The real type of a token would have to be looked up
by taking the first character in the token and looking up its actual type in a lookup table. 

For tokens that still has a token type character in the beginning - the value stored in the lookup
table will simply be the same as the first character of the token. Thus, for such tokens the lookup
is actually wasted.

For tokens that do not have a token type character in the beginning (named tokens and positive integer tokens) - 
the value stored in the lookup table will be different from the first character of the token. This lookup will
thus not be wasted.

The lookup table will look something like this (not finalized):

    byte[] tokenTypes = new byte[128];
    
    tokenTypes['a'] = '*';
    ...
    tokenTypes['z'] = '*';

    tokenTypes['A'] = '*';
    ...
    tokenTypes['Z'] = '*';

    tokenTypes['0'] = '+';
    ...
    tokenTypes['9'] = '+';

Looking up the first character of the named token "ref(" will result in a lookup with the character 'r' as index.
This will return the value '*' for that token type. 

Similarly, looking up the first character of the positive integer token "678" will result in a lookup with the character
'r' as index. This will return the value '+' for that token type.

The lookup code will look something like this (not finalized):

    int endCharacter = tokenEndCharacters[buffer.buffer[tokenStartOffset]];
    int tokenType    = tokenTypes[buffer.buffer[tokenStartOffset]];

This results in one extra lookup per token compared to the POS1 syntax - but saves the * and + characters in front
of named tokens and positive integer tokens.

Alternatively, the endCharacter and tokenType lookup tables could be merged into one big table. Instead of being
2 tables of bytes - it could be one table of shorts (2 bytes). As you can see, the first character of a token is
used as key in both tables - so the 2 lookups of 1 byte could be reduced to 1 lookup of 2 bytes. First byte (lsb)
could contain the token type - and the second byte (msb) could contain the end character.

With a merged lookup table the lookup code would something like this:

    int endCharacter = typesAndEndCharacters[buffer.buffer[tokenStartOffset]];
    int tokenType = endCharacter;

    tokenType = 0xFF & tokenType;      //remove msb containing end character
    endCharacter = endCharacter >> 8;  // remove lsb containing token type
    
Possibly, in assembly language you could move the endCharacter lookup value into a CPU register, and then only move
the lsb from that register into another register that would now contain the token type (only the lsb). That might be
slightly faster (1 instruction less) than first copying the whole endCharacter register (with the combined value)
to another register, and then AND the msb byte out.

As mentioned, the POS2 syntax variation is not finalized and has not been tested (implemented) nor benchmarked.
More info will be added when this work has been carried out.



## Token Type Characters
Here is a shorthand list of the token type characters used for different token types.
The token type character is the first character of a token.

| Character | Token Type                    | Token Examples                   |
|-----------|-------------------------------|----------------------------------|
| _         | Null                          | _"; _!; _+;                      |
| #         | Single-token comments         | #This is a single-token comment; |
| *         | Multi-token comments          | *This is a multi-token comment~  |
| $         | Binary data in hexadecimal    | $123A 44E3;                      |
| \|        | Binary data in base64         | \|QmFzZTY0IGRhdGE=;              |
| ^         | Binary data in UTF-8          | ^Binary data in UTF-8;           |
| !         | Booleans                      | !0; !1; !2;                      |
| +         | Positive integers             | +123;                            |
| -         | Negative integers             | -123;                            |
| %         | 32-bit floating point numbers | %123.45; %-123.45;               |
| /         | 64-bit floating point numbers | /567.89; /-567.89;               |
| '         | ASCII text                    | 'ASCII chars;                    |
| "         | UTF-8 text                    | "UTF-8 chars;                    |
| @         | UTC date and time             | @2030-12-31T23:59:59.999;        |
| :         | ID                            | :obj1;                           |
| =         | Copy of another field         | =obj1;                           |
| &         | Reference to another field    | &obj1;                           |
| .         | Keys                          | .key1; .column2; .property3;     |
| {         | Object begin                  | {; {                             |
| }         | Object end                    | }; }                             |
| [         | Table begin                   | [; [                             |
| ]         | Table end                     | ]; ]                             |
| <         | Metadata begin                | <; <                             |
| \>        | Metadata end                  | \>; >                            |
|           | Named tokens                  | id(+123;)  ref(+123;)            |


## Token Types

Polymorph Data Language has a set of token types that is should be able to represent - regardless of which syntax
variation that is used (which one becomes the final one is not yet decided). This section lists the token types
and shows how they would look in the different syntax variations suggested so far.

Note: As PDL is designed for streaming of data - there is no "root" token or root "object" - like you would normally
have in JSON or XML.


### Null Values
A PDL null token represents a null value. PDL null tokens use the _ character as type character and the ; character
as end marker character. 

Between the _ and the ; characters you put the type character of the type you want the null
to represent. In other words, PDL uses typed nulls. 

Here is an example PDL null token of the UTF-8 field type:

    _";

Here is an example PDL null token of the named token type:

    _specialField;


There are some PDL tokens (fields) it does not make sense to have null values for. These are:

- Nulls
- Comments
- Keys
- Metadata



### Comments
Comment tokens are intended for inserting comments into the PDL documents. YAML and XML has comments, but JSON 
and CSV does not have a standard comment format. Comments are useful e.g. in test data documents and in configuration files.

PDL has two types of comments:

- Single-token comments.
- Multi-token comments.

Single-token comments use the # character as type character.

Multi-token comments use the * character as type character.

A PDL single-token comment token would look like this:

    #This is a comment;

Single-token comments can be inserted anywhere between other PDL tokens - but not inside another token.

Single-token comments make it easy to comment out a single PDL token too. Here is an example:

    "This is a text;  #"This is also a text;  "This is one more text;

Notice how the middle UTF-8 token is commented out by placing a # character in front of that token.
This is possible because the single-comment token and the UTF-8 token both end with a semi-colon.

If the token you want to comment out does not end with a semi-colon, you also need to put a semi-colon 
after the token. Here is an example:

    #{; 

A PDL single-token comment can actually span multiple lines. Here is an example:

    #This is a 
     single-token comment
     that spans multiple lines;


In case you need to comment out multiple PDL tokens it can be annoying to have to explicitly comment out
every one of them individually - as shown in this example:

    #This is a single-token comment;

    #+123; 
    #/789.00;
    #"This is a text;

Notice how each of the tokens below the first comment token must be commented out individually, since each of them
ends with a ; which marks the end of a comment token too. This is annoying.

To solve this problem PDL also has multi-token comments. A multi-token comment uses the * as type character, 
and the ~ character as end marker character. Here is an example of a multi-token comment:

    *This is a multi-token comment~

This multi-token comment actually only consists of a single token. In fact, multi-token comments are all
only one token. The name comes from their ability to comment out multiple tokens more easily. 
Here is a multi-token comment example:

    *This is a multi-token comment
    +123; 
    /789.00;
    "This is a text;
    ~

While the above is actually still only a single PDL token - it comments out multiple tokens. It can do so because
it does not use the same end marker character as any other token in PDL.


### Binary
A PDL binary token represents binary data encoded using a textual representation. PDL supports 3 different
textual representations for binary data:

1) Hexadecimal encoding
2) Base64 encoding
3) UTF-8 encoding

Each of these are represented using a different token type character. Here are some example PDL binary field
tokens:

    $45A6 34E3;

    |VGhpcyBpcyBiYXNlNjQgZW5jb2RlZA==;

    ^This is binary data in UTF-8 encoding;

The $ character is the token type character for binary data encoded using hexadecimal encoding (white spaces allowed between digits).

The | character is the token type character for binary data encoded using base64 encoding.

The ^ character is the token type character for binary data encoded using UTF-8 encoding.



### Boolean
A PDL boolean token represents a boolean PDL field which can be either null, true or false. So far there is
no difference in how a boolean field is represented in PDL in the different syntax variances. Here are
three examples showing a null, true and false boolean PDL token (field):

    !0;    
    !1;

The ! character is the token type character for boolean tokens (fields). The value 0 represents a boolean field
with the value false. The value 1 represents a boolean field with the value true. 


### Integers
A PDL integer token represents an integer PDL field. Integers can have either null, positive or negative integer values.


Notice the + marking a positive integer value - immediately followed by the ; character, signaling that this integer
value is empty (null).

A positive integer will be encoded like this:

    +123;

Using the POS2 syntax it might be possible to omit the + in front of positive integer tokens.
In that case, they would be encoded like this:

    123;

A negative integer is encoded using the - character as type character, like this:

    -123;


### Floating Point Numbers
A PDL floating point token represents a floating point number. PDL enables you to represent both 32 bit and 64 bit
floating point numbers. The 32 bit floating point tokens use the % character as type character. 
The 64-bit floating point tokens use the / character as type character. 

Here are some example 32 bit floating point tokens:

    %123.45;
    %-123.45;

Here are some example 64 bit floating point tokens:

    /456.789;
    /-456.789;


### ASCII Text
A PDL ASCII text  token represents an ASCII text field. Sometimes it is useful to tell a consumer of data
that a string of text is only ASCII characters (exactly 1 byte per character) so they do not need to 
parse each character as an UTF-8 character.

The ASCII PDL token uses the ' character as token type character.

Notice how the ' character is immediately followed by the ; character.

Representing a PDL ASCII text token looks like this:

    'This is an ASCII text;



### UTF-8 Text
A PDL UTF-8 text token represents a UTF-8 text field. Sometimes it is useful to tell a consumer of data
that a string of text is UTF-8 characters (possibly more than 1 byte per character) so they need to
parse each character as an UTF-8 character.

The UTF-8 PDL token uses the " character as token type character.

Notice how the " character is immediately followed by the ; character.

Representing a PDL UTF-8 text token looks like this:

    "This is a UTF-8 text;


### UTC Date and Time
The PDL UTC token represents a date and time in UTC format (no time zones allowed). 
The UTC PDL token uses the @ character as type character. The UTC token can contain
year - or year + month + date + hour + minute + seconds + milliseconds - or anywhere
date + time in between. Here are all the allowed formats:

    @2030;
    @2030-12;
    @2030-12-31;
    @2030-12-31T23;
    @2030-12-31T23:59;
    @2030-12-31T23:59:59;
    @2030-12-31T23:59:59.999;


### ID
The PDL ID token represents an identifier you can reference via copy or reference field. 
The PDL ID token does not correspond to a field in PDE. Instead, an ID token represents
an "address" (offset) that you can reference from a copy or reference field. A bit similar
to a label in an assembly language program.

The PDL ID token uses the : character as token type character.

Here is how a PDL ID token looks:

    :obj1;

The ID token points to the PDL field immediately following the ID token. 
Here is a PDL ID token example:

    :obj1;
    { .name; "John Doe; }

In the above example the :obj1; field points to the following PDL object field.

See more info in the Copy and Reference fields for how to reference an ID field.


### Copy
The PDL copy token references an ID token earlier in the PDL script (it must be EARLIER - no forward references allowed).
The PDL copy token represents a "copy" of the PDL field immediately following the referenced ID field.

The PDL copy token uses the = character as token type character.
Here is a PDL copy token example:

    :obj1;
    { .name; "John Doe; }

    =obj1;

The copy field is useful if you have to repeat the same field multiple times in a PDL stream. Instead of
repeating the field - you can include it once and include copy fields pointing to the original.


### Reference
The PDL reference token references an ID token earlier in the PDL script (it must be EARLIER - no forward references allowed).
The PDL reference token represents a "reference" to the PDL field immediately following the referenced ID field.

The PDL reference token uses the & character as token type character.
Here is a PDL copy token example:

    :obj1;
    { .name; "John Doe; }

    { .name; "Jane Doe; .spouse;  &obj1; }

The PDL reference field is useful when you need to encode object graphs where one object references another.

PDL reference fields can also be used to represent cyclic object graphs. 
Here is an example of a cyclic object graph:

    :obj1;
    { .name; "John Doe; 
      .children; [
          { .name; "Phillis Doe; .parent;  &obj1; }
          { .name; "Aron Doe;    .parent;  &obj1; }
      ]
    }

Notice how the parent object references the two child objects via its nested table (without column names => array).
Notice also how both of the child objects reference the parent object via reference fields. This is a cyclic object graph.


### Key
The PDL key token is used to represent a property name inside a PDL object field (e.g. "property1") or a column name inside
a PDL table field (e.g. "column1"). The PDL key token uses the . character as token type character. 

Key tokens with null values are not allowed - as it does not really make sense to have a null key.

Here are some examples of PDL key tokens:

    .key1; .key2; .key3; 
    .col1; .col2; .col3; 
    .property1; .property2 .property3;


### Object
PDL object fields are demarcated by 2 tokens. An object begin token and an object end token.
The token type character for the object begin token is the { character. 
The token type character for the object end token is the } character. 

Here are some examples of PDL object begin and object end tokens using different syntax suggestions:

POS0:

    {;
    };

POS1:

    {
    }

Here are some PDL object fields with nested fields inside using POS1 syntax:

    { .firstName; "Jane; .lastName; "Doe; }
    { .nanme; "John Doe; .birthday; @1999-01-16; }


### Table
PDL table fields are demarcated by 2 tokens. A table begin token and a table end token.
The token type character for the table begin token is the [ character.
The token type character for the table end token is the ] character.

Here are some PDL table begin and end tokens using different syntax suggestions:

POS0:

    [;
    ];

POS1:

    [
    ]

Here are some PDL table fields with nested fields inside using POS1 syntax:

    [ .firstName; .lastName;
      "Jane;      "Doe;
      "John;      "Doe;
      "Joe;       "Blocks;
    ]

    [ .name; .birthday;
      "Jane Doe;   @1999-01-16;
      "John Doe;   @1995-09-18;
      "Joe Blocks; @1993-03-23; 
    ]

It is possible nest tables inside tables. This can be used to represent tree structures more compactly.
Here is a nested PDL table example using POS1 syntax:

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


### Metadata
A PDL metadata field contains metadata about the data in the PDL stream. What the metadata means semantically
is up to you to decide.  

A PDL metadata field is structurally similar to a PDL object. 
A PDL metadata field consists of a metadata start token < and a metadata end token > . 


Here is an example PDL metadata field in POS0 syntax:

    <; .type; "Customer; >;

Here is an example PDL metadata field in POS1 syntax:

    < .type; "Customer; >


A PDL metadata field typically precedes the PDL fields it belongs to.
Here is an example of a PDL metadata field belonging to a PDL object field:

    < .class; "com.company.project.product.Customer; >

    { .name; "John Doe";
      .customerId; 12345;
    }

In the above example the PDL metadata field contains the Java class name of the PDL object following it.
In other words, the metadata field signals that the object field was serialized from an object 
of the class com.company.project.product.Customer. 

Note: There are currently no predefined semantic meanings of metadata. It is up to you to decide what they mean.
I might introduce predefined semantic meanings of some metadata fields in the future. 


### Named Tokens
A PDL named token is used to represent fields that do not have their own special token type character.
Instead the name of a named token tells what kind of token it is. Thus, named tokens are more verbose than tokens
that use token type characters.

A named token uses the * as token type character. However, this only tells the tokenizer that this is a named token.
It does not tell the tokenizer what the actual token type is - which this token should represent. This must be
deduced from the name following the * character. 

Here are some example PDL named tokens using different syntax suggestions:

POS0:

    *id;(;+123;);
    *id(;+123;);   #This is 1 token shorter... and should be possible with POS0 syntax.;

    *ref;(;+123;);
    *ref(;+123;);  #This is 1 token shorter... and should be possible with POS0 syntax.;

POS1:

    *id(+123;)

    *ref(+123;)

POS2:

    id(123;)

    ref(123;)


