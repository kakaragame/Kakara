package org.kakara.engine.scene;

import org.kakara.engine.GameEngine;
import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.item.ItemHandler;

public abstract class AbstractGameScene extends AbstractScene {

    //TODO Add SkyBox and Light
    @Override
    public void render(GameHandler handler) {
        handler.getWindow().setCursorVisibility(getMouseStatus());
        handler.getGameEngine().getRenderer().render(handler.getWindow(), getItemHandler().getItemList(), handler.getCamera());
    }


    @Override
    public void unload() {
        getItemHandler().getItemList().forEach(GameItem::cleanup);
    }
}
