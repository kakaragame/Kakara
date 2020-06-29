package org.kakara.client.game.player;

import org.kakara.core.gui.InventoryBuilder;

public class PlayerContentInventoryBuilder implements InventoryBuilder<PlayerContentInventory> {
    @Override
    public PlayerContentInventory build() {
        return new PlayerContentInventory();
    }
}
