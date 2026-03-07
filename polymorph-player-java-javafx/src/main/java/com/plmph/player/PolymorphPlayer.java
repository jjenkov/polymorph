package com.plmph.player;

import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PolymorphPlayer {

    private List<PolymorphPlayerPlugin> plugins = new ArrayList<>();

    private Stage rootStage;

    public void setRootStage(Stage rootStage) {
        this.rootStage = rootStage;
    }

    public PolymorphPlayer init(){
        initPlugins();
        rootStage.setTitle("Polymorph Player");
        rootStage.show();

        return this;
    }

    private void initPlugins() {
        // add built-in plugins

        // init all plugins
        for(PolymorphPlayerPlugin plugin : plugins){
            plugin.init(new PolymorphPlayerProxy(this));
        }
    }

}
