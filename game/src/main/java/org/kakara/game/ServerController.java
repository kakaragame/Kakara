package org.kakara.game;

import org.kakara.core.common.game.ItemStack;
import org.kakara.core.common.world.Location;

public interface ServerController {
    /**
     * Called upon a packet to change the players location
     *
     * @param location
     */
    void movePlayer(Location location);

    void breakBlock(Location location);

    void placeBlock(Location location, ItemStack itemStack);

    /**
     * Called when a new message is received from the server. To be rendered
     *
     * @param message A byte array of the message
     */
    void sendMessage(byte[] message);

}
