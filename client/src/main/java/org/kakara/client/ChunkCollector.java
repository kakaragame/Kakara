package org.kakara.client;

import org.kakara.core.client.Save;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.World;
import org.kakara.game.GameUtils;
import org.kakara.game.IntegratedServer;
import org.kakara.game.Server;
import org.kakara.game.client.ClientWorld;

import java.util.ArrayList;
import java.util.List;

public class ChunkCollector extends Thread {
    private Save save;
    private Server server;

    public ChunkCollector(Save save, Server server) {
        this.save = save;
        this.server = server;
    }

    @Override
    public void run() {
        while (server.isRunning()) {
            try {
                sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ChunkLocation location = GameUtils.getChunkLocation(server.getPlayerEntity().getLocation());
            World world = server.getPlayerEntity().getLocation().getWorld();
            List<Chunk> chunksToUnload = new ArrayList<>();
            List<Chunk> chunksToSave = new ArrayList<>();
            System.out.println(world.getLoadedChunks().length);
            for (Chunk loadedChunk : world.getLoadedChunks()) {
                if (!GameUtils.isLocationInsideCurrentLocationRadius(location, loadedChunk.getLocation(), IntegratedServer.radius)) {
                    chunksToUnload.add(loadedChunk);
                } else {
                    chunksToSave.add(loadedChunk);
                }
            }
            world.unloadChunks(chunksToUnload);
            ((ClientWorld) world).saveChunks(chunksToSave);
        }
    }
}
