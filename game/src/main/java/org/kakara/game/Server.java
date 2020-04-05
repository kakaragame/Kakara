package org.kakara.game;

import org.kakara.core.game.Entity;
import org.kakara.core.player.Player;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.GameEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;

public interface Server {

    DataWatcher getDataWatcher();

    Player getPlayerEntity();

    List<Chunk> chunksToRender();

    List<Player> getOnlinePlayers();

    /**
     * IDK when this will be invoked.
     */
    void update();

    /**
     * Local Servers will be invoked locally external servers will be invoked that way
     */
    void tickUpdate();

    ExecutorService getExecutorService();
}
