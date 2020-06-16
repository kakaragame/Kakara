package org.kakara.client.game.player;

import org.kakara.client.game.gui.BasicNineBoxedInventory;
import org.kakara.core.game.ItemStack;

public class PlayerContentInventory extends BasicNineBoxedInventory {
    private int hotbarSize = 5;

    public PlayerContentInventory(int capacity) {
        super(capacity);
    }

    public ItemStack[] getHotBarContents() {
        ItemStack[] itemStacks = new ItemStack[hotbarSize];
        for (int i = 0; i < hotbarSize; i++) {
            itemStacks[i] = getListContent().get(0);
        }
        return itemStacks;
    }
}
