package org.kakara.game.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.kakara.core.Kakara;
import org.kakara.core.Utils;
import org.kakara.core.client.Save;
import org.kakara.core.exceptions.WorldLoadException;
import org.kakara.core.game.Block;
import org.kakara.core.game.ItemStack;
import org.kakara.core.world.*;
import org.kakara.game.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientWorld implements World {
    private final File worldFolder;
    private final JsonObject worldSettings;
    private Location worldSpawn;
    private final ClientChunkWriter clientChunkWriter;
    private final List<Chunk> loadedChunks = new CopyOnWriteArrayList<>();
    private final Server server;
    private final ChunkGenerator chunkGenerator;
    private final int seed;
    private final Random random;

    public ClientWorld(JsonElement element, Save save, Server server) throws WorldLoadException {
        this.server = server;
        worldFolder = new File(save.getSaveFolder(), element.getAsString());
        clientChunkWriter = new ClientChunkWriter(this);
        worldSettings = loadWorldSettings();
        chunkGenerator = Kakara.getWorldGenerationManager().getGenerator(worldSettings.get("generator").getAsString());
        seed = worldSettings.get("seed").getAsInt();
        random = new Random(seed);
    }

    private JsonObject loadWorldSettings() throws WorldLoadException {
        File file = new File(worldFolder, "world.json");
        if (!file.exists()) {
            throw new WorldLoadException("Unable to locate world.json");
        }
        try {
            FileReader reader = new FileReader(file);
            return Utils.getGson().fromJson(reader, JsonObject.class);
        } catch (FileNotFoundException e) {
            throw new WorldLoadException(e);
        }
    }

    @Override
    public Chunk[] getChunks() {
        return new Chunk[0];
    }

    @Override
    public UUID getUUID() {
        return UUID.fromString(worldSettings.get("uuid").getAsString());
    }

    @Override
    public String getName() {
        return worldSettings.get("name").getAsString();
    }

    @Override
    public GameBlock getBlockAt(int i, int i1, int i2) {
        return null;
    }

    @Override
    public GameBlock getBlockAt(Location location) {
        return null;
    }

    @Override
    public GameBlock setBlock(ItemStack itemStack, Location location) {
        return null;
    }

    @Override
    public GameBlock setBlock(Block block, Location location) {
        return null;
    }

    @Override
    public Location getWorldSpawn() {
        return worldSpawn;
    }

    @Override
    public void setWorldSpawn(Location location) {
        worldSpawn = location;
    }

    @Override
    public CompletableFuture<Chunk> getChunkAt(ChunkLocation location) {
        CompletableFuture<Chunk> completableFuture = new CompletableFuture<>();
        server.getExecutorService().submit(() -> {
            for (Chunk loadedChunk : loadedChunks) {
                if (loadedChunk.getLocation().equals(location))
                    completableFuture.complete(loadedChunk);
            }
            try {
                Chunk chunk1 = clientChunkWriter.getChunk(location);
                if (chunk1 != null) {
                    loadChunk(chunk1);
                    completableFuture.complete(chunk1);
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                completableFuture.completeExceptionally(e);
            }
            ChunkBase base = new ChunkBase(location, new ArrayList<>(), null);
            base = chunkGenerator.generateChunk(seed, random, base);
            Chunk chunk = new ClientChunk(base);
            loadChunk(chunk);
            completableFuture.complete(chunk);
        });

        return completableFuture;
    }

    @Override
    public void loadChunk(Chunk chunk) {
        if (isChunkLoaded(chunk.getLocation())) {
            return;
        }

        loadedChunks.add(chunk);
    }

    @Override
    public boolean isChunkLoaded(ChunkLocation location) {
        return loadedChunks.stream().anyMatch(chunk -> chunk.getLocation().equals(location));
    }


    @Override
    public Chunk[] getLoadedChunks() {
        return loadedChunks.toArray(Chunk[]::new);
    }

    public File getWorldFolder() {
        return worldFolder;
    }

    public ClientChunkWriter getClientChunkWriter() {
        return clientChunkWriter;
    }
}
