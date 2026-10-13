package com.plmph.vm;


import com.plmph.pde.PdeFieldTypes;
import com.plmph.pde.PdeReader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.plmph.pde.PdeFieldTypes.*;
import static com.plmph.vm.AlphaInstructionOpcodes.*;

/**
 * This VM is a "draft" of how the final VM will look. Thus, this VM is just experimental, and its design might be messy.
 *
 */
public class AlphaVM {

    /** The external functions that can be called from code executed by this VM. */
    protected Map<String, AlphaVMExternalFunction> externalFunctions = new HashMap<>();

    /** Internal function name to a byte array copy of that function's code. */
    protected Map<String, byte[]> internalFunctions = new HashMap<>();

    /** Receives values sent by the output instructions. */
    protected VMOutputListener outputListener = null;

    // parameter stack
    protected Object[] parameterStack = new Object[256];
    protected int parameterStackPointer = -1;
    protected int[] parameterStackScopes = new int[256];
    protected int parameterStackScopesPointer = -1;

    // instruction scope stack (if, loop etc.)
    protected long[] instructionScopeStack = new long[1024];
    protected int instructionScopeStackPointer = -1;
    protected int[] instructionScopeToVariableScope = new int[1024];


    // variable stack
    protected Map<String, Object>[] variableScopeStack = new Map[1024];
    protected int variableScopeStackPointer = -1;
    protected int[] variableScopeToExpressionScope = new int[1024];

    // expression stack
    protected Object[] expressionStack = new Object[256];
    protected int expressionStackPointer = -1;
    protected int[] expressionStackScopes = new int[256];
    protected int expressionStackScopesPointer = -1;

    // Code execution
    protected byte[] code = null;
    protected int codeLength = 0;
    protected int instructionPointer = 0;

    protected Object returnValue = null;

    /** Reused argument list for instructions that pass argument values out of the VM. */
    protected List arguments = new ArrayList();

    protected byte[][] returnCodeBlocks = new byte[1024][];
    protected int[] returnInstructionPointers = new int[1024];
    protected int returnLocationStackPointer = -1;

    protected PdeReader pdeReader = new PdeReader();
    protected byte[] pdeBuffer = new byte[1024];

    public AlphaVM() {}



    public AlphaVM addExternalFunction(String name, AlphaVMExternalFunction externalFunction) {
        this.externalFunctions.put(name, externalFunction);
        return this;
    }

    public AlphaVM setOutputListener(VMOutputListener outputListener) {
        this.outputListener = outputListener;
        return this;
    }

    public AlphaVM setCode(byte[] code, int length){
        this.code = code;
        this.codeLength = length;
        this.instructionPointer = 0;
        return this;
    }

    public boolean hasMoreInstructions() {
        //return this.instructionPointer < codeLength-1;
        return this.instructionPointer < codeLength;
    }

    public void execInstructions(int instructionCount){
        for(int i=0; i<instructionCount; i++){
            execInstruction();
        }
    }

    public void execInstruction(){

        // read next instruction opcode
        int opcode = 0xFF & this.code[this.instructionPointer++];

        // find out which instruction that opcode represents
        switch(opcode){
            // execute that instruction
            case NOP                    : { break; }
            case CALLEARG : { callearg(); break; }
            case OUT      : { out(); break; }
            case OUTR     : { outr(); break; }
            case PAR      : { par(); break; }
            case PSB      : { psb(); break; }
            case PSE      : { pse(); break; }
            case CALLE    : { calle(); break; }
            case EX       : { ex(); break; }
            case EXRET    : { exret(); break; }
            case EXPAR    : { expar(); break; }
            case PAREX    : { parex(); break; }
            case OUTEX    : { outex(); break; }
            case ESB      : { esb(); break; }
            case ESC      : { esc(); break; }
            case ESE      : { ese(); break; }
            case VAR      : { var(); break; }
            case GET      : { get(); break; }
            case SET      : { set(); break; }
            case SETEX    : { setex(); break; }
            case VSB      : { vsb(); break; }
            case VSE      : { vse(); break; }
            case ISB      : { isb(); break; }
            case ISE      : { ise(); break; }
            case TSB      : { tsb(); break; }
            case TSE      : { tse(); break; }
            case LEAVE    : { leave(); break; }
            case LEAVEIF  : { leaveif(); break; }
            case REPEAT   : { repeat(); break; }
            case REPEATIF : { repeatif(); break; }
            case IF       : { ift(); break; }
            case IFE      : { ife(); break; }
            case VARIABLE_GET           : { variableGet(); break; }
            case VARIABLE_SET_LITERAL   : { variableSetLiteral(); break; }
            case VARIABLE_SET_EXPRESSION_STACK_TOP : { variableSetExpressionStackTop(); break; }
            case VARIABLE_STACK_SCOPE_BEGIN: { variableStackScopeBegin(); break; }
            case VARIABLE_STACK_SCOPE_END  : { variableStackScopeEnd(); break; }
            case INSTRUCTION_SCOPE_BEGIN: { instructionScopeBegin(); break; }
            case INSTRUCTION_SCOPE_END  : { instructionScopeEnd(); break; }


            default : { throw new RuntimeException("Unknown opcode: " + opcode); }
        }
    }

    public void clear() {
        for(; this.expressionStackPointer >= 0; this.expressionStackPointer--){
            this.expressionStack[this.expressionStackPointer] = null;
        }
        this.expressionStackScopesPointer = -1;

        for(; this.parameterStackPointer >= 0; this.parameterStackPointer--){
            this.parameterStack[this.parameterStackPointer] = null;
        }
        this.parameterStackScopesPointer = -1;

        for(; this.variableScopeStackPointer >= 0; this.variableScopeStackPointer--){
            this.variableScopeStack[this.variableScopeStackPointer].clear();
            this.variableScopeStack[this.variableScopeStackPointer] = null;
        }

        this.instructionScopeStackPointer = -1;

        this.code = null;
        this.codeLength = 0;
        this.instructionPointer = 0;
        this.returnValue = null;
        this.arguments.clear();
        this.internalFunctions.clear();

        for(; this.returnLocationStackPointer >= 0; this.returnLocationStackPointer--){
            this.returnCodeBlocks[this.returnLocationStackPointer] = null;
        }
    }

    protected void callearg(){
        int argumentCount = 0xFF & this.code[this.instructionPointer++];

        // todo it is slow to create a new String just to hold the function name of a function. Use a more optimizes and reusable construct instead.
        this.pdeReader.setSource(this.code, this.instructionPointer, this.codeLength).next();
        String functionName = this.pdeReader.readKeyAsUtf8String();

        AlphaVMExternalFunction externalFunction = this.externalFunctions.get(functionName);
        if(externalFunction == null){
            throw new RuntimeException("Unknown external function: " + functionName);
        }

        this.arguments.clear();
        for(int i = 1; i < argumentCount; i++){
            this.pdeReader.next();
            this.arguments.add(readPdeValue());
        }

        this.returnValue = externalFunction.call(this.arguments);
        this.instructionPointer = pdeReader.nextOffset;
    }

    protected void out(){
        int argumentCount = 0xFF & this.code[this.instructionPointer++];

        this.pdeReader.setSource(this.code, this.instructionPointer, this.codeLength);

        this.arguments.clear();
        for(int i = 0; i < argumentCount; i++){
            this.pdeReader.next();
            this.arguments.add(readPdeValue());
        }
        this.outputListener.out(this.arguments);

        this.instructionPointer = this.pdeReader.nextOffset;
    }

    protected void outr(){
        this.outputListener.out(this.returnValue);
    }

    protected void psb(){
        this.parameterStackScopesPointer++;
        this.parameterStackScopes[this.parameterStackScopesPointer] = this.parameterStackPointer;
    }

    protected void pse(){
        int parameterScopeBoundary = this.parameterStackScopes[this.parameterStackScopesPointer];

        // Clear references to parameters stored on the parameter stack.
        for(;this.parameterStackPointer>parameterScopeBoundary; this.parameterStackPointer--){
            this.parameterStack[this.parameterStackPointer] = null;
        }

        this.parameterStackScopesPointer--;
    }

    protected void calle(){
        this.pdeReader.setSource(this.code, this.instructionPointer, this.codeLength).next();
        String functionName = this.pdeReader.readKeyAsUtf8String();

        AlphaVMExternalFunction externalFunction = this.externalFunctions.get(functionName);
        if(externalFunction == null){
            throw new RuntimeException("Unknown external function: " + functionName);
        }

        int parameterScopeBoundary = this.parameterStackScopes[this.parameterStackScopesPointer];
        this.arguments.clear();
        for(int i = parameterScopeBoundary + 1; i <= this.parameterStackPointer; i++){
            this.arguments.add(this.parameterStack[i]);
        }

        this.returnValue = externalFunction.call(this.arguments);
        pse();
        this.instructionPointer = this.pdeReader.nextOffset;
    }

    protected void ex(){
        this.pdeReader.setSource(this.code, this.instructionPointer, this.codeLength).next();
        Object value = readPdeValue();
        this.expressionStackPointer++;
        this.expressionStack[this.expressionStackPointer] = value;
        this.instructionPointer = pdeReader.nextOffset;
    }

    protected void exret(){
        this.expressionStackPointer++;
        this.expressionStack[this.expressionStackPointer] = this.returnValue;
    }

    protected void expar(){
        int index = 0xFF & this.code[this.instructionPointer++];
        int parameterScopeBoundary = this.parameterStackScopes[this.parameterStackScopesPointer];
        this.expressionStackPointer++;
        this.expressionStack[this.expressionStackPointer] = this.parameterStack[parameterScopeBoundary + 1 + index];
    }

    protected void parex(){
        this.parameterStackPointer++;
        this.parameterStack[this.parameterStackPointer] = this.expressionStack[this.expressionStackPointer];
    }

    protected void outex(){
        this.outputListener.out(this.expressionStack[this.expressionStackPointer]);
    }

    // todo remove this unused old method
    protected void parameterStackScopeEndOld() {
        int scopeBoundary = this.parameterStackScopes[this.parameterStackScopesPointer];

        // Clear references to parameters stored on the parameter stack.
        for(;this.parameterStackPointer>scopeBoundary; this.parameterStackPointer--){
            this.parameterStack[this.parameterStackPointer] = null;
        }
        this.parameterStackScopesPointer--;
    }

    public void instructionScopeBegin() {
        variableStackScopeBegin();
        this.instructionScopeStackPointer++;
        this.instructionScopeToVariableScope[this.instructionScopeStackPointer] = this.variableScopeStackPointer;

        long scopeStartOffset = (this.instructionPointer >> 24);
        scopeStartOffset <<= 8;
        scopeStartOffset |= 0xFF & (this.instructionPointer >> 16);
        scopeStartOffset <<= 8;
        scopeStartOffset |= 0xFF & (this.instructionPointer >> 8);
        scopeStartOffset <<= 8;
        scopeStartOffset |= 0xFF & (this.instructionPointer);

        // read end of scope offset from instructions - little endian - 2 bytes (what? only 2 bytes? todo revisit this!)
        long scopeEndOffset = 0xFF & this.code[this.instructionPointer + 1];
        scopeEndOffset <<=8;
        scopeEndOffset |= 0xFF & this.code[this.instructionPointer];

        long scopeBoundaries = (scopeEndOffset << 32) | scopeStartOffset;

        this.instructionScopeStack[this.instructionScopeStackPointer] = scopeBoundaries;
    }

    protected void instructionScopeEnd(){
        int variableScopeBoundary = this.instructionScopeToVariableScope[this.instructionScopeStackPointer];

        while(variableScopeBoundary <= this.variableScopeStackPointer){
            variableStackScopeEnd();
        }

        this.instructionScopeStackPointer--;
    }

    protected void variableStackScopeBegin(){
        this.variableScopeStackPointer++;
        this.variableScopeStack[this.variableScopeStackPointer] = new HashMap<>();
        this.variableScopeToExpressionScope[this.variableScopeStackPointer] = this.expressionStackScopesPointer;
    }

    protected void variableStackScopeEnd(){
        // end linked expression scope
        int expressionScopeBoundary = this.variableScopeToExpressionScope[this.variableScopeStackPointer];
        while(expressionScopeBoundary < this.expressionStackScopesPointer){
            ese();
        }

        this.variableScopeStack[this.variableScopeStackPointer].clear();
        this.variableScopeStack[this.variableScopeStackPointer] = null;
        this.variableScopeStackPointer--;
    }

    protected void esb(){
        this.expressionStackScopesPointer++;
        this.expressionStackScopes[this.expressionStackScopesPointer] = this.expressionStackPointer;
    }

    protected void esc(){
        int expressionScopeBoundary = this.expressionStackScopes[this.expressionStackScopesPointer];

        for(;this.expressionStackPointer>expressionScopeBoundary; this.expressionStackPointer--){
            this.expressionStack[this.expressionStackPointer] = null;
        }
    }

    protected void ese(){
        esc();
        this.expressionStackScopesPointer--;
    }

    protected void var(){
        this.pdeReader.setSource(this.code, this.instructionPointer, this.codeLength).next();
        String variableName = this.pdeReader.readKeyAsUtf8String();
        this.variableScopeStack[this.variableScopeStackPointer].put(variableName, null);
        this.instructionPointer = this.pdeReader.nextOffset;
    }

    protected void get(){
        this.pdeReader.setSource(this.code, this.instructionPointer, this.codeLength).next();
        String variableName = this.pdeReader.readKeyAsUtf8String();

        for(int scopeIndex = this.variableScopeStackPointer; scopeIndex >= 0; scopeIndex--){
            if(this.variableScopeStack[scopeIndex].containsKey(variableName)){
                this.expressionStackPointer++;
                this.expressionStack[this.expressionStackPointer] = this.variableScopeStack[scopeIndex].get(variableName);
                break;
            }
        }

        this.instructionPointer = this.pdeReader.nextOffset;
    }

    protected void set(){
        this.pdeReader.setSource(this.code, this.instructionPointer, this.codeLength).next();
        String variableName = this.pdeReader.readKeyAsUtf8String();
        this.pdeReader.next();
        Object value = readPdeValue();

        for(int scopeIndex = this.variableScopeStackPointer; scopeIndex >= 0; scopeIndex--){
            if(this.variableScopeStack[scopeIndex].containsKey(variableName)){
                this.variableScopeStack[scopeIndex].put(variableName, value);
                break;
            }
        }

        this.instructionPointer = this.pdeReader.nextOffset;
    }

    protected void setex(){
        this.pdeReader.setSource(this.code, this.instructionPointer, this.codeLength).next();
        String variableName = this.pdeReader.readKeyAsUtf8String();

        for(int scopeIndex = this.variableScopeStackPointer; scopeIndex >= 0; scopeIndex--){
            if(this.variableScopeStack[scopeIndex].containsKey(variableName)){
                this.variableScopeStack[scopeIndex].put(variableName, this.expressionStack[this.expressionStackPointer]);
                break;
            }
        }

        this.instructionPointer = this.pdeReader.nextOffset;
    }

    protected void vsb(){
        variableStackScopeBegin();
    }

    protected void vse(){
        variableStackScopeEnd();
    }

    protected void isb(){
        int endOffset = 0xFF & this.code[this.instructionPointer++];
        endOffset |= (0xFF & this.code[this.instructionPointer++]) << 8;

        this.instructionScopeStackPointer++;
        this.instructionScopeToVariableScope[this.instructionScopeStackPointer] = this.variableScopeStackPointer;
        long endLocation = (long) this.instructionPointer + endOffset;
        this.instructionScopeStack[this.instructionScopeStackPointer] =
                (endLocation << 32) | (0xFFFF_FFFFL & this.instructionPointer);
    }

    protected void ise(){
        int variableScopeBoundary = this.instructionScopeToVariableScope[this.instructionScopeStackPointer];
        while(variableScopeBoundary < this.variableScopeStackPointer){
            variableStackScopeEnd();
        }
        this.instructionScopeStackPointer--;
    }

    protected void tsb(){
        esb();
        vsb();
        isb();
    }

    protected void tse(){
        ise();
        vse();
        ese();
    }

    protected void leave(){
        long scopeBoundaries = this.instructionScopeStack[this.instructionScopeStackPointer];
        this.instructionPointer = (int) (scopeBoundaries >>> 32);
    }

    protected void leaveif(){
        if(this.expressionStack[this.expressionStackPointer] == Boolean.TRUE){
            leave();
        }
    }

    protected void repeat(){
        long scopeBoundaries = this.instructionScopeStack[this.instructionScopeStackPointer];
        this.instructionPointer = (int) scopeBoundaries;
    }

    protected void repeatif(){
        if(this.expressionStack[this.expressionStackPointer] == Boolean.TRUE){
            repeat();
        }
    }

    protected void ift(){
        if(this.expressionStack[this.expressionStackPointer] == Boolean.TRUE){
            return;
        }
        int endOffset = 0xFF & this.code[this.instructionPointer + 1];
        endOffset |= (0xFF & this.code[this.instructionPointer + 2]) << 8;
        this.instructionPointer = this.instructionPointer + 3 /*tsb*/ + 1 /*tse*/ + endOffset;
    }

    protected void ife(){
        if(this.expressionStack[this.expressionStackPointer] == Boolean.TRUE){
            this.instructionPointer = this.instructionPointer + 2;
            return;
        }
        int elseOffset = 0xFF & this.code[this.instructionPointer];
        elseOffset |= (0xFF & this.code[this.instructionPointer + 1]) << 8;
        this.instructionPointer = this.instructionPointer + 2 + elseOffset;
    }

    protected void par(){
        this.pdeReader.setSource(this.code, this.instructionPointer, this.codeLength).next();
        //this.pdeReader.next(); // parse the PDE field at the offset

        Object value = readPdeValue();

        this.parameterStackPointer++;
        this.parameterStack[this.parameterStackPointer] = value;
        this.instructionPointer = pdeReader.nextOffset;
    }



    protected void variableGet(){
        this.pdeReader.setSource(this.code, this.instructionPointer, this.codeLength);
        this.pdeReader.next(); // parse the PDE field at the offset

        int variableNameLength = this.pdeReader.readUtf8(this.pdeBuffer, 0);
        String variableName = new String (this.pdeBuffer, 0, variableNameLength, StandardCharsets.UTF_8);

        Object variableValue = this.variableScopeStack[this.variableScopeStackPointer].get(variableName);
        this.expressionStackPointer++;
        this.expressionStack[this.expressionStackPointer] = variableValue;
    }

    protected void variableSetLiteral(){
        this.pdeReader.setSource(this.code, this.instructionPointer, this.codeLength);
        this.pdeReader.next(); // parse the PDE field at the offset

        String variableName = readPdeValue();
        Object value = readPdeValue();
        this.variableScopeStack[this.variableScopeStackPointer].put(variableName, value);
    }

    protected void variableSetExpressionStackTop(){
        this.pdeReader.setSource(this.code, this.instructionPointer, this.codeLength);
        this.pdeReader.next(); // parse the PDE field at the offset

        String variableName = readPdeValue();
        this.variableScopeStack[this.variableScopeStackPointer].put(variableName,
                this.expressionStack[this.expressionStackPointer]);

    }

    private <T> T readPdeValue() {
        return (T) switch (this.pdeReader.fieldType) {
            case BOOLEAN_FALSE, PdeFieldTypes.BOOLEAN_TRUE -> pdeReader.readBoolean();
            case BOOLEAN_NULL -> null;

            case INT_POS_1_BYTES, INT_POS_2_BYTES, INT_POS_3_BYTES, INT_POS_4_BYTES,
                 INT_POS_5_BYTES, INT_POS_6_BYTES, INT_POS_7_BYTES, INT_POS_8_BYTES -> pdeReader.readInt();
            case INT_NULL -> null;

            case PdeFieldTypes.FLOAT_NULL -> null;
            case PdeFieldTypes.FLOAT_4_BYTES -> (Float) pdeReader.readFloat32();
            case PdeFieldTypes.FLOAT_8_BYTES -> (Double) pdeReader.readFloat64();

            case PdeFieldTypes.UTF_8_NULL -> null;
            case PdeFieldTypes.UTF_8_0_BYTES -> "";
            case PdeFieldTypes.UTF_8_1_BYTES, PdeFieldTypes.UTF_8_2_BYTES, PdeFieldTypes.UTF_8_3_BYTES, PdeFieldTypes.UTF_8_4_BYTES,
                 PdeFieldTypes.UTF_8_5_BYTES, PdeFieldTypes.UTF_8_6_BYTES, PdeFieldTypes.UTF_8_7_BYTES, PdeFieldTypes.UTF_8_8_BYTES,
                 PdeFieldTypes.UTF_8_9_BYTES, PdeFieldTypes.UTF_8_10_BYTES, PdeFieldTypes.UTF_8_11_BYTES, PdeFieldTypes.UTF_8_12_BYTES,
                 PdeFieldTypes.UTF_8_13_BYTES, PdeFieldTypes.UTF_8_14_BYTES, PdeFieldTypes.UTF_8_15_BYTES,
                 PdeFieldTypes.UTF_8_1_LENGTH_BYTES, PdeFieldTypes.UTF_8_2_LENGTH_BYTES, PdeFieldTypes.UTF_8_3_LENGTH_BYTES,
                 PdeFieldTypes.UTF_8_4_LENGTH_BYTES, PdeFieldTypes.UTF_8_5_LENGTH_BYTES, PdeFieldTypes.UTF_8_6_LENGTH_BYTES,
                 PdeFieldTypes.UTF_8_7_LENGTH_BYTES, PdeFieldTypes.UTF_8_8_LENGTH_BYTES -> {
                int valueLength = this.pdeReader.readUtf8(this.pdeBuffer, 0);
                yield new String(this.pdeBuffer, 0, valueLength, StandardCharsets.UTF_8);
            }

            default -> {
                throw new RuntimeException("Unknown or unsupported PDE field type: " + this.pdeReader.fieldType);
            }
        };
    }

}