package org.kakara.client.scenes;

import org.kakara.core.world.Location;
import org.kakara.engine.math.Vector3;

import java.util.Objects;

public class BreakingBlock {
    private final Location gbLocation = new Location(0, 0, 0);
    private final Vector3 chunkLocation;
    private final Vector3 blockLocation;
    private double percentage = 0.00;

    public BreakingBlock(Location gbLocation, Vector3 chunkLocation, Vector3 blockLocation1) {
        this.gbLocation.set(gbLocation);
        this.chunkLocation = chunkLocation.clone();
        this.blockLocation = blockLocation1.clone();
    }

    public boolean breakBlock(double amount) {
        percentage += amount;
        return (percentage >= 1);
    }

    public Vector3 getChunkLocation() {
        return chunkLocation;
    }

    public Vector3 getBlockLocation() {
        return blockLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BreakingBlock that = (BreakingBlock) o;
        return Objects.equals(getGbLocation(), that.getGbLocation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGbLocation());
    }

    public Location getGbLocation() {
        return gbLocation;
    }

    public double getPercentage() {
        return percentage;
    }
}
