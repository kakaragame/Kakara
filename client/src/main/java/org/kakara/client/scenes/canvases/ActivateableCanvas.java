package org.kakara.client.scenes.canvases;

import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.items.ComponentCanvas;

public abstract class ActivateableCanvas extends ComponentCanvas {
    private boolean activated = false;

    public ActivateableCanvas(Scene scene) {
        super(scene);
    }

    public void switchStatus() {
        if (activated) {
            activated = false;
            remove();
        } else {
            activated = true;
            add();
        }
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    abstract void add();

    abstract void remove();

    abstract public void update();

    abstract void close();
}
