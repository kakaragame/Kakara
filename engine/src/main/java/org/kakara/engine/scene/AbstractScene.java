package org.kakara.engine.scene;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.item.ItemHandler;

public abstract class AbstractScene implements Scene {
    private ItemHandler itemHandler = new ItemHandler();
    private boolean mouseStatus;
    protected GameHandler gameHandler;

    protected AbstractScene(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    @Override
    public void setCurserStatus(boolean status) {
        mouseStatus = status;
        gameHandler.getWindow().setCursorVisibility(status);
    }

    @Override
    public boolean getCurserStatus() {
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
