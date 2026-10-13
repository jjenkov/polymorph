package com.plmph.vm;

import com.plmph.pde.PdeWriter;

import java.nio.charset.StandardCharsets;

public class AlphaBincodeWriter {

    public byte[] dest = null;
    public int offset = 0;

    protected int[] instructionScopeStack = new int[1024];
    protected int instructionScopeStackPointer = -1;

    protected int[] ifeOffsetStack = new int[1024];
    protected int ifeStackPointer = -1;

    public PdeWriter pdeWriter = new PdeWriter();


    public AlphaBincodeWriter setDest(byte[] dest) {
        this.dest = dest;
        this.offset = 0;
        return this;
    }

    public AlphaBincodeWriter setDest(byte[] dest, int offset) {
        this.dest = dest;
        this.offset = offset;
        return this;
    }

    public AlphaBincodeWriter nop() {
        this.dest[this.offset++] = (byte) (0xFF & AlphaInstructionOpcodes.NOP);
        return this;
    }

    public AlphaBincodeWriter callearg(String functionName, Object... arguments) {
        int argumentCount = 1 + arguments.length;
        if(argumentCount > 255) {
            throw new RuntimeException("callearg supports at most 255 arguments, got " + argumentCount);
        }

        this.dest[this.offset++] = (byte) (0xFF & AlphaInstructionOpcodes.CALLEARG);
        this.dest[this.offset++] = (byte) argumentCount;

        this.pdeWriter.setDest(this.dest, this.offset)
                      .writeKeyUtf8(functionName);

        writeLiterals(arguments);
        return this;
    }

    public AlphaBincodeWriter out(Object... arguments) {
        if(arguments.length > 255) {
            throw new RuntimeException("out supports at most 255 arguments, got " + arguments.length);
        }

        this.dest[this.offset++] = (byte) (0xFF & AlphaInstructionOpcodes.OUT);
        this.dest[this.offset++] = (byte) arguments.length;

        writeLiteralsSetDest(arguments);
        return this;
    }

    public AlphaBincodeWriter outr() {
        this.dest[this.offset++] = (byte) (0xFF & AlphaInstructionOpcodes.OUTR);
        return this;
    }


    public AlphaBincodeWriter par(Object literal) {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.PAR;
        writeLiteralSetDest(literal);
        return this;
    }

    public AlphaBincodeWriter psb() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.PSB;
        return this;
    }

    public AlphaBincodeWriter pse() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.PSE;
        return this;
    }

    public AlphaBincodeWriter calle(String functionName) {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.CALLE;
        this.pdeWriter.setDest(this.dest, this.offset)
                      .writeKeyUtf8(functionName);
        this.offset = this.pdeWriter.offset;
        return this;
    }

    public AlphaBincodeWriter ex(Object literal) {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.EX;
        writeLiteralSetDest(literal);
        return this;
    }

    public AlphaBincodeWriter exret() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.EXRET;
        return this;
    }

    public AlphaBincodeWriter expar(int index) {
        if(index < 0 || index > 255) {
            throw new RuntimeException("expar index must be from 0 to 255, got " + index);
        }

        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.EXPAR;
        this.dest[this.offset++] = (byte) (0xFF & index);
        return this;
    }

    public AlphaBincodeWriter parex() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.PAREX;
        return this;
    }

    public AlphaBincodeWriter outex() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.OUTEX;
        return this;
    }

    public AlphaBincodeWriter esb() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.ESB;
        return this;
    }

    public AlphaBincodeWriter esc() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.ESC;
        return this;
    }

    public AlphaBincodeWriter ese() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.ESE;
        return this;
    }

    public AlphaBincodeWriter var(String variableName) {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.VAR;
        this.pdeWriter.setDest(this.dest, this.offset)
                      .writeKeyUtf8(variableName);
        this.offset = this.pdeWriter.offset;
        return this;
    }

    public AlphaBincodeWriter get(String variableName) {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.GET;
        this.pdeWriter.setDest(this.dest, this.offset)
                      .writeKeyUtf8(variableName);
        this.offset = this.pdeWriter.offset;
        return this;
    }

    public AlphaBincodeWriter set(String variableName, Object literal) {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.SET;
        this.pdeWriter.setDest(this.dest, this.offset)
                      .writeKeyUtf8(variableName);
        writeLiteral(literal);
        return this;
    }

    public AlphaBincodeWriter setex(String variableName) {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.SETEX;
        this.pdeWriter.setDest(this.dest, this.offset)
                      .writeKeyUtf8(variableName);
        this.offset = this.pdeWriter.offset;
        return this;
    }

    public AlphaBincodeWriter vsb() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.VSB;
        return this;
    }

    public AlphaBincodeWriter vse() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.VSE;
        return this;
    }

    public AlphaBincodeWriter isb() {
        this.instructionScopeStack[++this.instructionScopeStackPointer] = this.offset;
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.ISB;
        this.offset += 2;
        return this;
    }

    public AlphaBincodeWriter ise() {
        int isbOffset = this.instructionScopeStack[this.instructionScopeStackPointer--];
        int relativeOffset = this.offset - isbOffset - 3;
        if(relativeOffset < 0 || relativeOffset > 65535) {
            throw new RuntimeException("isb relative offset must be from 0 to 65535, got " + relativeOffset);
        }

        this.dest[isbOffset + 1] = (byte) (0xFF & relativeOffset);
        this.dest[isbOffset + 2] = (byte) (0xFF & (relativeOffset >> 8));
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.ISE;
        return this;
    }

    public AlphaBincodeWriter tsb() {
        this.instructionScopeStack[++this.instructionScopeStackPointer] = this.offset;
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.TSB;
        this.offset += 2;
        return this;
    }

    public AlphaBincodeWriter tse() {
        int tsbOffset = this.instructionScopeStack[this.instructionScopeStackPointer--];
        int relativeOffset = this.offset - tsbOffset - 3;
        if(relativeOffset < 0 || relativeOffset > 65535) {
            throw new RuntimeException("tsb relative offset must be from 0 to 65535, got " + relativeOffset);
        }

        this.dest[tsbOffset + 1] = (byte) (0xFF & relativeOffset);
        this.dest[tsbOffset + 2] = (byte) (0xFF & (relativeOffset >> 8));
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.TSE;
        return this;
    }

    public AlphaBincodeWriter leave() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.LEAVE;
        return this;
    }

    public AlphaBincodeWriter leaveif() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.LEAVEIF;
        return this;
    }

    public AlphaBincodeWriter repeat() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.REPEAT;
        return this;
    }

    public AlphaBincodeWriter repeatif() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.REPEATIF;
        return this;
    }

    public AlphaBincodeWriter ift() {
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.IF;
        return this;
    }

    public AlphaBincodeWriter ife() {
        this.ifeOffsetStack[++this.ifeStackPointer] = this.offset;
        this.dest[this.offset++] = (byte) 0xFF & AlphaInstructionOpcodes.IFE;
        this.offset += 2;
        return this;
    }

    public AlphaBincodeWriter ifeElse() {
        int ifeOffset = this.ifeOffsetStack[this.ifeStackPointer--];
        int relativeOffset = this.offset - ifeOffset - 3;
        if(relativeOffset < 0 || relativeOffset > 65535) {
            throw new RuntimeException("ife relative offset must be from 0 to 65535, got " + relativeOffset);
        }

        this.dest[ifeOffset + 1] = (byte) (0xFF & relativeOffset);
        this.dest[ifeOffset + 2] = (byte) (0xFF & (relativeOffset >> 8));
        return this;
    }

    private void writeLiterals(Object ... literals){
        for(Object literal : literals) {
            doWriteLiteral(literal);
        }
        this.offset = pdeWriter.offset;
    }

    private void writeLiteralsSetDest(Object ... literals){
        this.pdeWriter.setDest(this.dest, this.offset);
        for(Object literal : literals) {
            doWriteLiteral(literal);
        }
        this.offset = pdeWriter.offset;
    }

    private void writeLiteral(Object literal) {
        doWriteLiteral(literal);
        this.offset = pdeWriter.offset;
    }

    private void writeLiteralSetDest(Object literal) {
        this.pdeWriter.setDest(this.dest, this.offset);
        doWriteLiteral(literal);
        this.offset = pdeWriter.offset;
    }

    /**
     * Writes a literal object in PDE encoding to the destination byte buffer.
     * Note: You must set the destination of the pdeWriter (pdeWriter.setDest()) before calling this method,
     *       and you must capture the pdeWriter.offset yourself after calling this method (when you are ready to capture it).
     * @param literal The obect literal to write to the destination byte buffer in PDE encoding.
     */
    private void doWriteLiteral(Object literal) {
        switch(literal) {
            case String str -> {
                byte[] strBytes = str.getBytes(StandardCharsets.UTF_8); //todo faster conversion than getBytes()
                this.pdeWriter.writeUtf8(strBytes);
            }
            case Long longVal -> {
                this.pdeWriter.writeInt(longVal);
            }
            case Boolean boolVal -> {
                this.pdeWriter.writeBoolean(boolVal);
            }
            // todo add more literal types
            default -> {
                throw new RuntimeException("Unsupported Pasm literal type: " + literal.getClass().getName());
            }
        }
    }

}