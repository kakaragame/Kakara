package org.kakara.game;

import org.kakara.core.client.Save;
import org.kakara.core.player.Player;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.GameEntity;
import org.kakara.game.client.GameSave;
import org.kakara.game.client.TestSave;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IntergratedServer implements Server {
    private LocalDataWatcher dataWatcher;
    private Save save;
    private Player player;
    private int radius = 8;
    private ExecutorService executorService;
    private List<Player> players = new ArrayList<>();

    public IntergratedServer(LocalDataWatcher dataWatcher, Save save, Player gameEntity) {
        this.dataWatcher = dataWatcher;
        this.save = save;
        this.player = gameEntity;
        executorService = Executors.newFixedThreadPool(2);
        players.add(player);
        if(save instanceof GameSave){
            ((GameSave) save).setServer(this);
        }else if(save instanceof TestSave){
            ((TestSave) save).setServer(this);
        }
    }

    @Override
    public DataWatcher getDataWatcher() {
        return dataWatcher;
    }

    @Override
    public Player getPlayerEntity() {
        return player;
    }

    @Override
    public List<Chunk> chunksToRender() {
        //TODO do coolMath
        return null;
    }

    @Override
    public List<Player> getOnlinePlayers() {
        return players;
    }

    @Override
    public void loadMods() {

    }

    @Override
    public void update() {
        ChunkLocation start = GameUtils.getChunkLocation(player.getLocation());
        for (int x = start.getX(); x <= (start.getX() + (radius * 16)); x = x + 16) {
            for (int y = start.getY(); y <= (start.getY() + (radius * 16)); y = y + 16) {
                for (int z = start.getZ(); z <= (start.getZ() + (radius * 16)); z = z + 16) {
                    ChunkLocation chunkLocation = new ChunkLocation(x, y, z);
                    if (GameUtils.isLocationInsideCurrentLocationRadius(start, chunkLocation, radius)) {
                        CompletableFuture<Chunk> chunk = player.getLocation().getWorld().getChunkAt(chunkLocation);
                        chunk.thenAcceptAsync(chunk1 -> player.getLocation().getWorld().loadChunk(chunk1));

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
