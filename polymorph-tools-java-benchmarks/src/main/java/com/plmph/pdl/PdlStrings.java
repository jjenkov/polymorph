package com.plmph.pdl;

import java.nio.charset.StandardCharsets;

public class PdlStrings {
    public static String pdlString
            = """
              #single-token comment;
              *multi-token  comment;~
              !0;
              123;
              -789;
              %123.45;
              /12345.6789;
              $23EF 45 A2;
              |QmFzZTY0IGRhdGE=;
              ^UTF-8 binary;
              'ASCII token;
              "UTF-8 token;
              @2030-12-31T23:59:59.999;
              :obj1;
              =obj1;
              &obj1;
              .key1;
              {
              }
              [
              ]
              <
              >
              namedToken( )
              """;

    public static byte[] pdlBytes = PdlStrings.pdlString.getBytes(StandardCharsets.UTF_8);


    public static String pdlString2
            = """
              #single-token comment;
              *multi-token  comment;~
              !0;
              123;
              -789;
              %123.45;
              /12345.6789;
              $23EF 45 A2;
              |QmFzZTY0IGRhdGE=;
              ^UTF-8 binary;
              'ASCII token;
              "UTF-8 token;
              @2030-12-31T23:59:59.999;
              :obj1;
              =obj1;
              &obj1;
              .key1;
              {
              }
              [ .c1; .c2; .c3;
                "v1_1; 123; 'v1_3;
                "v2_1; 456; 'v2_3;
                "v3_1; 789; 'v2_3;
              ]
              <
              >
              namedToken( )
              """;

    public static byte[] pdlBytes2 = PdlStrings.pdlString2.getBytes(StandardCharsets.UTF_8);

    public static String pdlString3 =
            """
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
            """;

    public static byte[] pdlBytes3 = PdlStrings.pdlString3.getBytes(StandardCharsets.UTF_8);


    public static String pdlString3_x10 = pdlString3 + pdlString3 + pdlString3 + pdlString3 + pdlString3 +
                                          pdlString3 + pdlString3 + pdlString3 + pdlString3 + pdlString3;

    public static byte[] pdlBytes3_x10 = PdlStrings.pdlString3_x10.getBytes(StandardCharsets.UTF_8);

    public static String pdlString3_minified = "# single-token comment;*\"text1; \"text2; \"text3; multi-token comment~!;!0;!1;123;%123.45;/123.45;\"Hello world;@2030-12-31T23:59:59.999;{.f1;123;.f2;\"val2;}{123;\"val2;}[.c1;.c2;.c3;123;\"val2;@2023;456;\"val5;@2024;]{.name;\"Aya;.children;[.name;.children;\"Gretchen;[.name;.children;\"Rami;[]\"Fana;[]]\"Hansel;[.name;.children;\"Gordia;[]\"Victor;[]]]}:0;{.name;\"Parent;.child;{.parent;&0;}}:1;{.name;\"mother;.parent;!{;.children;[.name;.parent;.children;\"child1;&1;[]\"child2;&1;[]]}";
    public static byte[] pdlBytes3_minified = pdlString3_minified.getBytes(StandardCharsets.UTF_8);

    public static String pdlString3_x10_minified = pdlString3_minified + pdlString3_minified + pdlString3_minified + pdlString3_minified + pdlString3_minified +
                                                   pdlString3_minified + pdlString3_minified + pdlString3_minified + pdlString3_minified + pdlString3_minified;        
    public static byte[] pdlBytes3_x10_minified = pdlString3_x10_minified.getBytes(StandardCharsets.UTF_8);

}
