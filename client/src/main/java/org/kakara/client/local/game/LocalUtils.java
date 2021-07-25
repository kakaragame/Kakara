package org.kakara.client.local.game;

import org.kakara.core.common.Kakara;
import org.kakara.core.common.game.ItemStack;
import org.kakara.core.server.ServerGameInstance;
import org.kakara.core.server.game.ServerItemStack;

/**
 * Utilities for the local game.
 */
public final class LocalUtils {

    private LocalUtils() {
    }

    /**
     * Copy an ItemStack with a single item.
     *
     * @param itemstack The item stack to copy.
     * @return The copy of the item stack with only a single item.
     */
    public static ItemStack copyItemStackButOnlyOneCount(ItemStack itemstack) {
        ServerItemStack serverItemStack = ((ServerGameInstance) Kakara.getGameInstance()).createItemStack(itemstack.getItem());
        serverItemStack.setCount(1);
        serverItemStack.setName(itemstack.getName());
        serverItemStack.setLore(itemstack.getLore());
        return serverItemStack;
    }

}
