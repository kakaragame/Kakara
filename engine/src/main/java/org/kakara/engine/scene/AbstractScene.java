package org.kakara.engine.scene;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.item.ItemHandler;
import org.kakara.engine.lighting.LightHandler;
import org.kakara.engine.lighting.PointLight;
import org.kakara.engine.lighting.SpotLight;

public abstract class AbstractScene implements Scene {
    private ItemHandler itemHandler = new ItemHandler();
    private LightHandler lightHandler = new LightHandler();
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

    @Override
    public LightHandler getLightHandler(){
        return lightHandler;
    }

    public void addItem(GameItem gameItem) {
        itemHandler.addItem(gameItem);
    }

    public void add(GameItem gameItem){
        itemHandler.addItem(gameItem);
    }

    public void add(PointLight pointLight){
        lightHandler.addPointLight(pointLight);
    }

    public void add(SpotLight spotLight){
        lightHandler.addSpotLight(spotLight);
    }


}
