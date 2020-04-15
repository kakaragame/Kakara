package org.kakara.engine.scene;

import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.kakara.engine.Camera;
import org.kakara.engine.GameHandler;
import org.kakara.engine.collision.Collidable;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.renderobjects.ChunkHandler;
import org.kakara.engine.renderobjects.RenderChunk;
import org.kakara.engine.renderobjects.TextureAtlas;

import java.util.ArrayList;
import java.util.List;

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

    public Collidable selectGameItems(){
        Collidable selectedGameItem = null;
        float closestDistance = 20;

        Vector3f dir = new Vector3f();

        dir = getCamera().getViewMatrix().positiveZ(dir).negate();

        Vector3f max = new Vector3f();
        Vector3f min = new Vector3f();
        Vector2f nearFar = new Vector2f();

        for(Collidable collidable : gameHandler.getCollisionManager().getSelectionItems(getCamera().getPosition())){
            collidable.setSelected(false);
            min.set(collidable.getColPosition().toJoml());
            max.set(collidable.getColPosition().toJoml());
            min.add(-collidable.getColScale()/2, -collidable.getColScale()/2, -collidable.getColScale()/2);
            max.add(collidable.getColScale()/2, collidable.getColScale()/2, collidable.getColScale()/2);
            if (Intersectionf.intersectRayAab(getCamera().getPosition().toJoml(), dir, min, max, nearFar) && nearFar.x < closestDistance) {
                closestDistance = nearFar.x;
                selectedGameItem = collidable;
            }
        }

        if(selectedGameItem != null){
            selectedGameItem.setSelected(true);
        }
        return selectedGameItem;
    }




    @Override
    public void unload() {
        getItemHandler().cleanup();
    }
}
