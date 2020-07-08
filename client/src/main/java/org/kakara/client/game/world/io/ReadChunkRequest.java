package org.kakara.client.game.world.io;

import org.kakara.client.game.world.NullChunk;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkContent;
import org.kakara.core.world.ChunkLocation;
import org.kakara.game.GameUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ReadChunkRequest implements ChunkRequest {
    private final List<ChunkLocation> chunkLocations;
    private final CompletableFuture<List<ChunkContent>> completableFuture;
    private final Map<ChunkLocation, List<ChunkContent>> response = new HashMap<>();

    public ReadChunkRequest(List<ChunkLocation> chunkLocation, CompletableFuture<List<ChunkContent>> completableFuture) {
        this.chunkLocations = chunkLocation;
        this.completableFuture = completableFuture;
    }

    public void respond(ChunkLocation chunkLocation, List<ChunkContent> chunks) {
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
        List<ChunkContent> chunks = new ArrayList<>();
        for (List<ChunkContent> chunkList : response.values()) {
            chunkList.forEach(chunk -> {
                if (!chunk.isNullChunk()) {
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
