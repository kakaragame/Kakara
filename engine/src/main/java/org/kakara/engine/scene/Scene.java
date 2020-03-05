package org.kakara.engine.scene;

import org.kakara.engine.item.ItemHandler;
import org.kakara.engine.lighting.LightHandler;
import org.kakara.engine.ui.HUD;

public interface Scene {


    /**
     * Internal Use Only.
     * <p><b>DO NOT OVERRIDE</b></p>
     */
    void render();

    /**
     * Called every time the game updates.
     */
    void update();

    /**
     * Set if the cursor should be enabled.
     * @param status Cursor enabled.
     */
    void setCurserStatus(boolean status);

    /**
     * Check if the cursor is enabled.
     * @return If the cursor is enabled.
     */
    boolean getCurserStatus();

    /**
     * Get the ItemHandler
     * @return The item handler for this scene.
     */
    ItemHandler getItemHandler();

    /**
     * Get the LightHandler for this scene.
     * @return The light handler
     */
    LightHandler getLightHandler();

    /**
     * Get the HUD for this scene.
     * @return The hud.
     */
    HUD getHUD();

    void unload();
}
