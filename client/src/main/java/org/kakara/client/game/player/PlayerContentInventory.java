package org.kakara.client.game.player;

import org.kakara.client.KakaraGame;
import org.kakara.core.game.ItemStack;
import org.kakara.core.gui.EngineInventoryRenderer;
import org.kakara.core.gui.InventoryRenderer;
import org.kakara.core.gui.annotations.BuilderClass;
import org.kakara.core.gui.bnbi.BasicNineBoxedInventory;

@BuilderClass(PlayerContentInventoryBuilder.class)
public class PlayerContentInventory extends BasicNineBoxedInventory {
    private int hotbarSize = 5;
    private EngineInventoryRenderer engineInventoryRenderer;

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

    public void addItemStackForPickup(ItemStack itemStack) {
        for (int i = 0; i < getContents().length; i++) {
            if (getItemStack(i).equalsIgnoreCount(itemStack)) {
                System.out.println("itemStack.getCount() = " + itemStack.getCount());
                getItemStack(i).setCount(getItemStack(i).getCount() + itemStack.getCount());
                return;
            }
        }
        KakaraGame.LOGGER.debug("Adding itemstack");
        addItemStack(itemStack);
    }

    @Override
    public InventoryRenderer getRenderer() {
        return engineInventoryRenderer;
    }
}
