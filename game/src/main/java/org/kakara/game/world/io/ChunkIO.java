package org.kakara.game.world.io;

import org.kakara.core.world.ChunkContent;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.ChunkWriter;
import org.kakara.game.world.GameWorld;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ChunkIO extends Thread {
    protected GameWorld gameWorld;
    protected ChunkWriter chunkWriter;

    public ChunkIO(GameWorld clientWorld, ChunkWriter chunkWriter) {
        super(clientWorld.getName() + "-io");
        this.chunkWriter = chunkWriter;
        this.gameWorld = clientWorld;
    }

    public ChunkWriter getChunkWriter() {
        return chunkWriter;
    }

    public abstract CompletableFuture<List<ChunkContent>> get(List<ChunkLocation> chunkLocations);

    public abstract CompletableFuture<List<ChunkLocation>> write(List<ChunkContent> chunkLocations);
}
