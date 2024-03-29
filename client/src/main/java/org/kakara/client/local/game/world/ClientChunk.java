package org.kakara.client.local.game.world;

import org.jetbrains.annotations.Nullable;
import org.kakara.client.KakaraGame;
import org.kakara.core.common.Kakara;
import org.kakara.core.common.Status;
import org.kakara.core.common.world.*;
import org.kakara.core.server.ServerGameInstance;
import org.kakara.core.server.game.ServerItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientChunk implements Chunk {
    private ChunkLocation location;
    private List<GameBlock> gameBlockList;
    private boolean updatedHappened = true;
    @Nullable
    private UUID voxel;
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
        ((ServerItemStack) gameBlock.getItemStack().getServerVersion()).setCount(1);
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
        List<GameBlock> loop = new ArrayList<>(gameBlockList);
        for (GameBlock gameBlock : loop) {
            if (gameBlock.getLocation().equals(location)) {
                return Optional.of(gameBlock);
            }
        }
        gameBlockList.add(new GameBlock(location, ((ServerGameInstance) Kakara.getGameInstance()).createItemStack(Kakara.getGameInstance().getItemRegistry().getItem(0))));
        return getGameBlock(location);
    }

    public Optional<UUID> getVoxelID() {
        return Optional.ofNullable(voxel);
    }

    public void setVoxelID(UUID renderChunkID) {
        this.voxel = renderChunkID;
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

    @Override
    public int getPercent() {
        return 100;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void load(ChunkContent base) {
        gameBlockList = base.getGameBlocks();
        gameBlockList.forEach(block -> ((ServerItemStack) block.getItemStack().getServerVersion()).setCount(1));
        status = Status.LOADED;
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
