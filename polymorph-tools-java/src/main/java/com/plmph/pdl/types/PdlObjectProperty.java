package com.plmph.pdl.types;

public class PdlObjectProperty {

    public PdlKey key;
    public Object value;

    public PdlObjectProperty(PdlKey key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String toString(){
        return key.toString() + "=" + value.toString();
    }

}
