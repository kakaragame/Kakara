package org.kakara.game;

import org.kakara.core.client.Save;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.GameEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IntergratedServer implements Server {
    private LocalDataWatcher dataWatcher;
    private Save save;
    private GameEntity gameEntity;
    private int radius = 8;
    private ExecutorService executorService;

    public IntergratedServer(LocalDataWatcher dataWatcher, Save save, GameEntity gameEntity) {
        this.dataWatcher = dataWatcher;
        this.save = save;
        this.gameEntity = gameEntity;
        executorService = Executors.newFixedThreadPool(2);
    }

    @Override
    public DataWatcher getDataWatcher() {
        return dataWatcher;
    }

    @Override
    public GameEntity getPlayerEntity() {
        return gameEntity;
    }

    @Override
    public List<Chunk> chunksToRender() {
        //TODO do coolMath
        return null;
    }

    @Override
    public void update() {
        ChunkLocation start = GameUtils.getChunkLocation(gameEntity.getLocation());
        for (int x = start.getX(); x <= (start.getX() + (radius * 16)); x = x + 16) {
            for (int y = start.getY(); y <= (start.getY() + (radius * 16)); y = y + 16) {
                for (int z = start.getZ(); z <= (start.getZ() + (radius * 16)); z = z + 16) {
                    ChunkLocation chunkLocation = new ChunkLocation(x, y, z);
                    if (GameUtils.isLocationInsideCurrentLocationRadius(start, chunkLocation, radius)) {
                        CompletableFuture<Chunk> chunk = gameEntity.getLocation().getWorld().getChunkAt(chunkLocation);
                        chunk.thenAcceptAsync(chunk1 -> gameEntity.getLocation().getWorld().loadChunk(chunk1));

                    }
                }
            }
        }
    }


    @Override
    public void tickUpdate() {

    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
