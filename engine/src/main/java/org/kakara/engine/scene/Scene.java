package org.kakara.engine.scene;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.ItemHandler;
import org.kakara.engine.render.Renderer;

public interface Scene {


    void render(GameHandler handler);

    void setMouseStatus(boolean status);

    boolean getMouseStatus();

    ItemHandler getItemHandler();

    void unload();
}
