package org.kakara.engine.scene;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.item.ItemHandler;

public abstract class AbstractScene implements Scene {
    private ItemHandler itemHandler = new ItemHandler();
    private boolean mouseStatus;

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

    public void addItem(GameItem gameItem) {
        itemHandler.addItem(gameItem);
    }


}
