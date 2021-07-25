package org.kakara.client.scenes;

import org.kakara.core.common.world.Location;
import org.kakara.engine.math.Vector3;

import java.util.Objects;

/**
 * Stores information about a block that is being broken.
 */
public class BreakingBlock {
    private final Location gbLocation = new Location(0, 0, 0);
    private final Vector3 chunkLocation;
    private final Vector3 blockLocation;
    private double percentage = 0.00;

    /**
     * Create a breaking block.
     *
     * @param gbLocation     The location of the game block.
     * @param chunkLocation  The location of the chunk.
     * @param blockLocation1 THe location of the block in the chunk.
     */
    public BreakingBlock(Location gbLocation, Vector3 chunkLocation, Vector3 blockLocation1) {
        this.gbLocation.set(gbLocation);
        this.chunkLocation = chunkLocation.clone();
        this.blockLocation = blockLocation1.clone();
    }

    /**
     * Break a block by an amount.
     *
     * @param amount The amount to break the block by. (This will be added to the percentage)
     * @return If the block is broken by the call of this method.
     */
    public boolean breakBlock(double amount) {
        percentage += amount;
        return (percentage >= 1);
    }

    /**
     * Get the location of the Chunk (Engine).
     *
     * @return The location of the Chunk (Engine).
     */
    public Vector3 getChunkLocation() {
        return chunkLocation;
    }

    /**
     * Get the location of the block within the Render Chunk.
     *
     * @return The location of the block within the Render Chunk.
     */
    public Vector3 getBlockLocation() {
        return blockLocation;
    }

    /**
     * Get the location of the game block.
     *
     * @return The location of the game block.
     */
    public Location getGbLocation() {
        return gbLocation;
    }

    /**
     * Get the percentage that the block is broken.
     *
     * @return The percentage that the block is broken (0 - 1).
     */
    public double getPercentage() {
        return percentage;
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
}
