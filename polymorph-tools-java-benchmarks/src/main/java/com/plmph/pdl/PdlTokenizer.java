package com.plmph.pdl;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorSpecies;

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



    public static int tokenize_while_and(byte[] source, int offset, int length, long[] tokenOffsets){
        int tokenOffsetsIndex = 0;
        int endOffset = offset + length;

        //skip whitespace
        while(offset < endOffset && source[offset] <= 32) {
            offset++;
        }

        int tokenStartOffset = 0;
        int tokenEndCharacter = 0;

        while(offset < endOffset){
            tokenStartOffset  = offset;
            tokenEndCharacter = tokenEndCharacters[source[tokenStartOffset]];

            while(offset < endOffset && source[offset] != tokenEndCharacter){
                offset++;
            }

            offset++; // We want the index right after the last character in the token.
            long tokenOffsetPair = (long) ((long) offset << 32 | (long) tokenStartOffset);
            tokenOffsets[tokenOffsetsIndex++] = tokenOffsetPair;

            //skip whitespace
            while(offset < endOffset && source[offset] <= 32){
                offset++;
            }
        }

        return tokenOffsetsIndex; // return number of token offsets written into tokenOffset (=number of tokens found)
    }

    public static int tokenize_while(byte[] source, int offset, int length, long[] tokenOffsets){
        int tokenOffsetsIndex = 0;
        int endOffset = offset + length;

        //skip whitespace
        while(offset < endOffset){
            if(source[offset] > 32 ) { break; } // All ASCII values from 32 and down are considered white space
            offset++;
        }

        int tokenStartOffset = 0;
        int tokenEndCharacter = 0;

        while(offset < endOffset){
            tokenStartOffset  = offset;
            tokenEndCharacter = tokenEndCharacters[source[tokenStartOffset]];

            while(offset < endOffset){
                if(source[offset] == tokenEndCharacter) { break; }
                offset++;
            }

            offset++; // We want the index right after the last character in the token.
            long tokenOffsetPair = (long) ((long) offset << 32 | (long) tokenStartOffset);
            tokenOffsets[tokenOffsetsIndex++] = tokenOffsetPair;

            //skip whitespace
            while(offset < endOffset){
                if(source[offset] > 32 ) { break; } // All ASCII values from 32 and down are considered white space
                offset++;
            }
        }

        return tokenOffsetsIndex; // return number of token offsets written into tokenOffset (=number of tokens found)
    }

    public static int tokenize_doWhile(byte[] source, int offset, int length, long[] tokenOffsets){
        int tokenOffsetsIndex = 0;
        int endOffset = offset + length;

        //skip whitespace
        while(offset < endOffset){
            if(source[offset] > 32 ) { break; } // All ASCII values from 32 and down are considered white space
            offset++;
        }

        while(offset < endOffset){
            int tokenStartOffset  = offset;
            int tokenEndCharacter = tokenEndCharacters[source[tokenStartOffset]];

            do{
                if(source[offset] == tokenEndCharacter) { break; }
                offset++;
            } while(offset < endOffset);

            offset++; // We want the index right after the last character in the token.
            long tokenOffsetPair = (long) ((long) offset << 32 | (long) tokenStartOffset);
            tokenOffsets[tokenOffsetsIndex++] = tokenOffsetPair;

            //skip whitespace
            while(offset < endOffset){
                if(source[offset] > 32 ) { break; } // All ASCII values from 32 and down are considered white space
                offset++;
            }
        }

        return tokenOffsetsIndex; // return number of token offsets written into tokenOffset (=number of tokens found)
    }

    public static int tokenize_simd(byte[] source, int offset, int length, long[] tokenOffsets){
        int tokenOffsetsIndex = 0;
        int endOffset = offset + length;

        //skip whitespace
        //todo skip whitespace via SIMD
        while(offset < endOffset){
            if(source[offset] > 32 ) { break; } // All ASCII values from 32 and down are considered white space
            offset++;
        }

        // Start with the preferred vector shape
        VectorSpecies<Byte> species = ByteVector.SPECIES_PREFERRED;
        //VectorSpecies<Byte> species = ByteVector.SPECIES_128;
        //System.out.println("vector byte size: " + species.vectorByteSize());
        ByteVector vector = null;
        VectorMask<Byte> mask = null;
        int lastPossibleBigVectorOffset = endOffset - species.vectorByteSize();

        int tokenStartOffset  = offset;
        int tokenEndCharacter = tokenEndCharacters[source[tokenStartOffset]];

        while(offset < lastPossibleBigVectorOffset){
            while(offset < lastPossibleBigVectorOffset) {
                vector = ByteVector.fromArray(species, source, offset);
                mask = vector.eq((byte) tokenEndCharacter);
                int matchingIndex = mask.firstTrue();
                if (matchingIndex < species.vectorByteSize()) {  // End of token found!
                    offset += matchingIndex + 1;
                    long tokenOffsetPair = (long) ((long) offset << 32 | (long) tokenStartOffset);
                    tokenOffsets[tokenOffsetsIndex++] = tokenOffsetPair;

                    //skip whitespace between this and next token
                    //todo skip whitespace via SIMD
                    while(offset < endOffset){
                        if(source[offset] > 32 ) { break; } // All ASCII values from 32 and down are considered white space
                        offset++;
                    }
                    tokenStartOffset  = offset;
                    tokenEndCharacter = tokenEndCharacters[source[tokenStartOffset]];
                    break;
                }
                offset += matchingIndex;
            }

        }

        // Repeat block above using an 64 bit (8 byte) vector shape - on the last few bytes.
        species = ByteVector.SPECIES_64;
        //System.out.println("vector byte size: " + species.vectorByteSize());

        int lastPossibleSmallVectorOffset = endOffset - species.vectorByteSize();

        while(offset < lastPossibleSmallVectorOffset){
            while(offset < lastPossibleSmallVectorOffset) {
                vector = ByteVector.fromArray(species, source, offset);
                mask = vector.eq((byte) tokenEndCharacter);
                int matchingIndex = mask.firstTrue();
                if (matchingIndex < species.vectorByteSize()) { // End of token found!
                    offset += matchingIndex + 1;
                    long tokenOffsetPair = (long) ((long) offset << 32 | (long) tokenStartOffset);
                    tokenOffsets[tokenOffsetsIndex++] = tokenOffsetPair;

                    //skip whitespace
                    //todo skip whitespace via SIMD
                    while(offset < endOffset){
                        if(source[offset] > 32 ) { break; } // All ASCII values from 32 and down are considered white space
                        offset++;
                    }
                    tokenStartOffset  = offset;
                    tokenEndCharacter = tokenEndCharacters[source[tokenStartOffset]];
                    break;
                }
                offset += matchingIndex;
            }
        }

        // parse the rest of the bytes using scalar instructions.
        while(offset < endOffset){
            while(offset < endOffset){
                if(source[offset] == tokenEndCharacter) { break; }
                offset++;
            }

            offset++; // We want the index right after the last character in the token.
            long tokenOffsetPair = (long) ((long) offset << 32 | (long) tokenStartOffset);
            tokenOffsets[tokenOffsetsIndex++] = tokenOffsetPair;

            //skip whitespace
            while(offset < endOffset){
                if(source[offset] > 32 ) {
                    tokenStartOffset  = offset;
                    tokenEndCharacter = tokenEndCharacters[source[tokenStartOffset]];
                    break;
                } // All ASCII values from 32 and down are considered white space
                offset++;
            }
        }

        return tokenOffsetsIndex;
    }




}
