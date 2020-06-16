package org.kakara.client.game.gui;

import org.jetbrains.annotations.NotNull;
import org.kakara.client.MoreUtils;
import org.kakara.core.game.ItemStack;
import org.kakara.core.gui.Inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractInventory implements Inventory {
    private final List<ItemStack> contents;
    private final int capacity;

    public AbstractInventory(int capacity) {
        contents = MoreUtils.listWithAir(capacity);
        this.capacity = capacity;
    }

    @Override
    public @NotNull ItemStack getItemStack(int index) {
        if (index > capacity) {
            throw new IndexOutOfBoundsException("Max capacity of Inventory is " + capacity);
        }
        return contents.get(index);

    }

    @Override
    public int getSize() {
        return contents.size();
    }

    @Override
    public @NotNull ItemStack[] getContents() {
        return contents.toArray(ItemStack[]::new);
    }

    @Override
    public void setItemStack(@NotNull ItemStack itemStack, int index) {
        contents.set(index, itemStack);
    }

    public int getCapacity() {
        return capacity;
    }

    public List<ItemStack> getListContent() {
        return contents;
    }

    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return contents.iterator();
    }
}
