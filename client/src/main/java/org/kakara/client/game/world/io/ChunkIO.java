package org.kakara.client.game.world.io;

import org.kakara.client.game.world.ClientWorld;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkContent;
import org.kakara.core.world.ChunkLocation;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ChunkIO extends Thread {
    protected ClientWorld clientWorld;

    public ChunkIO(ClientWorld clientWorld) {
        super(clientWorld.getName() + "-io");
        this.clientWorld = clientWorld;
    }

    public abstract CompletableFuture<List<ChunkContent>> get(List<ChunkLocation> chunkLocations);

    public abstract CompletableFuture<List<ChunkLocation>> write(List<ChunkContent> chunkLocations);
}
