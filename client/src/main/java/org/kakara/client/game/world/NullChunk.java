package org.kakara.client.game.world;

import org.jetbrains.annotations.NotNull;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.GameBlock;

import java.util.Collections;
import java.util.List;

public class NullChunk implements Chunk {
    private ChunkLocation chunkLocation;
    public NullChunk(ChunkLocation chunkLocation) {
    this.chunkLocation = chunkLocation;
    }

    @Override
    public @NotNull List<GameBlock> getGameBlocks() {
        return Collections.emptyList();
    }

    @Override
    public @NotNull ChunkLocation getLocation() {
        return chunkLocation;
    }
}
