package org.kakara.client.game.gui;

import org.jetbrains.annotations.NotNull;
import org.kakara.core.game.ItemStack;
import org.kakara.core.gui.BoxedInventory;

import java.awt.*;

public abstract class AbstractBoxedInventory extends AbstractInventory implements BoxedInventory {
    public AbstractBoxedInventory(int capacity) {
        super(capacity);
        if ((capacity % rowSize()) != 0) {
            throw new RuntimeException("Capacity must be divisible by "+rowSize());
        }
    }

    abstract int rowSize();

    public static int pointToIndex(Point point, int rowSize) {
        return (point.x * rowSize) + point.y;
    }

    @Override
    public @NotNull ItemStack getItemStack(@NotNull Point point) {
        return getItemStack(pointToIndex(point, rowSize()));
    }

    @Override
    public void setItemStack(@NotNull ItemStack itemStack, @NotNull Point point) {
        setItemStack(itemStack, pointToIndex(point, rowSize()));
    }
}
