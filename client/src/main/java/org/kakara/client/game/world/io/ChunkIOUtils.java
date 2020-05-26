package org.kakara.client.game.world.io;

import org.kakara.core.world.ChunkLocation;
import org.kakara.game.GameUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkIOUtils {
    public static Map<ChunkLocation, List<ChunkLocation>> sort(List<ChunkLocation> locationList) {
        Map<ChunkLocation, List<ChunkLocation>> values = new HashMap<>();
        for (ChunkLocation location : locationList) {
            values.getOrDefault(GameUtils.getChunkFileLocation(location), new ArrayList<>()).add(location);
        }
        return values;
    }
}
