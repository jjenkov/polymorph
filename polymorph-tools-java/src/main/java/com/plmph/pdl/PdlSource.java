package com.plmph.pdl;

import java.nio.charset.StandardCharsets;

public class PdlSource {
    public byte[] source;
    public int offset;
    public int length;

    public long[] tokenOffsetPairs;
    public int    tokenOffsetPairsLength;


    public PdlSource setSource(byte[] source) {
        this.source = source;
        this.offset = 0;
        this.length = source.length;
        this.tokenOffsetPairsLength = 0;
        return this;
    }

    public PdlSource setSource(byte[] source, int length) {
        this.source = source;
        this.offset = 0;
        this.length = length;
        this.tokenOffsetPairsLength = 0;
        return this;
    }

    public PdlSource setSource(byte[] source, int offset, int length) {
        this.source = source;
        this.offset = offset;
        this.length = length;
        this.tokenOffsetPairsLength = 0;
        return this;
    }

    public PdlSource setSource(String source) {
        this.source = source.getBytes(StandardCharsets.UTF_8);
        this.offset = 0;
        this.length = this.source.length;
        this.tokenOffsetPairsLength = 0;
        return this;
    }

    public PdlSource setTokenOffsetArray(long[] tokenOffsetPairsArray) {
        this.tokenOffsetPairs = tokenOffsetPairsArray;
        this.tokenOffsetPairsLength = 0;
        return this;
    }


}
