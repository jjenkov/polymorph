package com.plmph.pde;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class PdeReader {

    public static final int FIELD_TYPE_BYTE_COUNT = 1;
    public static TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

    private byte[] lengthSpecs = null;

    private byte[] source = null;
    public  int    offset = 0;
    public  int    nextOffset = 0;
    public  int    scopeEndOffset = 0;

    private long[] intoIndexStack      = null;
    private int    intoIndexStackIndex = 0;

    public int fieldType = -1;
    public int fieldLengthLength = 0;  // number of length bytes for this field.
    public int fieldValueLength  = 0;  // number of value  bytes for this field.

    public PdeReader(){
        this.lengthSpecs = PdeFieldLengthSpecs.defaultPdeLengthEncodings();
        this.intoIndexStack = new long[64];
    }

    public PdeReader(byte[] source){
        this();
        setSource(source);
    }
    public PdeReader(byte[] source, int offset){
        this();
        setSource(source, offset);
    }
    public PdeReader setLengthSpecs(byte[] lengthSpecs) {
        if(lengthSpecs.length != 256){
            throw new IllegalArgumentException("Length encodings array must have exactly 256 bytes (length) but was: " + lengthSpecs.length);
        }
        this.lengthSpecs = lengthSpecs;
        return this;
    }
    public PdeReader setIntoIndexStack(long[] stack){
        this.intoIndexStack = stack;
        return this;
    }

    public PdeReader setSource(byte[] source){
        return setSource(source, 0, source.length);
    }

    public PdeReader setSource(byte[] source, int offset) {
        return setSource(source, offset, source.length);
    }
    public PdeReader setSource(byte[] source, int offset, int length){
        this.source         = source;
        this.offset         = offset;
        this.nextOffset     = offset;
        this.scopeEndOffset = offset + length ;
        return this;
    }
    public byte[] getSource() { return this.source; }

    public boolean hasNext() {
        return this.nextOffset < this.scopeEndOffset;
    }


    public PdeReader next() {
        this.offset = this.nextOffset;
        this.fieldType = 0xFF & this.source[offset];

        // Extract number of length bytes (fieldLengthLength) and fixed value length (fieldValueLength) from length spec
        // Note: One of these number will always be 0
        int fieldLengthSpec    = 0xFF & this.lengthSpecs[this.fieldType];
        this.fieldLengthLength = fieldLengthSpec >> 4;   // no of length bytes extracted from length spec
        this.fieldValueLength  = 0x0F & fieldLengthSpec; // fixed value length extracted from length spec

        // Reading field length bytes into field value length field (little endian encoded length bytes)
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, n = offset + FIELD_TYPE_BYTE_COUNT; tempOffset > n;) {
            this.fieldValueLength = (this.fieldValueLength << 8) + (0xFF & source[--tempOffset]);
        }

        //what should this.nextOffset point to here?
        this.nextOffset = this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength + this.fieldValueLength;

        return this;
    }

    public void moveInto() {
        //moveInto() only works for composite types like objects, tables and arrays

        long stackValue = this.offset;
        stackValue <<= 32;
        stackValue |= this.scopeEndOffset;

        this.intoIndexStack[this.intoIndexStackIndex++] = stackValue;

        this.scopeEndOffset = this.nextOffset;
        this.nextOffset = this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength;  //restart nextOffset counting from inside composite field.

        // should offset not be set to something? - first byte of value (body) - of composite field?
        // or will that happen when next() is called after a moveInto() call ?
        //this.offset += this.fieldLengthLength;Er
    }

    public void moveOutOf() {
        long stackValue = this.intoIndexStack[--this.intoIndexStackIndex];

        this.scopeEndOffset = (int) (0xFFFF_FFFF & stackValue);
        this.offset         = (int) (0xFFFF_FFFF & (stackValue >> 32));  // todo Is this operation necessary? Or should this.nextOffset just be set to this value directly, before calling next() ?

        this.nextOffset = this.offset;  //restart nextIndex counting from outer object.
        next();
    }

    public boolean readBoolean() {
        return this.fieldType == PdeFieldTypes.BOOLEAN_TRUE;
    }

    public Boolean readBooleanObj() {
        switch(this.fieldType){
            case PdeFieldTypes.BOOLEAN_TRUE: return Boolean.TRUE;
            case PdeFieldTypes.BOOLEAN_FALSE: return Boolean.FALSE;
            default: { return null; }
        }
    }

    public long readInt() {
        long value = 0;
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldValueLength; tempOffset > offset + FIELD_TYPE_BYTE_COUNT;) {
            value = (value << 8) + (0xFF & source[--tempOffset]);
        }
        if(this.fieldType >= PdeFieldTypes.INT_NEG_1_BYTES && this.fieldType <= PdeFieldTypes.INT_NEG_8_BYTES){
            return -value-1;
        }
        return value;
    }

    public Long readIntObj(){
        if(this.fieldType == PdeFieldTypes.INT_NULL){
            return null;
        }
        long value = 0;
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldValueLength; tempOffset > offset + FIELD_TYPE_BYTE_COUNT;) {
            value = (value << 8) + (0xFF & source[--tempOffset]);
        }
        if(this.fieldType >= PdeFieldTypes.INT_NEG_1_BYTES && this.fieldType <= PdeFieldTypes.INT_NEG_8_BYTES){
            value = -value-1;
        }
        return value;
    }

    public float readFloat32() {
        long value = 0;
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldValueLength; tempOffset > offset + FIELD_TYPE_BYTE_COUNT;) {
            value = (value << 8) + (0xFF & source[--tempOffset]);
        }
        return Float.intBitsToFloat((int) (0xFFFFFFFF & value));

        /*
        int value = 0;
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldValueLength; tempOffset > offset;) {
            value = (value << 8) + (0xFF & source[--tempOffset]);
        }
        return Float.intBitsToFloat(value);
        */
    }

    public Float readFloat32Obj() {
        if(this.fieldType == PdeFieldTypes.FLOAT_NULL){
            return null;
        }
        int value = 0;
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldValueLength; tempOffset > offset + FIELD_TYPE_BYTE_COUNT;) {
            value = (value << 8) + (0xFF & source[--tempOffset]);
        }
        return Float.intBitsToFloat(value);
    }

    public double readFloat64() {
        long value = 0;
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldValueLength; tempOffset > offset + FIELD_TYPE_BYTE_COUNT;) {
            value = (value << 8) + (0xFF & source[--tempOffset]);
        }
        return Double.longBitsToDouble(value);
    }

    public Double readFloat64Obj() {
        if(this.fieldType == PdeFieldTypes.FLOAT_NULL){
            return null;
        }
        long value = 0;
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldValueLength; tempOffset > offset + FIELD_TYPE_BYTE_COUNT;) {
            value = (value << 8) + (0xFF & source[--tempOffset]);
        }
        return Double.longBitsToDouble(value);
    }

    public int readBytes(byte[] dest, int offset){
        if(this.fieldType == PdeFieldTypes.BYTES_NULL || this.fieldType == PdeFieldTypes.BYTES_0_BYTES){
            return -1;
        }
        System.arraycopy(this.source, this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, dest, offset, this.fieldValueLength);
        return this.fieldValueLength;
    }

    public int readBytes(byte[] dest, int offset, int length){
        if(this.fieldType == PdeFieldTypes.BYTES_NULL || this.fieldType == PdeFieldTypes.BYTES_0_BYTES){
            return -1;
        }
        int lengthToRead = Math.min(length, this.fieldValueLength);

        System.arraycopy(this.source, this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, dest, offset, Math.min(this.fieldValueLength, lengthToRead));
        return lengthToRead;
    }

    public int readUtf8(byte[] dest, int offset){
        if(this.fieldType == PdeFieldTypes.UTF_8_NULL || this.fieldType == PdeFieldTypes.UTF_8_0_BYTES){
            return -1;
        }
        System.arraycopy(this.source, this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, dest, offset, this.fieldValueLength);
        return this.fieldValueLength;
    }

    public int readUtf8(byte[] dest, int offset, int length){
        if(this.fieldType == PdeFieldTypes.UTF_8_NULL || this.fieldType == PdeFieldTypes.UTF_8_0_BYTES){
            return -1;
        }
        int lengthToRead = Math.min(length, this.fieldValueLength);
        System.arraycopy(this.source, this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, dest, offset, Math.min(this.fieldValueLength, lengthToRead));
        return lengthToRead;
    }

    public Calendar readCalendar() {
        return readUtc(new GregorianCalendar());
    }

    public Calendar readUtc(Calendar calendar){
        if(this.fieldLengthLength == 0) return null;

        //todo can this be optimized ? many of these fields might be set again later, in the switch-block !
        calendar.setTimeZone(UTC_TIME_ZONE);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        int fieldPartValue = 0;
        int startOffset = this.offset + FIELD_TYPE_BYTE_COUNT;

        switch(this.fieldValueLength){
            case 8 : {
                for (int tempOffset = startOffset + 8; tempOffset > offset;) {
                    fieldPartValue = (fieldPartValue << 8) + (0xFF & source[--tempOffset]);
                }
                calendar.setTimeInMillis(fieldPartValue);
                return calendar;
            }
            case 9 : {
                fieldPartValue = (0xFF) & this.source[startOffset + 8];
                fieldPartValue <<= 8;
                fieldPartValue += (0xFF) & this.source[startOffset + 7];
                calendar.set(Calendar.MILLISECOND, fieldPartValue);
            }
            case 7 : {
                calendar.set(Calendar.SECOND, (0xFF) & this.source[startOffset + 6]);
            }
            case 6 : {
                calendar.set(Calendar.MINUTE, (0xFF) & this.source[startOffset + 5]);
            }
            case 5 : {
                calendar.set(Calendar.HOUR_OF_DAY, (0xFF) & this.source[startOffset + 4]);
            }
            case 4 : {
                calendar.set(Calendar.DAY_OF_MONTH, (0xFF) & this.source[startOffset + 3]);
            }
            case 3 : {
                calendar.set(Calendar.MONTH, (0xFF) & this.source[startOffset + 2] -1);
            }
            case 2 : {
                fieldPartValue = (0xFF) & this.source[startOffset + 1];
                fieldPartValue <<= 8;
                fieldPartValue += (0xFF) & this.source[startOffset];
                calendar.set(Calendar.YEAR, fieldPartValue);
            }
        }
        return calendar;

    }

    public int readKey(byte[] dest) {
        System.arraycopy(this.source, this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, dest, 0, this.fieldValueLength);
        return this.fieldValueLength;
    }










}
