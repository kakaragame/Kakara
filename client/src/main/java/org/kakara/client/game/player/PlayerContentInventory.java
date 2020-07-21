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

    public boolean addItemStackForPickup(ItemStack itemStack) {
        for (int i = 0; i < getContents().length; i++) {
            if (getItemStack(i).equalsIgnoreCount(itemStack)) {
                getItemStack(i).setCount(getItemStack(i).getCount() + itemStack.getCount());
                return false;

            }
        }
        System.out.println("Calling");
        addItemStack(itemStack);
        return true;
    }
}
