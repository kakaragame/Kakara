package org.kakara.game;

import org.kakara.core.client.Save;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.GameEntity;

import java.util.List;

public class IntergratedServer implements Server {
    private LocalDataWatcher dataWatcher;
    private Save save;
    private GameEntity gameEntity;

    public IntergratedServer(LocalDataWatcher dataWatcher, Save save, GameEntity gameEntity) {
        this.dataWatcher = dataWatcher;
        this.save = save;
        this.gameEntity = gameEntity;
    }

    @Override
    public DataWatcher getDataWatcher() {
        return dataWatcher;
    }

    @Override
    public GameEntity getPlayerEntity() {
        return gameEntity;
    }

    @Override
    public List<Chunk> chunksToRender() {
        //TODO do coolMath
        return null;
    }

    @Override
    public void update() {

    }

    @Override
    public void tickUpdate() {

    }
}
