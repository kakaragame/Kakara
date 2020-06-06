package org.kakara.client.game.world.io;

import com.google.common.collect.ArrayListMultimap;
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
            ChunkLocation chunkLocation = GameUtils.getChunkFileLocation(location);
            values.computeIfAbsent(chunkLocation, k -> new ArrayList<>());
            values.get(chunkLocation).add(location);
        }
        return values;
    }

    public static ArrayListMultimap<ChunkLocation, Chunk> sortByChunk(List<Chunk> chunks) {

        ArrayListMultimap<ChunkLocation, Chunk> values = ArrayListMultimap.create();
        for (Chunk chunk : chunks) {
            values.put(GameUtils.getChunkFileLocation(chunk.getLocation()), chunk);
        }
        return values;
    }
}
