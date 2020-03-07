package org.kakara.engine.scene;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;

public abstract class AbstractGameScene extends AbstractScene {

    public AbstractGameScene(GameHandler gameHandler) {
        super(gameHandler);
    }

    @Override
    public final void render( ) {
        gameHandler.getGameEngine().getRenderer().render(gameHandler.getWindow(), getItemHandler().getItemList(), gameHandler.getCamera());
        if(getSkyBox() != null)
            gameHandler.getGameEngine().getRenderer().renderSkyBox(gameHandler.getWindow(), gameHandler.getCamera(), this);
        hud.render(gameHandler.getWindow());
    }




    @Override
    public void unload() {
        getItemHandler().getItemList().forEach(GameItem::cleanup);
    }
}
