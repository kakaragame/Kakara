package org.kakara.engine.scene;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.lighting.PointLight;

public abstract class AbstractGameScene extends AbstractScene {

    public AbstractGameScene(GameHandler gameHandler) {
        super(gameHandler);
    }

    //TODO Add SkyBox
    @Override
    public final void render( ) {
        gameHandler.getGameEngine().getRenderer().render(gameHandler.getWindow(), getItemHandler().getItemList(), gameHandler.getCamera());
    }




    @Override
    public void unload() {
        getItemHandler().getItemList().forEach(GameItem::cleanup);
    }
}
