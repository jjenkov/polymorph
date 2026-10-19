package com.plmph.vm;

import com.plmph.pdl.PdlTokenizer;

import java.util.ArrayList;

public class AlphaPasmToBincodeCompiler {

    protected byte[] source;
    protected int sourceOffset;
    protected int sourceLength;

    protected long[] tokenOffsetPairs = new long[1024];
    protected int    tokenCount = 0;

    protected AlphaBincodeWriter writer = new AlphaBincodeWriter();

    private String[] instructionStack = new String[1024];
    private int instructionStackIndex = -1;


    public int compile(byte[] source, int offset, int length, byte[] dest, int destOffset){
        this.source = source;
        this.sourceOffset = offset;
        this.sourceLength = length;

        // step 1: tokenize the Pasm. Use the PDL tokenizer as the syntax is the same.
        tokenCount = tokenizePasm();

        writer.setDest(dest, destOffset);

        // step 2: iterate through the tokens and generate bincode using the bincode writer.
        for(int i=0; i<tokenCount; i++){
            // step 2.1 find next token offset pair
            long tokenOffsetPair = tokenOffsetPairs[i];
            int tokenStartOffset = (int) (tokenOffsetPair & 0xFFFFFFFFL);
            int tokenEndOffset   = (int) (tokenOffsetPair >> 32);

            // step 2.2 figure out which type of instruction we are looking at:
            int tokenType = source[tokenStartOffset];
            switch(tokenType){
                case 'a','b','c','d','e','f','g','h','i', 'j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
                     'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
                     : {
                    // starts with an alphabetic character, so this is an instruction - not a PDL literal
                    String instruction = new String(source, tokenStartOffset, (tokenEndOffset - tokenStartOffset) - 1); //todo find a faster match mechanism than creating a new string every time.
                    //System.out.println("instruction: " + instruction);
                    instructionStack[++instructionStackIndex] = instruction;

                    switch(instruction){
                        case "nop"      -> nop();
                        case "callearg" -> i = callearg(i);
                        case "out"      -> i = out(i);
                        case "outr"     -> outr();
                        case "par"      -> i = par(i);
                        case "psb"      -> psb();
                        case "pse"      -> pse();
                        case "calle"    -> i = calle(i);
                        case "ex"       -> i = ex(i);
                        case "exret"    -> exret();
                        case "expar"    -> i = expar(i);
                        case "parex"    -> parex();
                        case "outex"    -> outex();
                        case "esb"      -> esb();
                        case "esc"      -> esc();
                        case "ese"      -> ese();
                        case "var"      -> i = var(i);
                        case "get"      -> i = get(i);
                        case "set"      -> i = set(i);
                        case "setex"    -> i = setex(i);
                        case "vsb"      -> vsb();
                        case "vse"      -> vse();
                        case "isb"      -> isb();
                        case "ise"      -> ise();
                        case "tsb"      -> tsb();
                        case "tse"      -> tse();
                        case "leave"    -> leave();
                        case "leaveif"  -> leaveif();
                        case "repeat"   -> repeat();
                        case "repeatif" -> repeatif();
                        case "if"       -> ift();
                        case "ife"      -> ife();
                        case "else"     -> ifeElse();
                        default -> throw new RuntimeException("Unknown instruction: " + instruction);
                    }
                    break;
                }
                case '(' : {
                    instructionStack[++instructionStackIndex] = "(";
                    tsb();
                    break;
                }
                case ')' : {
                    String opened = instructionStack[instructionStackIndex];
                    instructionStack[instructionStackIndex] = null;
                    instructionStackIndex--;

                    if(opened.equals("(")){
                        tse();
                    }
                    break;
                }

            }
        }

        return writer.offset;
    }

    private void nop(){
        writer.nop();
    }

    private int callearg(int i){
        i++;
        if(i >= tokenCount){
            throw new RuntimeException("callearg requires a function name key");
        }
        long tokenOffsetPair  = tokenOffsetPairs[i];
        int tokenStartOffset = (int) (tokenOffsetPair & 0xFFFFFFFFL);
        int tokenEndOffset   = (int) (tokenOffsetPair >> 32);

        if(source[tokenStartOffset] != '.'){
            throw new RuntimeException("Expected a key as the first parameter of callearg");
        }

        String functionName = new String(source, tokenStartOffset + 1, (tokenEndOffset - tokenStartOffset) - 2);

        ArrayList<Object> arguments = new ArrayList<>();
        i = parseLiteralArguments(i, arguments);

        writer.callearg(functionName, arguments.toArray()); // todo replace the list with an array to avoid creating new arrays all the time.
        return i;
    }

    private int out(int i){
        ArrayList<Object> arguments = new ArrayList<>();
        i = parseLiteralArguments(i, arguments);
        writer.out(arguments.toArray());
        return i;
    }

    private void outr(){
        writer.outr();
    }

    private int par(int i){
        //todo check if there is a faster way to parse just 1 argument than parsing a list of arguments and taking only the first argument from the list.
        ArrayList<Object> arguments = new ArrayList<>();
        i = parseLiteralArguments(i, arguments);
        if(arguments.size() != 1){
            throw new RuntimeException("par requires one literal argument");
        }
        writer.par(arguments.get(0));
        return i;
    }

    private void psb(){
        writer.psb();
    }

    private void pse(){
        writer.pse();
    }

    private int calle(int i){
        i++;
        if(i >= tokenCount){
            throw new RuntimeException("calle requires a function name key");
        }
        long tokenOffsetPair  = tokenOffsetPairs[i];
        int tokenStartOffset = (int) (tokenOffsetPair & 0xFFFFFFFFL);
        int tokenEndOffset   = (int) (tokenOffsetPair >> 32);

        if(source[tokenStartOffset] != '.'){
            throw new RuntimeException("Expected a key as the first parameter of calle");
        }

        String functionName = new String(source, tokenStartOffset + 1, (tokenEndOffset - tokenStartOffset) - 2);

        int closeIndex = i + 1;
        if(closeIndex >= tokenCount){
            throw new RuntimeException("End of source encountered before a ) was found");
        }
        long closePair = tokenOffsetPairs[closeIndex];
        int closeStart = (int) (closePair & 0xFFFFFFFFL);
        if(source[closeStart] != ')'){
            throw new RuntimeException("calle takes only one argument - the function name key ");
        }

        writer.calle(functionName);
        return i;
    }

    private int ex(int i){
        ArrayList<Object> arguments = new ArrayList<>();
        i = parseLiteralArguments(i, arguments);
        if(arguments.size() != 1){
            throw new RuntimeException("ex requires one literal argument");
        }
        writer.ex(arguments.get(0));
        return i;
    }

    private void exret(){
        writer.exret();
    }

    private int expar(int i){
        ArrayList<Object> arguments = new ArrayList<>();
        i = parseLiteralArguments(i, arguments);
        if(arguments.size() != 1 || !(arguments.get(0) instanceof Long index)){
            throw new RuntimeException("expar requires one index argument");
        }
        if(index < 0 || index > 255){
            throw new RuntimeException("expar index must be from 0 to 255, got " + index);
        }
        writer.expar(index.intValue());
        return i;
    }

    private void parex(){
        writer.parex();
    }

    private void outex(){
        writer.outex();
    }

    private void esb(){
        writer.esb();
    }

    private void esc(){
        writer.esc();
    }

    private void ese(){
        writer.ese();
    }

    private int var(int i){
        i++;
        if(i >= tokenCount){
            throw new RuntimeException("var requires a variable name key");
        }
        long tokenOffsetPair  = tokenOffsetPairs[i];
        int tokenStartOffset = (int) (tokenOffsetPair & 0xFFFFFFFFL);
        int tokenEndOffset   = (int) (tokenOffsetPair >> 32);

        if(source[tokenStartOffset] != '.'){
            throw new RuntimeException("Expected a key as the first parameter of var");
        }

        String variableName = new String(source, tokenStartOffset + 1, (tokenEndOffset - tokenStartOffset) - 2);

        int closeIndex = i + 1;
        if(closeIndex >= tokenCount){
            throw new RuntimeException("End of source encountered before a ) was found");
        }
        long closePair = tokenOffsetPairs[closeIndex];
        int closeStart = (int) (closePair & 0xFFFFFFFFL);
        if(source[closeStart] != ')'){
            throw new RuntimeException("var takes only one argument - the variable name key ");
        }

        writer.var(variableName);
        return i;
    }

    private int get(int i){
        i++;
        if(i >= tokenCount){
            throw new RuntimeException("get requires a variable name key");
        }
        long tokenOffsetPair  = tokenOffsetPairs[i];
        int tokenStartOffset = (int) (tokenOffsetPair & 0xFFFFFFFFL);
        int tokenEndOffset   = (int) (tokenOffsetPair >> 32);

        if(source[tokenStartOffset] != '.'){
            throw new RuntimeException("Expected a key as the first parameter of get");
        }

        String variableName = new String(source, tokenStartOffset + 1, (tokenEndOffset - tokenStartOffset) - 2);

        int closeIndex = i + 1;
        if(closeIndex >= tokenCount){
            throw new RuntimeException("End of source encountered before a ) was found");
        }
        long closePair = tokenOffsetPairs[closeIndex];
        int closeStart = (int) (closePair & 0xFFFFFFFFL);
        if(source[closeStart] != ')'){
            throw new RuntimeException("get takes only one argument - the variable name key ");
        }

        writer.get(variableName);
        return i;
    }

    private int set(int i){
        i++;
        if(i >= tokenCount){
            throw new RuntimeException("set requires a variable name key");
        }
        long tokenOffsetPair  = tokenOffsetPairs[i];
        int tokenStartOffset = (int) (tokenOffsetPair & 0xFFFFFFFFL);
        int tokenEndOffset   = (int) (tokenOffsetPair >> 32);

        if(source[tokenStartOffset] != '.'){
            throw new RuntimeException("Expected a key as the first parameter of set");
        }

        String variableName = new String(source, tokenStartOffset + 1, (tokenEndOffset - tokenStartOffset) - 2);

        ArrayList<Object> arguments = new ArrayList<>();
        i = parseLiteralArguments(i, arguments);
        if(arguments.size() != 1){
            throw new RuntimeException("set requires one literal argument");
        }

        writer.set(variableName, arguments.get(0));
        return i;
    }

    private int setex(int i){
        i++;
        if(i >= tokenCount){
            throw new RuntimeException("setex requires a variable name key");
        }
        long tokenOffsetPair  = tokenOffsetPairs[i];
        int tokenStartOffset = (int) (tokenOffsetPair & 0xFFFFFFFFL);
        int tokenEndOffset   = (int) (tokenOffsetPair >> 32);

        if(source[tokenStartOffset] != '.'){
            throw new RuntimeException("Expected a key as the first parameter of setex");
        }

        String variableName = new String(source, tokenStartOffset + 1, (tokenEndOffset - tokenStartOffset) - 2);

        int closeIndex = i + 1;
        if(closeIndex >= tokenCount){
            throw new RuntimeException("End of source encountered before a ) was found");
        }
        long closePair = tokenOffsetPairs[closeIndex];
        int closeStart = (int) (closePair & 0xFFFFFFFFL);
        if(source[closeStart] != ')'){
            throw new RuntimeException("setex takes only one argument - the variable name key ");
        }

        writer.setex(variableName);
        return i;
    }

    private void vsb(){
        writer.vsb();
    }

    private void vse(){
        writer.vse();
    }

    private void isb(){
        writer.isb();
    }

    private void ise(){
        writer.ise();
    }

    private void tsb(){
        writer.tsb();
    }

    private void tse(){
        writer.tse();
    }

    private void leave(){
        writer.leave();
    }

    private void leaveif(){
        writer.leaveif();
    }

    private void repeat(){
        writer.repeat();
    }

    private void repeatif(){
        writer.repeatif();
    }

    private void ift(){
        writer.ift();
    }

    private void ife(){
        writer.ife();
    }

    private void ifeElse(){
        writer.leave();
        writer.ifeElse();
    }

    private int parseLiteralArguments(int tokenOffsetsIndex, ArrayList<Object> arguments){
        for(int i=tokenOffsetsIndex + 1; i<tokenCount; i++){
            long tokenOffsetPair  = tokenOffsetPairs[i];
            int tokenStartOffset = (int) (tokenOffsetPair & 0xFFFFFFFFL);
            int tokenEndOffset   = (int) (tokenOffsetPair >> 32);

            int tokenLength = tokenEndOffset - tokenStartOffset;
            int tokenType = source[tokenStartOffset];
            switch(tokenType){
                case ')' : {
                    i--;
                    return i;
                }
                case '"' : {
                    arguments.add(new String(source, tokenStartOffset + 1, tokenLength - 2));
                    break;
                }
                case '0','1','2','3','4','5','6','7','8','9' : {
                    arguments.add(Long.parseLong(new String(source, tokenStartOffset, tokenLength - 1)));
                    break;
                }
                default : {
                    throw new RuntimeException("Unsupported argument");
                }
            }
        }
        throw new RuntimeException("End of source encountered before a ) was found");
    }

    private int tokenizePasm(){
        return PdlTokenizer.tokenize(source, sourceOffset, sourceLength, tokenOffsetPairs);
    }

}
