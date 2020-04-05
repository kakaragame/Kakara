package org.kakara.client;

import org.kakara.core.world.Chunk;

import java.util.UUID;

public class RenderedChunk {
    private UUID renderChunkID;
    private Chunk chunk;

    public RenderedChunk(UUID renderChunkID, Chunk chunk) {
        this.renderChunkID = renderChunkID;
        this.chunk = chunk;
    }

    public UUID getRenderChunkID() {
        return renderChunkID;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
