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
    public static ArrayListMultimap<ChunkLocation, ChunkLocation> sort(List<ChunkLocation> locationList) {
        ArrayListMultimap<ChunkLocation, ChunkLocation> values = ArrayListMultimap.create();
        for (ChunkLocation location : locationList) {
            ChunkLocation chunkLocation = GameUtils.getChunkFileLocation(location);
            values.put(chunkLocation, location);
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
