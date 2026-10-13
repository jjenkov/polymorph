package com.plmph.vm;

import java.util.List;

public class VMOutputListenerMock implements VMOutputListener {

    public int callCount = 0;
    public Object value = null;
    public List values = null;

    @Override
    public void out(Object value) {
        callCount++;
        this.value = value;
    }

    @Override
    public void out(List values) {
        callCount++;
        this.values = values;
    }
}
