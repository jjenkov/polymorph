# Polymorph Data Language (PDL) Benchmarks

Here are some early benchmarks of tokenizing PDL (without SIMD instructions):

Hardware: Intel core i9 12900H (mobile) (released 2022) – laptop in Turbo mode.

| Benchmark               | Performance                  | 
|-------------------------|------------------------------|
| Tokenizing PDL          | 2.214 billion bytes / second |
| Tokenizing minified PDL | 2.606 billion bytes / second |
|                         |                              |

Code:
[polymorph-tools-java-benchmarks/src/main/java/com/plmph/pdl/PdlTokenizerBenchmark.java](../polymorph-tools-java-benchmarks/src/main/java/com/plmph/pdl/PdlTokenizerBenchmark.java)