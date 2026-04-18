package com.plmph.pdl;

public class PdlTokenizer {

    private static final byte[] tokenEndCharacters = new byte[128];

    static {
        // default end character is ;
        for(int i=0; i<tokenEndCharacters.length; i++) {
            tokenEndCharacters[i] = ';';
        }
        // single-character tokens end with themselves as end character.
        tokenEndCharacters['{'] = '{';
        tokenEndCharacters['}'] = '}';
        tokenEndCharacters['['] = '[';
        tokenEndCharacters[']'] = ']';
        tokenEndCharacters['<'] = '<';
        tokenEndCharacters['>'] = '>';
        tokenEndCharacters['('] = '(';
        tokenEndCharacters[')'] = ')';
        tokenEndCharacters['*'] = '~';

        // tokens that start with a letter (a-z + A-Z) are named tokens ending with a ( as end character.
        for(int i='a'; i<'z'; i++){
            tokenEndCharacters[i] = '(';
        }
        for(int i='A'; i<'Z'; i++){
            tokenEndCharacters[i] = '(';
        }
    }


    public static int tokenizeMinified(byte[] source, int offset, int length, long[] tokenOffsets){
        int tokenOffsetsIndex = 0;
        int endOffset = offset + length;

        for(;offset < endOffset;){
            int tokenStartOffset  = offset;
            int tokenEndCharacter = tokenEndCharacters[source[tokenStartOffset]];

            for(;offset < endOffset; offset++){
                if(source[offset] == tokenEndCharacter) { break; }
            }

            offset++; // We want the index right after the last character in the token.
            long tokenOffsetPair = (long) ((long) offset << 32 | (long) tokenStartOffset);
            tokenOffsets[tokenOffsetsIndex++] = tokenOffsetPair;
        }

        return tokenOffsetsIndex; // return number of token offsets written into tokenOffset (=number of tokens found)
    }

    public static int tokenize_for(byte[] source, int offset, int length, long[] tokenOffsets){
        int tokenOffsetsIndex = 0;
        int endOffset = offset + length;

        //skip whitespace
        for(;offset < endOffset; offset++){
            if(source[offset] > 32 ) { break; } // All ASCII values from 32 and down are considered white space
        }

        for(;offset < endOffset;){
            int tokenStartOffset  = offset;
            int tokenEndCharacter = tokenEndCharacters[source[tokenStartOffset]];

            for(;offset < endOffset; offset++){
                if(source[offset] == tokenEndCharacter) { break; }
            }

            offset++; // We want the index right after the last character in the token.
            long tokenOffsetPair = (long) ((long) offset << 32 | (long) tokenStartOffset);
            tokenOffsets[tokenOffsetsIndex++] = tokenOffsetPair;

            //skip whitespace
            for(;offset < endOffset; offset++){
                if(source[offset] > 32 ) { break; } // All ASCII values from 32 and down are considered white space
            }
        }

        return tokenOffsetsIndex; // return number of token offsets written into tokenOffset (=number of tokens found)
    }

}
