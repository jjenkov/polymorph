package com.plmph.player;


/**
 * A Playable represents a file or stream that the Polymorph Player can play.
 */
public class Playable {

    private int type = UNKNOWN;
    private String address = null;

    public Playable(int type, String address) {
        this.type = type;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public int getType() {
        return type;
    }


    public static final int UNKNOWN = -1;
    public static final int SCRIPT = 1;

    // Audio, video etc.



}
