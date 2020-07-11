package org.kakara.client.scenes.maingamescene;

import org.kakara.client.MoreUtils;
import org.kakara.client.game.IntegratedServer;
import org.kakara.client.game.world.ClientChunk;
import org.kakara.core.Kakara;
import org.kakara.core.Status;
import org.kakara.core.resources.TextureResolution;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.GameBlock;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.renderobjects.RenderBlock;
import org.kakara.engine.renderobjects.RenderChunk;
import org.kakara.engine.renderobjects.mesh.MeshType;
import org.kakara.engine.renderobjects.renderlayouts.BlockLayout;
import org.kakara.game.GameUtils;
import org.kakara.game.items.blocks.AirBlock;

import java.util.ArrayList;

public class GameChunkManager {
    private final MainGameScene scene;

    public GameChunkManager(MainGameScene mainGameScene) {
        this.scene = mainGameScene;
    }

    /**
     * Adds any needed new chunks to the game
     */
    public void update() {
        if (scene.server.getPlayerEntity().getLocation().getWorld().isEmpty()) return;

        for (Chunk loadedChunk : scene.server.getPlayerEntity().getLocation().getWorld().get().getChunks()) {
            if (loadedChunk.getStatus() != Status.LOADED) continue;
            ClientChunk clientChunk = (ClientChunk) loadedChunk;

            if (!GameUtils.isLocationInsideCurrentLocationRadius(GameUtils.getChunkLocation(scene.server.getPlayerEntity().getLocation()), loadedChunk.getLocation(), IntegratedServer.RADIUS)) {
                if (clientChunk.getRenderChunkID().isPresent())
                    scene.getChunkHandler().removeChunk(clientChunk.getRenderChunkID().get());
            }
            if (clientChunk.getRenderChunkID().isEmpty() || clientChunk.isUpdatedHappened()) {
                if (clientChunk.getRenderChunkID().isPresent())
                    scene.getChunkHandler().removeChunk(clientChunk.getRenderChunkID().get());
                if (GameUtils.isLocationInsideCurrentLocationRadius(GameUtils.getChunkLocation(scene.server.getPlayerEntity().getLocation()), loadedChunk.getLocation(), IntegratedServer.RADIUS)) {
                    ChunkLocation cb = loadedChunk.getLocation();
                    RenderChunk rc = new RenderChunk(new ArrayList<>(), scene.getTextureAtlas());
                    rc.setPosition(cb.getX(), cb.getY(), cb.getZ());

                    for (GameBlock gb : loadedChunk.getGameBlocks()) {
                        if (gb.getItemStack().getItem() instanceof AirBlock) continue;
                        Vector3 vector3 = MoreUtils.locationToVector3(gb.getLocation());
                        vector3 = vector3.subtract(cb.getX(), cb.getY(), cb.getZ());
                        RenderBlock rb = new RenderBlock(new BlockLayout(),
                                scene.renderResourceManager.get
                                        ((Kakara.getResourceManager().getTexture(gb.getItemStack().getItem().getTexture(), TextureResolution._16, gb.getItemStack().getItem().getMod()).getLocalPath())), vector3);

                        rc.addBlock(rb);
                    }
                    clientChunk.setUpdatedHappened(false);
                    scene.server.getExecutorService().submit(() -> {
                        rc.regenerateChunk(scene.getTextureAtlas(), MeshType.MULTITHREAD);
                    });
                    scene.getChunkHandler().addChunk(rc);
                    clientChunk.setRenderChunkID(rc.getId());
                }
            }

        }
    }
}
