package org.kakara.game.world.io;

import org.kakara.core.world.ChunkContent;
import org.kakara.core.world.ChunkLocation;
import org.kakara.game.world.GameWorld;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ChunkIO extends Thread {
    protected GameWorld gameWorld;

    public ChunkIO(GameWorld clientWorld) {
        super(clientWorld.getName() + "-io");
        this.gameWorld = clientWorld;
    }

    public abstract CompletableFuture<List<ChunkContent>> get(List<ChunkLocation> chunkLocations);

    public abstract CompletableFuture<List<ChunkLocation>> write(List<ChunkContent> chunkLocations);
}
