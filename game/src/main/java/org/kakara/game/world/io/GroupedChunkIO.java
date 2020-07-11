package org.kakara.game.world.io;

import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.lang3.tuple.Pair;
import org.kakara.core.world.ChunkContent;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.ChunkWriter;
import org.kakara.game.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;

public class GroupedChunkIO extends ChunkIO {
    private BlockingQueue<Pair<ChunkLocation, List<ChunkRequest>>> requests;
    private ChunkWriter chunkWriter;

    public GroupedChunkIO(GameWorld gameWorld, ChunkWriter chunkWriter) {
        super(gameWorld);
        this.chunkWriter = chunkWriter;
        requests = new LinkedBlockingDeque<>();
        start();
    }

    @Override
    public void run() {
        while (gameWorld.isLoaded()) {
            try {
                Pair<ChunkLocation, List<ChunkRequest>> request = requests.take();
                List<ChunkLocation> reads = new ArrayList<>();
                List<ChunkContent> writes = new ArrayList<>();
                for (ChunkRequest chunkRequest : request.getRight()) {
                    if (chunkRequest instanceof ReadChunkRequest) {
                        reads.addAll(((ReadChunkRequest) chunkRequest).getChunkLocations());
                    } else if (chunkRequest instanceof WriteChunkRequest) {
                        writes.addAll(((WriteChunkRequest) chunkRequest).getChunks());
                    }
                }
                chunkWriter.writeChunks(writes);
                List<ChunkContent> chunksFound = chunkWriter.getChunksByLocation(reads);
                for (ChunkRequest chunkRequest : request.getRight()) {
                    if (!(chunkRequest instanceof ReadChunkRequest)) continue;
                    ReadChunkRequest readChunkRequest = (ReadChunkRequest) chunkRequest;
                    List<ChunkContent> chunks = new ArrayList<>();
                    for (ChunkContent chunk : chunksFound) {
                        if (readChunkRequest.getChunkLocations().contains(chunk.getLocation())) {
                            chunks.add(chunk);
                        }
                    }
                    ((ReadChunkRequest) chunkRequest).respond(request.getLeft(), chunks);
                }
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }


    @Override
    public CompletableFuture<List<ChunkContent>> get(List<ChunkLocation> chunkLocations) {
        CompletableFuture<List<ChunkContent>> completableFuture = new CompletableFuture<>();
        ChunkRequest chunkRequest = new ReadChunkRequest(chunkLocations, completableFuture);
        try {
            ArrayListMultimap<ChunkLocation, ChunkLocation> locations = ChunkIOUtils.sort(chunkLocations);
            for (ChunkLocation key : locations.keySet()) {

                boolean found = false;
                for (Pair<ChunkLocation, List<ChunkRequest>> request : requests) {
                    if (request.getLeft().equals(key)) {
                        request.getRight().add(chunkRequest);
                        found = true;
                    }
                }
                //Creates a new Pair
                if (!found) {
                    List<ChunkRequest> requestsList = new CopyOnWriteArrayList<>();
                    requestsList.add(chunkRequest);
                    requests.offer(Pair.of(key, requestsList));
                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            completableFuture.completeExceptionally(e);
        }
        return completableFuture;
    }

    @Override
    public CompletableFuture<List<ChunkLocation>> write(List<ChunkContent> chunkLocations) {
        CompletableFuture<List<ChunkLocation>> completableFuture = new CompletableFuture<>();
        ChunkRequest chunkRequest = new WriteChunkRequest(chunkLocations, completableFuture);
        ArrayListMultimap<ChunkLocation, ChunkContent> locations = ChunkIOUtils.sortByChunk(chunkLocations);
        for (ChunkLocation key : locations.keySet()) {
            boolean found = false;
            for (Pair<ChunkLocation, List<ChunkRequest>> request : requests) {
                if (request.getLeft().equals(key)) {
                    request.getRight().add(chunkRequest);
                    found = true;
                }
            }
            //Creates a new Pair
            if (!found) {
                List<ChunkRequest> requestsList = new ArrayList<>();
                requestsList.add(chunkRequest);
                requests.offer(Pair.of(key, requestsList));
            }
        }
        return completableFuture;
    }
}