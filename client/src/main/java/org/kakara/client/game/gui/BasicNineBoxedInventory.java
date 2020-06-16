package org.kakara.client.game.gui;

public class BasicNineBoxedInventory extends AbstractBoxedInventory {
    public BasicNineBoxedInventory(int capacity) {
        super(capacity);
    }

    @Override
    int rowSize() {
        return 9;
    }
}
