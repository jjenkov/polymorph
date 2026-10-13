package com.plmph.vm;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlphaVMTest {

    private AlphaBincodeWriter createBincodeWriter() {
        return new AlphaBincodeWriter().setDest(new byte[1024]);
    }

    private AlphaVM createVM(AlphaBincodeWriter writer) {
        return new AlphaVM().setCode(writer.dest, writer.offset);
    }

    @Test
    public void testNewVM() {
        AlphaVMState state = AlphaVMState.of(new AlphaVM());
        state.assertInstructionPointer(0);
    }

    @Test
    public void testClear() {
        AlphaVM vm = new AlphaVM();
        AlphaVMExternalFunctionMock externalFunction = new AlphaVMExternalFunctionMock("myFun");
        vm.addExternalFunction("myFun", externalFunction);
        VMOutputListenerMock outputListener = new VMOutputListenerMock();
        vm.setOutputListener(outputListener);

        byte[] code = new byte[] { 0 };
        vm.setCode(code, code.length);
        vm.instructionPointer = 4;

        vm.expressionStackPointer = 0;
        vm.expressionStack[0] = "value";

        Map<String, Object> variableScope = new HashMap<>();
        variableScope.put("name", "value");
        vm.variableScopeStackPointer = 0;
        vm.variableScopeStack[0] = variableScope;

        vm.instructionScopeStackPointer = 2;

        vm.returnValue = "result";
        vm.internalFunctions.put("returnLast", new byte[] { 1, 2, 3 });

        vm.returnLocationStackPointer = 0;
        vm.returnCodeBlocks[0] = new byte[] { 4, 5 };
        vm.returnInstructionPointers[0] = 7;

        vm.clear();

        AlphaVMState state = AlphaVMState.of(vm);
        state.assertInstructionPointer(0);
        assertNull(vm.code);
        assertEquals(0, vm.codeLength);

        state.assertExpressionStackPointer(-1);
        assertNull(vm.expressionStack[0]);
        state.assertVariableScopeStackPointer(-1);
        assertNull(vm.variableScopeStack[0]);
        assertEquals(0, variableScope.size());
        state.assertInstructionScopeStackPointer(-1);
        state.assertReturnValue(null);
        assertEquals(0, vm.internalFunctions.size());
        state.assertReturnLocationStackPointer(-1);
        assertNull(vm.returnCodeBlocks[0]);

        assertSame(externalFunction, vm.externalFunctions.get("myFun"));
        assertSame(outputListener, vm.outputListener);
    }

    @Test
    public void testNop(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .nop();

        AlphaVM      vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstruction();

        state.assertInstructionPointer(1);
        state.assertStacksUnchanged();
    }

    @Test
    public void testCallearg(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .callearg("myfun", 123L, "param2");

        AlphaVM vm = createVM(writer);
        AlphaVMExternalFunctionMock externalFunction = new AlphaVMExternalFunctionMock("myfun").setReturnValue("result");
        vm.addExternalFunction("myfun", externalFunction);

        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstruction();

        state.assertInstructionPointer(17);
        state.assertStacksUnchanged();
        state.assertReturnValue("result");
        assertEquals(1, externalFunction.callCount);
        assertEquals(123L, externalFunction.params.get(0));
        assertEquals("param2", externalFunction.params.get(1));
    }

    @Test
    public void testOut(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .out(123L, "param2");

        AlphaVM vm = createVM(writer);
        VMOutputListenerMock outputListener = new VMOutputListenerMock();
        vm.setOutputListener(outputListener);

        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstruction();

        state.assertInstructionPointer(11);
        state.assertStacksUnchanged();
        assertEquals(1, outputListener.callCount);
        assertEquals(2, outputListener.values.size());
        assertEquals(123L, outputListener.values.get(0));
        assertEquals("param2", outputListener.values.get(1));
    }

    @Test
    public void testOutr(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .outr();

        AlphaVM vm = createVM(writer);
        VMOutputListenerMock outputListener = new VMOutputListenerMock();
        vm.setOutputListener(outputListener);

        vm.returnValue = "result";

        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstruction();

        state.assertInstructionPointer(1);
        state.assertStacksUnchanged();
        state.assertReturnValue("result");
        assertEquals(1, outputListener.callCount);
        assertEquals("result", outputListener.value);
        assertNull(outputListener.values);
    }

    @Test
    public void testPar(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .par(123L);

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstruction();

        state.assertInstructionPointer(3);
        state.assertParameterStackPointer(0);
        assertEquals(123L, vm.parameterStack[0]);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);
        state.assertExpressionStackPointer(-1);
        state.assertExpressionStackScopesPointer(-1);
    }

    @Test
    public void testPsb(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .psb();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstruction();

        state.assertInstructionPointer(1);
        state.assertParameterStackScopesPointer(0);
        assertEquals(-1, vm.parameterStackScopes[0]);
        state.assertParameterStackPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);
        state.assertExpressionStackPointer(-1);
        state.assertExpressionStackScopesPointer(-1);
    }

    @Test
    public void testPse(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .psb()
                .par(123L)
                .par(456L)
                .pse();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(4);

        state.assertInstructionPointer(9);
        state.assertParameterStackPointer(-1);
        assertNull(vm.parameterStack[0]);
        assertNull(vm.parameterStack[1]);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);
        state.assertExpressionStackPointer(-1);
        state.assertExpressionStackScopesPointer(-1);
    }

    @Test
    public void testCalle(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .psb()
                .par(123L)
                .par(456L)
                .calle("myfun");

        AlphaVM vm = createVM(writer);
        AlphaVMExternalFunctionMock externalFunction = new AlphaVMExternalFunctionMock("myfun").setReturnValue("result");
        vm.addExternalFunction("myfun", externalFunction);

        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(4);

        state.assertInstructionPointer(15);
        state.assertReturnValue("result");
        assertEquals(1, externalFunction.callCount);
        assertEquals(123L, externalFunction.params.get(0));
        assertEquals(456L, externalFunction.params.get(1));
        state.assertParameterStackPointer(-1);
        assertNull(vm.parameterStack[0]);
        assertNull(vm.parameterStack[1]);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);
        state.assertExpressionStackPointer(-1);
        state.assertExpressionStackScopesPointer(-1);
    }

    @Test
    public void testEx(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .ex(123L);

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstruction();

        state.assertInstructionPointer(3);
        state.assertExpressionStackPointer(0);
        assertEquals(123L, vm.expressionStack[0]);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);
    }

    @Test
    public void testExret(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .exret();

        AlphaVM vm = createVM(writer);
        vm.returnValue = "result";

        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstruction();

        state.assertInstructionPointer(1);
        state.assertReturnValue("result");
        state.assertExpressionStackPointer(0);
        assertEquals("result", vm.expressionStack[0]);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);
    }

    @Test
    public void testExpar(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .psb()
                .par(123L)
                .par(456L)
                .expar(0);

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(4);

        state.assertInstructionPointer(10);
        state.assertExpressionStackPointer(0);
        assertEquals(123L, vm.expressionStack[0]);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(1);
        assertEquals(123L, vm.parameterStack[0]);
        assertEquals(456L, vm.parameterStack[1]);
        state.assertParameterStackScopesPointer(0);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);
    }

    @Test
    public void testParex(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .ex(123L)
                .parex();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(2);

        state.assertInstructionPointer(4);
        state.assertExpressionStackPointer(0);
        assertEquals(123L, vm.expressionStack[0]);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(0);
        assertEquals(123L, vm.parameterStack[0]);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);
    }

    @Test
    public void testOutex(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .ex(123L)
                .outex();

        AlphaVM vm = createVM(writer);
        VMOutputListenerMock outputListener = new VMOutputListenerMock();
        vm.setOutputListener(outputListener);

        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(2);

        state.assertInstructionPointer(4);
        state.assertExpressionStackPointer(0);
        assertEquals(123L, vm.expressionStack[0]);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);

        assertEquals(1, outputListener.callCount);
        assertEquals(123L, outputListener.value);
        assertNull(outputListener.values);
    }

    @Test
    public void testEsb(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .esb();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstruction();

        state.assertInstructionPointer(1);
        state.assertExpressionStackScopesPointer(0);
        assertEquals(-1, vm.expressionStackScopes[0]);
        state.assertExpressionStackPointer(-1);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);
    }

    @Test
    public void testEsc(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .ex(123L)
                .esb()
                .ex(456L)
                .esc();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(4);

        state.assertInstructionPointer(9);
        state.assertExpressionStackPointer(0);
        assertEquals(123L, vm.expressionStack[0]);
        assertNull(vm.expressionStack[1]);
        state.assertExpressionStackScopesPointer(0);
        assertEquals(0, vm.expressionStackScopes[0]);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);
    }

    @Test
    public void testEse(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .ex(123L)
                .esb()
                .ex(456L)
                .ese();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(4);

        state.assertInstructionPointer(9);
        state.assertExpressionStackPointer(0);
        assertEquals(123L, vm.expressionStack[0]);
        assertNull(vm.expressionStack[1]);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);
    }

    @Test
    public void testVar(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .vsb()
                .var("count");

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(2);

        state.assertInstructionPointer(8);
        state.assertVariableScopeStackPointer(0);
        assertTrue(vm.variableScopeStack[0].containsKey("count"));
        assertNull(vm.variableScopeStack[0].get("count"));
        state.assertExpressionStackPointer(-1);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
    }

    @Test
    public void testGet(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .vsb()
                .var("count")
                .get("count");

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(3);

        state.assertInstructionPointer(15);
        state.assertVariableScopeStackPointer(0);
        assertTrue(vm.variableScopeStack[0].containsKey("count"));
        assertNull(vm.variableScopeStack[0].get("count"));
        state.assertExpressionStackPointer(0);
        assertNull(vm.expressionStack[0]);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
    }

    @Test
    public void testSet(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .vsb()
                .var("count")
                .set("count", 123L);

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(3);

        state.assertInstructionPointer(17);
        state.assertVariableScopeStackPointer(0);
        assertEquals(123L, vm.variableScopeStack[0].get("count"));
        state.assertExpressionStackPointer(-1);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
    }

    @Test
    public void testSetex(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .vsb()
                .var("count")
                .ex(123L)
                .setex("count");

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(4);

        state.assertInstructionPointer(18);
        state.assertVariableScopeStackPointer(0);
        assertEquals(123L, vm.variableScopeStack[0].get("count"));
        state.assertExpressionStackPointer(0);
        assertEquals(123L, vm.expressionStack[0]);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
    }

    @Test
    public void testVsb(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .vsb();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstruction();

        state.assertInstructionPointer(1);
        state.assertVariableScopeStackPointer(0);
        assertEquals(0, vm.variableScopeStack[0].size());
        assertEquals(-1, vm.variableScopeToExpressionScope[0]);
        state.assertExpressionStackPointer(-1);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
    }

    @Test
    public void testVse(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .esb()
                .ex(123L)
                .vsb()
                .var("count")
                .esb()
                .ex(456L)
                .vse();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(7);

        state.assertInstructionPointer(18);
        state.assertVariableScopeStackPointer(-1);
        assertNull(vm.variableScopeStack[0]);
        state.assertExpressionStackPointer(0);
        assertEquals(123L, vm.expressionStack[0]);
        assertNull(vm.expressionStack[1]);
        state.assertExpressionStackScopesPointer(0);
        assertEquals(-1, vm.expressionStackScopes[0]);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
        state.assertInstructionScopeStackPointer(-1);
    }

    @Test
    public void testIsb(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .vsb()
                .isb()
                .nop()
                .ise();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(2);

        state.assertInstructionPointer(4);
        state.assertInstructionScopeStackPointer(0);
        assertEquals((5L << 32) | 4L, vm.instructionScopeStack[0]);
        assertEquals(0, vm.instructionScopeToVariableScope[0]);
        state.assertVariableScopeStackPointer(0);
        assertEquals(0, vm.variableScopeStack[0].size());
        assertNull(vm.variableScopeStack[1]);
        assertEquals(-1, vm.variableScopeToExpressionScope[0]);
        state.assertExpressionStackPointer(-1);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
    }

    @Test
    public void testIse(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .esb()
                .ex(123L)
                .vsb()
                .var("count")
                .isb()
                .vsb()
                .esb()
                .ex(456L)
                .ise();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(9);

        state.assertInstructionPointer(22);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(0);
        assertEquals(1, vm.variableScopeStack[0].size());
        assertNull(vm.variableScopeStack[0].get("count"));
        assertNull(vm.variableScopeStack[1]);
        state.assertExpressionStackPointer(0);
        assertEquals(123L, vm.expressionStack[0]);
        assertNull(vm.expressionStack[1]);
        state.assertExpressionStackScopesPointer(0);
        assertEquals(-1, vm.expressionStackScopes[0]);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
    }

    @Test
    public void testTsb(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .tsb()
                .nop()
                .tse();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(1);

        state.assertInstructionPointer(3);
        state.assertInstructionScopeStackPointer(0);
        assertEquals((4L << 32) | 3L, vm.instructionScopeStack[0]);
        assertEquals(0, vm.instructionScopeToVariableScope[0]);
        state.assertVariableScopeStackPointer(0);
        assertEquals(0, vm.variableScopeStack[0].size());
        assertNull(vm.variableScopeStack[1]);
        assertEquals(0, vm.variableScopeToExpressionScope[0]);
        state.assertExpressionStackPointer(-1);
        state.assertExpressionStackScopesPointer(0);
        assertEquals(-1, vm.expressionStackScopes[0]);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
    }

    @Test
    public void testTse(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .esb()
                .ex(123L)
                .vsb()
                .var("count")
                .tsb()
                .vsb()
                .esb()
                .ex(456L)
                .tse();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(9);

        state.assertInstructionPointer(22);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(0);
        assertEquals(1, vm.variableScopeStack[0].size());
        assertNull(vm.variableScopeStack[0].get("count"));
        assertNull(vm.variableScopeStack[1]);
        state.assertExpressionStackPointer(0);
        assertEquals(123L, vm.expressionStack[0]);
        assertNull(vm.expressionStack[1]);
        state.assertExpressionStackScopesPointer(0);
        assertEquals(-1, vm.expressionStackScopes[0]);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
    }

    @Test
    public void testLeave(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .esb()
                .vsb()
                .isb()
                .var("count")
                .ex(456L)
                .leave()
                .nop()
                .ise()
                .vse()
                .ese();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(9);

        state.assertInstructionPointer(21);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);
        assertNull(vm.variableScopeStack[0]);
        state.assertExpressionStackPointer(-1);
        assertNull(vm.expressionStack[0]);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
    }

    @Test
    public void testLeaveif(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .esb()
                .vsb()
                .isb()
                .var("count")
                .ex(Boolean.TRUE)
                .leaveif()
                .nop()
                .ise()
                .vse()
                .ese();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(9);

        state.assertInstructionPointer(19);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);
        assertNull(vm.variableScopeStack[0]);
        state.assertExpressionStackPointer(-1);
        assertNull(vm.expressionStack[0]);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);

        writer = createBincodeWriter()
                .esb()
                .vsb()
                .isb()
                .var("count")
                .ex(Boolean.FALSE)
                .leaveif()
                .nop()
                .ise()
                .vse()
                .ese();

        vm = createVM(writer);
        state = AlphaVMState.of(vm);

        vm.execInstructions(7);

        state.assertInstructionPointer(16);
        state.assertInstructionScopeStackPointer(0);
        state.assertVariableScopeStackPointer(0);
        assertNull(vm.variableScopeStack[0].get("count"));
        state.assertExpressionStackPointer(0);
        assertSame(Boolean.FALSE, vm.expressionStack[0]);
        state.assertExpressionStackScopesPointer(0);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
    }

    @Test
    public void testRepeat(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .esb()
                .vsb()
                .isb()
                .nop()
                .ex(123L)
                .repeat()
                .ise()
                .vse()
                .ese();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(7);

        state.assertInstructionPointer(6);
        state.assertInstructionScopeStackPointer(0);
        assertEquals((10L << 32) | 5L, vm.instructionScopeStack[0]);
        assertEquals(0, vm.instructionScopeToVariableScope[0]);
        state.assertVariableScopeStackPointer(0);
        assertEquals(0, vm.variableScopeStack[0].size());
        assertEquals(0, vm.variableScopeToExpressionScope[0]);
        state.assertExpressionStackPointer(0);
        assertEquals(123L, vm.expressionStack[0]);
        assertNull(vm.expressionStack[1]);
        state.assertExpressionStackScopesPointer(0);
        assertEquals(-1, vm.expressionStackScopes[0]);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
    }

    @Test
    public void testRepeatif(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .esb()
                .vsb()
                .isb()
                .nop()
                .ex(Boolean.TRUE)
                .repeatif()
                .nop()
                .ise()
                .vse()
                .ese();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(7);

        state.assertInstructionPointer(6);
        state.assertInstructionScopeStackPointer(0);
        assertEquals((10L << 32) | 5L, vm.instructionScopeStack[0]);
        assertEquals(0, vm.instructionScopeToVariableScope[0]);
        state.assertVariableScopeStackPointer(0);
        assertEquals(0, vm.variableScopeStack[0].size());
        assertEquals(0, vm.variableScopeToExpressionScope[0]);
        state.assertExpressionStackPointer(0);
        assertSame(Boolean.TRUE, vm.expressionStack[0]);
        assertNull(vm.expressionStack[1]);
        state.assertExpressionStackScopesPointer(0);
        assertEquals(-1, vm.expressionStackScopes[0]);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);

        writer = createBincodeWriter()
                .esb()
                .vsb()
                .isb()
                .nop()
                .ex(Boolean.FALSE)
                .repeatif()
                .nop()
                .ise()
                .vse()
                .ese();

        vm = createVM(writer);
        state = AlphaVMState.of(vm);

        vm.execInstructions(7);

        state.assertInstructionPointer(10);
        state.assertInstructionScopeStackPointer(0);
        assertEquals((10L << 32) | 5L, vm.instructionScopeStack[0]);
        assertEquals(0, vm.instructionScopeToVariableScope[0]);
        state.assertVariableScopeStackPointer(0);
        assertEquals(0, vm.variableScopeStack[0].size());
        assertEquals(0, vm.variableScopeToExpressionScope[0]);
        state.assertExpressionStackPointer(0);
        assertSame(Boolean.FALSE, vm.expressionStack[0]);
        assertNull(vm.expressionStack[1]);
        state.assertExpressionStackScopesPointer(0);
        assertEquals(-1, vm.expressionStackScopes[0]);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
    }

    @Test
    public void testIft(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .ex(Boolean.TRUE)
                .ift()
                .tsb()
                .ex(123L)
                .tse()
                .nop();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(4);

        state.assertInstructionPointer(9);
        state.assertInstructionScopeStackPointer(0);
        assertEquals((9L << 32) | 6L, vm.instructionScopeStack[0]);
        assertEquals(0, vm.instructionScopeToVariableScope[0]);
        state.assertVariableScopeStackPointer(0);
        assertEquals(0, vm.variableScopeStack[0].size());
        assertEquals(0, vm.variableScopeToExpressionScope[0]);
        state.assertExpressionStackPointer(1);
        assertSame(Boolean.TRUE, vm.expressionStack[0]);
        assertEquals(123L, vm.expressionStack[1]);
        state.assertExpressionStackScopesPointer(0);
        assertEquals(0, vm.expressionStackScopes[0]);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);

        writer = createBincodeWriter()
                .ex(Boolean.FALSE)
                .ift()
                .tsb()
                .ex(123L)
                .tse()
                .nop();

        vm = createVM(writer);
        state = AlphaVMState.of(vm);

        vm.execInstructions(2);

        state.assertInstructionPointer(10);
        state.assertInstructionScopeStackPointer(-1);
        state.assertVariableScopeStackPointer(-1);
        assertNull(vm.variableScopeStack[0]);
        state.assertExpressionStackPointer(0);
        assertSame(Boolean.FALSE, vm.expressionStack[0]);
        assertNull(vm.expressionStack[1]);
        state.assertExpressionStackScopesPointer(-1);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
    }

    @Test
    public void testIfe(){
        AlphaBincodeWriter writer = createBincodeWriter()
                .ex(Boolean.TRUE)
                .tsb()
                .ife()
                .tsb()
                .ex(123L)
                .tse()
                .leave()
                .ifeElse()
                .tsb()
                .ex(456L)
                .tse()
                .tse();

        AlphaVM vm = createVM(writer);
        AlphaVMState state = AlphaVMState.of(vm);

        vm.execInstructions(7);

        state.assertInstructionPointer(24);
        state.assertInstructionScopeStackPointer(0);
        assertEquals((24L << 32) | 5L, vm.instructionScopeStack[0]);
        assertEquals(0, vm.instructionScopeToVariableScope[0]);
        state.assertVariableScopeStackPointer(0);
        assertEquals(0, vm.variableScopeStack[0].size());
        assertNull(vm.variableScopeStack[1]);
        assertEquals(0, vm.variableScopeToExpressionScope[0]);
        state.assertExpressionStackPointer(0);
        assertSame(Boolean.TRUE, vm.expressionStack[0]);
        assertNull(vm.expressionStack[1]);
        state.assertExpressionStackScopesPointer(0);
        assertEquals(0, vm.expressionStackScopes[0]);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);

        writer = createBincodeWriter()
                .ex(Boolean.FALSE)
                .tsb()
                .ife()
                .tsb()
                .ex(123L)
                .tse()
                .leave()
                .ifeElse()
                .tsb()
                .ex(456L)
                .tse()
                .tse();

        vm = createVM(writer);
        state = AlphaVMState.of(vm);

        vm.execInstructions(5);

        state.assertInstructionPointer(23);
        state.assertInstructionScopeStackPointer(1);
        assertEquals((24L << 32) | 5L, vm.instructionScopeStack[0]); //todo create an assertScope() method that takes 5 and 24 as scope offset boundaries.
        assertEquals((23L << 32) | 19L, vm.instructionScopeStack[1]);
        assertEquals(0, vm.instructionScopeToVariableScope[0]);
        assertEquals(1, vm.instructionScopeToVariableScope[1]);
        state.assertVariableScopeStackPointer(1);
        assertEquals(0, vm.variableScopeStack[0].size());
        assertEquals(0, vm.variableScopeStack[1].size());
        assertEquals(0, vm.variableScopeToExpressionScope[0]);
        assertEquals(1, vm.variableScopeToExpressionScope[1]);
        state.assertExpressionStackPointer(1);
        assertSame(Boolean.FALSE, vm.expressionStack[0]);
        assertEquals(456L, vm.expressionStack[1]);
        state.assertExpressionStackScopesPointer(1);
        assertEquals(0, vm.expressionStackScopes[0]);
        assertEquals(0, vm.expressionStackScopes[1]);
        state.assertParameterStackPointer(-1);
        state.assertParameterStackScopesPointer(-1);
    }


}