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





}
