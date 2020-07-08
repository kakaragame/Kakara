package org.kakara.client.game.world.io;

import org.apache.commons.lang3.tuple.Pair;
import org.kakara.client.game.world.ClientWorld;
import org.kakara.core.world.ChunkContent;
import org.kakara.core.world.ChunkLocation;
import org.kakara.game.world.ChunkWriter;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;

public class DefaultChunkIO extends ChunkIO {
    private BlockingQueue<ChunkRequest> requests;
    private ChunkWriter chunkWriter;

    public DefaultChunkIO(ClientWorld clientWorld, ChunkWriter chunkWriter) {
        super(clientWorld);
        this.chunkWriter = chunkWriter;
        requests = new LinkedBlockingDeque<>();
        start();
    }

    @Override
    public void run() {
        while (clientWorld.isLoaded()) {

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
}
