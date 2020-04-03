package org.kakara.game;


import org.kakara.core.world.Location;

public class GameUtils {
    public static Location getChunkLocation(Location l) {
        double x = l.getX() - l.getX() % 16;
        double y = l.getY() - l.getY() % 16;
        double z = l.getZ() - l.getZ() % 16;

        return new Location(l.getWorld(), x, y, z);
    }
}
