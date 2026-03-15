package com.plmph.player;


import java.util.ArrayList;
import java.util.List;

/**
 * A PlayGroup represents a shared execution contexts for scripts executed by the Polymorph Player.
 * Scripts executed within the same PlayGroup will share VM state and PolymorphPlayerProxy state.
 * Multiple PlayGroup's could be created to run multiple groups of scripts that share state only with other
 * scripts in the same PlayGroup.
 */
public class PlayGroup {

    private PolymorphPlayerProxy proxy = null;

    // VM property will be added when the VM code moves into this mono-repo.

    private List<Playable> playables = new ArrayList<Playable>();

    public List<Playable> getPlayables() {
        return this.playables;
    }

    public PolymorphPlayerProxy getProxy() {
        return proxy;
    }

    public void setProxy(PolymorphPlayerProxy proxy) {
        this.proxy = proxy;
    }

    public void play(Playable playable){
        System.out.println("Playing: " + playable.getAddress());
        this.playables.add(playable);
    }



}
