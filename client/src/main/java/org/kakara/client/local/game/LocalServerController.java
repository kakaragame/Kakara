package org.kakara.client.local.game;

import org.kakara.client.ClientServerController;
import org.kakara.client.local.game.player.ClientPlayer;
import org.kakara.client.local.game.world.ClientWorld;
import org.kakara.core.common.world.GameBlock;
import org.kakara.core.common.world.Location;
import org.kakara.core.server.game.ServerItemStack;

import java.util.Optional;

//Call from server is {action}{What}
//Call from client is {what}{Action}
public class LocalServerController implements ClientServerController {
    private final IntegratedServer server;

    public LocalServerController(IntegratedServer server) {
        this.server = server;
    }

    @Override
    public void playerMove(Location location) {
        movePlayer(location);
    }

    @Override
    public void blockBreak(Location location) {
        breakBlock(location);
    }

    @Override
    public void breakBlock(Location location) {
        Optional<GameBlock> blockAt = server.getPlayerEntity().getLocation().getNullableWorld().getBlockAt(location);
        blockAt.ifPresent(block -> {
            ((ServerItemStack) block.getItemStack()).setCount(1);
            ((ClientWorld) server.getPlayerEntity().getLocation().getNullableWorld()).dropItem(block.getLocation(), block.getItemStack());

        });
    }

    @Override
    public void movePlayer(Location location) {
        ((ClientPlayer) server.getPlayerEntity()).setLocation(location);

    }
}
