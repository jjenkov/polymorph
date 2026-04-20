package com.plmph.hex;

public class HexUtil {

    public static final char[] hexChars = new char[16];
    static {
        hexChars[0] = '0';
        hexChars[1] = '1';
        hexChars[2] = '2';
        hexChars[3] = '3';
        hexChars[4] = '4';
        hexChars[5] = '5';
        hexChars[6] = '6';
        hexChars[7] = '7';
        hexChars[8] = '8';
        hexChars[9] = '9';
        hexChars[10] = 'A';
        hexChars[11] = 'B';
        hexChars[12] = 'C';
        hexChars[13] = 'D';
        hexChars[14] = 'E';
        hexChars[15] = 'F';
    }

    public static void bytesToHex(byte[] source, int offset, int length, byte[] dest, int destOffset){
        for(int i=offset, n=offset + length; i < n; i++) {
            int byteVal = 0xFF & source[i];

            int leftDigit  = byteVal >> 4;
            int rightDigit = byteVal & 0x0F;

            char leftChar  = hexChars[leftDigit];
            char rightChar = hexChars[rightDigit];

            dest[destOffset++] = (byte) (0xFF & leftChar);
            dest[destOffset++] = (byte) (0xFF & rightChar);
        }
    }

    public static StringBuffer bytesToHex(byte[] source, int offset, int length, StringBuffer dest) {
        for(int i=offset, n=offset + length; i < n; i++) {
            int byteVal = 0xFF & source[i];

            int leftDigit  = byteVal >> 4;
            int rightDigit = byteVal & 0x0F;

            char leftChar  = hexChars[leftDigit];
            char rightChar = hexChars[rightDigit];

            dest.append(leftChar);
            dest.append(rightChar);
        }
        return dest;
    }

    public static int hexToBytes(byte[] hexSource, int sourceOffset, int sourceLength, byte[] dest, int destOffset) {
        int startDestOffset = destOffset;
        int sourceEndOffset = sourceOffset + sourceLength;

        //skip whitespace
        while(sourceOffset < sourceEndOffset){
            if(hexSource[sourceOffset] > 32 ) { break; } // All ASCII values from 32 and down are considered white space
            sourceOffset++;
        }

        while(sourceOffset < sourceEndOffset){
            int leftDigit  = hexSource[sourceOffset++];
            int rightDigit = hexSource[sourceOffset++];

            if(leftDigit >= 48 && leftDigit <= 57) {
                leftDigit -= 48;
            } else if(leftDigit >= 65 && leftDigit <= 70) {
                leftDigit -= 55; // 55 = 65 - 10 - because A is 10, not 0
            } else if(leftDigit >= 97 && leftDigit <= 102) {
                leftDigit -= 87; // 87 = 97 - 10 - because a is 10, not 0
            } else {
                // todo not a hex digit char... do what? ignore? throw exception?
                leftDigit = 0;
            }

            if(rightDigit >= 48 && rightDigit <= 57) {
                rightDigit -= 48;
            } else if(rightDigit >= 65 && rightDigit <= 70) {
                rightDigit -= 55; // 55 = 65 - 10 - because A is 10, not 0
            } else if(rightDigit >= 97 && rightDigit <= 102) {
                rightDigit -= 87; // 87 = 97 - 10 - because a is 10, not 0
            } else {
                // todo not a hex digit char... do what? ignore? throw exception?
                rightDigit = 0;
            }

            int value = leftDigit << 4 | (0xF & rightDigit); // todo check if   0xF &   is really necessary here.

            dest[destOffset++] = (byte) (0xFF & value);

            //skip whitespace
            while(sourceOffset < sourceEndOffset){
                if(hexSource[sourceOffset] > 32 ) { break; } // All ASCII values from 32 and down are considered white space
                sourceOffset++;
            }
        }

          return destOffset - startDestOffset;

    }




}
