package org.kakara.client.game;

import org.kakara.core.game.ItemStack;
import org.kakara.core.world.Location;

import java.util.Objects;
import java.util.UUID;

public class DroppedItem {
    private final Location location = new Location(0, 0, 0);
    private final ItemStack itemStack;
    private UUID gameID;

    public DroppedItem(Location location, ItemStack itemStack) {
        this.location.set(location);
        this.itemStack = itemStack;
    }

    public UUID getGameID() {
        return gameID;
    }

    public void setGameID(UUID gameID) {
        this.gameID = gameID;
    }

    public Location getLocation() {
        return location;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DroppedItem that = (DroppedItem) o;
        return Objects.equals(getLocation(), that.getLocation()) &&
                Objects.equals(getItemStack(), that.getItemStack()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocation(), getItemStack(), getGameID());
    }
}
