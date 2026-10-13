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