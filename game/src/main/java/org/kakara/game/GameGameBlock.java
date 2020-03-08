package org.kakara.game;

import org.kakara.core.game.ItemStack;
import org.kakara.core.world.GameBlock;
import org.kakara.core.world.Location;

public class GameGameBlock implements GameBlock {
    private Location location;
    private ItemStack itemStack;

    public GameGameBlock(Location location, ItemStack itemStack) {
        this.location = location;
        this.itemStack = itemStack;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }
}
