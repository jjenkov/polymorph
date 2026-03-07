package com.plmph.player;

import com.plmph.player.plugins.controldeck.ControlDeckPlugin;
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
        //rootStage.setTitle("Polymorph Player");
        //rootStage.show();

        return this;
    }

    public Stage getRootStage() {
        return rootStage;
    }

    private void initPlugins() {
        // add built-in plugins
        this.plugins.add(new ControlDeckPlugin());

        // init all plugins
        for(PolymorphPlayerPlugin plugin : plugins){
            plugin.init(new PolymorphPlayerProxy(this));
        }
    }

}
