package org.kakara.client.game.world;

import org.jetbrains.annotations.Nullable;
import org.kakara.core.world.*;

import java.util.ArrayList;
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

    public void setGameBlock(GameBlock gameBlock) {
        List<GameBlock> loop = new ArrayList<>(gameBlockList);
        for (int i = 0; i < loop.size(); i++) {
            if (loop.get(i).getLocation().equals(gameBlock.getLocation())) {
                gameBlockList.set(i, gameBlock);
                return;
            }
        }
        gameBlockList.add(gameBlock);
    }

    public Optional<GameBlock> getGameBlock(Location location) {
        return new ArrayList<>(gameBlockList).stream().filter(gameBlock -> gameBlock.getLocation().equals(location)).findFirst();
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
