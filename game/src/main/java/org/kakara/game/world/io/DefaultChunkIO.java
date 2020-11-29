package org.kakara.game.world.io;

import org.kakara.core.common.world.ChunkContent;
import org.kakara.core.common.world.ChunkLocation;
import org.kakara.core.common.world.ChunkWriter;
import org.kakara.game.world.GameWorld;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;

public class DefaultChunkIO extends ChunkIO {
    private final BlockingQueue<ChunkRequest> requests;

    public DefaultChunkIO(GameWorld gameWorld, ChunkWriter chunkWriter) {
        super(gameWorld, chunkWriter);
        requests = new LinkedBlockingDeque<>();
        start();
    }

    @Override
    public void run() {
        while (gameWorld.isLoaded()) {

        }
    }

    @Override
    public CompletableFuture<List<ChunkContent>> get(List<ChunkLocation> chunkLocations) {
        CompletableFuture<List<ChunkContent>> completableFuture = new CompletableFuture<>();
        ReadChunkRequest readChunkRequest = new ReadChunkRequest(chunkLocations, completableFuture);
        requests.add(readChunkRequest);
        return completableFuture;
    }

    @Override
    public CompletableFuture<List<ChunkLocation>> write(List<ChunkContent> contents) {
        CompletableFuture<List<ChunkLocation>> completableFuture = new CompletableFuture<>();
        WriteChunkRequest readChunkRequest = new WriteChunkRequest(contents, completableFuture);
        requests.add(readChunkRequest);
        return completableFuture;
    }

    @Override
    public void close() {

    }
}
