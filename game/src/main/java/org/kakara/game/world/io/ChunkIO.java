package org.kakara.game.world.io;


import org.kakara.core.common.LoadableObject;
import org.kakara.core.common.Status;
import org.kakara.core.common.world.*;
import org.kakara.game.world.GameWorld;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ChunkIO extends Thread implements LoadableObject {
    protected GameWorld gameWorld;
    protected ChunkWriter chunkWriter;
    protected Status status = Status.LOADED;

    public ChunkIO(GameWorld clientWorld, ChunkWriter chunkWriter) {
        super(clientWorld.getName() + "-io");
        this.chunkWriter = chunkWriter;
        this.gameWorld = clientWorld;
    }

    public boolean supportsSingleBlockWrites() {
        return chunkWriter.supportsSingleBlockWrites();
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public ChunkWriter getChunkWriter() {
        return chunkWriter;
    }

    public abstract CompletableFuture<List<ChunkContent>> get(List<ChunkLocation> chunkLocations);

    public abstract CompletableFuture<List<ChunkLocation>> write(List<ChunkContent> chunkLocations);

    public abstract CompletableFuture<List<Location>> writeBlock(List<GameBlock> chunkLocations);

    public abstract void close();
}
