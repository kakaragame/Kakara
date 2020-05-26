package org.kakara.client.game.world.io;

import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.game.GameUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class WriteChunkRequest implements ChunkRequest {
    private final List<Chunk> chunks;
    private final CompletableFuture<List<ChunkLocation>> completableFuture;
    private final List<ChunkLocation> response = new ArrayList<>();

    public WriteChunkRequest(List<Chunk> chunkLocation, CompletableFuture<List<ChunkLocation>> completableFuture) {
        this.chunks = chunkLocation;
        this.completableFuture = completableFuture;
    }

    public List<Chunk> getChunks() {
        return chunks;
    }

    public CompletableFuture<List<ChunkLocation>> getCompletableFuture() {
        return completableFuture;
    }

    public void respond(ChunkLocation chunkLocation) {
        response.add(chunkLocation);
        if (needsToRespond()) respond();
    }

    public boolean needsToRespond() {
        Set<ChunkLocation> groupedChunkLocations = new HashSet<>();
        for (Chunk chunk : this.chunks) {
            groupedChunkLocations.add(GameUtils.getChunkFileLocation(chunk.getLocation()));
        }
        return response.size() == groupedChunkLocations.size();
    }

    public void respond() {

        completableFuture.complete(chunks.stream().map(Chunk::getLocation).collect(Collectors.toList()));
    }

}
