package org.kakara.client.game.world;

import org.jetbrains.annotations.Nullable;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkBase;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.GameBlock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientChunk implements Chunk {
    private ChunkLocation location;
    private List<GameBlock> gameBlockList;
    private boolean updatedHappened = true;
    @Nullable
    private UUID renderChunkID;

    public ClientChunk(ChunkBase base) {
        location = new ChunkLocation(base.getX(), base.getY(), base.getZ());
        gameBlockList = base.getGameBlocks();
    }

    public ClientChunk(ChunkLocation chunkLocation, List<GameBlock> gameBlocks) {
        location = chunkLocation;
        gameBlockList = gameBlocks;
    }

    public Optional<UUID> getRenderChunkID() {
        return Optional.ofNullable(renderChunkID);
    }

    public void setRenderChunkID(UUID renderChunkID) {
        this.renderChunkID = renderChunkID;
    }

    @Override
    public List<GameBlock> getGameBlocks() {
        return gameBlockList;
    }

    @Override
    public ChunkLocation getLocation() {
        return location;
    }

    public boolean isUpdatedHappened() {
        return updatedHappened;
    }

    public void setUpdatedHappened(boolean updatedHappened) {
        this.updatedHappened = updatedHappened;
    }
}
