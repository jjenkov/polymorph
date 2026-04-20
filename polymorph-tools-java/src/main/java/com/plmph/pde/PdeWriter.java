package com.plmph.pde;

import java.nio.charset.StandardCharsets;

public class PdeWriter {

    public static final int FIELD_TYPE_BYTE_COUNT = 1;


    public  byte[] dest   = null;
    public  int    offset = 0;

    protected int[] compositeFieldStack = null; //used to store start indexes of complex fields that can contain nested fields.
    protected int   compositeFieldStackIndex = -1; //start at -1 - will be incremented before first use.

    public PdeWriter() {}
    public PdeWriter(byte[] dest){
        this.dest   = dest;
        this.offset = 0;
    }
    public PdeWriter(byte[] dest, int offset){
        this.dest   = dest;
        this.offset = offset;
    }

    public PdeWriter setDest(byte[] dest){
        this.dest   = dest;
        this.offset = 0;
        return this;
    }

    public PdeWriter setDest(byte[] dest, int offset){
        this.dest = dest;
        this.offset = offset;
        return this;
    }

    public PdeWriter setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public PdeWriter setCompositeFieldStack(int[] stack) {
        this.compositeFieldStack = stack;
        return this;
    }

    public void writeBoolean(boolean value) {
        if(value) {
            dest[offset++] = PdeFieldTypes.BOOLEAN_TRUE;
        } else {
            dest[offset++] = PdeFieldTypes.BOOLEAN_FALSE;
        }
    }

    public void writeBooleanObj(Boolean value) {
        if(value == null) {
            dest[offset++] = PdeFieldTypes.BOOLEAN_NULL;
        } else {
            boolean booleanValue = value.booleanValue();
            if(booleanValue) {
                dest[offset++] = PdeFieldTypes.BOOLEAN_TRUE;
            } else {
                dest[offset++] = PdeFieldTypes.BOOLEAN_FALSE;
            }
        }
    }

    public void writeIntObj(Integer value){
        if(value == null) {
            dest[offset++] = PdeFieldTypes.INT_NULL;
        } else {
            int integerValue = value.intValue();
            int baseFieldTypeCode = PdeFieldTypes.INT_NULL;
            if(integerValue < 0){
                baseFieldTypeCode = PdeFieldTypes.INT_POS_8_BYTES;
                integerValue = (-integerValue) -1; // -1 becomes 0. -2 becomes 1 etc.
            }

            int length = PdeUtil.byteLengthOfInt32Value(integerValue);
            dest[offset++] = (byte) (0xFF & (baseFieldTypeCode + length));

            for(int i=0, n=length*8; i < n; i+=8){
                dest[offset++] = (byte) (0xFF & (value >> i));
            }
        }
    }

    public void writeIntObj(Long value){
        if(value == null) {
            dest[offset++] = PdeFieldTypes.INT_NULL;
        } else {
            int longValue = value.intValue();
            int baseFieldTypeCode = PdeFieldTypes.INT_NULL;
            if(longValue < 0){
                baseFieldTypeCode = PdeFieldTypes.INT_POS_8_BYTES;
                longValue = (-longValue) -1; // -1 becomes 0. -2 becomes 1 etc.
            }
            int length = PdeUtil.byteLengthOfInt64Value(longValue);
            dest[offset++] = (byte) (0xFF & (baseFieldTypeCode + length));

            for(int i=0, n=length*8; i < n; i+=8){
                dest[offset++] = (byte) (0xFF & (value >> i));
            }
        }
    }


    public void writeInt(int value) {
        int baseFieldTypeCode = PdeFieldTypes.INT_NULL;
        if(value < 0){
            baseFieldTypeCode = PdeFieldTypes.INT_POS_8_BYTES;
            value = (-value) -1; // -1 becomes 0. -2 becomes 1 etc.
        }

        int length = PdeUtil.byteLengthOfInt32Value(value);
        dest[offset++] = (byte) (0xFF & (baseFieldTypeCode + length));

        for(int i=0, n=length*8; i < n; i+=8){
            dest[offset++] = (byte) (0xFF & (value >> i));
        }
    }

    public void writeInt(long value) {
        int baseFieldTypeCode = PdeFieldTypes.INT_NULL;
        if(value < 0){
            baseFieldTypeCode = PdeFieldTypes.INT_POS_8_BYTES;
            value = (-value) -1; // -1 becomes 0. -2 becomes 1 etc.
        }

        int length = PdeUtil.byteLengthOfInt64Value(value);
        dest[offset++] = (byte) (0xFF & (baseFieldTypeCode + length));

        for(int i=0, n=length*8; i < n; i+=8){
            dest[offset++] = (byte) (0xFF & (value >> i));
        }
    }

    public void writeFloat32(float value) {
        int intBits = Float.floatToIntBits(value);
        dest[offset++] = (byte) (0xFF & (PdeFieldTypes.FLOAT_4_BYTES));

        for(int i=0, n=4*8; i < n; i+=8){
            dest[offset++] = (byte) (0xFF & (intBits >> i));

        }
    }

    public void writeFloat32Obj(Float value) {
        if(value == null) {
            dest[offset++] = PdeFieldTypes.FLOAT_NULL;
        } else {
            int intBits = Float.floatToIntBits(value.floatValue());
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.FLOAT_4_BYTES));

            for(int i=0, n=4*8; i < n; i+=8){
                dest[offset++] = (byte) (0xFF & (intBits >> i));
            }
        }

    }

    public void writeFloat64(double value) {
        long longBits = Double.doubleToLongBits(value);
        dest[offset++] = (byte) (0xFF & (PdeFieldTypes.FLOAT_8_BYTES));

        for(int i=0, n=8*8; i < n; i+=8){
            dest[offset++] = (byte) (0xFF & (longBits >> i));
        }
    }

    public void writeFloat64Obj(Double value) {
        if(value == null) {
            dest[offset++] = PdeFieldTypes.FLOAT_NULL;
        } else {
            long longBits = Double.doubleToLongBits(value.doubleValue());
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.FLOAT_8_BYTES));

            for(int i=0, n=8*8; i < n; i+=8){
                dest[offset++] = (byte) (0xFF & (longBits >> i));
            }
        }
    }

    public void writeBytes(byte[] bytes) {
        if(bytes == null) {
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.BYTES_NULL));
            return;
        }
        writeBytes(bytes, 0, bytes.length);
    }

    public void writeBytes(byte[] bytes, int bytesOffset, int bytesLength){

        if(bytesLength < 16){
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.BYTES_0_BYTES + bytesLength));
        } else {
            int lengthLength = PdeUtil.byteLengthOfInt64Value(bytesLength);
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.BYTES_15_BYTES + lengthLength));
            for(int i=0, n=lengthLength*8; i < n; i+=8){
                dest[offset++] = (byte) (0xFF & (bytesLength >> i));
            }
        }

        System.arraycopy(bytes, bytesOffset, dest, offset, bytesLength);

        this.offset += bytesLength;
    }


    protected void writeAtomicBeginPush(int baseAtomicFieldTypeCode, int lengthLength){
        this.compositeFieldStack[++this.compositeFieldStackIndex] = this.offset;
        this.dest[this.offset++] = (byte) (0xFF & ((baseAtomicFieldTypeCode + lengthLength)));
        this.offset += lengthLength;
    }

    public void writeAtomicEndPop(int baseAtomicFieldTypeCode){
        int bytesStartIndex = this.compositeFieldStack[this.compositeFieldStackIndex--];
        int lengthLength = ((int) (0xFF & this.dest[bytesStartIndex])) - ((int) (0xFF & baseAtomicFieldTypeCode));
        int length = this.offset - bytesStartIndex - FIELD_TYPE_BYTE_COUNT - lengthLength;

        bytesStartIndex++; //jump over lead byte of object field.

        //Encode length bytes using little endian instead of big endian.
        for(int i=0, n=lengthLength*8; i < n; i+=8){
            dest[bytesStartIndex++] = (byte) (0xFF & (length >> i));
        }
    }

    /*
    public void writeBytesBeginPush(int lengthLength){
        this.compositeFieldStack[++this.compositeFieldStackIndex] = this.offset;
        this.dest[this.offset++] = (byte) (0xFF & ((PdeFieldTypes.BYTES_15_BYTES + lengthLength)));
        this.offset += lengthLength;
    }

    public void writeBytesEndPop(){
        int bytesStartIndex = this.compositeFieldStack[this.compositeFieldStackIndex--];
        int lengthLength = ((int) (0xFF & this.dest[bytesStartIndex])) - ((int) (0xFF & PdeFieldTypes.BYTES_15_BYTES));
        int length = this.offset - bytesStartIndex - FIELD_TYPE_BYTE_COUNT - lengthLength;

        bytesStartIndex++; //jump over lead byte of object field.

        //Encode length bytes using little endian instead of big endian.
        for(int i=0, n=lengthLength*8; i < n; i+=8){
            dest[bytesStartIndex++] = (byte) (0xFF & (length >> i));
        }
    }

    */
    public void writeBytesBeginPush(int lengthLength){
        writeAtomicBeginPush(PdeFieldTypes.BYTES_15_BYTES, lengthLength);
    }
    public void writeBytesEndPop(){
        writeAtomicEndPop(PdeFieldTypes.BYTES_15_BYTES);
    }



    public void writeAsciiString(String asciiStr){
        if(asciiStr == null) {
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.ASCII_NULL));
            return;
        }
        writeAscii(asciiStr.getBytes(StandardCharsets.US_ASCII));
    }

    public void writeAscii(byte[] bytes) {
        if(bytes == null) {
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.ASCII_NULL));
            return;
        }
        writeAscii(bytes, 0, bytes.length);

    }

    public void writeAscii(byte[] bytes, int offset, int length) {
        if(length < 16){
            dest[this.offset++] = (byte) (0xFF & (PdeFieldTypes.ASCII_0_BYTES + length));
        } else {
            int lengthLength = PdeUtil.byteLengthOfInt64Value(length);
            dest[this.offset++] = (byte) (0xFF & (PdeFieldTypes.ASCII_15_BYTES + lengthLength));
            for(int i=0, n=lengthLength*8; i < n; i+=8){
                dest[this.offset++] = (byte) (0xFF & (length >> i));
            }
        }

        System.arraycopy(bytes, offset, dest, this.offset, length);

        this.offset += length;
    }

    public void writeAsciiBeginPush(int lengthLength){
        writeAtomicBeginPush(PdeFieldTypes.ASCII_15_BYTES, lengthLength);
    }
    public void writeAsciiEndPop(){
        writeAtomicEndPop(PdeFieldTypes.ASCII_15_BYTES);
    }


    public void writeUtf8String(String utf8Str){
        if(utf8Str == null) {
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.UTF_8_NULL));
            return;
        }
        writeUtf8(utf8Str.getBytes(StandardCharsets.UTF_8));
    }

    public void writeUtf8(byte[] utf8Bytes) {
        if(utf8Bytes == null) {
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.UTF_8_NULL));
            return;
        }
        writeUtf8(utf8Bytes, 0, utf8Bytes.length);
    }

    public void writeUtf8(byte[] bytes, int offset, int length) {
        if(length < 16){
            dest[this.offset++] = (byte) (0xFF & (PdeFieldTypes.UTF_8_0_BYTES + length));
        } else {
            int lengthLength = PdeUtil.byteLengthOfInt64Value(length);
            dest[this.offset++] = (byte) (0xFF & (PdeFieldTypes.UTF_8_15_BYTES + lengthLength));
            for(int i=0, n=lengthLength*8; i < n; i+=8){
                dest[this.offset++] = (byte) (0xFF & (length >> i));
            }
        }

        System.arraycopy(bytes, offset, dest, this.offset, length);

        this.offset += length;
    }

    public void writeUtf8BeginPush(int lengthLength){
        writeAtomicBeginPush(PdeFieldTypes.UTF_8_15_BYTES, lengthLength);
    }

    public void writeUtf8EndPop(){
        writeAtomicEndPop(PdeFieldTypes.UTF_8_15_BYTES);
    }

    public void writeUtc(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        int length = 9;

        this.dest[this.offset++] = (byte) (0xFF & PdeFieldTypes.UTC_9_BYTES);
        this.dest[this.offset++] = (byte) (0xFF & year);           // lower byte of year
        this.dest[this.offset++] = (byte) (0xFF & ( year >> 8) );  // higher byte of year
        this.dest[this.offset++] = (byte) (0xFF & month);
        this.dest[this.offset++] = (byte) (0xFF & day);
        this.dest[this.offset++] = (byte) (0xFF & hour);
        this.dest[this.offset++] = (byte) (0xFF & minute);
        this.dest[this.offset++] = (byte) (0xFF & second);
        this.dest[this.offset++] = (byte) (0xFF & millisecond);  // lower byte of millisecond
        this.dest[this.offset++] = (byte) (0xFF & (millisecond >> 8) );    // higher byte of millisecond
    }

    public void writeUtcMillis(long millis) {
        int baseFieldTypeCode = PdeFieldTypes.UTC_8_BYTES;

        //int length = PdeUtil.byteLengthOfInt64Value(value);
        int length = 8;
        dest[offset++] = (byte) (0xFF & (baseFieldTypeCode));

        for(int i=0, n=length*8; i < n; i+=8){
            dest[offset++] = (byte) (0xFF & (millis >> i));
        }
    }

    public void writeCopy(int copyOffset){
        int length = PdeUtil.byteLengthOfInt32Value(copyOffset);
        dest[offset++] = (byte) (0xFF & ((PdeFieldTypes.COPY_1_BYTES - 1) + length));

        for(int i=0, n=length*8; i < n; i+=8){
            dest[offset++] = (byte) (0xFF & (copyOffset >> i));
        }
    }

    public void writeCopy(long copyOffset){
        int length = PdeUtil.byteLengthOfInt64Value(copyOffset);
        dest[offset++] = (byte) (0xFF & ((PdeFieldTypes.COPY_1_BYTES - 1) + length));

        for(int i=0, n=length*8; i < n; i+=8){
            dest[offset++] = (byte) (0xFF & (copyOffset >> i));
        }
    }


    public void writeReference(int referenceOffset){
        int length = PdeUtil.byteLengthOfInt32Value(referenceOffset);
        dest[offset++] = (byte) (0xFF & ((PdeFieldTypes.REFERENCE_1_BYTES - 1) + length));

        for(int i=0, n=length*8; i < n; i+=8){
            dest[offset++] = (byte) (0xFF & (referenceOffset >> i));
        }
    }

    public void writeReference(long referenceOffset){
        int length = PdeUtil.byteLengthOfInt64Value(referenceOffset);
        dest[offset++] = (byte) (0xFF & ((PdeFieldTypes.REFERENCE_1_BYTES - 1) + length));

        for(int i=0, n=length*8; i < n; i+=8){
            dest[offset++] = (byte) (0xFF & (referenceOffset >> i));
        }
    }


    public void writeKeyAscii(String keyStr){
        writeKey(keyStr.getBytes(StandardCharsets.US_ASCII));
    }

    public void writeKeyUtf8(String keyStr){
        writeKey(keyStr.getBytes(StandardCharsets.UTF_8));
    }

    public void writeKey(byte[] bytes){
        if(bytes == null) {
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.KEY_NULL));
            return;
        }
        writeKey(bytes, 0, bytes.length);
    }

    public void writeKey(byte[] bytes, int keyOffset, int length) {
        if(length < 16){
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.KEY_0_BYTES + length));
        } else {
            int lengthLength = PdeUtil.byteLengthOfInt64Value(length);
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.KEY_15_BYTES + lengthLength));
            for(int i=0, n=lengthLength*8; i < n; i+=8){
                dest[offset++] = (byte) (0xFF & (length >> i));
            }
        }

        System.arraycopy(bytes, keyOffset, dest, offset, length);

        this.offset += length;
    }

    public void writeObjectNull(){
        this.dest[this.offset++] = (byte) (0xFF & (PdeFieldTypes.OBJECT_NULL));
    }

    public void writeObjectBeginPush(int lengthLength){
        this.compositeFieldStack[++this.compositeFieldStackIndex] = this.offset;
        this.dest[this.offset++] = (byte) (0xFF & ((PdeFieldTypes.OBJECT_NULL + lengthLength)));
        this.offset += lengthLength;
    }

    public void writeObjectEndPop(){
        int objectStartIndex = this.compositeFieldStack[this.compositeFieldStackIndex--];
        int lengthLength = ((int) (0xFF & this.dest[objectStartIndex])) - ((int) (0xFF & PdeFieldTypes.OBJECT_NULL));
        int length = this.offset - objectStartIndex - FIELD_TYPE_BYTE_COUNT - lengthLength;

        objectStartIndex++; //jump over lead byte of object field.

        //Encode length bytes using little endian instead of big endian.
        for(int i=0, n=lengthLength*8; i < n; i+=8){
            dest[objectStartIndex++] = (byte) (0xFF & (length >> i));
        }
    }

    public void writeTableNull(){
        this.dest[this.offset++] = (byte) (0xFF & (PdeFieldTypes.TABLE_NULL));
    }

    public void writeTableBeginPush(int lengthLength){
        this.compositeFieldStack[++this.compositeFieldStackIndex] = this.offset;
        this.dest[this.offset++] = (byte) (0xFF & (PdeFieldTypes.TABLE_NULL + lengthLength));
        this.offset +=     lengthLength;  //reserve Table field length bytes - to be filled when Table writing ends.
        this.offset += 1 + lengthLength;  //reserve space for Table element count field (encoded as an Int64Pos => 1 type byte + N (max length length) value bytes)
    }


    public void writeTableEndPop(int rowCount){
        int tableStartIndex = this.compositeFieldStack[this.compositeFieldStackIndex--];
        int lengthLength = ((int) (0xFF & this.dest[tableStartIndex])) - ((int) (0xFF & PdeFieldTypes.TABLE_NULL));
        int length = this.offset - tableStartIndex - 1 - lengthLength;

        //jump over lead byte of Table field.
        tableStartIndex++;

        //write Table length bytes
        //Encode length bytes using little endian instead of big endian.
        for(int i=0, n=lengthLength*8; i < n; i+=8){
            dest[tableStartIndex++] = (byte) (0xFF & (length >> i));
        }

        // Write Table row count field (Int64Pos field)
        // Remember, lengthLength bytes were reserved for the element count field too, because rowCount was unknown at that time.
        // Therefore, the rowCount is encoded using lengthLength number of bytes instead of the minimum possible bytes for the rowCount.
        // Using Little Endian encoding
        this.dest[tableStartIndex++] = (byte) (0xFF & (PdeFieldTypes.INT_NULL + lengthLength));
        for(int i=0, n=lengthLength*8; i < n; i+=8){
            dest[tableStartIndex++] = (byte) (0xFF & (rowCount >> i));
        }

    }


    public void writeMetadataBeginPush(int lengthLength){
        this.compositeFieldStack[++this.compositeFieldStackIndex] = this.offset;
        this.dest[this.offset++] = (byte) (0xFF & ((PdeFieldTypes.METADATA_NULL + lengthLength)));
        this.offset += lengthLength;
    }

    public void writeMetadataEndPop(){
        int objectStartIndex = this.compositeFieldStack[this.compositeFieldStackIndex--];
        int lengthLength = ((int) (0xFF & this.dest[objectStartIndex])) - ((int) (0xFF & PdeFieldTypes.METADATA_NULL));
        int length = this.offset - objectStartIndex - FIELD_TYPE_BYTE_COUNT - lengthLength;

        objectStartIndex++; //jump over lead byte of object field.

        //Encode length bytes using little endian instead of big endian.
        for(int i=0, n=lengthLength*8; i < n; i+=8){
            dest[objectStartIndex++] = (byte) (0xFF & (length >> i));
        }
    }




}
