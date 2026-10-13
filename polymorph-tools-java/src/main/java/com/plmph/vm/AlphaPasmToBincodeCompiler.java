package com.plmph.vm;

import com.plmph.pdl.PdlTokenizer;

import java.util.ArrayList;

public class AlphaPasmToBincodeCompiler {

    private String[] instructionStack = new String[1024];
    private int instructionStackIndex = -1;

    public int compile(byte[] source, int offset, int length, byte[] dest, int destOffset){

        // step 1: tokenize the Pasm. Use the PDL tokenizer as the syntax is the same.
        long[] tokenOffsetPairs = new long[1024]; // todo do not create this every time. It can be reused.
        int tokenCount = tokenizePasm(source, offset, length, tokenOffsetPairs);

        AlphaBincodeWriter writer = new AlphaBincodeWriter().setDest(dest, destOffset);

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
                    String instruction = new String(source, tokenStartOffset, (tokenEndOffset - tokenStartOffset) - 1);
                    System.out.println("instruction: " + instruction);
                    instructionStack[++instructionStackIndex] = instruction;

                    if("nop".equals(instruction)){
                        writer.nop();
                        break;
                    }

                    if("callearg".equals(instruction)){
                        i++;
                        if(i >= tokenCount){
                            throw new RuntimeException("callearg requires a function name key");
                        }
                        tokenOffsetPair  = tokenOffsetPairs[i];
                        tokenStartOffset = (int) (tokenOffsetPair & 0xFFFFFFFFL);
                        tokenEndOffset   = (int) (tokenOffsetPair >> 32);

                        if(source[tokenStartOffset] != '.'){
                            throw new RuntimeException("Expected a key as the first parameter of callearg");
                        }

                        String functionName = new String(source, tokenStartOffset + 1, (tokenEndOffset - tokenStartOffset) - 2);

                        ArrayList<Object> arguments = new ArrayList<>();
                        while(true){
                            i++;
                            if(i >= tokenCount){
                                throw new RuntimeException("callearg is missing its closing )");
                            }
                            tokenOffsetPair  = tokenOffsetPairs[i];
                            tokenStartOffset = (int) (tokenOffsetPair & 0xFFFFFFFFL);
                            tokenEndOffset   = (int) (tokenOffsetPair >> 32);

                            int argumentType = source[tokenStartOffset];
                            if(argumentType == ')'){
                                i--;
                                break;
                            }

                            int argumentLength = tokenEndOffset - tokenStartOffset;
                            switch(argumentType){
                                case '"' : {
                                    arguments.add(new String(source, tokenStartOffset + 1, argumentLength - 2));
                                    break;
                                }
                                case '0','1','2','3','4','5','6','7','8','9' : {
                                    arguments.add(Long.parseLong(new String(source, tokenStartOffset, argumentLength - 1)));
                                    break;
                                }
                                default : {
                                    throw new RuntimeException("Unsupported callearg argument");
                                }
                            }
                        }

                        writer.callearg(functionName, arguments.toArray()); // todo replace the list with an array to avoid creating new arrays all the time.
                        break;
                    }
                }
                case ')' : {
                    instructionStackIndex--;
                    break;
                }

            }
        }

        return writer.offset;
    }

    private int tokenizePasm(byte[] source, int offset, int length, long[] tokenOffsetPairs){
        return PdlTokenizer.tokenize(source, offset, length, tokenOffsetPairs);
    }

}