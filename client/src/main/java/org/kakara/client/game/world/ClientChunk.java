package org.kakara.client.game.world;

import org.jetbrains.annotations.Nullable;
import org.kakara.client.KakaraGame;
import org.kakara.core.Status;
import org.kakara.core.world.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class ClientChunk implements Chunk {
    private ChunkLocation location;
    private List<GameBlock> gameBlockList;
    private boolean updatedHappened = true;
    @Nullable
    private UUID renderChunkID;
    private Status status = Status.UNLOADED;

    public ClientChunk(ChunkLocation location) {
        this.location = location;
    }


    public ClientChunk(ChunkLocation chunkLocation, List<GameBlock> gameBlocks) {
        location = chunkLocation;
        gameBlockList = gameBlocks;
    }

    public void setGameBlock(GameBlock gameBlock) {
        placeBlock(gameBlock);
        updatedHappened = true;

    }

    public void placeBlock(GameBlock gameBlock) {
        List<GameBlock> loop = new ArrayList<>(gameBlockList);
        for (int i = 0; i < loop.size(); i++) {
            if (loop.get(i).getLocation().equals(gameBlock.getLocation())) {
                gameBlockList.set(i, gameBlock);
                return;
            }
        }
        gameBlockList.add(gameBlock);
    }

    public World getWorld() {
        return location.getNullableWorld();
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

    @Override
    public Status getStatus() {
        return status;
    }

    public void load(ChunkContent base) {
        gameBlockList = base.getGameBlocks();
        status = Status.LOADED;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isUpdatedHappened() {
        return updatedHappened;
    }

    public void setUpdatedHappened(boolean updatedHappened) {
        this.updatedHappened = updatedHappened;
    }

    public ChunkContent getContents() {
        return new ChunkContent(gameBlockList, location);
    }


}
