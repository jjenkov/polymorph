package com.plmph.vm;

import java.util.List;

public interface AlphaVMExternalFunction {

    public void attach(AlphaVM vm);

    public Object call(List params);


}
