package com.plmph.streams;

import java.util.Arrays;

public class StreamId {

    private byte[] idBytes;

    public StreamId(byte[] idBytes){
        this.idBytes = idBytes;
    }

    public byte[] getIdBytes() {
        return idBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StreamId streamId = (StreamId) o;
        return Arrays.equals(idBytes, streamId.idBytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(idBytes);
    }
}
