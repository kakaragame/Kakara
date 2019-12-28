package org.kakara.engine.scene;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.ItemHandler;
import org.kakara.engine.render.Renderer;

public interface Scene {


    void render( );

    void setCurserStatus(boolean status);

    boolean getCurserStatus();

    ItemHandler getItemHandler();

    void unload();
}
