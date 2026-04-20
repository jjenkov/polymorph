package com.plmph.pde;

public class PdeUtil {

    public static final long TWO_POW_8  = 256L;
    public static final long TWO_POW_16 = TWO_POW_8 * TWO_POW_8;
    public static final long TWO_POW_24 = TWO_POW_8 * TWO_POW_16;
    public static final long TWO_POW_32 = TWO_POW_8 * TWO_POW_24;
    public static final long TWO_POW_40 = TWO_POW_8 * TWO_POW_32;
    public static final long TWO_POW_48 = TWO_POW_8 * TWO_POW_40;
    public static final long TWO_POW_56 = TWO_POW_8 * TWO_POW_48;



    public static int byteCountForLength(long length){
        if(length == 0)         return 0;
        if(length < TWO_POW_8)  return 1;
        if(length < TWO_POW_16) return 2;
        if(length < TWO_POW_24) return 3;
        if(length < TWO_POW_32) return 4;
        if(length < TWO_POW_40) return 5;
        if(length < TWO_POW_48) return 6;
        if(length < TWO_POW_56) return 7;
        return 8;
    }

    public static int byteLengthOfInt64Value(long value){
        if(value < TWO_POW_8)  return 1;
        if(value < TWO_POW_16) return 2;
        if(value < TWO_POW_24) return 3;
        if(value < TWO_POW_32) return 4;
        if(value < TWO_POW_40) return 5;
        if(value < TWO_POW_48) return 6;
        if(value < TWO_POW_56) return 7;
        return 8;
    }

    public static int byteLengthOfInt32Value(int value){
        if(value < TWO_POW_8)  return 1;
        if(value < TWO_POW_16) return 2;
        if(value < TWO_POW_24) return 3;
        return 4;
    }


    public static long readLittleEndianByteSequenceToLong(byte[] source, int offset, int length) {
        long result = 0;
        for (int tempOffset = offset + length; tempOffset > offset;) {
            result = (result << 8) + (0xFF & source[--tempOffset]);
        }
        return result;
    }

    public static long readBigEndianByteSequenceToLong(byte[] source, int offset, int length){
        long result = 0;
        for (int endOffset = offset + length; offset < endOffset;) {
            result = (result << 8) + (0xFF & source[offset++]);
        }
        return result;
    }


    public static void writeLongToLittleEndianByteSequence(byte[] dest, int offset, int length, long value){
        for(int i=0, n=length*8; i < n; i+=8){
            dest[offset++] = (byte) (0xFF & (value >> i));
        }
    }

    public static void writeLongToBigEndianByteSequence(byte[] dest, int offset, int length, long value){
        for(int i=(length-1)*8; i >= 0; i-=8){
            dest[offset++] = (byte) (0xFF & (value >> i));
        }
    }

    public static void writeFloatToLittleEndianByteSequence(byte[] dest, int offset, float value) {
        int intValue = Float.floatToIntBits(value);

        for(int i=0, n=4*8; i < n; i+=8){
            dest[offset++] = (byte) (0xFF & (intValue >> i));
        }
    }

    public static void writeDoubleToLittleEndianByteSequence(byte[] dest, int offset, double value) {
        long longValue = Double.doubleToLongBits(value);

        for(int i=0, n=8*8; i < n; i+=8){
            dest[offset++] = (byte) (0xFF & (longValue >> i));
        }
    }

    public static float readLittleEndianByteSequenceToFloat(byte[] source, int offset) {
        int result = 0;
        for (int tempOffset = offset + 4; tempOffset > offset;) {
            result = (result << 8) + (0xFF & source[--tempOffset]);
        }
        return Float.intBitsToFloat(result);
    }

    public static double readLittleEndianByteSequenceToDouble(byte[] source, int offset) {
        long result = 0;
        for (int tempOffset = offset + 8; tempOffset > offset;) {
            result = (result << 8) + (0xFF & source[--tempOffset]);
        }
        return Double.longBitsToDouble(result);
    }


}
