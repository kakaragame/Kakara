package org.kakara.game.server.gui;

import org.jetbrains.annotations.NotNull;
import org.kakara.core.common.game.ItemStack;
import org.kakara.core.common.gui.Inventory;
import org.kakara.core.server.game.ServerItemStack;
import org.kakara.core.server.gui.ServerInventoryContainer;

import java.util.Iterator;

public abstract class AbstractInventory implements Inventory {
    private final int capacity;

    public AbstractInventory(int capacity) {
        this.capacity = capacity;
    }

    public void addItemStack(@NotNull ItemStack itemStack) {
        ServerItemStack serverItemStack = (ServerItemStack) itemStack;
        ((ServerInventoryContainer) getContainer()).addItemStack(serverItemStack);
        redraw();
    }


    @Override
    public int getSize() {
        return capacity;
    }


    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return getContainer().iterator();
    }
}
