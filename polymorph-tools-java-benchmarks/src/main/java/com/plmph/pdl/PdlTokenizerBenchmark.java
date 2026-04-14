package com.plmph.pdl;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

public class PdlTokenizerBenchmark {

    @State(Scope.Thread)
    public static class TokenizerState {
        public byte[] source = PdlStrings.pdlBytes3_x10;
        public byte[] sourceMinified = PdlStrings.pdlBytes3_x10_minified;


        public int tokenCount = 0;
        public long[] tokenOffsets = new long[10 * 1024];

        @Setup(Level.Trial)
        public void doSetup() {
            System.out.println("PDL script length: " + source.length);
            System.out.println("PDL script minified length: " + sourceMinified.length);
        }

        @TearDown(Level.Trial)
        public void doTearDown() {
            System.out.println("Tokens found: " + tokenCount);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Object tokenize_minified(TokenizerState state, Blackhole blackhole){
        state.tokenCount = PdlTokenizer.tokenizeMinified(state.sourceMinified, 0, state.sourceMinified.length, state.tokenOffsets);
        blackhole.consume(state.tokenCount);
        return state.tokenCount;
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Object tokenize_for(TokenizerState state, Blackhole blackhole){
        state.tokenCount = PdlTokenizer.tokenize_for(state.source, 0, state.source.length, state.tokenOffsets);
        blackhole.consume(state.tokenCount);
        return state.tokenCount;
    }

    //@Benchmark
    //@BenchmarkMode(Mode.Throughput)
    public Object tokenize_while(TokenizerState state, Blackhole blackhole){
        state.tokenCount = PdlTokenizer.tokenize_while(state.source, 0, state.source.length, state.tokenOffsets);
        blackhole.consume(state.tokenCount);
        return state.tokenCount;
    }

    //@Benchmark
    //@BenchmarkMode(Mode.Throughput)
    public Object tokenize_while_and(TokenizerState state, Blackhole blackhole){
        state.tokenCount = PdlTokenizer.tokenize_while_and(state.source, 0, state.source.length, state.tokenOffsets);
        blackhole.consume(state.tokenCount);
        return state.tokenCount;
    }

    //@Benchmark
    //@BenchmarkMode(Mode.Throughput)
    public Object tokenize_doWhile(TokenizerState state, Blackhole blackhole){
        state.tokenCount = PdlTokenizer.tokenize_doWhile(state.source, 0, state.source.length, state.tokenOffsets);
        blackhole.consume(state.tokenCount);
        return state.tokenCount;
    }

    //@Benchmark
    //@BenchmarkMode(Mode.Throughput)
    public Object simd(TokenizerState state, Blackhole blackhole){
        state.tokenCount = PdlTokenizer.tokenize_simd(state.source, 0, state.source.length, state.tokenOffsets);
        blackhole.consume(state.tokenCount);
        return state.tokenCount;
    }

}