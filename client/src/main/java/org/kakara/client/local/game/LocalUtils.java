package org.kakara.client.local.game;

import org.kakara.core.common.Kakara;
import org.kakara.core.common.game.ItemStack;
import org.kakara.core.server.ServerGameInstance;
import org.kakara.core.server.game.ServerItemStack;

public class LocalUtils {
    public static ItemStack copyItemStackButOnlyOneCount(ItemStack itemstack) {
        ServerItemStack serverItemStack = ((ServerGameInstance) Kakara.getGameInstance()).createItemStack(itemstack.getItem());
        serverItemStack.setCount(1);
        serverItemStack.setName(itemstack.getName());
        serverItemStack.setLore(itemstack.getLore());
        return serverItemStack;
    }

}
