package com.plmph.vm;

import com.plmph.pde.PdeFieldTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlphaBincodeWriterTest {

    private static AlphaBincodeWriter createWriter() {
        return new AlphaBincodeWriter().setDest(new byte[1024]);
    }

    private void assertByteValue(int expected, byte[] array, int offset) {
        assertEquals(expected, 0xFF & array[offset]);

    }

    private void assertByteSequence(byte[] array, int offset, String expected) {
        assertByteSequence(array, offset, expected.toCharArray());
    }

    private void assertByteSequence(byte[] array, int offset, char... expected) {
        for(int i = 0; i < expected.length; i++) {
            assertByteValue(expected[i], array, offset + i);
        }
    }

    @Test
    void testNop() {
        AlphaBincodeWriter writer = createWriter()
                .nop();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.NOP, writer.dest, 0);
    }

    @Test
    void testCallearg() {
        AlphaBincodeWriter writer = createWriter()
                .callearg("myfun");

        assertEquals(8, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.CALLEARG, writer.dest, 0);
        assertByteValue(1, writer.dest, 1);
        assertByteValue(PdeFieldTypes.KEY_5_BYTES, writer.dest, 2);
        assertByteSequence(writer.dest, 3, "myfun");

        writer.offset = 0;
        writer.callearg("myfun", 123L, "param2");

        assertEquals(17, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.CALLEARG, writer.dest, 0);
        assertByteValue(3, writer.dest, 1);
        assertByteValue(PdeFieldTypes.KEY_5_BYTES, writer.dest, 2);
        assertByteSequence(writer.dest, 3, "myfun");
        assertByteValue(PdeFieldTypes.INT_POS_1_BYTES, writer.dest, 8);
        assertByteValue(123, writer.dest, 9);
        assertByteValue(PdeFieldTypes.UTF_8_6_BYTES, writer.dest, 10);
        assertByteSequence(writer.dest, 11, "param2");
    }

    @Test
    void testOut() {
        AlphaBincodeWriter writer = createWriter()
                .out(123L);

        assertEquals(4, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.OUT, writer.dest, 0);
        assertByteValue(1, writer.dest, 1);
        assertByteValue(PdeFieldTypes.INT_POS_1_BYTES, writer.dest, 2);
        assertByteValue(123, writer.dest, 3);

        writer.out(123L, "param2");

        assertEquals(15, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.OUT, writer.dest, 4);
        assertByteValue(2, writer.dest, 5);
        assertByteValue(PdeFieldTypes.INT_POS_1_BYTES, writer.dest, 6);
        assertByteValue(123, writer.dest, 7);
        assertByteValue(PdeFieldTypes.UTF_8_6_BYTES, writer.dest, 8);
        assertByteSequence(writer.dest, 9, "param2");
    }

    @Test
    void testOutr() {
        AlphaBincodeWriter writer = createWriter()
                .outr();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.OUTR, writer.dest, 0);
    }

    @Test
    void testPar() {
        AlphaBincodeWriter writer = createWriter()
                .par(42L);

        assertByteValue(AlphaInstructionOpcodes.PAR, writer.dest, 0);
        assertByteValue(PdeFieldTypes.INT_POS_1_BYTES, writer.dest, 1);
        assertByteValue(42, writer.dest, 2);

        writer.offset = 0;
        writer.par("hello");

        assertByteValue(AlphaInstructionOpcodes.PAR, writer.dest, 0);
        assertByteValue(PdeFieldTypes.UTF_8_5_BYTES, writer.dest, 1);
        assertByteSequence(writer.dest, 2, "hello");

        //todo test more literal types
    }

    @Test
    void testPsb() {
        AlphaBincodeWriter writer = createWriter()
                .psb();
        assertByteValue(AlphaInstructionOpcodes.PSB, writer.dest, 0);
    }

    @Test
    void testPse() {
        AlphaBincodeWriter writer = createWriter()
                .pse();
        assertByteValue(AlphaInstructionOpcodes.PSE, writer.dest, 0);
    }

    @Test
    void testCalle() {
        AlphaBincodeWriter writer = createWriter()
                .calle("myfun");

        assertEquals(7, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.CALLE, writer.dest, 0);
        assertByteValue(PdeFieldTypes.KEY_5_BYTES, writer.dest, 1);
        assertByteSequence(writer.dest, 2, "myfun");
    }

    @Test
    void testEx() {
        AlphaBincodeWriter writer = createWriter()
                .ex(123L);

        assertEquals(3, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.EX, writer.dest, 0);
        assertByteValue(PdeFieldTypes.INT_POS_1_BYTES, writer.dest, 1);
        assertByteValue(123, writer.dest, 2);
    }

    @Test
    void testExret() {
        AlphaBincodeWriter writer = createWriter()
                .exret();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.EXRET, writer.dest, 0);
    }

    @Test
    void testExpar() {
        AlphaBincodeWriter writer = createWriter()
                .expar(0);

        assertEquals(2, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.EXPAR, writer.dest, 0);
        assertByteValue(0, writer.dest, 1);
    }

    @Test
    void testParex() {
        AlphaBincodeWriter writer = createWriter()
                .parex();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.PAREX, writer.dest, 0);
    }

    @Test
    void testOutex() {
        AlphaBincodeWriter writer = createWriter()
                .outex();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.OUTEX, writer.dest, 0);
    }

    @Test
    void testEsb() {
        AlphaBincodeWriter writer = createWriter()
                .esb();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.ESB, writer.dest, 0);
    }

    @Test
    void testEsc() {
        AlphaBincodeWriter writer = createWriter()
                .esc();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.ESC, writer.dest, 0);
    }

    @Test
    void testEse() {
        AlphaBincodeWriter writer = createWriter()
                .ese();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.ESE, writer.dest, 0);
    }

    @Test
    void testVar() {
        AlphaBincodeWriter writer = createWriter()
                .var("count");

        assertEquals(7, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.VAR, writer.dest, 0);
        assertByteValue(PdeFieldTypes.KEY_5_BYTES, writer.dest, 1);
        assertByteSequence(writer.dest, 2, "count");
    }

    @Test
    void testGet() {
        AlphaBincodeWriter writer = createWriter()
                .get("count");

        assertEquals(7, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.GET, writer.dest, 0);
        assertByteValue(PdeFieldTypes.KEY_5_BYTES, writer.dest, 1);
        assertByteSequence(writer.dest, 2, "count");
    }

    @Test
    void testSet() {
        AlphaBincodeWriter writer = createWriter()
                .set("count", 123L);

        assertEquals(9, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.SET, writer.dest, 0);
        assertByteValue(PdeFieldTypes.KEY_5_BYTES, writer.dest, 1);
        assertByteSequence(writer.dest, 2, "count");
        assertByteValue(PdeFieldTypes.INT_POS_1_BYTES, writer.dest, 7);
        assertByteValue(123, writer.dest, 8);
    }

    @Test
    void testSetex() {
        AlphaBincodeWriter writer = createWriter()
                .setex("count");

        assertEquals(7, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.SETEX, writer.dest, 0);
        assertByteValue(PdeFieldTypes.KEY_5_BYTES, writer.dest, 1);
        assertByteSequence(writer.dest, 2, "count");
    }

    @Test
    void testVsb() {
        AlphaBincodeWriter writer = createWriter()
                .vsb();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.VSB, writer.dest, 0);
    }

    @Test
    void testVse() {
        AlphaBincodeWriter writer = createWriter()
                .vse();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.VSE, writer.dest, 0);
    }

    @Test
    void testIsb() {
        AlphaBincodeWriter writer = createWriter()
                .isb()
                .nop()
                .isb()
                .ise()
                .ise();

        assertEquals(9, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.ISB, writer.dest, 0);
        assertByteValue(0x05, writer.dest, 1);
        assertByteValue(0x00, writer.dest, 2);
        assertByteValue(AlphaInstructionOpcodes.NOP, writer.dest, 3);
        assertByteValue(AlphaInstructionOpcodes.ISB, writer.dest, 4);
        assertByteValue(0x00, writer.dest, 5);
        assertByteValue(0x00, writer.dest, 6);
        assertByteValue(AlphaInstructionOpcodes.ISE, writer.dest, 7);
        assertByteValue(AlphaInstructionOpcodes.ISE, writer.dest, 8);
    }

    @Test
    void testIse() {
        AlphaBincodeWriter writer = createWriter()
                .isb()
                .nop()
                .ise();

        assertEquals(5, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.ISB, writer.dest, 0);
        assertByteValue(0x01, writer.dest, 1);
        assertByteValue(0x00, writer.dest, 2);
        assertByteValue(AlphaInstructionOpcodes.NOP, writer.dest, 3);
        assertByteValue(AlphaInstructionOpcodes.ISE, writer.dest, 4);
    }

    @Test
    void testTsb() {
        AlphaBincodeWriter writer = createWriter()
                .tsb()
                .nop()
                .tsb()
                .tse()
                .tse();

        assertEquals(9, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.TSB, writer.dest, 0);
        assertByteValue(0x05, writer.dest, 1);
        assertByteValue(0x00, writer.dest, 2);
        assertByteValue(AlphaInstructionOpcodes.NOP, writer.dest, 3);
        assertByteValue(AlphaInstructionOpcodes.TSB, writer.dest, 4);
        assertByteValue(0x00, writer.dest, 5);
        assertByteValue(0x00, writer.dest, 6);
        assertByteValue(AlphaInstructionOpcodes.TSE, writer.dest, 7);
        assertByteValue(AlphaInstructionOpcodes.TSE, writer.dest, 8);
    }

    @Test
    void testTse() {
        AlphaBincodeWriter writer = createWriter()
                .tsb()
                .nop()
                .tse();

        assertEquals(5, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.TSB, writer.dest, 0);
        assertByteValue(0x01, writer.dest, 1);
        assertByteValue(0x00, writer.dest, 2);
        assertByteValue(AlphaInstructionOpcodes.NOP, writer.dest, 3);
        assertByteValue(AlphaInstructionOpcodes.TSE, writer.dest, 4);
    }

    @Test
    void testLeave() {
        AlphaBincodeWriter writer = createWriter()
                .leave();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.LEAVE, writer.dest, 0);
    }

    @Test
    void testLeaveif() {
        AlphaBincodeWriter writer = createWriter()
                .leaveif();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.LEAVEIF, writer.dest, 0);
    }

    @Test
    void testRepeat() {
        AlphaBincodeWriter writer = createWriter()
                .repeat();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.REPEAT, writer.dest, 0);
    }

    @Test
    void testRepeatif() {
        AlphaBincodeWriter writer = createWriter()
                .repeatif();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.REPEATIF, writer.dest, 0);
    }

    @Test
    void testIft() {
        AlphaBincodeWriter writer = createWriter()
                .ift();

        assertEquals(1, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.IF, writer.dest, 0);
    }

    @Test
    void testIfe() {
        AlphaBincodeWriter writer = createWriter()
                .tsb()
                .ife()
                .tsb()
                .nop()
                .tse()
                .leave()
                .ifeElse()
                .tsb()
                .nop()
                .tse()
                .tse();

        assertEquals(18, writer.offset);
        assertByteValue(AlphaInstructionOpcodes.TSB, writer.dest, 0);
        assertByteValue(0x0E, writer.dest, 1);
        assertByteValue(0x00, writer.dest, 2);
        assertByteValue(AlphaInstructionOpcodes.IFE, writer.dest, 3);
        assertByteValue(0x06, writer.dest, 4);
        assertByteValue(0x00, writer.dest, 5);
        assertByteValue(AlphaInstructionOpcodes.TSB, writer.dest, 6);
        assertByteValue(0x01, writer.dest, 7);
        assertByteValue(0x00, writer.dest, 8);
        assertByteValue(AlphaInstructionOpcodes.NOP, writer.dest, 9);
        assertByteValue(AlphaInstructionOpcodes.TSE, writer.dest, 10);
        assertByteValue(AlphaInstructionOpcodes.LEAVE, writer.dest, 11);
        assertByteValue(AlphaInstructionOpcodes.TSB, writer.dest, 12);
        assertByteValue(0x01, writer.dest, 13);
        assertByteValue(0x00, writer.dest, 14);
        assertByteValue(AlphaInstructionOpcodes.NOP, writer.dest, 15);
        assertByteValue(AlphaInstructionOpcodes.TSE, writer.dest, 16);
        assertByteValue(AlphaInstructionOpcodes.TSE, writer.dest, 17);
    }

}