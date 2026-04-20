package com.plmph.pde;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PdeUtilTest {


    @Test
    public void testWriteLongToBigEndianByteSequence() {
        byte[] dest = new byte[8];
        long value = 0xFF00FF00FF00FF00L;

        PdeUtil.writeLongToBigEndianByteSequence(dest, 0, 8, value);
        assertEquals(0xFF, 0xFF & dest[0]);
        assertEquals(0x00, 0xFF & dest[1]);
        assertEquals(0xFF, 0xFF & dest[2]);
        assertEquals(0x00, 0xFF & dest[3]);
        assertEquals(0xFF, 0xFF & dest[4]);
        assertEquals(0x00, 0xFF & dest[5]);
        assertEquals(0xFF, 0xFF & dest[6]);
        assertEquals(0x00, 0xFF & dest[7]);
    }

    @Test
    public void testWriteLongToLittleEndianByteSequence() {
        byte[] dest  = new byte[8];
        long   value = 0xFF00FF00FF00FF00L;

        PdeUtil.writeLongToLittleEndianByteSequence(dest, 0, 8, value);
        assertEquals(0x00, 0xFF & dest[0]);
        assertEquals(0xFF, 0xFF & dest[1]);
        assertEquals(0x00, 0xFF & dest[2]);
        assertEquals(0xFF, 0xFF & dest[3]);
        assertEquals(0x00, 0xFF & dest[4]);
        assertEquals(0xFF, 0xFF & dest[5]);
        assertEquals(0x00, 0xFF & dest[6]);
        assertEquals(0xFF, 0xFF & dest[7]);
    }

    @Test
    public void testReadLittleEndianByteSequenceToLong() {
        byte[] dest  = new byte[8];
        long   value = 0xFF00FF00FF00FF00L;

        PdeUtil.writeLongToLittleEndianByteSequence(dest, 0, 8, value);

        long result = PdeUtil.readLittleEndianByteSequenceToLong(dest, 0, 8);
        assertEquals(value, result);
    }

    @Test
    public void testReadBigEndianByteSequenceToLong() {
        byte[] dest = new byte[8];
        long   value = 0xFF00FF00FF00FF00L;

        PdeUtil.writeLongToBigEndianByteSequence(dest, 0, 8, value);

        long result = PdeUtil.readBigEndianByteSequenceToLong(dest, 0, 8);
        assertEquals(value, result);
    }


}
