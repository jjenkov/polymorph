package com.plmph.vm;

import java.util.List;

public class AlphaVMExternalFunctionMock implements AlphaVMExternalFunction{

    public String name = null;
    public AlphaVM alphaVM;
    public int callCount = 0;
    public List params = null;
    public Object returnValue = null;

    public AlphaVMExternalFunctionMock(String name){
        this.name = name;
    }

    @Override
    public void attach(AlphaVM vm) {
        this.alphaVM = vm;
    }

    @Override
    public Object call(List params) {
        callCount++;
        this.params = params;
        System.out.println("Called mock function " + callCount + " times.");
        return returnValue;
    }

    public AlphaVMExternalFunctionMock setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
        return this;
    }
}