package com.plmph.player;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Polymorph Player JavaFX App
 */
public class PolymorphPlayerApp extends Application {

    private static PolymorphPlayer polymorphPlayer;

    public static void main(String[] args) {
        polymorphPlayer = new PolymorphPlayer();
        launch();
    }

    @Override
    public void start(Stage rootStage) {
        polymorphPlayer.setRootStage(rootStage);
        polymorphPlayer.init();
    }
}