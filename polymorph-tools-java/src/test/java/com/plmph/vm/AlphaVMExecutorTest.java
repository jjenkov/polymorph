package com.plmph.vm;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AlphaVMExecutorTest {

    AlphaVMExecutor executor = new AlphaVMExecutor();

    @Test
    public void testExecPasm() {
        byte[] pasmBytes = pasmBytes(
                """
                nop()
                """);

        executor.execPasm(pasmBytes, 0, pasmBytes.length);
    }

    @Test
    public void testExecBincode() {
        byte[] bincode = new byte[1024];
        AlphaBincodeWriter writer = new AlphaBincodeWriter();
        writer.setDest(bincode);
        writer.nop();

        executor.execBincode(bincode, writer.offset);
        assertFalse(executor.alphaVM.hasMoreInstructions());
        assertEquals(1, executor.alphaVM.codeLength);
        assertEquals(1, executor.alphaVM.instructionPointer);
    }

    @Test
    public void testExecuteNop() {
        byte[] pasmBytes = pasmBytes("nop()");

        executor.execPasm(pasmBytes, 0, pasmBytes.length);

        assertEquals(1, executor.bincodeLength);
        assertFalse(executor.alphaVM.hasMoreInstructions());
    }

    @Test
    public void testExecuteCallExternalFunction() {
        String pasmStr =
                """
                callearg(.myFun;)
                """;
        byte[] pasmBytes = pasmStr.getBytes(StandardCharsets.UTF_8);

        AlphaVMExecutor executor = new AlphaVMExecutor();
        AlphaVMExternalFunctionMock externalFunctionMock = new AlphaVMExternalFunctionMock("myFun");
        executor.alphaVM.addExternalFunction("myFun", externalFunctionMock);

        executor.execPasm(pasmBytes, 0, pasmBytes.length);

        assertEquals(1, externalFunctionMock.callCount);
        assertFalse(executor.alphaVM.hasMoreInstructions());
    }

    private byte[] pasmBytes(String pasmString) {
        return pasmString.getBytes(StandardCharsets.UTF_8);
    }

}