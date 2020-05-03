package org.kakara.client;

import org.kakara.client.game.IntegratedServer;
import org.kakara.client.game.world.ClientWorld;
import org.kakara.core.Kakara;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.World;
import org.kakara.game.GameUtils;
import org.kakara.game.Server;

import java.util.ArrayList;
import java.util.List;

public class ChunkCleaner extends Thread {
    private Server server;


    public ChunkCleaner(Server server) {
        super("chunk_cleaner");
        this.server = server;
    }

    @Override
    public void run() {
        while (server.isRunning()) {
            try {
                sleep(30000);
            } catch (InterruptedException e) {
                KakaraGame.LOGGER.error("InterruptedException", e);
            }
            ChunkLocation location = GameUtils.getChunkLocation(server.getPlayerEntity().getLocation());
            ClientWorld world = (ClientWorld) server.getPlayerEntity().getLocation().getWorld();
            List<Chunk> chunksToUnload = new ArrayList<>();
            List<Chunk> chunksToSave = new ArrayList<>();

            for (Chunk loadedChunk : world.getLoadedChunks()) {
                if (!GameUtils.isLocationInsideCurrentLocationRadius(location, loadedChunk.getLocation(), IntegratedServer.RADIUS)) {
                    chunksToUnload.add(loadedChunk);
                } else {
                    chunksToSave.add(loadedChunk);
                }
            }
            world.unloadChunks(chunksToUnload);
            world.saveChunks(chunksToSave);
        }
    }

}
