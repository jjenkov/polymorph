package com.plmph.player;

import javafx.application.Platform;

import java.util.function.Consumer;

/**
 * A PolymorphPlayerProxy is a proxy object that can interact with the Player UI.
 * The PolymorphPlayerProxy object is intended to be used by background threads, either directly, or indirectly
 * e.g. via a virtual machine executing a script (that accesses the proxy).
 *
 * Not all methods in this class are thread-safe. Some of its methods are only safe for the JavaFX UI thread
 * to call. If you want to call these methods from a background thread, you msut wrap the call to these methods
 * in a call to the runInUiThreadLater() method. This method forwards the call to the JavaFX UI thread via Platform.runLater().
 *
 */
public class PolymorphPlayerProxy {

    private PolymorphPlayer polymorphPlayer;

    public PolymorphPlayerProxy(PolymorphPlayer polymorphPlayer) {
        this.polymorphPlayer = polymorphPlayer;
    }

    public void runInUiThreadLater(Consumer<PolymorphPlayerProxy> action) {
        Platform.runLater(() -> action.accept(this) );
    }
}
