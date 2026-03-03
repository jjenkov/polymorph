# Polymorph Data Language (PDL) Design
As mentioned in the [Polymorph Data Language (PDL) Specification](../specifications/pdl-specification.md), 
I started designing the syntax for PDL by initially using a JSON-like syntax. However, I soon realized that 
branch mis-predictions during parsing of this syntax were a significant performance bottleneck. 

To address this, I redesigned the PDL syntax to minimize branches in the parser, resulting in a more efficient and 
predictable parsing process. Additionally, the PDL tokenizer became a good deal simpler than a JSON tokenizer.

The resulting PDL syntax is unfortunately a bit different from what we are used to seeing in JSON or
in programming languages. However, it is my experience that you get used to this syntax reasonably quickly,
especially once you understand why it is designed this way.

This document provides a detailed explanation of the design process and the resulting syntaxes 
for Polymorph Data Language (PDL).

Note: It is helpful if you are already familiar with the PDL syntax when reading this document, because
I do not explain what each single token represents. I assume you already know what the tokens represent.
This document mostly focuses on why the syntax ended up like it did.


## Syntax Designs

So far I have experimented with 3 different syntax designs for PDL. I call them:

- POS 0
- POS 1
- POS 2

POS is short for "Parser Optimized Syntax". These 3 design variations will be explained in the
following sections.


## Tokenizer Challenges

The two biggest challenges I have come across in tokenizer designs are:

- Finding the end of a token.
- Determining the type of the token.
 

### Finding the Beginning of a Token

Finding the beginning of a token is typically just scanning over any (if any) white space characters either at the beginning
of the input text or after the end of the previous token. The next non-white space character is the beginning
of the next token.


### Finding the End of a Token

Finding the end of that token, however, is not always easy. Where a token ends depends on the syntax. In JSON,
a token can end at a white space, a colon ( : ), a comma ( , ) , a quote character ( " ), a bracket ( { } or [ ] ) etc. 
This means that you will have to check each character of the input against more than one possible
end-of-token delimiting character.

There are several ways to implement the check of an input character against multiple end-of-token characters, so 
performance is not completely terrible. 
However, I realized it was smarter to change the syntax so that all tokens have one, and only one, possible end-of-token delimiting character. 

The first version of this changed syntax was the Parser Optimized Syntax 0 (POS 0). However, POS 0 is not that easy to read or write, 
so I made two more variations before I settled on a syntax. 

Unfortunately, POS 0 is the fastest of the syntaxes and the easiest to implement a tokenizer for, but sometimes compromises
must be made between performance and readability and writeability.

Even though POS 0 and 1 are not the final PDL syntax, I will take you through the design of both. This will help you
understand the design choices made in the final syntax, POS 2. 


### Parser Optimized Syntax 0
In POS0 the following is true for all tokens in the language:

- The first character of a token specifies the token type.
- All tokens end with a semicolon character ( ; ) .
- String tokens that contain semicolon characters within the token value - will need to escape the semicolon, 
  so no semicolons occur anywhere else than as token end markers.

Using these three rules, I came up with the following syntax for POS0:

    # single-token comment;
    
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
    
    :0;
    {; .name; "Parent;
      .child; {;
        .parent; &0;
      };
    };
    
    :1;
    {; .name; "mother;
      .parent; !{;
      .children; [;
        .name;     .parent;  .children;
        "child1;   &1;       [;];
        "child2;   &1;       [;];
      ];
    };


As you can see, it is in some ways similar to JSON – but with a few differences. 

First, the token type character is different. The token type character is the first character of each token.
JSON does not always have token type characters, but kind of do for some types of tokens ( arrays, objects and strings ).

Second, the fact that all tokens are at least 2 characters long is a difference. Thus, objects and arrays are demarcated like this:

    {;
    };

    [;
    ];

This is not a super beautiful syntax, nor as compact as it could be, but it has the advantage of being very easy and
fast to tokenize. This is all the code needed to tokenize the above script:

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

Not only is this code easy to write - it also executes quite fast! There are not that many branches in this
code. The few branches the code has are reasonably predictable, especially for longer tokens. Therefore, the number
of branch mispredictions is lower.

Unfortunately, I could not live with the 2-character tokens [; ]; {; }; so I sought to improve the syntax.
This process lead to the Parser Optimized Syntax 1.


### Parser Optimized Syntax 1

The second syntax I experimented with for PDL is what I call Parser Optimized Syntax 1 (POS 1).

The purpose of the POS 1 syntax variation was to get object and array demarcations down to one character, like this:

    {
    }

    [
    ]

Additionally, I wanted the named token syntax to be shorter. A named token is actually a composite textual element 
represented using multiple tokens. However, a named token typically still only represents a single PDE-field.
Named tokens are thus a somewhat longer way of representing single PDE-field tokens in PDL.

I wanted the named token syntax abbreviated from

    *id;(;+0;);
    *ref;(;0;);

which uses 4 tokens to represent a named token, e.g.:

    *id;    (;    +0;    );

to

    *id(+0;)
    *ref(+0;)

which only uses 3 tokens to represent a named token, e.g.:

    *id(    +0;    )

The ( character is now used as the token end character for the first token of a named token, instead of semicolon.
This saves 1 semicolon character plus the (; token.

Also, the ) character is now a single character token. This saves one more semicolon character. 
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

Notice how the token type character is now examined to see if it is also the token end character.
For tokens that were before 2-character tokens, but which are now 1-character tokens, this makes no difference.
Two characters are checked in both cases.

But for tokens where the token type character is not also the token end character, one more character is now checked
to find the token end delimiting character. The token type character is checked unnecessarily. 
This is a small performance penalty. But I have not found any fast way to avoid this extra character check.

Parser Optimized Syntax 1 is a bit slower to tokenize than POS 0 – but the syntax is a bit easier to read and write.
However, since some tokens require fewer characters to represent, you gain a little speed from
having fewer bytes to store, load and transport. However, even with the saved characters, this tokenizer is slower
than the POS 0 tokenizer.

Parser Optimized Syntax 1 also enabled me to add a multi-token comment that looks like this:

    * Multi-token comment 0; 1; "blabla; ~

POS 1 enabled me to map the * token type character to the ~ token end character. Thus, a multi-token
comment can now easily enclose multiple normal tokens. A single-token comment only extends until the
first semicolon character, so commenting out a single token is easy, but commenting out multiple tokens
would require you to remove the end semicolon for each of the enclosed tokens. For instance,

    #"UTF-8 token that was commented out;
    #"text1 "text2 "text3 ;

The second line is a single-token comment that spans 3 UTF-8 tokens, which had their end semicolons removed to be included
inside the single-token comment syntax. The same 3 UTF-8 token could have been commented out using a single 
multi-token comment, like this:

    *"text1; "text2; "text3; ~

No need to remove the end semicolons.


### Parser Optimized Syntax 2

The third syntax variation I experimented with is what I call Parser Optimized Syntax 2 (POS 2).

The purpose of the POS2 syntax variation is to get rid of the * type character in front of named tokens.
Thus, the named token

    *ref(+0;)

would become

    ref(+0;)

Additionally, this syntax variation could be used to get rid of the + type character in front of positive integers,
so the above named token parameter would be encoded like this:

    ref(0;)

This is one step closer to what we are used to from various programming languages.

To be able to tokenize this syntax using the tokenizer from the Parser Optimized Syntax 1, we would have to 
accept that more than one token type character could be mapped to the same token type. In other words, instead
of a named token always starting with the * character, now any alphabetic character as token type character
means that the token is a named token.

Similarly, any decimal token type character ( 0 to 9 ) means that the token is a positive integer token.
Thus, the + in front of positive integer tokens is no longer necessary.

The price of this syntax variation is not paid by the tokenizer. Instead, the code that iterates the tokens
have to be changed to allow for multiple characters to be mapped to the same token type. This is not a big change,
and I don't think there is any significant performance penalty for it.

The resulting POS 2 syntax looks like this:

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
          .name; .children;
          "Gordia; []
          "Victor; []
        ]
      ]
    }
    
    ;0;
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
