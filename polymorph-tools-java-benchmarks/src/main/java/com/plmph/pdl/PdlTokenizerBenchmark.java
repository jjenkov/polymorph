package com.plmph.pdl;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

public class PdlTokenizerBenchmark {

    @State(Scope.Thread)
    public static class TokenizerState {
        public int tokenCount = 0;
        public long[] tokenOffset = new long[1024];

        @Setup(Level.Trial)
        public void doSetup() {
            System.out.println("PDL script length: " + PdlStrings.pdlBytes2.length);
        }

        @TearDown(Level.Trial)
        public void doTearDown() {
            System.out.println("Tokens found: " + tokenCount);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Object tokenize_for(TokenizerState state, Blackhole blackhole){
        state.tokenCount = PdlTokenizer.tokenize_for(PdlStrings.pdlBytes2, 0, PdlStrings.pdlBytes.length, state.tokenOffset);
        blackhole.consume(state.tokenCount);
        return state.tokenCount;
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Object tokenize_while(TokenizerState state, Blackhole blackhole){
        state.tokenCount = PdlTokenizer.tokenize_while(PdlStrings.pdlBytes2, 0, PdlStrings.pdlBytes.length, state.tokenOffset);
        blackhole.consume(state.tokenCount);
        return state.tokenCount;
    }

    //@Benchmark
    //@BenchmarkMode(Mode.Throughput)
    public Object tokenize_doWhile(TokenizerState state, Blackhole blackhole){
        state.tokenCount = PdlTokenizer.tokenize_doWhile(PdlStrings.pdlBytes2, 0, PdlStrings.pdlBytes.length, state.tokenOffset);
        blackhole.consume(state.tokenCount);
        return state.tokenCount;
    }

    //@Benchmark
    //@BenchmarkMode(Mode.Throughput)
    public Object simd(TokenizerState state, Blackhole blackhole){
        state.tokenCount = PdlTokenizer.tokenize_simd(PdlStrings.pdlBytes2, 0, PdlStrings.pdlBytes.length, state.tokenOffset);
        blackhole.consume(state.tokenCount);
        return state.tokenCount;
    }

}