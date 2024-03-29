package org.kakara.client.local.game;

import org.kakara.client.ClientServerController;
import org.kakara.client.local.game.player.ClientPlayer;
import org.kakara.client.local.game.world.ClientWorld;
import org.kakara.core.common.Kakara;
import org.kakara.core.common.game.ItemStack;
import org.kakara.core.common.world.GameBlock;
import org.kakara.core.common.world.Location;
import org.kakara.core.server.ServerGameInstance;
import org.kakara.core.server.game.ServerItemStack;
import org.kakara.core.server.gui.ServerInventoryContainer;

import java.util.Optional;

//Call from server is {action}{What}
//Call from client is {what}{Action}

/**
 * This class controls the
 */
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
    public void blockPlace(Location location, ItemStack itemStack) {
        placeBlock(location, itemStack);
    }

    @Override
    public void messageSend(byte[] message) {
        //TODO call Message send Event

        String stringMessage = new String(message);
        if (stringMessage.startsWith("/")) {
            Kakara.getGameInstance().getCommandManager().executeCommand(stringMessage.substring(1), server.getPlayerEntity());
            return;
        }
        sendMessage(message);
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
    public void placeBlock(Location location, ItemStack itemStack) {
        for (int i = 0; i < server.getPlayerEntity().getInventory().getContainer().getContents().length; i++) {
            ItemStack stack = server.getPlayerEntity().getInventory().getContainer().getContents()[i];
            if (stack.equals(itemStack)) {
                ((ServerItemStack) stack).setCount(stack.getCount() - 1);
                // TODO :: This needs to be made more official. Something this long is kinda unacceptable.
                if(stack.getCount() < 1) {
                    ItemStack air = ((ServerGameInstance) Kakara.getGameInstance()).createItemStack(Kakara.getGameInstance().getItemRegistry().getItem(0));
                    ((ServerInventoryContainer) server.getPlayerEntity().getInventory().getContainer()).setItemStack(i, air);
                }
            }
        }

        // Redraw the Player's inventory.
        server.getPlayerEntity().getInventory().redraw();

        ((ClientWorld) location.getNullableWorld()).placeBlock(LocalUtils.copyItemStackButOnlyOneCount(itemStack), location);

    }

    @Override
    public void sendMessage(byte[] message) {
        server.getGameScene().getChatComponent().addMessage(new String(message));
    }

    @Override
    public void movePlayer(Location location) {
        ((ClientPlayer) server.getPlayerEntity()).setLocation(location);

    }
}
