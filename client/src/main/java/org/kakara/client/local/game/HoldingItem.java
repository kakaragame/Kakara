package org.kakara.client.local.game;


import org.kakara.core.common.game.ItemStack;

public class HoldingItem {
    private ItemStack itemStack;
    private int ogSlot;

    public HoldingItem(ItemStack itemStack, int ogSlot) {
        this.itemStack = itemStack;
        this.ogSlot = ogSlot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public int getOgSlot() {
        return ogSlot;
    }

    public void setOgSlot(int ogSlot) {
        this.ogSlot = ogSlot;
    }
}
