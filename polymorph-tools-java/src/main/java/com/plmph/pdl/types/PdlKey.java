package com.plmph.pdl.types;

import java.nio.charset.StandardCharsets;

public class PdlKey {

    public byte[] source = null;
    public int offset = 0;
    public int length = 0;

    public PdlKey(byte[] source, int offset, int length){
        this.source = source;
        this.offset = offset;
        this.length = length;
    }
    public String toString(){
        return new String(source, offset, length, StandardCharsets.UTF_8);
    }

}
