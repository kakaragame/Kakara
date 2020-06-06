package org.kakara.client.game.world.io;

import org.kakara.client.game.world.NullChunk;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.game.GameUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ReadChunkRequest implements ChunkRequest {
    private final List<ChunkLocation> chunkLocations;
    private final CompletableFuture<List<Chunk>> completableFuture;
    private final Map<ChunkLocation, List<Chunk>> response = new HashMap<>();

    public ReadChunkRequest(List<ChunkLocation> chunkLocation, CompletableFuture<List<Chunk>> completableFuture) {
        this.chunkLocations = chunkLocation;
        this.completableFuture = completableFuture;
    }

    public void respond(ChunkLocation chunkLocation, List<Chunk> chunks) {
        response.put(chunkLocation, chunks);
        if (needsToRespond()) respond();
    }

    public boolean needsToRespond() {
        Set<ChunkLocation> groupedChunkLocations = new HashSet<>();
        for (ChunkLocation chunkLocation : this.chunkLocations) {
            groupedChunkLocations.add(GameUtils.getChunkFileLocation(chunkLocation));
        }

        return response.keySet().size() == groupedChunkLocations.size();
    }

    public void respond() {
        List<Chunk> chunks = new ArrayList<>();
        for (List<Chunk> chunkList : response.values()) {
            chunkList.forEach(chunk -> {
                if (!(chunk instanceof NullChunk)) {
                    chunks.add(chunk);
                }
            });
        }
        completableFuture.complete(chunks);
    }

    public List<ChunkLocation> getChunkLocations() {
        return chunkLocations;
    }
}
