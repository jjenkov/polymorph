package com.plmph.pde;

public class PdeFieldTypes {

    public static final int BOOLEAN_NULL = 0;
    public static final int BOOLEAN_TRUE = 1;
    public static final int BOOLEAN_FALSE = 2;

    public static final int INT_NULL = 3;
    public static final int INT_POS_1_BYTES = 4;
    public static final int INT_POS_2_BYTES = 5;
    public static final int INT_POS_3_BYTES = 6;
    public static final int INT_POS_4_BYTES = 7;
    public static final int INT_POS_5_BYTES = 8;
    public static final int INT_POS_6_BYTES = 9;
    public static final int INT_POS_7_BYTES = 10;
    public static final int INT_POS_8_BYTES = 11;

    public static final int INT_NEG_1_BYTES = 12;
    public static final int INT_NEG_2_BYTES = 13;
    public static final int INT_NEG_3_BYTES = 14;
    public static final int INT_NEG_4_BYTES = 15;
    public static final int INT_NEG_5_BYTES = 16;
    public static final int INT_NEG_6_BYTES = 17;
    public static final int INT_NEG_7_BYTES = 18;
    public static final int INT_NEG_8_BYTES = 19;

    public static final int FLOAT_NULL   = 20;
    public static final int FLOAT_4_BYTES = 21;
    public static final int FLOAT_8_BYTES = 22;

    public static final int BYTES_NULL   = 23;
    public static final int BYTES_0_BYTES = 24;
    public static final int BYTES_1_BYTES = 25;
    public static final int BYTES_2_BYTES = 26;
    public static final int BYTES_3_BYTES = 27;
    public static final int BYTES_4_BYTES = 28;
    public static final int BYTES_5_BYTES = 29;
    public static final int BYTES_6_BYTES = 30;
    public static final int BYTES_7_BYTES = 31;
    public static final int BYTES_8_BYTES = 32;
    public static final int BYTES_9_BYTES = 33;
    public static final int BYTES_10_BYTES = 34;
    public static final int BYTES_11_BYTES = 35;
    public static final int BYTES_12_BYTES = 36;
    public static final int BYTES_13_BYTES = 37;
    public static final int BYTES_14_BYTES = 38;
    public static final int BYTES_15_BYTES = 39;
    public static final int BYTES_1_LENGTH_BYTES = 40;
    public static final int BYTES_2_LENGTH_BYTES = 41;
    public static final int BYTES_3_LENGTH_BYTES = 42;
    public static final int BYTES_4_LENGTH_BYTES = 43;
    public static final int BYTES_5_LENGTH_BYTES = 44;
    public static final int BYTES_6_LENGTH_BYTES = 45;
    public static final int BYTES_7_LENGTH_BYTES = 46;
    public static final int BYTES_8_LENGTH_BYTES = 47;

    public static final int ASCII_NULL   = 48;
    public static final int ASCII_0_BYTES = 49;
    public static final int ASCII_1_BYTES = 50;
    public static final int ASCII_2_BYTES = 51;
    public static final int ASCII_3_BYTES = 52;
    public static final int ASCII_4_BYTES = 53;
    public static final int ASCII_5_BYTES = 54;
    public static final int ASCII_6_BYTES = 55;
    public static final int ASCII_7_BYTES = 56;
    public static final int ASCII_8_BYTES = 57;
    public static final int ASCII_9_BYTES = 58;
    public static final int ASCII_10_BYTES = 59;
    public static final int ASCII_11_BYTES = 60;
    public static final int ASCII_12_BYTES = 61;
    public static final int ASCII_13_BYTES = 62;
    public static final int ASCII_14_BYTES = 63;
    public static final int ASCII_15_BYTES = 64;
    public static final int ASCII_1_LENGTH_BYTES = 65;
    public static final int ASCII_2_LENGTH_BYTES = 66;
    public static final int ASCII_3_LENGTH_BYTES = 67;
    public static final int ASCII_4_LENGTH_BYTES = 68;
    public static final int ASCII_5_LENGTH_BYTES = 69;
    public static final int ASCII_6_LENGTH_BYTES = 70;
    public static final int ASCII_7_LENGTH_BYTES = 71;
    public static final int ASCII_8_LENGTH_BYTES = 72;

    public static final int UTF_8_NULL   = 73;
    public static final int UTF_8_0_BYTES = 74;
    public static final int UTF_8_1_BYTES = 75;
    public static final int UTF_8_2_BYTES = 76;
    public static final int UTF_8_3_BYTES = 77;
    public static final int UTF_8_4_BYTES = 78;
    public static final int UTF_8_5_BYTES = 79;
    public static final int UTF_8_6_BYTES = 80;
    public static final int UTF_8_7_BYTES = 81;
    public static final int UTF_8_8_BYTES = 82;
    public static final int UTF_8_9_BYTES = 83;
    public static final int UTF_8_10_BYTES = 84;
    public static final int UTF_8_11_BYTES = 85;
    public static final int UTF_8_12_BYTES = 86;
    public static final int UTF_8_13_BYTES = 87;
    public static final int UTF_8_14_BYTES = 88;
    public static final int UTF_8_15_BYTES = 89;
    public static final int UTF_8_1_LENGTH_BYTES = 90;
    public static final int UTF_8_2_LENGTH_BYTES = 91;
    public static final int UTF_8_3_LENGTH_BYTES = 92;
    public static final int UTF_8_4_LENGTH_BYTES = 93;
    public static final int UTF_8_5_LENGTH_BYTES = 94;
    public static final int UTF_8_6_LENGTH_BYTES = 95;
    public static final int UTF_8_7_LENGTH_BYTES = 96;
    public static final int UTF_8_8_LENGTH_BYTES = 97;

    public static final int UTC_NULL    = 98;
    public static final int UTC_2_BYTES = 99; // only year
    public static final int UTC_3_BYTES = 100; // year + month
    public static final int UTC_4_BYTES = 101; // year + month + day
    public static final int UTC_5_BYTES = 102; // year + month + day + 24 hour
    public static final int UTC_6_BYTES = 103; // year + month + day + 24 hour + minutes
    public static final int UTC_7_BYTES = 104; // year + month + day + 24 hour + minutes + seconds
    public static final int UTC_8_BYTES = 105; // UTC timestamp in milliseconds (like system timestamps).
    public static final int UTC_9_BYTES = 106; // year + month + day + 24 hour + minutes + seconds + milliseconds
    public static final int UTC_10_BYTES = 107; // year + month + day + 24 hour + minutes + seconds + nanoseconds (3 bytes)

    public static final int COPY_1_BYTES = 108; // are "copies" ever needed? Or is a REFERENCE good enough for all use cases?
    public static final int COPY_2_BYTES = 109;
    public static final int COPY_3_BYTES = 110;
    public static final int COPY_4_BYTES = 111;
    public static final int COPY_5_BYTES = 112;  //does it make sense to copy something which exists more than 4BG earlier in a file?
    public static final int COPY_6_BYTES = 113;  //does it make sense to copy something which exists more than 4BG earlier in a file?
    public static final int COPY_7_BYTES = 114;  //does it make sense to copy something which exists more than 4BG earlier in a file?
    public static final int COPY_8_BYTES = 115;  //does it make sense to copy something which exists more than 4BG earlier in a file?

    public static final int REFERENCE_1_BYTES = 116;
    public static final int REFERENCE_2_BYTES = 117;
    public static final int REFERENCE_3_BYTES = 118;
    public static final int REFERENCE_4_BYTES = 119;
    public static final int REFERENCE_5_BYTES = 120; //does it make sense to reference something which exists more than 4BG earlier in a file?
    public static final int REFERENCE_6_BYTES = 121; //does it make sense to reference something which exists more than 4BG earlier in a file?
    public static final int REFERENCE_7_BYTES = 122; //does it make sense to reference something which exists more than 4BG earlier in a file?
    public static final int REFERENCE_8_BYTES = 123; //does it make sense to reference something which exists more than 4BG earlier in a file?

    public static final int KEY_NULL   = 124;        //do null keys make sense?
    public static final int KEY_0_BYTES = 125;       //do empty "string" / 0 byte keys make sense?
    public static final int KEY_1_BYTES = 126;
    public static final int KEY_2_BYTES = 127;
    public static final int KEY_3_BYTES = 128;
    public static final int KEY_4_BYTES = 129;
    public static final int KEY_5_BYTES = 130;
    public static final int KEY_6_BYTES = 131;
    public static final int KEY_7_BYTES = 132;
    public static final int KEY_8_BYTES = 133;
    public static final int KEY_9_BYTES = 134;
    public static final int KEY_10_BYTES = 135;
    public static final int KEY_11_BYTES = 136;
    public static final int KEY_12_BYTES = 137;
    public static final int KEY_13_BYTES = 138;
    public static final int KEY_14_BYTES = 139;
    public static final int KEY_15_BYTES = 140;
    public static final int KEY_1_LENGTH_BYTES = 141;
    public static final int KEY_2_LENGTH_BYTES = 142;

    public static final int OBJECT_NULL = 143;
    public static final int OBJECT_1_LENGTH_BYTES = 144;
    public static final int OBJECT_2_LENGTH_BYTES = 145;
    public static final int OBJECT_3_LENGTH_BYTES = 146;
    public static final int OBJECT_4_LENGTH_BYTES = 147;
    public static final int OBJECT_5_LENGTH_BYTES = 148;
    public static final int OBJECT_6_LENGTH_BYTES = 149;
    public static final int OBJECT_7_LENGTH_BYTES = 150;
    public static final int OBJECT_8_LENGTH_BYTES = 151;

    public static final int TABLE_NULL = 152;
    public static final int TABLE_1_LENGTH_BYTES = 153;
    public static final int TABLE_2_LENGTH_BYTES = 154;
    public static final int TABLE_3_LENGTH_BYTES = 155;
    public static final int TABLE_4_LENGTH_BYTES = 156;
    public static final int TABLE_5_LENGTH_BYTES = 157;
    public static final int TABLE_6_LENGTH_BYTES = 158;
    public static final int TABLE_7_LENGTH_BYTES = 159;
    public static final int TABLE_8_LENGTH_BYTES = 160;


    /*
    public static final int COMPOSITE_NULL           = 137;
    public static final int COMPOSITE_1_LENGTH_BYTES = 138;
    public static final int COMPOSITE_2_LENGTH_BYTES = 139;
    public static final int COMPOSITE_3_LENGTH_BYTES = 140;
    public static final int COMPOSITE_4_LENGTH_BYTES = 141;
    public static final int COMPOSITE_5_LENGTH_BYTES = 142;
    public static final int COMPOSITE_6_LENGTH_BYTES = 143;
    public static final int COMPOSITE_7_LENGTH_BYTES = 144;
    public static final int COMPOSITE_8_LENGTH_BYTES = 145;
    */

    // meta-data - such as:
    // - stream ID
    // - semantic type ala a class name or similar

    // ASCII - as supplement to UTF-8 ?

    // Tag? ... like - 1 to 8 byte single-value tags? (the value itself also contains its semantic meaning)

    // Table record batches?
    // Object key + value pair batches?

    // Composite Types:
    //  - uniform graph - same column names for all child nodes (like a Table with nested Tables, but with only the root table having columns - since all nested Tables have the same column names as the root Table).
    //  - file ( file name + mime type data )
    //  - compressed  ( compression type )  ?!?
    //  - encrypted   ( encryption type )   ?!?
    //  - signed
    //  - encrypted, signed


    /*
        From the indexes above to the indexes below is a space of indexes (146 to 240) which can be used for
        contextually dependent field types (custom field types) - which can be interleaved with PIN data. E.g. special indexes for
        Polymorph Protocol specific fields, or Protocol Streams specific fields.

        It is recommended to start from 240 and move down towards the "top" of the PIN fields (currently index 145). That way the "gap"
        in indexes will appear between your custom fields and the bottom predefined PIN fields. This reduces the chance
        that a new PIN field added later will use the same indexes as you have used in your custom fields.
     */

    public static final int METADATA_NULL = 232;
    public static final int METADATA_1_LENGTH_BYTES = 233;
    public static final int METADATA_2_LENGTH_BYTES = 234;
    public static final int METADATA_3_LENGTH_BYTES = 235;
    public static final int METADATA_4_LENGTH_BYTES = 236;
    public static final int METADATA_5_LENGTH_BYTES = 237;
    public static final int METADATA_6_LENGTH_BYTES = 238;
    public static final int METADATA_7_LENGTH_BYTES = 239;
    public static final int METADATA_8_LENGTH_BYTES = 240;

    public static final int EXTENSION_1_BYTE_1 = 241;
    public static final int EXTENSION_1_BYTE_2 = 242;
    public static final int EXTENSION_1_BYTE_3 = 243;
    public static final int EXTENSION_1_BYTE_4 = 244;
    public static final int EXTENSION_1_BYTE_5 = 245;
    public static final int EXTENSION_1_BYTE_6 = 246;
    public static final int EXTENSION_1_BYTE_7 = 247;
    public static final int EXTENSION_1_BYTE_8 = 248;
    public static final int EXTENSION_2_BYTES  = 249;
    public static final int EXTENSION_3_BYTES = 250;
    public static final int EXTENSION_4_BYTES = 251;
    public static final int EXTENSION_5_BYTES = 252;
    public static final int EXTENSION_6_BYTES = 253;
    public static final int EXTENSION_7_BYTES = 254;
    public static final int EXTENSION_8_BYTES = 255;

}
