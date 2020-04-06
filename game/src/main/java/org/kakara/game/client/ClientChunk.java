package org.kakara.game.client;

import org.kakara.core.world.*;

import java.util.List;

public class ClientChunk implements Chunk {
    private ChunkLocation location;
    private List<GameBlock> gameBlockList;
    private boolean updatedHappened = true;

    public ClientChunk(ChunkBase base) {
        location = new ChunkLocation(base.getX(), base.getY(), base.getZ());
        gameBlockList = base.getGameBlocks();
    }

    public ClientChunk(ChunkLocation chunkLocation, List<GameBlock> gameBlocks) {
        location = chunkLocation;
        gameBlockList = gameBlocks;
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
