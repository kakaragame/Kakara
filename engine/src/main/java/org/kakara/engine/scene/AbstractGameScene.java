package org.kakara.engine.scene;

import org.kakara.engine.GameHandler;

public abstract class AbstractGameScene extends AbstractScene {

    public AbstractGameScene(GameHandler gameHandler) {
        super(gameHandler);
    }

    @Override
    public final void render( ) {
        gameHandler.getGameEngine().getRenderer().render(gameHandler.getWindow(), gameHandler.getCamera(), this);
        if(getSkyBox() != null)
            gameHandler.getGameEngine().getRenderer().renderSkyBox(gameHandler.getWindow(), gameHandler.getCamera(), this);
        hud.render(gameHandler.getWindow());
    }




    @Override
    public void unload() {
        getItemHandler().cleanup();
    }
}
