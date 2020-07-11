package org.kakara.client.scenes;

import org.kakara.core.world.Location;

public class BreakingBlock {
    private final Location blockLocation;
    private double percentage = 0.00;

    public BreakingBlock(Location blockLocation) {
        this.blockLocation = blockLocation;
    }

    public boolean breakBlock(double amount) {
        percentage += amount;
        return (percentage >= 1);
    }

    public Location getBlockLocation() {
        return blockLocation;
    }

    public double getPercentage() {
        return percentage;
    }
}
