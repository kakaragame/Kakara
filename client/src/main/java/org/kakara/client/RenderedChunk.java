package org.kakara.client;

import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;

import java.util.UUID;

public class RenderedChunk {
    private UUID renderChunkID;
    private ChunkLocation chunk;

    public RenderedChunk(UUID renderChunkID, ChunkLocation chunk) {
        this.renderChunkID = renderChunkID;
        this.chunk = chunk;
    }

    public UUID getRenderChunkID() {
        return renderChunkID;
    }

    public ChunkLocation getChunk() {
        return chunk;
    }
}
