package org.kakara.game.server.gui;

import org.kakara.core.common.gui.BoxedInventory;

import java.awt.*;

public abstract class AbstractBoxedInventory extends AbstractInventory implements BoxedInventory {
    public AbstractBoxedInventory(int capacity) {
        super(capacity);
        if ((capacity % rowSize()) != 0) {
            throw new RuntimeException("Capacity must be divisible by " + rowSize());
        }
    }

    public static int pointToIndex(Point point, int rowSize) {
        return (point.y - 1) * rowSize + point.x - 1;
    }

    public abstract int rowSize();


}
