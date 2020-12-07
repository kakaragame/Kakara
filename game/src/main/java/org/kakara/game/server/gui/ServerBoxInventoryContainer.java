package org.kakara.game.server.gui;

import org.jetbrains.annotations.NotNull;
import org.kakara.core.common.Serverable;
import org.kakara.core.common.exceptions.NoServerVersionAvailableException;
import org.kakara.core.common.game.ItemStack;
import org.kakara.core.server.game.ServerItemStack;
import org.kakara.core.server.gui.ServerBoxedInventoryContainer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerBoxInventoryContainer implements ServerBoxedInventoryContainer {
    private final List<ServerItemStack> items = new ArrayList<>();


    @Override
    public @NotNull ItemStack getItemStack(@NotNull Point point) {
        return null;
    }

    @Override
    public @NotNull ItemStack getItemStack(int i) {
        return null;
    }

    @Override
    public @NotNull ItemStack[] getContents() {
        return items.toArray(new ServerItemStack[0]);
    }

    @Override
    public int getNextEmtpySlot() {
        return 0;
    }

    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return items.iterator().hasNext();
            }

            @Override
            public ServerItemStack next() {
                return items.iterator().next();
            }
        };
    }


    @Override
    public void setItemStack(@NotNull ItemStack itemStack, @NotNull Point point) {

    }

    @Override
    public void setItemStack(int i, ItemStack itemStack) {

    }

    @Override
    public void addItemStack(ItemStack itemStack) {
        items.add((ServerItemStack) itemStack);
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
        return null;
    }

    @Override
    public void requiresServerVersion() throws NoServerVersionAvailableException {

    }
}
