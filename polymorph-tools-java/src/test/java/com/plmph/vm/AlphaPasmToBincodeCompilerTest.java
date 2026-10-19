package com.plmph.vm;

import com.plmph.pde.PdeFieldTypes;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlphaPasmToBincodeCompilerTest {


    @Test
    public void testCompilePasm() {
        String pasmStr =
        """
        nop()
        callearg(.externalFunction; "param1; 123;)
        """;

        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(30, bincodeLength);

        assertEquals(AlphaInstructionOpcodes.NOP  , 0xFF & bincodeBytes[0]);
        assertEquals(AlphaInstructionOpcodes.CALLEARG, 0xFF & bincodeBytes[1]);
    }

    @Test
    public void testNop() {
        String pasmStr = "nop()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.NOP, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testCallearg() {
        String pasmStr =
        """
        callearg( .myfun; 123; "param2; )
        """;
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(17, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.CALLEARG, 0xFF & bincodeBytes[0]);
        assertEquals(3, 0xFF & bincodeBytes[1]);
        assertEquals(PdeFieldTypes.KEY_5_BYTES, 0xFF & bincodeBytes[2]);
        assertCharacters("myfun", bincodeBytes, 3);
        assertEquals(PdeFieldTypes.INT_POS_1_BYTES, 0xFF & bincodeBytes[8]);
        assertEquals(123, 0xFF & bincodeBytes[9]);
        assertEquals(PdeFieldTypes.UTF_8_6_BYTES, 0xFF & bincodeBytes[10]);
        assertCharacters("param2", bincodeBytes, 11);
    }

    @Test
    public void testOutr() {
        String pasmStr = "outr()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.OUTR, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testPar() {
        String pasmStr = "par(123;)";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(3, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.PAR, 0xFF & bincodeBytes[0]);
        assertEquals(PdeFieldTypes.INT_POS_1_BYTES, 0xFF & bincodeBytes[1]);
        assertEquals(123, 0xFF & bincodeBytes[2]);
    }

    @Test
    public void testPsb() {
        String pasmStr = "psb()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.PSB, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testPse() {
        String pasmStr = "pse()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.PSE, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testCalle() {
        String pasmStr = "calle(.myfun;)";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(7, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.CALLE, 0xFF & bincodeBytes[0]);
        assertEquals(PdeFieldTypes.KEY_5_BYTES, 0xFF & bincodeBytes[1]);
        assertCharacters("myfun", bincodeBytes, 2);
    }

    @Test
    public void testEx() {
        String pasmStr = "ex(123;)";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(3, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.EX, 0xFF & bincodeBytes[0]);
        assertEquals(PdeFieldTypes.INT_POS_1_BYTES, 0xFF & bincodeBytes[1]);
        assertEquals(123, 0xFF & bincodeBytes[2]);
    }

    @Test
    public void testExret() {
        String pasmStr = "exret()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.EXRET, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testExpar() {
        String pasmStr = "expar(0;)";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(2, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.EXPAR, 0xFF & bincodeBytes[0]);
        assertEquals(0, 0xFF & bincodeBytes[1]);
    }

    @Test
    public void testParex() {
        String pasmStr = "parex()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.PAREX, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testOutex() {
        String pasmStr = "outex()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.OUTEX, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testEsb() {
        String pasmStr = "esb()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.ESB, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testEsc() {
        String pasmStr = "esc()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.ESC, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testEse() {
        String pasmStr = "ese()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.ESE, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testVar() {
        String pasmStr = "var(.count;)";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(7, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.VAR, 0xFF & bincodeBytes[0]);
        assertEquals(PdeFieldTypes.KEY_5_BYTES, 0xFF & bincodeBytes[1]);
        assertCharacters("count", bincodeBytes, 2);
    }

    @Test
    public void testGet() {
        String pasmStr = "get(.count;)";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(7, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.GET, 0xFF & bincodeBytes[0]);
        assertEquals(PdeFieldTypes.KEY_5_BYTES, 0xFF & bincodeBytes[1]);
        assertCharacters("count", bincodeBytes, 2);
    }

    @Test
    public void testSet() {
        String pasmStr = "set(.count; 123;)";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(9, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.SET, 0xFF & bincodeBytes[0]);
        assertEquals(PdeFieldTypes.KEY_5_BYTES, 0xFF & bincodeBytes[1]);
        assertCharacters("count", bincodeBytes, 2);
        assertEquals(PdeFieldTypes.INT_POS_1_BYTES, 0xFF & bincodeBytes[7]);
        assertEquals(123, 0xFF & bincodeBytes[8]);
    }

    @Test
    public void testSetex() {
        String pasmStr = "setex(.count;)";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(7, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.SETEX, 0xFF & bincodeBytes[0]);
        assertEquals(PdeFieldTypes.KEY_5_BYTES, 0xFF & bincodeBytes[1]);
        assertCharacters("count", bincodeBytes, 2);
    }

    @Test
    public void testVsb() {
        String pasmStr = "vsb()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.VSB, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testVse() {
        String pasmStr = "vse()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.VSE, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testIsb() {
        String pasmStr = "isb()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(3, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.ISB, 0xFF & bincodeBytes[0]);
        assertEquals(0, 0xFF & bincodeBytes[1]);
        assertEquals(0, 0xFF & bincodeBytes[2]);
    }

    @Test
    public void testIse() {
        String pasmStr = "isb() nop() ise()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(5, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.ISB, 0xFF & bincodeBytes[0]);
        assertEquals(1, 0xFF & bincodeBytes[1]);
        assertEquals(0, 0xFF & bincodeBytes[2]);
        assertEquals(AlphaInstructionOpcodes.NOP, 0xFF & bincodeBytes[3]);
        assertEquals(AlphaInstructionOpcodes.ISE, 0xFF & bincodeBytes[4]);
    }

    @Test
    public void testTsb() {
        String pasmStr = "tsb()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(3, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.TSB, 0xFF & bincodeBytes[0]);
        assertEquals(0, 0xFF & bincodeBytes[1]);
        assertEquals(0, 0xFF & bincodeBytes[2]);
    }

    @Test
    public void testTse() {
        String pasmStr = "tsb() nop() tse()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(5, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.TSB, 0xFF & bincodeBytes[0]);
        assertEquals(1, 0xFF & bincodeBytes[1]);
        assertEquals(0, 0xFF & bincodeBytes[2]);
        assertEquals(AlphaInstructionOpcodes.NOP, 0xFF & bincodeBytes[3]);
        assertEquals(AlphaInstructionOpcodes.TSE, 0xFF & bincodeBytes[4]);
    }

    @Test
    public void testLeave() {
        String pasmStr = "leave()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.LEAVE, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testLeaveif() {
        String pasmStr = "leaveif()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.LEAVEIF, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testRepeat() {
        String pasmStr = "repeat()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.REPEAT, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testRepeatif() {
        String pasmStr = "repeatif()";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(1, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.REPEATIF, 0xFF & bincodeBytes[0]);
    }

    @Test
    public void testIf() {
        String pasmStr = "if( ( nop() ) )";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(6, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.IF, 0xFF & bincodeBytes[0]);
        assertEquals(AlphaInstructionOpcodes.TSB, 0xFF & bincodeBytes[1]);
        assertEquals(1, 0xFF & bincodeBytes[2]);
        assertEquals(0, 0xFF & bincodeBytes[3]);
        assertEquals(AlphaInstructionOpcodes.NOP, 0xFF & bincodeBytes[4]);
        assertEquals(AlphaInstructionOpcodes.TSE, 0xFF & bincodeBytes[5]);
    }

    @Test
    public void testIfe() {
        String pasmStr = "( ife( nop() else() nop() ) )";
        byte[] bincodeBytes = new byte[1024];

        int bincodeLength = compile(pasmStr, bincodeBytes);

        assertEquals(10, bincodeLength);
        assertEquals(AlphaInstructionOpcodes.TSB,   0xFF & bincodeBytes[0]);
        assertEquals(6,                              0xFF & bincodeBytes[1]);
        assertEquals(0,                              0xFF & bincodeBytes[2]);
        assertEquals(AlphaInstructionOpcodes.IFE,   0xFF & bincodeBytes[3]);
        assertEquals(2,                              0xFF & bincodeBytes[4]);
        assertEquals(0,                              0xFF & bincodeBytes[5]);
        assertEquals(AlphaInstructionOpcodes.NOP,   0xFF & bincodeBytes[6]);
        assertEquals(AlphaInstructionOpcodes.LEAVE, 0xFF & bincodeBytes[7]);
        assertEquals(AlphaInstructionOpcodes.NOP,   0xFF & bincodeBytes[8]);
        assertEquals(AlphaInstructionOpcodes.TSE,   0xFF & bincodeBytes[9]);
    }


    private void assertCharacters(String characters, byte[] bincodeBytes, int offset) {
        for(int i=0; i < characters.length(); i++) {
            assertEquals(characters.charAt(i), (char) (0xFF & bincodeBytes[offset + i]));
        }
    }

    private int compile(String pasmString, byte[] bincodeBytes) {
        byte[] pasmBytes = pasmBytes(pasmString);
        AlphaPasmToBincodeCompiler compiler = new AlphaPasmToBincodeCompiler();
        return compiler.compile(pasmBytes, 0, pasmBytes.length, bincodeBytes, 0);
    }

    private byte[] pasmBytes(String pasmString){
        return pasmString.getBytes(StandardCharsets.UTF_8);
    }
}