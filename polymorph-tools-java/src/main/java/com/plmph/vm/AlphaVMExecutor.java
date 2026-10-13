package com.plmph.vm;


/**
 * The AlphaVMExecutor provides a simpler interface to execute code in an AlphaVM.
 * You can pass a Pasm script to the AlphaVMExecutor, and it compiles it to bincode and executes it.
 * You can also pass already compiled bincode to the AlphaVM and execute that.
 *
 */
public class AlphaVMExecutor {

    public AlphaPasmToBincodeCompiler alphaCompiler = new AlphaPasmToBincodeCompiler();
    public AlphaVM alphaVM = new AlphaVM();

    public byte[] bincode = new byte[1024];
    public int bincodeLength = 0;

    public void execPasm(byte[] pasmSource, int offset, int length){
        this.bincodeLength = alphaCompiler.compile(pasmSource, offset, length, this.bincode, 0);
        this.alphaVM.setCode(this.bincode, this.bincodeLength);
        while(this.alphaVM.hasMoreInstructions()) {
            this.alphaVM.execInstruction();
        }
    }

    public void execBincode(byte[] bincode, int length){
        this.alphaVM.setCode(bincode, length);
        while(this.alphaVM.hasMoreInstructions()) {
            this.alphaVM.execInstruction();
        }
    }
}
