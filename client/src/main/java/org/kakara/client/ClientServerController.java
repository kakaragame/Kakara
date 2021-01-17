package org.kakara.client;

import org.kakara.core.common.game.ItemStack;
import org.kakara.core.common.world.Location;
import org.kakara.game.ServerController;

public interface ClientServerController extends ServerController {
    /**
     * Called by the MainGameScene to mean tell the server to move the player
     *
     * @param location
     */
    void playerMove(Location location);

    /**
     * This will change.
     *
     * @param location
     */
    void blockBreak(Location location);

    void blockPlace(Location location, ItemStack itemStack);

    void messageSend(byte[] message);
}
