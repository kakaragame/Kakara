package org.kakara.game.server.gui;

import org.jetbrains.annotations.NotNull;
import org.kakara.core.common.game.ItemStack;
import org.kakara.core.common.gui.Inventory;
import org.kakara.core.server.game.ServerItemStack;
import org.kakara.core.server.gui.InventoryUtils;

import java.util.Iterator;
import java.util.List;

public abstract class AbstractInventory implements Inventory {
    protected final List<ItemStack> contents;
    private final int capacity;

    public AbstractInventory(int capacity) {
        contents = InventoryUtils.listWithAir(capacity);
        this.capacity = capacity;
    }

    public void addItemStack(@NotNull ItemStack itemStack) {
        ServerItemStack serverItemStack = (ServerItemStack) itemStack;
        for (int i = 0; i < contents.size(); i++) {
            if (contents.get(i).equalsIgnoreCount(itemStack)) {
                ((ServerItemStack) contents.get(i)).setCount(contents.get(i).getCount() + itemStack.getCount());
                break;
            } else if (contents.get(i).getItem().getId() == 0) {
                contents.set(i, serverItemStack);
                break;
            }
        }
        redraw();
    }


    @Override
    public int getSize() {
        return contents.size();
    }


    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return contents.iterator();
    }
}
