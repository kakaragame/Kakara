package org.kakara.engine.scene;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.ItemHandler;

public class SimpleGameScene implements Scene {
    private ItemHandler itemHandler = new ItemHandler();
    private boolean mouseStatus = true;


    @Override
    public void render(GameHandler handler) {
        handler.getWindow().setCursorVisibility(mouseStatus);

        handler.getGameEngine().getRenderer().render(handler.getWindow(), itemHandler.getItemList(), handler.getCamera());
    }

    @Override
    public void setMouseStatus(boolean status) {
        mouseStatus = status;
    }

    @Override
    public boolean getMouseStatus() {
        return mouseStatus;
    }

    @Override
    public ItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public void unload() {
//TODO i am not sure if I plan to actually have this
    }
}
