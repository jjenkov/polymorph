package com.plmph.pde;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import com.plmph.hex.HexUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class PdeWriterTest {

    // Tests of basics of internal state of a PdeWriter

    @Test
    public void testInstantiation() {
        byte[] dest = new byte[1024];

        PdeWriter writer = new PdeWriter(dest);

        assertSame  (writer.dest, dest);
        assertEquals(writer.offset, 0);

        writer = new PdeWriter(dest, 16);
        assertSame  (writer.dest, dest);
        assertEquals(writer.offset, 16);
    }

    @Test
    public void testSetDest() {
        byte[] dest = new byte[1024];
        PdeWriter writer = new PdeWriter();

        PdeWriter writer2 = writer.setDest(dest);

        assertSame(writer, writer2);
        assertSame(writer.dest, dest);
        assertEquals(0, writer.offset);

        writer  = new PdeWriter();
        writer2 = writer.setDest(dest, 16);
        assertSame(writer, writer2);
        assertSame(writer.dest, dest);
        assertEquals(16, writer.offset);
    }


    @Test
    public void testSetOffset() {
        PdeWriter writer = new PdeWriter().setDest(new byte[1024], 8).setOffset(24);

        assertEquals(1024, writer.dest.length);
        assertEquals(24  , writer.offset);
    }

    @Test
    public void testSetCompositeFieldStack() {
        int[] compositeFieldStack = new int[256];
        PdeWriter writer = new PdeWriter().setDest(new byte[1024]).setCompositeFieldStack(compositeFieldStack);

        assertSame  (compositeFieldStack, writer.compositeFieldStack);
        assertEquals(256, writer.compositeFieldStack.length);
    }

    // Tests of writing various PDE fields

    @Test
    public void testWriteBoolean() {
        byte[] dest = new byte[1024];

        PdeWriter writer = new PdeWriter(dest);

        writer.writeBoolean(true);
        assertEquals(1, writer.offset);
        assertEquals(PdeFieldTypes.BOOLEAN_TRUE, dest[0]);

        writer.writeBoolean(false);
        assertEquals(2, writer.offset);
        assertEquals(PdeFieldTypes.BOOLEAN_FALSE, dest[1]);

        writer.writeBooleanObj(Boolean.valueOf(true));
        assertEquals(3, writer.offset);
        assertEquals(PdeFieldTypes.BOOLEAN_TRUE, dest[2]);

        writer.writeBooleanObj(Boolean.valueOf(false));
        assertEquals(4, writer.offset);
        assertEquals(PdeFieldTypes.BOOLEAN_FALSE, dest[3]);

        writer.writeBooleanObj(null);
        assertEquals(5, writer.offset);
        assertEquals(PdeFieldTypes.BOOLEAN_NULL, dest[4]);

    }

    @Test
    public void testWriteInt() {
        PdeWriter writer = new PdeWriter(new byte[1024]);

        writer.writeInt(123);
        assertEquals(2, writer.offset);
        assertEquals(PdeFieldTypes.INT_POS_1_BYTES, writer.dest[0]);
        assertEquals(123, writer.dest[1]);

        writer.writeInt(65534);
        assertEquals(5, writer.offset);
        assertEquals(PdeFieldTypes.INT_POS_2_BYTES, writer.dest[2]);
        assertEquals(0xfe, 0xff & writer.dest[3]);
        assertEquals(0xff, 0xff & writer.dest[4]);
    }

    @Test
    public void testWriteIntAsLong() {
        PdeWriter writer = new PdeWriter(new byte[1024]);

        writer.writeInt(123L);
        assertEquals(2, writer.offset);
        assertEquals(PdeFieldTypes.INT_POS_1_BYTES, writer.dest[0]);
        assertEquals(123, writer.dest[1]);

        writer.writeInt(0x08_07_06_05_04_03_02_01L);
        assertEquals(11, writer.offset);
        assertEquals(PdeFieldTypes.INT_POS_8_BYTES, writer.dest[2]);
        assertEquals(0x01, 0xff & writer.dest[3]);
        assertEquals(0x02, 0xff & writer.dest[4]);
        assertEquals(0x03, 0xff & writer.dest[5]);
        assertEquals(0x04, 0xff & writer.dest[6]);
        assertEquals(0x05, 0xff & writer.dest[7]);
        assertEquals(0x06, 0xff & writer.dest[8]);
        assertEquals(0x07, 0xff & writer.dest[9]);
        assertEquals(0x08, 0xff & writer.dest[10]);
    }


    @Test
    public void testWriteIntObj() {
        PdeWriter writer = new PdeWriter(new byte[1024]);

        writer.writeIntObj((Integer) null);

        assertEquals(1, writer.offset);
        assertEquals(PdeFieldTypes.INT_NULL, writer.dest[0]);

        writer.writeIntObj(Integer.valueOf(123));
        assertEquals(3, writer.offset);
        assertEquals(PdeFieldTypes.INT_POS_1_BYTES, writer.dest[1]);
        assertEquals(123, writer.dest[2]);

        writer.writeIntObj(Integer.valueOf(65534));
        assertEquals(6, writer.offset);
        assertEquals(PdeFieldTypes.INT_POS_2_BYTES, writer.dest[3]);
        assertEquals(0xfe, 0xff & writer.dest[4]);
        assertEquals(0xff, 0xff & writer.dest[5]);
    }

    @Test
    public void testWriteIntObjAsLong() {
        PdeWriter writer = new PdeWriter(new byte[1024]);

        writer.writeIntObj((Long) null);

        assertEquals(1, writer.offset);
        assertEquals(PdeFieldTypes.INT_NULL, writer.dest[0]);

        writer.writeIntObj(Long.valueOf(123));
        assertEquals(3, writer.offset);
        assertEquals(PdeFieldTypes.INT_POS_1_BYTES, writer.dest[1]);
        assertEquals(123, writer.dest[2]);

        writer.writeIntObj(Long.valueOf(65534));
        assertEquals(6, writer.offset);
        assertEquals(PdeFieldTypes.INT_POS_2_BYTES, writer.dest[3]);
        assertEquals(0xfe, 0xff & writer.dest[4]);
        assertEquals(0xff, 0xff & writer.dest[5]);
    }



    @Test
    public void testWriteFloat32() {
        PdeWriter writer = new PdeWriter(new byte[1024]);

        float value  = 123.45f;
        int intValue = Float.floatToIntBits(value);

        writer.writeFloat32(value);
        assertEquals(5, writer.offset);
        assertEquals(PdeFieldTypes.FLOAT_4_BYTES, writer.dest[0]);
        assertEquals((byte) (intValue     & 0x00_00_00_FF), writer.dest[1]);
        assertEquals((byte) (intValue>>8  & 0x00_00_00_FF), writer.dest[2]);
        assertEquals((byte) (intValue>>16 & 0x00_00_00_FF), writer.dest[3]);
        assertEquals((byte) (intValue>>24 & 0x00_00_00_FF), writer.dest[4]);
    }

    @Test
    public void testWriteFloat32Obj() {
        PdeWriter writer = new PdeWriter(new byte[1024]);

        Float value  = 123.45f;
        int intValue = Float.floatToIntBits(value);

        writer.writeFloat32Obj(null);
        writer.writeFloat32Obj(value);
        assertEquals(6, writer.offset);

        int offset = 0;
        assertEquals(PdeFieldTypes.FLOAT_NULL, writer.dest[offset++]);
        assertEquals(PdeFieldTypes.FLOAT_4_BYTES, writer.dest[offset++]);
        assertEquals((byte) (intValue     & 0x00_00_00_FF), writer.dest[offset++]);
        assertEquals((byte) (intValue>>8  & 0x00_00_00_FF), writer.dest[offset++]);
        assertEquals((byte) (intValue>>16 & 0x00_00_00_FF), writer.dest[offset++]);
        assertEquals((byte) (intValue>>24 & 0x00_00_00_FF), writer.dest[offset++]);
    }

    @Test
    public void testWriteFloat64() {
        PdeWriter writer = new PdeWriter(new byte[1024]);

        double value   = 123.4567d;
        long longValue = Double.doubleToLongBits(value);

        writer.writeFloat64(value);
        assertEquals(9, writer.offset);
        assertEquals(PdeFieldTypes.FLOAT_8_BYTES, writer.dest[0]);
        assertEquals((byte) (longValue     & 0x00_00_00_FF), writer.dest[1]);
        assertEquals((byte) (longValue>>8  & 0x00_00_00_FF), writer.dest[2]);
        assertEquals((byte) (longValue>>16 & 0x00_00_00_FF), writer.dest[3]);
        assertEquals((byte) (longValue>>24 & 0x00_00_00_FF), writer.dest[4]);
        assertEquals((byte) (longValue>>32 & 0x00_00_00_FF), writer.dest[5]);
        assertEquals((byte) (longValue>>40 & 0x00_00_00_FF), writer.dest[6]);
        assertEquals((byte) (longValue>>48 & 0x00_00_00_FF), writer.dest[7]);
        assertEquals((byte) (longValue>>56 & 0x00_00_00_FF), writer.dest[8]);
    }

    @Test
    public void testWriteFloat64Obj() {
        PdeWriter writer = new PdeWriter(new byte[1024]);

        Double value   = 123.4567d;
        long longValue = Double.doubleToLongBits(value);

        writer.writeFloat64Obj(null);
        writer.writeFloat64Obj(value);
        assertEquals(10, writer.offset);

        int offset = 0;
        assertEquals(PdeFieldTypes.FLOAT_NULL, writer.dest[offset++]);
        assertEquals(PdeFieldTypes.FLOAT_8_BYTES, writer.dest[offset++]);
        assertEquals((byte) (longValue     & 0x00_00_00_FF), writer.dest[offset++]);
        assertEquals((byte) (longValue>>8  & 0x00_00_00_FF), writer.dest[offset++]);
        assertEquals((byte) (longValue>>16 & 0x00_00_00_FF), writer.dest[offset++]);
        assertEquals((byte) (longValue>>24 & 0x00_00_00_FF), writer.dest[offset++]);
        assertEquals((byte) (longValue>>32 & 0x00_00_00_FF), writer.dest[offset++]);
        assertEquals((byte) (longValue>>40 & 0x00_00_00_FF), writer.dest[offset++]);
        assertEquals((byte) (longValue>>48 & 0x00_00_00_FF), writer.dest[offset++]);
        assertEquals((byte) (longValue>>56 & 0x00_00_00_FF), writer.dest[offset++]);
    }



    @Test
    public void testWriteBytes(){
        byte[] dest = new byte[65 * 1024];

        PdeWriter writer = new PdeWriter(dest);

        byte[] source = new byte[]{ 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};

        writer.writeBytes(null);
        assertEquals(1, writer.offset);
        assertEquals(PdeFieldTypes.BYTES_NULL, dest[0]);

        writer.writeBytes(source);

        assertEquals(10, writer.offset);
        assertEquals(PdeFieldTypes.BYTES_8_BYTES, dest[1]);
        assertEquals(0x00, dest[2]);
        assertEquals(0x01, dest[3]);
        assertEquals(0x02, dest[4]);
        assertEquals(0x03, dest[5]);
        assertEquals(0x04, dest[6]);
        assertEquals(0x05, dest[7]);
        assertEquals(0x06, dest[8]);
        assertEquals(0x07, dest[9]);
        assertEquals(0x00, dest[10]);

        source = new byte[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
                0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x77
        };

        writer.writeBytes(source);
        assertEquals(36, writer.offset);
        assertEquals(PdeFieldTypes.BYTES_1_LENGTH_BYTES, dest[10]);
        assertEquals(  24, dest[11]); // length;
        assertEquals(0x00, dest[12]);
        assertEquals(0x01, dest[13]);
        assertEquals(0x02, dest[14]);
        assertEquals(0x03, dest[15]);
        assertEquals(0x04, dest[16]);
        assertEquals(0x05, dest[17]);
        assertEquals(0x06, dest[18]);
        assertEquals(0x07, dest[19]);
        assertEquals(0x08, dest[20]);
        assertEquals(0x09, dest[21]);
        assertEquals(0x0A, dest[22]);
        assertEquals(0x0B, dest[23]);
        assertEquals(0x0C, dest[24]);
        assertEquals(0x0D, dest[25]);
        assertEquals(0x0E, dest[26]);
        assertEquals(0x0F, dest[27]);
        assertEquals(0x10, dest[28]);
        assertEquals(0x20, dest[29]);
        assertEquals(0x30, dest[30]);
        assertEquals(0x40, dest[31]);
        assertEquals(0x50, dest[32]);
        assertEquals(0x60, dest[33]);
        assertEquals(0x70, dest[34]);
        assertEquals(0x77, dest[35]);
        assertEquals(0x00, dest[36]);

        source = new byte[ 64 * 1024 ];
        assertEquals(65536, source.length);

        for(int i=0; i<source.length; i++){
            source[i] = (byte) (0xFF & i);
        }
        writer.writeBytes(source);

        assertEquals(36 + 1 + 3 + (64*1024), writer.offset);
        assertEquals(PdeFieldTypes.BYTES_3_LENGTH_BYTES, dest[36]);

        for(int i=0; i<source.length; i++) {
            assertEquals(source[i], dest[40 + i]);
        }
    }

    @Test
    public void testWriteBytesPushPop(){
        byte[] dest = new byte[1024];

        PdeWriter writer = new PdeWriter(dest);
        writer.setCompositeFieldStack(new int[16]);

        byte[] source = new byte[]{ 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};

        writer.writeBytesBeginPush(2);
        System.arraycopy(source, 0, writer.dest, writer.offset, source.length);
        writer.offset += source.length;
        writer.writeBytesEndPop();

        assertEquals(11, writer.offset);

        int offset = 0;
        assertEquals(PdeFieldTypes.BYTES_2_LENGTH_BYTES, dest[offset++]);
        assertEquals(0x08, 0xFF & dest[offset++]); // first length byte
        assertEquals(0x00, 0xFF & dest[offset++]); // second length byte
        assertEquals(0x00, 0xFF & dest[offset++]); // first value byte
        assertEquals(0x01, 0xFF & dest[offset++]);
        assertEquals(0x02, 0xFF & dest[offset++]);
        assertEquals(0x03, 0xFF & dest[offset++]);
        assertEquals(0x04, 0xFF & dest[offset++]);
        assertEquals(0x05, 0xFF & dest[offset++]);
        assertEquals(0x06, 0xFF & dest[offset++]);
        assertEquals(0x07, 0xFF & dest[offset++]);
    }

    @Test
    public void testWriteAscii() {
        byte[] dest = new byte[65 * 1024];

        PdeWriter writer = new PdeWriter(dest);

        writer.writeAsciiString((String) null);
        assertEquals(1, writer.offset);
        assertEquals(PdeFieldTypes.ASCII_NULL, dest[0]);

        String sourceString1 = "abcde";
        writer.writeAscii(sourceString1.getBytes(StandardCharsets.UTF_8));
        assertEquals(7, writer.offset);
        assertEquals(PdeFieldTypes.ASCII_5_BYTES, dest[1]);
        assertEquals('a', dest[2]);
        assertEquals('b', dest[3]);
        assertEquals('c', dest[4]);
        assertEquals('d', dest[5]);
        assertEquals('e', dest[6]);

        String sourceString2   = "abcdefghijklmnopqrstuvxyz" ;
        byte[] sourceUtf8Bytes = sourceString2.getBytes(StandardCharsets.UTF_8);
        writer.writeAscii(sourceUtf8Bytes);
        assertEquals(PdeFieldTypes.ASCII_1_LENGTH_BYTES, dest[7]);
        assertEquals(sourceUtf8Bytes.length, dest[8]);
        for(int i=0; i<sourceUtf8Bytes.length; i++) {
            assertEquals(sourceUtf8Bytes[i], dest[9 + i]);
        }
    }

    @Test
    public void testWriteAsciiPushPop(){
        byte[] dest = new byte[1024];

        PdeWriter writer = new PdeWriter(dest);
        writer.setCompositeFieldStack(new int[16]);

        String sourceStr = "ASCII text";
        byte[] source = sourceStr.getBytes(StandardCharsets.US_ASCII);

        writer.writeAsciiBeginPush(2);
        System.arraycopy(source, 0, writer.dest, writer.offset, source.length);
        writer.offset += source.length;
        writer.writeAsciiEndPop();

        assertEquals(13, writer.offset);

        int offset = 0;
        assertEquals(PdeFieldTypes.ASCII_2_LENGTH_BYTES, dest[offset++]);
        assertEquals(0x0A, 0xFF & dest[offset++]); // first length byte
        assertEquals(0x00, 0xFF & dest[offset++]); // second length byte
        assertEquals('A', 0xFF & dest[offset++]);  // first value byte
        assertEquals('S', 0xFF & dest[offset++]);
        assertEquals('C', 0xFF & dest[offset++]);
        assertEquals('I', 0xFF & dest[offset++]);
        assertEquals('I', 0xFF & dest[offset++]);
        assertEquals(' ', 0xFF & dest[offset++]);
        assertEquals('t', 0xFF & dest[offset++]);
        assertEquals('e', 0xFF & dest[offset++]);
        assertEquals('x', 0xFF & dest[offset++]);
        assertEquals('t', 0xFF & dest[offset++]);
    }

    @Test
    public void testWriteUtf8() {
        byte[] dest = new byte[65 * 1024];

        PdeWriter writer = new PdeWriter(dest);

        writer.writeUtf8String((String) null);
        assertEquals(1, writer.offset);
        assertEquals(PdeFieldTypes.UTF_8_NULL, dest[0]);

        writer.writeUtf8String("abcde");
        assertEquals(7, writer.offset);
        assertEquals(PdeFieldTypes.UTF_8_5_BYTES, dest[1]);
        assertEquals('a', dest[2]);
        assertEquals('b', dest[3]);
        assertEquals('c', dest[4]);
        assertEquals('d', dest[5]);
        assertEquals('e', dest[6]);

        String sourceString2   = "abcdefghijklmnopqrstuvxyzæøå" ;
        byte[] sourceUtf8Bytes = sourceString2.getBytes(StandardCharsets.UTF_8);
        writer.writeUtf8(sourceUtf8Bytes);
        assertEquals(PdeFieldTypes.UTF_8_1_LENGTH_BYTES, dest[7]);
        assertEquals(sourceUtf8Bytes.length, dest[8]);
        for(int i=0; i<sourceUtf8Bytes.length; i++) {
            assertEquals(sourceUtf8Bytes[i], dest[9 + i]);
        }
    }

    @Test
    public void testWriteUtf8PushPop(){
        byte[] dest = new byte[1024];

        PdeWriter writer = new PdeWriter(dest);
        writer.setCompositeFieldStack(new int[16]);

        String sourceStr = "UTF-8 text";
        byte[] source = sourceStr.getBytes(StandardCharsets.UTF_8);

        writer.writeUtf8BeginPush(2);
        System.arraycopy(source, 0, writer.dest, writer.offset, source.length);
        writer.offset += source.length;
        writer.writeUtf8EndPop();

        assertEquals(13, writer.offset);

        int offset = 0;
        assertEquals(PdeFieldTypes.UTF_8_2_LENGTH_BYTES, dest[offset++]);
        assertEquals(0x0A, 0xFF & dest[offset++]); // first length byte
        assertEquals(0x00, 0xFF & dest[offset++]); // second length byte
        assertEquals('U', 0xFF & dest[offset++]);  // first value byte
        assertEquals('T', 0xFF & dest[offset++]);
        assertEquals('F', 0xFF & dest[offset++]);
        assertEquals('-', 0xFF & dest[offset++]);
        assertEquals('8', 0xFF & dest[offset++]);
        assertEquals(' ', 0xFF & dest[offset++]);
        assertEquals('t', 0xFF & dest[offset++]);
        assertEquals('e', 0xFF & dest[offset++]);
        assertEquals('x', 0xFF & dest[offset++]);
        assertEquals('t', 0xFF & dest[offset++]);
    }


    @Test
    public void testWriteUtc() {
        byte[] dest = new byte[65 * 1024];

        PdeWriter writer = new PdeWriter(dest);

        writer.writeUtc(2001, 0, 28, 23, 59, 59, 999);

        assertEquals(10, writer.offset);
        assertEquals(PdeFieldTypes.UTC_9_BYTES, dest[0]);

        assertEquals( (0xFF &  2001)       , 0xFF & dest[1]);
        assertEquals( (0xFF & (2001 >> 8) ), 0xFF & dest[2]);
        assertEquals(  0, dest[3]);
        assertEquals( 28, dest[4]);
        assertEquals( 23, dest[5]);
        assertEquals( 59, dest[6]);
        assertEquals( 59, dest[7]);
        assertEquals( (0xFF &  999)       , 0xFF & dest[8]);
        assertEquals( (0xFF & (999 >> 8) ), 0xFF & dest[9]);
    }

    @Test
    public void testWriteUtcMillis() {
        byte[] dest = new byte[65 * 1024];

        PdeWriter writer = new PdeWriter(dest);

        writer.writeUtcMillis(999L);
        assertEquals(9, writer.offset);

        assertEquals(PdeFieldTypes.UTC_8_BYTES, dest[0]);

        assertEquals( (0xFF &  999)       , 0xFF & dest[1]);
        assertEquals( (0xFF & (999 >> 8) ), 0xFF & dest[2]);
        assertEquals(  0, dest[3]);
        assertEquals(  0, dest[4]);
        assertEquals(  0, dest[5]);
        assertEquals(  0, dest[6]);
        assertEquals(  0, dest[7]);
        assertEquals(  0, dest[8]);
        assertEquals(  0, dest[9]); // should not have been touched by writeUtcMillis() - just double checking
    }


    @Test
    public void testWriteCopy() {
        PdeWriter writer = new PdeWriter(new byte[1024]);

        // Even though this copyOffset is positive (123) - it is to be interpreted as a negative offset back in
        // in the byte stream to a PDE field found at the location of the beginning of the Copy field minus the
        // copyOffset. In this case, the offset actually references outside of the byte buffer - so it would not
        // make much sense in reality - but for the sake of this unit test, a backwards relative offset of 123 works.
        writer.writeCopy(123);
        assertEquals(2, writer.offset);
        assertEquals(PdeFieldTypes.COPY_1_BYTES, writer.dest[0]);
        assertEquals(123, writer.dest[1]);

        writer.writeCopy(65534L);
        assertEquals(5, writer.offset);
        assertEquals(PdeFieldTypes.COPY_2_BYTES, writer.dest[2]);
        assertEquals(0xfe, 0xff & writer.dest[3]);
        assertEquals(0xff, 0xff & writer.dest[4]);
    }

    @Test
    public void testWriteReference() {
        PdeWriter writer = new PdeWriter(new byte[1024]);

        // Even though this referenceOffset is positive (123) - it is to be interpreted as a negative offset back in
        // in the byte stream to a PDE field found at the location of the beginning of the Reference field minus the
        // referenceOffset. In this case, the offset actually references outside of the byte buffer - so it would not
        // make much sense in reality - but for the sake of this unit test, a backwards relative offset of 123 works.
        writer.writeReference(123);
        assertEquals(2, writer.offset);
        assertEquals(PdeFieldTypes.REFERENCE_1_BYTES, writer.dest[0]);
        assertEquals(123, writer.dest[1]);

        writer.writeReference(65534L);
        assertEquals(5, writer.offset);
        assertEquals(PdeFieldTypes.REFERENCE_2_BYTES, writer.dest[2]);
        assertEquals(0xfe, 0xff & writer.dest[3]);
        assertEquals(0xff, 0xff & writer.dest[4]);
    }


    @Test
    public void testWriteKey() {
        byte[] dest = new byte[65 * 1024];

        PdeWriter writer = new PdeWriter(dest);

        writer.writeKey(null);
        assertEquals(1, writer.offset);
        assertEquals(PdeFieldTypes.KEY_NULL, dest[0]);

        writer.writeKey("0123456789".getBytes(StandardCharsets.UTF_8));
        assertEquals(12, writer.offset);
        assertEquals(PdeFieldTypes.KEY_10_BYTES, 0xFF & dest[1]);
        assertEquals('0', (char) (0xFF & dest[2]));
        assertEquals('1', (char) (0xFF & dest[3]));
        assertEquals('2', (char) (0xFF & dest[4]));
        assertEquals('3', (char) (0xFF & dest[5]));
        assertEquals('4', (char) (0xFF & dest[6]));
        assertEquals('5', (char) (0xFF & dest[7]));
        assertEquals('6', (char) (0xFF & dest[8]));
        assertEquals('7', (char) (0xFF & dest[9]));
        assertEquals('8', (char) (0xFF & dest[10]));
        assertEquals('9', (char) (0xFF & dest[11]));


        writer.writeKey("01234567890123456789".getBytes(StandardCharsets.UTF_8));
        assertEquals(34, writer.offset);
        assertEquals(PdeFieldTypes.KEY_1_LENGTH_BYTES, 0xFF & dest[12]);
        assertEquals(20, (0xFF & dest[13]));  // the length byte
        assertEquals('0', (char) (0xFF & dest[14]));
        assertEquals('1', (char) (0xFF & dest[15]));
        assertEquals('2', (char) (0xFF & dest[16]));
        assertEquals('3', (char) (0xFF & dest[17]));
        assertEquals('4', (char) (0xFF & dest[18]));
        assertEquals('5', (char) (0xFF & dest[19]));
        assertEquals('6', (char) (0xFF & dest[20]));
        assertEquals('7', (char) (0xFF & dest[21]));
        assertEquals('8', (char) (0xFF & dest[22]));
        assertEquals('9', (char) (0xFF & dest[23]));
        assertEquals('0', (char) (0xFF & dest[24]));
        assertEquals('1', (char) (0xFF & dest[25]));
        assertEquals('2', (char) (0xFF & dest[26]));
        assertEquals('3', (char) (0xFF & dest[27]));
        assertEquals('4', (char) (0xFF & dest[28]));
        assertEquals('5', (char) (0xFF & dest[29]));
        assertEquals('6', (char) (0xFF & dest[30]));
        assertEquals('7', (char) (0xFF & dest[31]));
        assertEquals('8', (char) (0xFF & dest[32]));
        assertEquals('9', (char) (0xFF & dest[33]));

    }


    @Test
    public void testWriteObject() {
        byte[] dest = new byte[65 * 1024];

        PdeWriter writer = new PdeWriter(dest);
        writer.setCompositeFieldStack(new int[16]);

        assertEquals(-1, writer.compositeFieldStackIndex);
        writer.writeObjectBeginPush(3);
        assertEquals(0, writer.compositeFieldStackIndex);
        writer.writeObjectEndPop();
        assertEquals(-1, writer.compositeFieldStackIndex);

        assertEquals(PdeFieldTypes.OBJECT_3_LENGTH_BYTES, 0xFF & dest[0]);
        assertEquals(0, 0xFF & dest[1]);
        assertEquals(0, 0xFF & dest[2]);
        assertEquals(0, 0xFF & dest[3]);

        byte[] bytes = "12345".getBytes(StandardCharsets.UTF_8);

        writer.writeObjectBeginPush(4);
        writer.writeBytes(bytes);
        writer.writeObjectEndPop();

        assertEquals(PdeFieldTypes.OBJECT_4_LENGTH_BYTES, 0xFF & dest[4]);
        assertEquals(6, 0xFF & dest[5]);
        assertEquals(0, 0xFF & dest[6]);
        assertEquals(0, 0xFF & dest[7]);
        assertEquals(0, 0xFF & dest[8]);    // 1 field type byte for PdeFieldTypes.BYTES_5_BYTES + 5 value bytes
        assertEquals(PdeFieldTypes.BYTES_5_BYTES, (0xFF & dest[9]));
        assertEquals('1', (char) (0xFF & dest[10]));
        assertEquals('2', (char) (0xFF & dest[11]));
        assertEquals('3', (char) (0xFF & dest[12]));
        assertEquals('4', (char) (0xFF & dest[13]));
        assertEquals('5', (char) (0xFF & dest[14]));
    }


    @Test
    public void testWriteTable() {
        byte[] dest = new byte[65 * 1024];

        PdeWriter writer = new PdeWriter(dest);
        writer.setCompositeFieldStack(new int[16]);

        assertEquals(-1, writer.compositeFieldStackIndex);
        writer.writeTableBeginPush(3);
        assertEquals(0, writer.compositeFieldStackIndex);
        writer.writeTableEndPop(0);
        assertEquals(-1, writer.compositeFieldStackIndex);

        // 8 = 1 for Table type byte + 3 for length bytes + 4 (1 + 3) for IntPos field containing row count.
        assertEquals(8, writer.offset);

        assertEquals(PdeFieldTypes.TABLE_3_LENGTH_BYTES, 0xFF & dest[0]);
        assertEquals(4, 0xFF & dest[1]);   // length of 4 = IntPos type byte + 3 value bytes
        assertEquals(0, 0xFF & dest[2]);
        assertEquals(0, 0xFF & dest[3]);
        assertEquals(PdeFieldTypes.INT_POS_3_BYTES, 0xFF & dest[4]);
        assertEquals(0, 0xFF & dest[5]);
        assertEquals(0, 0xFF & dest[6]);
        assertEquals(0, 0xFF & dest[7]);

        writer.writeTableBeginPush(4);

        writer.writeKey("col1".getBytes(StandardCharsets.UTF_8));
        writer.writeKey("col2".getBytes(StandardCharsets.UTF_8));
        writer.writeKey("col3".getBytes(StandardCharsets.UTF_8));

        writer.writeUtf8("val1_1".getBytes(StandardCharsets.UTF_8));
        writer.writeUtf8("val1_2".getBytes(StandardCharsets.UTF_8));
        writer.writeUtf8("val1_3".getBytes(StandardCharsets.UTF_8));

        writer.writeUtf8("val2_1".getBytes(StandardCharsets.UTF_8));
        writer.writeUtf8("val2_2".getBytes(StandardCharsets.UTF_8));
        writer.writeUtf8("val2_3".getBytes(StandardCharsets.UTF_8));

        writer.writeUtf8("val3_1".getBytes(StandardCharsets.UTF_8));
        writer.writeUtf8("val3_2".getBytes(StandardCharsets.UTF_8));
        writer.writeUtf8("val3_3".getBytes(StandardCharsets.UTF_8));

        writer.writeTableEndPop(3);

        assertEquals(96, writer.offset);

        assertEquals(PdeFieldTypes.TABLE_4_LENGTH_BYTES, 0xFF & dest[8]);
        assertEquals(83, 0xFF & dest[9]);   // length of 4 = IntPos type byte + 3 value bytes
        assertEquals(0, 0xFF & dest[10]);
        assertEquals(0, 0xFF & dest[11]);
        assertEquals(0, 0xFF & dest[12]);
        assertEquals(PdeFieldTypes.INT_POS_4_BYTES, 0xFF & dest[13]);
        assertEquals(3, 0xFF & dest[14]);
        assertEquals(0, 0xFF & dest[15]);
        assertEquals(0, 0xFF & dest[16]);
        assertEquals(0, 0xFF & dest[17]);
    }


    @Test
    public void testWriteMetadata() {
        byte[] dest = new byte[65 * 1024];

        PdeWriter writer = new PdeWriter(dest);
        writer.setCompositeFieldStack(new int[16]);

        assertEquals(-1, writer.compositeFieldStackIndex);
        writer.writeMetadataBeginPush(3);
        assertEquals(0, writer.compositeFieldStackIndex);
        writer.writeMetadataEndPop();
        assertEquals(-1, writer.compositeFieldStackIndex);

        assertEquals(PdeFieldTypes.METADATA_3_LENGTH_BYTES, 0xFF & dest[0]);
        assertEquals(0, 0xFF & dest[1]);
        assertEquals(0, 0xFF & dest[2]);
        assertEquals(0, 0xFF & dest[3]);

        byte[] bytes = "12345".getBytes(StandardCharsets.UTF_8);

        writer.writeMetadataBeginPush(4);
        writer.writeBytes(bytes);
        writer.writeMetadataEndPop();

        assertEquals(PdeFieldTypes.METADATA_4_LENGTH_BYTES, 0xFF & dest[4]);
        assertEquals(6, 0xFF & dest[5]);
        assertEquals(0, 0xFF & dest[6]);
        assertEquals(0, 0xFF & dest[7]);
        assertEquals(0, 0xFF & dest[8]);    // 1 field type byte for PdeFieldTypes.BYTES_5_BYTES + 5 value bytes
        assertEquals(PdeFieldTypes.BYTES_5_BYTES, (0xFF & dest[9]));
        assertEquals('1', (char) (0xFF & dest[10]));
        assertEquals('2', (char) (0xFF & dest[11]));
        assertEquals('3', (char) (0xFF & dest[12]));
        assertEquals('4', (char) (0xFF & dest[13]));
        assertEquals('5', (char) (0xFF & dest[14]));

        System.out.println(HexUtil.bytesToHex(writer.dest, 0, writer.offset, new StringBuffer()));
    }




    /*
        Added tests - that should be in PdeReader too:
            Write Reference
            Write Copy
            Write ASCII
            Write Metadata

        Missing tests:
            Write Extended Field

     */

}
