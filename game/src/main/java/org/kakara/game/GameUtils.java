package org.kakara.game;


import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.Location;

public class GameUtils {
    public static ChunkLocation getChunkLocation(Location l) {
        int x = (int) (l.getX() - l.getX() % 16);
        int y = (int) (l.getY() - l.getY() % 16);
        int z = (int) (l.getZ() - l.getZ() % 16);

        return new ChunkLocation(x, y, z, l.getWorld());
    }
}
