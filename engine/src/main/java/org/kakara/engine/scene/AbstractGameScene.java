package org.kakara.engine.scene;

import org.kakara.engine.GameHandler;
import org.kakara.engine.renderobjects.ChunkHandler;
import org.kakara.engine.renderobjects.TextureAtlas;

public abstract class AbstractGameScene extends AbstractScene {

    private ChunkHandler chunkHandler;
    private TextureAtlas textureAtlas;

    public AbstractGameScene(GameHandler gameHandler) {
        super(gameHandler);
        this.chunkHandler = new ChunkHandler();
    }

    @Override
    public final void render( ) {
        gameHandler.getGameEngine().getRenderer().render(gameHandler.getWindow(), gameHandler.getCamera(), this);
        if(getSkyBox() != null)
            gameHandler.getGameEngine().getRenderer().renderSkyBox(gameHandler.getWindow(), gameHandler.getCamera(), this);
        hud.render(gameHandler.getWindow());
    }

    /**
     * THIS MUST BE SET IF RENDER CHUNKS ARE TO BE USED.
     * @param textureAtlas The texture atlas.
     */
    public void setTextureAtlas(TextureAtlas textureAtlas){
        this.textureAtlas = textureAtlas;
    }

    public ChunkHandler getChunkHandler(){
        return chunkHandler;
    }

    public TextureAtlas getTextureAtlas(){
        return textureAtlas;
    }




    @Override
    public void unload() {
        getItemHandler().cleanup();
    }
}
