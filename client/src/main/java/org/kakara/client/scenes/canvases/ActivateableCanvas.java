package org.kakara.client.scenes.canvases;

import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.canvases.ComponentCanvas;

/**
 * A canvas that can be enabled and disabled.
 */
public abstract class ActivateableCanvas extends ComponentCanvas {
    private boolean activated = false;

    /**
     * The constructor for the Activateable canvas.
     *
     * @param scene The instance of the scene.
     */
    public ActivateableCanvas(Scene scene) {
        super(scene);
    }

    /**
     * Switch the status of the canvas.
     *
     * <p>If enabled, it will be disabled. If disabled, it will be enabled.</p>
     */
    public void switchStatus() {
        if (activated) {
            activated = false;
            remove();
        } else {
            activated = true;
            add();
        }
    }

    /**
     * Check if the canvas is activated.
     *
     * @return If the canvas is activated.
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Set if the canvas is activated.
     *
     * @param activated If the canvas should be activated.
     */
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    /**
     * Add items to the canvas.
     */
    abstract void add();

    /**
     * Remove items from the canvas.
     */
    abstract void remove();

    /**
     * Update the canvas.
     */
    abstract public void update();

    /**
     * Close the canvas.
     */
    abstract public void close();
}
