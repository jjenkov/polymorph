package com.plmph.pde;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PdeReaderTest {


    @Test
    public void testRead() {
        byte[] dest = new byte[1024];

        PdeWriter writer = new PdeWriter(dest).setCompositeFieldStack(new int[16]);

        writer.writeBoolean(true);
        writer.writeBoolean(false);
        writer.writeBooleanObj(null);

        writer.writeIntObj((Integer) null);
        writer.writeIntObj(Integer.valueOf(123));
        writer.writeIntObj(Long.valueOf(123_456_789L));

        writer.writeInt(789);
        writer.writeInt(383_383L);

        //todo test writing + reading of float null

        //todo test writing + reading of float32
        writer.writeFloat32(123.45f);


        //todo test writing + reading of float64
        writer.writeFloat64(12345.6789d);

        writer.writeBytes(null);
        writer.writeBytes(new byte[]{ 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14});
        writer.writeBytes(new byte[]{ 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});

        //test writing of object
        writer.writeObjectBeginPush(2);
        writer.writeKey(new byte[] { 1,2,3,4,5});
        writer.writeInt(999);
        writer.writeObjectEndPop();

        //test writing of table
        writer.writeTableBeginPush(2);
        writer.writeKey(new byte[]{7, 9, 11, 13});
        writer.writeKey(new byte[]{1, 3,  5,  7});
        writer.writeKey(new byte[]{4, 6,  8, 10});
        writer.writeTableEndPop(3);

        //test writing of keys

        PdeReader reader = new PdeReader();
        reader.setSource(dest, 0, writer.offset);

        reader.next();
        assertEquals(PdeFieldTypes.BOOLEAN_TRUE, reader.fieldType);

        reader.next();
        assertEquals(PdeFieldTypes.BOOLEAN_FALSE, reader.fieldType);

        reader.next();
        assertEquals(PdeFieldTypes.BOOLEAN_NULL, reader.fieldType);

        reader.next();
        assertEquals(PdeFieldTypes.INT_NULL, reader.fieldType);

        reader.next();
        assertEquals(PdeFieldTypes.INT_POS_1_BYTES, reader.fieldType);
        assertEquals(1, reader.fieldValueLength);
        assertEquals(0, reader.fieldLengthLength);
        assertEquals(123, reader.readInt());
        assertEquals(123, reader.readIntObj());

        reader.next();
        assertEquals(PdeFieldTypes.INT_POS_4_BYTES, reader.fieldType);
        assertEquals(4, reader.fieldValueLength);
        assertEquals(0, reader.fieldLengthLength);
        assertEquals(123_456_789L, reader.readInt());
        assertEquals(123_456_789L, reader.readIntObj());

        reader.next();
        assertEquals(PdeFieldTypes.INT_POS_2_BYTES, reader.fieldType);
        assertEquals(0, reader.fieldLengthLength);
        assertEquals(2, reader.fieldValueLength);
        assertEquals(789, reader.readInt());
        assertEquals(789, reader.readIntObj());


        reader.next();
        assertEquals(PdeFieldTypes.INT_POS_3_BYTES, reader.fieldType);
        assertEquals(0, reader.fieldLengthLength);
        assertEquals(3, reader.fieldValueLength);
        assertEquals(383_383L, reader.readInt());
        assertEquals(383_383L, reader.readIntObj());

        reader.next();
        assertEquals(PdeFieldTypes.FLOAT_4_BYTES, reader.fieldType);
        assertEquals(0, reader.fieldLengthLength);
        assertEquals(4, reader.fieldValueLength);
        assertEquals(123.45f, reader.readFloat32());
        assertEquals(123.45f, reader.readFloat32Obj());

        reader.next();
        assertEquals(PdeFieldTypes.FLOAT_8_BYTES, reader.fieldType);
        assertEquals(0, reader.fieldLengthLength);
        assertEquals(8, reader.fieldValueLength);
        assertEquals(12345.6789d, reader.readFloat64());
        assertEquals(12345.6789d, reader.readFloat64Obj());


        byte[] readBuf = new byte[1024];
        reader.next();
        assertEquals(PdeFieldTypes.BYTES_NULL, reader.fieldType);
        assertEquals(0, reader.fieldLengthLength);
        assertEquals(0, reader.fieldValueLength);
        assertEquals(-1, reader.readBytes(readBuf, 0));

        reader.next();
        assertEquals(PdeFieldTypes.BYTES_15_BYTES, reader.fieldType);
        assertEquals(0, reader.fieldLengthLength);
        assertEquals(15, reader.fieldValueLength);
        assertEquals(15, reader.readBytes(readBuf, 0));
        assertEquals(5, reader.readBytes(readBuf, 0, 5));
        assertTrue(areEqual(dest, readBuf, reader.offset + 1 + reader.fieldLengthLength, 0, reader.fieldValueLength));


        reader.next();
        assertEquals(PdeFieldTypes.BYTES_1_LENGTH_BYTES, reader.fieldType);
        assertEquals(1, reader.fieldLengthLength);
        assertEquals(16, reader.fieldValueLength);
        assertEquals(16, reader.readBytes(readBuf, 0));
        assertEquals(5, reader.readBytes(readBuf, 0, 5));

        //read object - so need to move into the object.
        reader.next();
        assertEquals(PdeFieldTypes.OBJECT_2_LENGTH_BYTES, reader.fieldType);
        assertEquals(2, reader.fieldLengthLength);
        assertEquals(9, reader.fieldValueLength);

        reader.moveInto();
        reader.next();

        assertTrue(reader.hasNext());
        assertEquals(PdeFieldTypes.KEY_5_BYTES, reader.fieldType);

        reader.next();
        assertFalse(reader.hasNext());
        assertEquals(PdeFieldTypes.INT_POS_2_BYTES, reader.fieldType);

        // need to move out of object again
        reader.moveOutOf();


        //read table - so need to move into the table.
        reader.next();
        assertEquals(PdeFieldTypes.TABLE_2_LENGTH_BYTES, reader.fieldType);
        assertEquals( 2, reader.fieldLengthLength);
        assertEquals(18, reader.fieldValueLength);

        reader.moveInto();
        reader.next();
        assertEquals(PdeFieldTypes.INT_POS_2_BYTES, reader.fieldType);

        reader.next();
        assertEquals(PdeFieldTypes.KEY_4_BYTES, reader.fieldType);
        int bytesRead = reader.readKey(readBuf);
        assertEquals(4, bytesRead);
        assertTrue(areEqual(dest, readBuf, reader.offset + 1 + reader.fieldLengthLength, 0, reader.fieldValueLength));

        reader.next();
        assertEquals(PdeFieldTypes.KEY_4_BYTES, reader.fieldType);
        bytesRead = reader.readKey(readBuf);
        assertEquals(4, bytesRead);
        assertTrue(areEqual(dest, readBuf, reader.offset + 1 + reader.fieldLengthLength, 0, reader.fieldValueLength));

        reader.next();
        assertEquals(PdeFieldTypes.KEY_4_BYTES, reader.fieldType);
        bytesRead = reader.readKey(readBuf);
        assertEquals(4, bytesRead);
        assertTrue(areEqual(dest, readBuf, reader.offset + 1 + reader.fieldLengthLength, 0, reader.fieldValueLength));

        assertFalse(reader.hasNext());

        //need to move out of object again.
        reader.moveOutOf();

        assertFalse(reader.hasNext());
    }

    private boolean areEqual(byte[] a, byte[] b, int aOffset, int bOffset, int length) {
        if(a == b) {
            return true;
        }
        if(a == null && b != null) {
            return false;
        }
        if(a != null && b == null) {
            return false;
        }

        if(a.length != b.length) {
            return false;
        }

        for(int i=0; i<length; i++) {
            if (a[aOffset + i] != b[bOffset + i]) {
                return false;
            }
        }

        return true;
    }
}
