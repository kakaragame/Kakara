package org.kakara.game.server.gui;

import org.apache.commons.collections4.iterators.ArrayIterator;
import org.jetbrains.annotations.NotNull;
import org.kakara.core.common.Serverable;
import org.kakara.core.common.exceptions.NoServerVersionAvailableException;
import org.kakara.core.common.game.ItemStack;
import org.kakara.core.server.game.ServerItemStack;
import org.kakara.core.server.gui.InventoryUtils;
import org.kakara.core.server.gui.ServerBoxedInventoryContainer;
import org.kakara.game.items.blocks.AirBlock;

import java.awt.*;
import java.util.Iterator;

public class ServerBoxInventoryContainer implements ServerBoxedInventoryContainer {
    private final ServerItemStack[] serverItemStacks;

    public ServerBoxInventoryContainer(int i) {
        serverItemStacks = InventoryUtils.arrayWithAir(i);

    }


    @Override
    public @NotNull ItemStack getItemStack(@NotNull Point point) {
        return null;
    }

    @Override
    public @NotNull ItemStack getItemStack(int i) {
        return serverItemStacks[i];
    }

    @Override
    public @NotNull ItemStack[] getContents() {
        return serverItemStacks;
    }

    @Override
    public int getNextEmtpySlot() {
        for (int i = 0; i < serverItemStacks.length; i++) {
            if (serverItemStacks[i].getItem() instanceof AirBlock) {
                return i;
            }
        }
        return serverItemStacks.length + 1;
    }

    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return new ArrayIterator<>(serverItemStacks);
    }


    @Override
    public void setItemStack(@NotNull ItemStack itemStack, @NotNull Point point) {

    }

    @Override
    public void setItemStack(int i, ItemStack itemStack) {
        serverItemStacks[i] = (ServerItemStack) itemStack;
    }

    @Override
    public void addItemStack(ItemStack itemStack) {
        //TODO combine ItemStacks if possible
        for (ServerItemStack serverItemStack : serverItemStacks) {
            if (serverItemStack.equalsIgnoreCount(itemStack)) {
                serverItemStack.setCount(serverItemStack.getCount() + 1);
                return;
            }
        }
        if (getNextEmtpySlot() > serverItemStacks.length) {
            return;
        }
        setItemStack(getNextEmtpySlot(), itemStack);
    }

    @Override
    public void deleteItemStack(int i) {

    }

    @Override
    public boolean isServerVersionAvailable() {
        return true;
    }

    @Override
    public <T extends Serverable> T getServerVersion() {
        return (T) this;
    }

    @Override
    public void requiresServerVersion() throws NoServerVersionAvailableException {

    }
}
