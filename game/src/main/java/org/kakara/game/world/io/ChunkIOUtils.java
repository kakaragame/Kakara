package org.kakara.game.world.io;

import com.google.common.collect.ArrayListMultimap;
import org.kakara.core.common.world.ChunkContent;
import org.kakara.core.common.world.ChunkLocation;
import org.kakara.game.GameUtils;

import java.util.List;

public class ChunkIOUtils {
    public static ArrayListMultimap<ChunkLocation, ChunkLocation> sort(List<ChunkLocation> locationList) {
        ArrayListMultimap<ChunkLocation, ChunkLocation> values = ArrayListMultimap.create();
        for (ChunkLocation location : locationList) {
            ChunkLocation chunkLocation = GameUtils.getChunkFileLocation(location);
            values.put(chunkLocation, location);
        }
        return values;
    }

    public static ArrayListMultimap<ChunkLocation, ChunkContent> sortByChunk(List<ChunkContent> chunks) {

        ArrayListMultimap<ChunkLocation, ChunkContent> values = ArrayListMultimap.create();
        for (ChunkContent chunk : chunks) {
            values.put(GameUtils.getChunkFileLocation(chunk.getLocation()), chunk);
        }
        return values;
    }
}
