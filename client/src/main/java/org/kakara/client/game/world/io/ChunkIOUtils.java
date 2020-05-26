package org.kakara.client.game.world.io;

import org.kakara.core.world.Chunk;
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

    public static Map<ChunkLocation, List<Chunk>> sortByChunk(List<Chunk> chunks) {

        Map<ChunkLocation, List<Chunk>> values = new HashMap<>();
        for (Chunk chunk : chunks) {
            values.getOrDefault(GameUtils.getChunkFileLocation(chunk.getLocation()), new ArrayList<>()).add(chunk);
        }
        return values;
    }
}
