package org.kakara.game.world.io;

import org.kakara.core.common.Status;
import org.kakara.core.common.world.*;
import org.kakara.core.common.world.exceptions.ChunkLoadException;
import org.kakara.core.common.world.exceptions.ChunkWriteException;
import org.kakara.game.world.GameWorld;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultChunkIO extends ChunkIO {
    private final BlockingQueue<ChunkRequest> requests;

    private volatile int percent = 0;

    public DefaultChunkIO(GameWorld gameWorld, ChunkWriter chunkWriter) {
        super(gameWorld, chunkWriter);
        requests = new LinkedBlockingDeque<>();
        start();
    }

    @Override
    public void run() {
        while (gameWorld.getStatus() == Status.LOADED || gameWorld.getStatus() == Status.LOADING) {
            // Synchronize the status on this thread.
            synchronized (gameWorld.getStatus()) {
                ChunkRequest request = null;
                try {
                    request = requests.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                if (request instanceof ReadChunkRequest) {
                    ChunkLocation chunkLocation = ((ReadChunkRequest) request).getChunkLocations().get(0);
                    try {
                        ((ReadChunkRequest) request).respond(chunkLocation, chunkWriter.getChunksByLocation(((ReadChunkRequest) request).getChunkLocations()));
                        ((ReadChunkRequest) request).respond();
                    } catch (ChunkLoadException e) {
                        e.printStackTrace();
                        gameWorld.errorClose();
                    }
                } else if (request instanceof WriteChunkRequest) {
                    try {
                        chunkWriter.writeChunks(((WriteChunkRequest) request).getChunks());
                    } catch (ChunkWriteException e) {
                        e.printStackTrace();
                    }
                    ((WriteChunkRequest) request).respond(((WriteChunkRequest) request).getChunks().get(0).getLocation());
                    ((WriteChunkRequest) request).respond();
                }
            }
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
    public CompletableFuture<List<Location>> writeBlock(List<GameBlock> chunkLocations) {
        if (!supportsSingleBlockWrites())
            throw new IllegalArgumentException("The ChunkWriter does not support single writes");
        return null;
    }

    @Override
    public void close() {
        this.interrupt();
        Queue<ChunkRequest> newRequests = new LinkedBlockingQueue<>();
        // Drain the entire requests queue into one that is not multithreaded.
        synchronized (requests) {
            requests.drainTo(newRequests);
        }

        int startingSize = newRequests.size();
        int i = 0;
        while (!newRequests.isEmpty()) {
            // Synchronize the status on this thread.
            synchronized (gameWorld.getStatus()) {
                ChunkRequest request = null;
                request = newRequests.poll();
                if (request instanceof ReadChunkRequest) {
                    System.out.println("TRIED TO READ");
                } else if (request instanceof WriteChunkRequest) {
                    try {
                        chunkWriter.writeChunks(((WriteChunkRequest) request).getChunks());
                    } catch (ChunkWriteException e) {
                        e.printStackTrace();
                    }
                    ((WriteChunkRequest) request).respond(((WriteChunkRequest) request).getChunks().get(0).getLocation());
                    ((WriteChunkRequest) request).respond();
                }
                percent = (int) (((float) i / startingSize) * 100);
                i++;
            }
        }

    }

    /**
     * Get the loading percent.
     *
     * @return The loading percent. (Always 100% on this object).
     */
    @Override
    public synchronized int getPercent() {
        return percent;
    }
}
