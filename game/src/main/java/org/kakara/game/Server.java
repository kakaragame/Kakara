package org.kakara.game;


import org.kakara.core.common.Loadable;
import org.kakara.core.common.player.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public interface Server extends Loadable {


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


    void errorClose(Exception e);

    ServerController getServerController();
}
