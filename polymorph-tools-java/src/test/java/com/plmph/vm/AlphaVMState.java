package com.plmph.vm;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlphaVMState {

    private final AlphaVM vm;

    private final int parameterStackPointer;
    private final int parameterStackScopesPointer;
    private final int instructionScopeStackPointer;
    private final int variableScopeStackPointer;
    private final int expressionStackPointer;
    private final int expressionStackScopesPointer;
    private final int returnLocationStackPointer;

    public static AlphaVMState of(AlphaVM vm) {
        return new AlphaVMState(vm);
    }

    private AlphaVMState(AlphaVM vm) {
        this.vm = vm;
        this.parameterStackPointer = vm.parameterStackPointer;
        this.parameterStackScopesPointer = vm.parameterStackScopesPointer;
        this.instructionScopeStackPointer = vm.instructionScopeStackPointer;
        this.variableScopeStackPointer = vm.variableScopeStackPointer;
        this.expressionStackPointer = vm.expressionStackPointer;
        this.expressionStackScopesPointer = vm.expressionStackScopesPointer;
        this.returnLocationStackPointer = vm.returnLocationStackPointer;
    }

    public void assertInstructionPointer(int expected) {
        assertEquals(expected, this.vm.instructionPointer);
    }

    public void assertReturnValue(Object expected) {
        assertEquals(expected, this.vm.returnValue);
    }

    public void assertParameterStackPointer(int expected) {
        assertEquals(expected, this.vm.parameterStackPointer);
    }

    public void assertParameterStackScopesPointer(int expected) {
        assertEquals(expected, this.vm.parameterStackScopesPointer);
    }

    public void assertInstructionScopeStackPointer(int expected) {
        assertEquals(expected, this.vm.instructionScopeStackPointer);
    }

    public void assertVariableScopeStackPointer(int expected) {
        assertEquals(expected, this.vm.variableScopeStackPointer);
    }

    public void assertExpressionStackPointer(int expected) {
        assertEquals(expected, this.vm.expressionStackPointer);
    }

    public void assertExpressionStackScopesPointer(int expected) {
        assertEquals(expected, this.vm.expressionStackScopesPointer);
    }

    public void assertReturnLocationStackPointer(int expected) {
        assertEquals(expected, this.vm.returnLocationStackPointer);
    }

    public void assertStacksUnchanged() {
        assertParameterStackPointer(this.parameterStackPointer);
        assertParameterStackScopesPointer(this.parameterStackScopesPointer);
        assertInstructionScopeStackPointer(this.instructionScopeStackPointer);
        assertVariableScopeStackPointer(this.variableScopeStackPointer);
        assertExpressionStackPointer(this.expressionStackPointer);
        assertExpressionStackScopesPointer(this.expressionStackScopesPointer);
        assertReturnLocationStackPointer(this.returnLocationStackPointer);
    }
}
