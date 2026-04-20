package com.plmph.hex;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HexUtilTest {


    @Test
    public void testBinToHex() {
        byte[] bytes = new byte[]{0,1,2,3,4,5, -1, -2, -3, -4, -5};

        StringBuffer hexBuffer = HexUtil.bytesToHex(bytes, 0, bytes.length, new StringBuffer());
        assertEquals("000102030405FFFEFDFCFB", hexBuffer.toString());
    }

    @Test void testHexToBin() {
        String hex = "00010203040506070809 0A0B0C0D0E0F     102030405060708090 A0B0C0D0E0F0";
        byte[] hexBytes = hex.getBytes(StandardCharsets.US_ASCII);
        byte[] destBytes = new byte[64];
        int bytesWritten = HexUtil.hexToBytes(hexBytes, 0, hexBytes.length, destBytes, 0);

        assertEquals(31, bytesWritten);

        int offset = 0;
        assertEquals(0x00, 0xFF & destBytes[offset++]);
        assertEquals(0x01, 0xFF & destBytes[offset++]);
        assertEquals(0x02, 0xFF & destBytes[offset++]);
        assertEquals(0x03, 0xFF & destBytes[offset++]);
        assertEquals(0x04, 0xFF & destBytes[offset++]);
        assertEquals(0x05, 0xFF & destBytes[offset++]);
        assertEquals(0x06, 0xFF & destBytes[offset++]);
        assertEquals(0x07, 0xFF & destBytes[offset++]);
        assertEquals(0x08, 0xFF & destBytes[offset++]);
        assertEquals(0x09, 0xFF & destBytes[offset++]);
        assertEquals(0x0A, 0xFF & destBytes[offset++]);
        assertEquals(0x0B, 0xFF & destBytes[offset++]);
        assertEquals(0x0C, 0xFF & destBytes[offset++]);
        assertEquals(0x0D, 0xFF & destBytes[offset++]);
        assertEquals(0x0E, 0xFF & destBytes[offset++]);
        assertEquals(0x0F, 0xFF & destBytes[offset++]);

        assertEquals(0x10, 0xFF & destBytes[offset++]);
        assertEquals(0x20, 0xFF & destBytes[offset++]);
        assertEquals(0x30, 0xFF & destBytes[offset++]);
        assertEquals(0x40, 0xFF & destBytes[offset++]);
        assertEquals(0x50, 0xFF & destBytes[offset++]);
        assertEquals(0x60, 0xFF & destBytes[offset++]);
        assertEquals(0x70, 0xFF & destBytes[offset++]);
        assertEquals(0x80, 0xFF & destBytes[offset++]);
        assertEquals(0x90, 0xFF & destBytes[offset++]);
        assertEquals(0xA0, 0xFF & destBytes[offset++]);
        assertEquals(0xB0, 0xFF & destBytes[offset++]);
        assertEquals(0xC0, 0xFF & destBytes[offset++]);
        assertEquals(0xD0, 0xFF & destBytes[offset++]);
        assertEquals(0xE0, 0xFF & destBytes[offset++]);
        assertEquals(0xF0, 0xFF & destBytes[offset++]);
    }
}
