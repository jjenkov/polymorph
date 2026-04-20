package com.plmph.pde;

public class PdeFieldLengthSpecs {


    public static byte[] defaultPdeLengthEncodings() {
        byte[] encodings = new byte[256];

        encodings[PdeFieldTypes.BOOLEAN_NULL]  = 0x00;  // 0 length bytes, 0 value length (1 byte fields)
        encodings[PdeFieldTypes.BOOLEAN_TRUE]  = 0x00;
        encodings[PdeFieldTypes.BOOLEAN_FALSE] = 0x00;

        encodings[PdeFieldTypes.INT_NULL]        = 0x00;  // 0 length bytes, 0 value length (1 byte fields)
        encodings[PdeFieldTypes.INT_POS_1_BYTES]  = 0x01;
        encodings[PdeFieldTypes.INT_POS_2_BYTES]  = 0x02;
        encodings[PdeFieldTypes.INT_POS_3_BYTES]  = 0x03;
        encodings[PdeFieldTypes.INT_POS_4_BYTES]  = 0x04;
        encodings[PdeFieldTypes.INT_POS_5_BYTES]  = 0x05;
        encodings[PdeFieldTypes.INT_POS_6_BYTES]  = 0x06;
        encodings[PdeFieldTypes.INT_POS_7_BYTES]  = 0x07;
        encodings[PdeFieldTypes.INT_POS_8_BYTES]  = 0x08;
        encodings[PdeFieldTypes.INT_NEG_1_BYTES]  = 0x01;
        encodings[PdeFieldTypes.INT_NEG_2_BYTES]  = 0x02;
        encodings[PdeFieldTypes.INT_NEG_3_BYTES]  = 0x03;
        encodings[PdeFieldTypes.INT_NEG_4_BYTES]  = 0x04;
        encodings[PdeFieldTypes.INT_NEG_5_BYTES]  = 0x05;
        encodings[PdeFieldTypes.INT_NEG_6_BYTES]  = 0x06;
        encodings[PdeFieldTypes.INT_NEG_7_BYTES]  = 0x07;
        encodings[PdeFieldTypes.INT_NEG_8_BYTES]  = 0x08;

        encodings[PdeFieldTypes.FLOAT_NULL]       = 0x00;
        encodings[PdeFieldTypes.FLOAT_4_BYTES]    = 0x04;
        encodings[PdeFieldTypes.FLOAT_8_BYTES]    = 0x08;

        encodings[PdeFieldTypes.BYTES_NULL]       = 0x00;
        encodings[PdeFieldTypes.BYTES_0_BYTES]    = 0x00;
        encodings[PdeFieldTypes.BYTES_1_BYTES]    = 0x01;
        encodings[PdeFieldTypes.BYTES_2_BYTES]    = 0x02;
        encodings[PdeFieldTypes.BYTES_3_BYTES]    = 0x03;
        encodings[PdeFieldTypes.BYTES_4_BYTES]    = 0x04;
        encodings[PdeFieldTypes.BYTES_5_BYTES]    = 0x05;
        encodings[PdeFieldTypes.BYTES_6_BYTES]    = 0x06;
        encodings[PdeFieldTypes.BYTES_7_BYTES]    = 0x07;
        encodings[PdeFieldTypes.BYTES_8_BYTES]    = 0x08;
        encodings[PdeFieldTypes.BYTES_9_BYTES]    = 0x09;
        encodings[PdeFieldTypes.BYTES_10_BYTES]   = 0x0A;
        encodings[PdeFieldTypes.BYTES_11_BYTES]   = 0x0B;
        encodings[PdeFieldTypes.BYTES_12_BYTES]   = 0x0C;
        encodings[PdeFieldTypes.BYTES_13_BYTES]   = 0x0D;
        encodings[PdeFieldTypes.BYTES_14_BYTES]   = 0x0E;
        encodings[PdeFieldTypes.BYTES_15_BYTES]   = 0x0F;
        encodings[PdeFieldTypes.BYTES_1_LENGTH_BYTES]   = 0x10;
        encodings[PdeFieldTypes.BYTES_2_LENGTH_BYTES]   = 0x20;
        encodings[PdeFieldTypes.BYTES_3_LENGTH_BYTES]   = 0x30;
        encodings[PdeFieldTypes.BYTES_4_LENGTH_BYTES]   = 0x40;
        encodings[PdeFieldTypes.BYTES_5_LENGTH_BYTES]   = 0x50;
        encodings[PdeFieldTypes.BYTES_6_LENGTH_BYTES]   = 0x60;
        encodings[PdeFieldTypes.BYTES_7_LENGTH_BYTES]   = 0x70;
        encodings[PdeFieldTypes.BYTES_8_LENGTH_BYTES]   = (byte) (0xFF & 0x80);

        encodings[PdeFieldTypes.ASCII_NULL]       = 0x00;
        encodings[PdeFieldTypes.ASCII_0_BYTES]    = 0x00;
        encodings[PdeFieldTypes.ASCII_1_BYTES]    = 0x01;
        encodings[PdeFieldTypes.ASCII_2_BYTES]    = 0x02;
        encodings[PdeFieldTypes.ASCII_3_BYTES]    = 0x03;
        encodings[PdeFieldTypes.ASCII_4_BYTES]    = 0x04;
        encodings[PdeFieldTypes.ASCII_5_BYTES]    = 0x05;
        encodings[PdeFieldTypes.ASCII_6_BYTES]    = 0x06;
        encodings[PdeFieldTypes.ASCII_7_BYTES]    = 0x07;
        encodings[PdeFieldTypes.ASCII_8_BYTES]    = 0x08;
        encodings[PdeFieldTypes.ASCII_9_BYTES]    = 0x09;
        encodings[PdeFieldTypes.ASCII_10_BYTES]   = 0x0A;
        encodings[PdeFieldTypes.ASCII_11_BYTES]   = 0x0B;
        encodings[PdeFieldTypes.ASCII_12_BYTES]   = 0x0C;
        encodings[PdeFieldTypes.ASCII_13_BYTES]   = 0x0D;
        encodings[PdeFieldTypes.ASCII_14_BYTES]   = 0x0E;
        encodings[PdeFieldTypes.ASCII_15_BYTES]   = 0x0F;
        encodings[PdeFieldTypes.ASCII_1_LENGTH_BYTES]   = 0x10;
        encodings[PdeFieldTypes.ASCII_2_LENGTH_BYTES]   = 0x20;
        encodings[PdeFieldTypes.ASCII_3_LENGTH_BYTES]   = 0x30;
        encodings[PdeFieldTypes.ASCII_4_LENGTH_BYTES]   = 0x40;
        encodings[PdeFieldTypes.ASCII_5_LENGTH_BYTES]   = 0x50;
        encodings[PdeFieldTypes.ASCII_6_LENGTH_BYTES]   = 0x60;
        encodings[PdeFieldTypes.ASCII_7_LENGTH_BYTES]   = 0x70;
        encodings[PdeFieldTypes.ASCII_8_LENGTH_BYTES]   = (byte) (0xFF & 0x80);

        encodings[PdeFieldTypes.UTF_8_NULL]       = 0x00;
        encodings[PdeFieldTypes.UTF_8_0_BYTES]    = 0x00;
        encodings[PdeFieldTypes.UTF_8_1_BYTES]    = 0x01;
        encodings[PdeFieldTypes.UTF_8_2_BYTES]    = 0x02;
        encodings[PdeFieldTypes.UTF_8_3_BYTES]    = 0x03;
        encodings[PdeFieldTypes.UTF_8_4_BYTES]    = 0x04;
        encodings[PdeFieldTypes.UTF_8_5_BYTES]    = 0x05;
        encodings[PdeFieldTypes.UTF_8_6_BYTES]    = 0x06;
        encodings[PdeFieldTypes.UTF_8_7_BYTES]    = 0x07;
        encodings[PdeFieldTypes.UTF_8_8_BYTES]    = 0x08;
        encodings[PdeFieldTypes.UTF_8_9_BYTES]    = 0x09;
        encodings[PdeFieldTypes.UTF_8_10_BYTES]   = 0x0A;
        encodings[PdeFieldTypes.UTF_8_11_BYTES]   = 0x0B;
        encodings[PdeFieldTypes.UTF_8_12_BYTES]   = 0x0C;
        encodings[PdeFieldTypes.UTF_8_13_BYTES]   = 0x0D;
        encodings[PdeFieldTypes.UTF_8_14_BYTES]   = 0x0E;
        encodings[PdeFieldTypes.UTF_8_15_BYTES]   = 0x0F;
        encodings[PdeFieldTypes.UTF_8_1_LENGTH_BYTES]   = 0x10;
        encodings[PdeFieldTypes.UTF_8_2_LENGTH_BYTES]   = 0x20;
        encodings[PdeFieldTypes.UTF_8_3_LENGTH_BYTES]   = 0x30;
        encodings[PdeFieldTypes.UTF_8_4_LENGTH_BYTES]   = 0x40;
        encodings[PdeFieldTypes.UTF_8_5_LENGTH_BYTES]   = 0x50;
        encodings[PdeFieldTypes.UTF_8_6_LENGTH_BYTES]   = 0x60;
        encodings[PdeFieldTypes.UTF_8_7_LENGTH_BYTES]   = 0x70;
        encodings[PdeFieldTypes.UTF_8_8_LENGTH_BYTES]   = (byte) (0xFF & 0x80);

        encodings[PdeFieldTypes.UTC_NULL]     = 0x00;
        encodings[PdeFieldTypes.UTC_2_BYTES]  = 0x02;
        encodings[PdeFieldTypes.UTC_3_BYTES]  = 0x03;
        encodings[PdeFieldTypes.UTC_4_BYTES]  = 0x04;
        encodings[PdeFieldTypes.UTC_5_BYTES]  = 0x05;
        encodings[PdeFieldTypes.UTC_6_BYTES]  = 0x06;
        encodings[PdeFieldTypes.UTC_7_BYTES]  = 0x07;
        encodings[PdeFieldTypes.UTC_8_BYTES]  = 0x08;
        encodings[PdeFieldTypes.UTC_9_BYTES]  = 0x09;
        encodings[PdeFieldTypes.UTC_10_BYTES] = 0x0A;

        encodings[PdeFieldTypes.KEY_NULL]       = 0x00;
        encodings[PdeFieldTypes.KEY_0_BYTES]    = 0x00;
        encodings[PdeFieldTypes.KEY_1_BYTES]    = 0x01;
        encodings[PdeFieldTypes.KEY_2_BYTES]    = 0x02;
        encodings[PdeFieldTypes.KEY_3_BYTES]    = 0x03;
        encodings[PdeFieldTypes.KEY_4_BYTES]    = 0x04;
        encodings[PdeFieldTypes.KEY_5_BYTES]    = 0x05;
        encodings[PdeFieldTypes.KEY_6_BYTES]    = 0x06;
        encodings[PdeFieldTypes.KEY_7_BYTES]    = 0x07;
        encodings[PdeFieldTypes.KEY_8_BYTES]    = 0x08;
        encodings[PdeFieldTypes.KEY_9_BYTES]    = 0x09;
        encodings[PdeFieldTypes.KEY_10_BYTES]   = 0x0A;
        encodings[PdeFieldTypes.KEY_11_BYTES]   = 0x0B;
        encodings[PdeFieldTypes.KEY_12_BYTES]   = 0x0C;
        encodings[PdeFieldTypes.KEY_13_BYTES]   = 0x0D;
        encodings[PdeFieldTypes.KEY_14_BYTES]   = 0x0E;
        encodings[PdeFieldTypes.KEY_15_BYTES]   = 0x0F;
        encodings[PdeFieldTypes.KEY_1_LENGTH_BYTES]   = 0x10;
        encodings[PdeFieldTypes.KEY_2_LENGTH_BYTES]   = 0x20;



        encodings[PdeFieldTypes.OBJECT_NULL]             = (byte) (0x00);
        encodings[PdeFieldTypes.OBJECT_1_LENGTH_BYTES]   = (byte) (0xFF & 0x10);
        encodings[PdeFieldTypes.OBJECT_2_LENGTH_BYTES]   = (byte) (0xFF & 0x20);
        encodings[PdeFieldTypes.OBJECT_3_LENGTH_BYTES]   = (byte) (0xFF & 0x30);
        encodings[PdeFieldTypes.OBJECT_4_LENGTH_BYTES]   = (byte) (0xFF & 0x40);
        encodings[PdeFieldTypes.OBJECT_5_LENGTH_BYTES]   = (byte) (0xFF & 0x50);
        encodings[PdeFieldTypes.OBJECT_6_LENGTH_BYTES]   = (byte) (0xFF & 0x60);
        encodings[PdeFieldTypes.OBJECT_7_LENGTH_BYTES]   = (byte) (0xFF & 0x70);
        encodings[PdeFieldTypes.OBJECT_8_LENGTH_BYTES]   = (byte) (0xFF & 0x80);

        encodings[PdeFieldTypes.TABLE_NULL]             = (byte) (0x00);
        encodings[PdeFieldTypes.TABLE_1_LENGTH_BYTES]   = (byte) (0xFF & 0x10);
        encodings[PdeFieldTypes.TABLE_2_LENGTH_BYTES]   = (byte) (0xFF & 0x20);
        encodings[PdeFieldTypes.TABLE_3_LENGTH_BYTES]   = (byte) (0xFF & 0x30);
        encodings[PdeFieldTypes.TABLE_4_LENGTH_BYTES]   = (byte) (0xFF & 0x40);
        encodings[PdeFieldTypes.TABLE_5_LENGTH_BYTES]   = (byte) (0xFF & 0x50);
        encodings[PdeFieldTypes.TABLE_6_LENGTH_BYTES]   = (byte) (0xFF & 0x60);
        encodings[PdeFieldTypes.TABLE_7_LENGTH_BYTES]   = (byte) (0xFF & 0x70);
        encodings[PdeFieldTypes.TABLE_8_LENGTH_BYTES]   = (byte) (0xFF & 0x80);

        encodings[PdeFieldTypes.METADATA_NULL]             = (byte) (0x00);
        encodings[PdeFieldTypes.METADATA_1_LENGTH_BYTES]   = (byte) (0xFF & 0x10);
        encodings[PdeFieldTypes.METADATA_2_LENGTH_BYTES]   = (byte) (0xFF & 0x20);
        encodings[PdeFieldTypes.METADATA_3_LENGTH_BYTES]   = (byte) (0xFF & 0x30);
        encodings[PdeFieldTypes.METADATA_4_LENGTH_BYTES]   = (byte) (0xFF & 0x40);
        encodings[PdeFieldTypes.METADATA_5_LENGTH_BYTES]   = (byte) (0xFF & 0x50);
        encodings[PdeFieldTypes.METADATA_6_LENGTH_BYTES]   = (byte) (0xFF & 0x60);
        encodings[PdeFieldTypes.METADATA_7_LENGTH_BYTES]   = (byte) (0xFF & 0x70);
        encodings[PdeFieldTypes.METADATA_8_LENGTH_BYTES]   = (byte) (0xFF & 0x80);




        //todo encoding config for copy, reference and metadata too.

        return encodings;
    }
}
