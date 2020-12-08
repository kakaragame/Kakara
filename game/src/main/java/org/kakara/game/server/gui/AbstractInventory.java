package org.kakara.game.server.gui;

import org.jetbrains.annotations.NotNull;
import org.kakara.core.common.Kakara;
import org.kakara.core.common.game.ItemStack;
import org.kakara.core.common.gui.Inventory;
import org.kakara.core.common.gui.container.InventoryContainer;
import org.kakara.core.server.game.ServerItemStack;

import java.util.Iterator;

public abstract class AbstractInventory implements Inventory {
    protected final InventoryContainer container;
    private final int capacity;

    public AbstractInventory(int capacity) {
        container = Kakara.getGameInstance().getContainerUtils().createInventoryContainer(capacity);
        this.capacity = capacity;
    }

    public void addItemStack(@NotNull ItemStack itemStack) {
        ServerItemStack serverItemStack = (ServerItemStack) itemStack;

        redraw();
    }


    @Override
    public int getSize() {
        return capacity;
    }


    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return container.iterator();
    }
}
