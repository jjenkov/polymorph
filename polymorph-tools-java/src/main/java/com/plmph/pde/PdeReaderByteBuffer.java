package com.plmph.pde;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class PdeReaderByteBuffer {

    public static final int FIELD_TYPE_BYTE_COUNT = 1;
    public static TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

    private byte[] lengthSpecs = null;

    private ByteBuffer source = null;
    public  int    offset = 0;
    public  int    nextOffset = 0;
    public  int    scopeEndOffset = 0;

    private long[] intoIndexStack      = null;
    private int    intoIndexStackIndex = 0;

    public int fieldType = -1;
    public int fieldLengthLength = 0;  // number of length bytes for this field.
    public int fieldValueLength  = 0;  // number of value  bytes for this field.

    public PdeReaderByteBuffer() {
        this.lengthSpecs = PdeFieldLengthSpecs.defaultPdeLengthEncodings();
        this.intoIndexStack = new long[64];
    }

    public PdeReaderByteBuffer(ByteBuffer source) {
        this();
        setSource(source);
    }

    /**
     * Readable window is {@code source.position()} .. {@code source.limit()}.
     * Parsing uses absolute indices and does not advance the buffer position.
     */
    public PdeReaderByteBuffer setSource(ByteBuffer source) {
        this.source         = source;
        this.offset         = source.position();
        this.nextOffset     = this.offset;
        this.scopeEndOffset = source.limit();
        return this;
    }

    public ByteBuffer getSource() {
        return this.source;
    }

    public boolean hasNext() {
        return this.nextOffset < this.scopeEndOffset;
    }

    public PdeReaderByteBuffer next() {
        this.offset = this.nextOffset;
        this.fieldType = 0xFF & this.source.get(offset);

        // Extract number of length bytes (fieldLengthLength) and fixed value length (fieldValueLength) from length spec
        // Note: One of these numbers will always be 0
        int fieldLengthSpec    = 0xFF & this.lengthSpecs[this.fieldType];
        this.fieldLengthLength = fieldLengthSpec >> 4;   // no of length bytes extracted from length spec
        this.fieldValueLength  = 0x0F & fieldLengthSpec; // fixed value length extracted from length spec

        // Reading field length bytes into field value length field (little endian encoded length bytes)
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, n = offset + FIELD_TYPE_BYTE_COUNT; tempOffset > n;) {
            this.fieldValueLength = (this.fieldValueLength << 8) + (0xFF & source.get(--tempOffset));
        }

        this.nextOffset = this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength + this.fieldValueLength;

        return this;
    }

    public void moveInto() {
        // moveInto() only works for composite types like objects, tables and arrays

        long stackValue = this.offset;
        stackValue <<= 32;
        stackValue |= this.scopeEndOffset;

        this.intoIndexStack[this.intoIndexStackIndex++] = stackValue;

        this.scopeEndOffset = this.nextOffset;
        this.nextOffset = this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength;  //restart nextOffset inside composite field body.
    }

    public void moveOutOf() {
        long stackValue = this.intoIndexStack[--this.intoIndexStackIndex];

        this.scopeEndOffset = (int) (0xFFFF_FFFF & stackValue);
        this.offset         = (int) (0xFFFF_FFFF & (stackValue >> 32));
        // todo Is the above operation necessary? ( this.offset = ... )
        //  Or should this.nextOffset just be set to this value directly, before calling next() ?

        this.nextOffset = this.offset;  // restart nextOffset counting from outer object.
        next();
    }

    public boolean readBoolean() {
        return this.fieldType == PdeFieldTypes.BOOLEAN_TRUE;
    }

    public Boolean readBooleanObj() {
        switch (this.fieldType) {
            case PdeFieldTypes.BOOLEAN_TRUE:  return Boolean.TRUE;
            case PdeFieldTypes.BOOLEAN_FALSE: return Boolean.FALSE;
            default: { return null; }
        }
    }

    public long readInt() {
        long value = 0;
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldValueLength; tempOffset > offset + FIELD_TYPE_BYTE_COUNT;) {
            value = (value << 8) + (0xFF & source.get(--tempOffset));
        }
        if (this.fieldType >= PdeFieldTypes.INT_NEG_1_BYTES && this.fieldType <= PdeFieldTypes.INT_NEG_8_BYTES) {
            return -value - 1;
        }
        return value;
    }

    public Long readIntObj() {
        if (this.fieldType == PdeFieldTypes.INT_NULL) {
            return null;
        }
        long value = 0;
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldValueLength; tempOffset > offset + FIELD_TYPE_BYTE_COUNT;) {
            value = (value << 8) + (0xFF & source.get(--tempOffset));
        }
        if (this.fieldType >= PdeFieldTypes.INT_NEG_1_BYTES && this.fieldType <= PdeFieldTypes.INT_NEG_8_BYTES) {
            value = -value - 1;
        }
        return value;
    }

    public float readFloat32() {
        long value = 0;
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldValueLength; tempOffset > offset + FIELD_TYPE_BYTE_COUNT;) {
            value = (value << 8) + (0xFF & source.get(--tempOffset));
        }
        return Float.intBitsToFloat((int) (0xFFFFFFFF & value));
    }

    public Float readFloat32Obj() {
        if (this.fieldType == PdeFieldTypes.FLOAT_NULL) {
            return null;
        }
        int value = 0;
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldValueLength; tempOffset > offset + FIELD_TYPE_BYTE_COUNT;) {
            value = (value << 8) + (0xFF & source.get(--tempOffset));
        }
        return Float.intBitsToFloat(value);
    }

    public double readFloat64() {
        long value = 0;
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldValueLength; tempOffset > offset + FIELD_TYPE_BYTE_COUNT;) {
            value = (value << 8) + (0xFF & source.get(--tempOffset));
        }
        return Double.longBitsToDouble(value);
    }

    public Double readFloat64Obj() {
        if (this.fieldType == PdeFieldTypes.FLOAT_NULL) {
            return null;
        }
        long value = 0;
        for (int tempOffset = offset + FIELD_TYPE_BYTE_COUNT + this.fieldValueLength; tempOffset > offset + FIELD_TYPE_BYTE_COUNT;) {
            value = (value << 8) + (0xFF & source.get(--tempOffset));
        }
        return Double.longBitsToDouble(value);
    }

    public byte[] readBytes() {
        if (this.fieldType == PdeFieldTypes.BYTES_NULL) {
            return null;
        }
        if (this.fieldType == PdeFieldTypes.BYTES_0_BYTES) {
            return new byte[0];
        }
        byte[] bytes = new byte[this.fieldValueLength];
        // Absolute bulk get: does not advance the buffer position
        this.source.get(this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, bytes, 0, this.fieldValueLength);
        return bytes;
    }

    public int readBytes(byte[] dest, int offset) {
        if (this.fieldType == PdeFieldTypes.BYTES_NULL || this.fieldType == PdeFieldTypes.BYTES_0_BYTES) {
            return -1;
        }
        this.source.get(this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, dest, offset, this.fieldValueLength);
        return this.fieldValueLength;
    }

    public int readBytes(byte[] dest, int offset, int length) {
        if (this.fieldType == PdeFieldTypes.BYTES_NULL || this.fieldType == PdeFieldTypes.BYTES_0_BYTES) {
            return -1;
        }
        int lengthToRead = Math.min(length, this.fieldValueLength);
        this.source.get(this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, dest, offset, lengthToRead);
        return lengthToRead;
    }

    public byte[] readAscii() {
        if (this.fieldType == PdeFieldTypes.ASCII_NULL) {
            return null;
        }
        if (this.fieldType == PdeFieldTypes.ASCII_0_BYTES) {
            return new byte[0];
        }
        byte[] bytes = new byte[this.fieldValueLength];
        this.source.get(this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, bytes, 0, this.fieldValueLength);
        return bytes;
    }

    public int readAscii(byte[] dest, int offset) {
        if (this.fieldType == PdeFieldTypes.ASCII_NULL || this.fieldType == PdeFieldTypes.ASCII_0_BYTES) {
            return -1;
        }
        this.source.get(this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, dest, offset, this.fieldValueLength);
        return this.fieldValueLength;
    }

    public int readAscii(byte[] dest, int offset, int length) {
        if (this.fieldType == PdeFieldTypes.ASCII_NULL || this.fieldType == PdeFieldTypes.ASCII_0_BYTES) {
            return -1;
        }
        int lengthToRead = Math.min(length, this.fieldValueLength);
        this.source.get(this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, dest, offset, lengthToRead);
        return lengthToRead;
    }

    public int readUtf8(byte[] dest, int offset) {
        if (this.fieldType == PdeFieldTypes.UTF_8_NULL || this.fieldType == PdeFieldTypes.UTF_8_0_BYTES) {
            return -1;
        }
        this.source.get(this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, dest, offset, this.fieldValueLength);
        return this.fieldValueLength;
    }

    public int readUtf8(byte[] dest, int offset, int length) {
        if (this.fieldType == PdeFieldTypes.UTF_8_NULL || this.fieldType == PdeFieldTypes.UTF_8_0_BYTES) {
            return -1;
        }
        int lengthToRead = Math.min(length, this.fieldValueLength);
        this.source.get(this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, dest, offset, lengthToRead);
        return lengthToRead;
    }

    public Calendar readCalendar() {
        return readUtc(new GregorianCalendar());
    }

    public Calendar readUtc(Calendar calendar) {
        if (this.fieldLengthLength == 0) return null;

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

        switch (this.fieldValueLength) {
            case 8: {
                for (int tempOffset = startOffset + 8; tempOffset > offset;) {
                    fieldPartValue = (fieldPartValue << 8) + (0xFF & source.get(--tempOffset));
                }
                calendar.setTimeInMillis(fieldPartValue);
                return calendar;
            }
            case 9: {
                fieldPartValue = (0xFF) & this.source.get(startOffset + 8);
                fieldPartValue <<= 8;
                fieldPartValue += (0xFF) & this.source.get(startOffset + 7);
                calendar.set(Calendar.MILLISECOND, fieldPartValue);
            }
            case 7: {
                calendar.set(Calendar.SECOND, (0xFF) & this.source.get(startOffset + 6));
            }
            case 6: {
                calendar.set(Calendar.MINUTE, (0xFF) & this.source.get(startOffset + 5));
            }
            case 5: {
                calendar.set(Calendar.HOUR_OF_DAY, (0xFF) & this.source.get(startOffset + 4));
            }
            case 4: {
                calendar.set(Calendar.DAY_OF_MONTH, (0xFF) & this.source.get(startOffset + 3));
            }
            case 3: {
                calendar.set(Calendar.MONTH, (0xFF) & this.source.get(startOffset + 2) - 1);
            }
            case 2: {
                fieldPartValue = (0xFF) & this.source.get(startOffset + 1);
                fieldPartValue <<= 8;
                fieldPartValue += (0xFF) & this.source.get(startOffset);
                calendar.set(Calendar.YEAR, fieldPartValue);
            }
        }
        return calendar;
    }

    public int readKey(byte[] dest) {
        this.source.get(this.offset + FIELD_TYPE_BYTE_COUNT + this.fieldLengthLength, dest, 0, this.fieldValueLength);
        return this.fieldValueLength;
    }
}
