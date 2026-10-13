package com.plmph.vm;

import java.util.List;

public interface VMOutputListener {

    void out(Object value);
    void out(List values);

}
