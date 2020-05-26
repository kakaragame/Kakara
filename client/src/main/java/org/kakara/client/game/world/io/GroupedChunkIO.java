package org.kakara.client.game.world.io;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.kakara.client.game.world.ClientWorld;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.game.world.ChunkWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class GroupedChunkIO extends ChunkIO {
    private BlockingQueue<Pair<ChunkLocation, List<ChunkRequest>>> requests;
    private ChunkWriter chunkWriter;

    public GroupedChunkIO(ClientWorld clientWorld, ChunkWriter chunkWriter) {
        super(clientWorld);
        this.chunkWriter = chunkWriter;
        requests = new LinkedBlockingDeque<>();
    }

    @Override
    public void run() {
        while (clientWorld.isLoaded()) {
            try {
                Pair<ChunkLocation, List<ChunkRequest>> request = requests.take();
                List<ChunkLocation> reads = new ArrayList<>();
                List<Chunk> writes = new ArrayList<>();
                for (ChunkRequest chunkRequest : request.getRight()) {
                    if (chunkRequest instanceof ReadChunkRequest) {
                        reads.addAll(((ReadChunkRequest) chunkRequest).getChunkLocations());
                    } else if (chunkRequest instanceof WriteChunkRequest) {
                        //TODO later
                    }
                }
                List<Chunk> chunksFound = chunkWriter.getChunksByLocation(reads);
                for (ChunkRequest chunkRequest : request.getRight()) {
                    ReadChunkRequest readChunkRequest = (ReadChunkRequest) chunkRequest;
                    List<Chunk> chunks = chunksFound.stream().filter(chunk -> readChunkRequest.getChunkLocations().contains(chunk.getLocation())).collect(Collectors.toList());
                    ((ReadChunkRequest) chunkRequest).respond(request.getLeft(), chunks);
                }
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }


    @Override
    public CompletableFuture<List<Chunk>> get(List<ChunkLocation> chunkLocations) {
        CompletableFuture<List<Chunk>> completableFuture = new CompletableFuture<>();
        ChunkRequest chunkRequest = new ReadChunkRequest(chunkLocations, completableFuture);
        Map<ChunkLocation, List<ChunkLocation>> locations = ChunkIOUtils.sort(chunkLocations);
        for (Map.Entry<ChunkLocation, List<ChunkLocation>> entry : locations.entrySet()) {
            ChunkLocation key = entry.getKey();
            List<ChunkLocation> chunkLocations1 = entry.getValue();
            for (Pair<ChunkLocation, List<ChunkRequest>> request : requests) {
                if (request.getLeft().equals(key)) {
                    request.getRight().add(chunkRequest);
                }
            }
        }
        return completableFuture;
    }
}
