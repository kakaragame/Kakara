package org.kakara.game;

import org.kakara.core.ManagedObject;
import org.kakara.core.player.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public interface Server extends ManagedObject {


    void loadPlayer(UUID uuid);


    Player getPlayerEntity();


    List<Player> getOnlinePlayers();

    void loadMods();

    /**
     * IDK when this will be invoked.
     */
    void update();

    /**
     * Local Servers will be invoked locally external servers will be invoked that way
     */
    void tickUpdate();

    ExecutorService getExecutorService();

    boolean isRunning();

    void close();

    void renderMessageToConsole(String message);

    void errorClose(Exception e);
}
