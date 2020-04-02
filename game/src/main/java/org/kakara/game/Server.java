package org.kakara.game;

import org.kakara.core.game.Entity;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.GameEntity;

import java.util.List;

public interface Server {

    DataWatcher getDataWatcher();

    GameEntity getPlayerEntity();

    List<Chunk> chunksToRender();

    /**
     * IDK when this will be invoked.
     */
    void update();

    /**
     * Local Servers will be invoked locally external servers will be invoked that way
     *
     */
    void tickUpdate();
}
