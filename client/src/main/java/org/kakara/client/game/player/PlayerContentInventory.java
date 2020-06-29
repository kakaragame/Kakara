package org.kakara.client.game.player;

import org.kakara.core.game.ItemStack;
import org.kakara.core.gui.annotations.BuilderClass;
import org.kakara.core.gui.bnbi.BasicNineBoxedInventory;

@BuilderClass(PlayerContentInventoryBuilder.class)
public class PlayerContentInventory extends BasicNineBoxedInventory {
    private int hotbarSize = 5;

    public PlayerContentInventory() {
        super(36);
    }

    public ItemStack[] getHotBarContents() {
        ItemStack[] itemStacks = new ItemStack[hotbarSize];
        for (int i = 0; i < hotbarSize; i++) {
            itemStacks[i] = getContents()[i];
        }
        return itemStacks;
    }
}
