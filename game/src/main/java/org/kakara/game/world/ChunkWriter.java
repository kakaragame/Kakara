package org.kakara.game.world;

import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;

import java.util.List;

public interface ChunkWriter {
    /**
     * gets a single chunk
     *
     * @param chunkLocation the location
     * @return the single chunk null if not found
     */
    Chunk getChunkByLocation(ChunkLocation chunkLocation);

    /**
     * Gets a group of chunks. This will sort the locations so we dont have to open a file multiple times
     *
     * @param locations the locations to find
     * @return the chunks no value if not found
     */
    List<Chunk> getChunksByLocation(List<ChunkLocation> locations);

    /**
     * writes a single chunk to its correct file
     *
     * @param chunk the chunk
     */
    void writeChunk(Chunk chunk);

    /**
     * Writes a group of chunks. This will sort the locations so we dont have to open a file multiple times
     *
     * @param chunks the chunks to save
     */
    void writeChunks(List<Chunk> chunks);
}
