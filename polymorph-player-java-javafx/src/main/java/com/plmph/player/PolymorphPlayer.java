package com.plmph.player;

import com.plmph.player.plugins.controldeck.ControlDeckPlugin;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PolymorphPlayer {

    private List<PolymorphPlayerPlugin> plugins = new ArrayList<>();
    private List<PlayGroup> playGroups = new ArrayList<>();

    private Stage rootStage;

    public Stage getRootStage() {
        return rootStage;
    }
    public void setRootStage(Stage rootStage) {
        this.rootStage = rootStage;
    }

    public List<PlayGroup> getPlayGroups() {
        return playGroups;
    }

    public PolymorphPlayer init(){
        initPlugins();
        return this;
    }

    private void initPlugins() {
        // add built-in plugins
        this.plugins.add(new ControlDeckPlugin());

        // init all plugins
        for(PolymorphPlayerPlugin plugin : plugins){
            PlayGroup playGroup = new PlayGroup();
            PolymorphPlayerProxy proxy = new PolymorphPlayerProxy(this);
            proxy.setPlayGroup(playGroup);
            playGroup.setProxy(proxy);
            playGroups.add(playGroup);
            plugin.init(proxy);
        }
    }

}
