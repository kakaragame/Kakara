package org.kakara.client.game.player;


import org.kakara.core.common.gui.InventoryBuilder;

public class PlayerContentInventoryBuilder implements InventoryBuilder<PlayerContentInventory> {
    @Override
    public PlayerContentInventory build() {
        return new PlayerContentInventory();
    }
}
