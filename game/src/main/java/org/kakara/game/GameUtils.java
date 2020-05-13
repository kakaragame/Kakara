package org.kakara.game;


import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.Location;

public class GameUtils {
    public static ChunkLocation getChunkLocation(Location l) {
        int x = (int) (l.getX() - l.getX() % 16);
        int y = (int) (l.getY() - l.getY() % 16);
        int z = (int) (l.getZ() - l.getZ() % 16);

        return new ChunkLocation(l.getWorld().isPresent() ? l.getWorld().get() : null, x, y, z);
    }

    public static boolean isLocationInsideCurrentLocationRadius(ChunkLocation centerPoint, ChunkLocation location, int radius) {
        //(x-cx)^2 + (y-cy)^2 + (z-cz)^2 < r^2
        //so x-chunkX squared + x-chunkY squared + z- chunkZ squared is less than radius squared?
        return (Math.pow((centerPoint.getX() - location.getX()), 2) +
                Math.pow((centerPoint.getY() - location.getY()), 2) +
                Math.pow((centerPoint.getZ() - location.getZ()), 2)) <= Math.pow((radius * 16), 2);
    }

    public static ChunkLocation getChunkFileLocation(ChunkLocation location) {
        int x = (int) Math.floor(location.getX() / 64);
        int y = (int) Math.floor(location.getY() / 64);
        int z = (int) Math.floor(location.getZ() / 64);
        return new ChunkLocation(x, y, z);
    }
}

