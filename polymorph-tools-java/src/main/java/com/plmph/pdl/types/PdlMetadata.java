package com.plmph.pdl.types;

import java.util.ArrayList;
import java.util.List;

public class PdlMetadata {

    public List contents = new ArrayList();

    public String toString() {
        return "<" + contents + ">";
    }

}
