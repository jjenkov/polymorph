package com.plmph.pdl.types;

import java.util.ArrayList;
import java.util.List;

public class PdlTable {

    public List<PdlKey> keys = new ArrayList();
    public List contents = new ArrayList();

    public String toString() {
        return "[" + keys + contents + "]";
    }

    public boolean isArray() {
        return keys.size() == 0;
    }

    public int getColumnCount() {
        if(isArray()) return 1;
        return keys.size();
    }

    public int getRowCount() {
        return contents.size() / getColumnCount() ;
    }
}
