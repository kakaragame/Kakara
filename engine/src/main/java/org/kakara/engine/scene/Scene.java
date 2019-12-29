package org.kakara.engine.scene;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.ItemHandler;
import org.kakara.engine.lighting.LightHandler;
import org.kakara.engine.render.Renderer;

public interface Scene {


    void render( );

    void update();

    void setCurserStatus(boolean status);

    boolean getCurserStatus();

    ItemHandler getItemHandler();

    LightHandler getLightHandler();

    void unload();
}
