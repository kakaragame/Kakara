package org.kakara.client.game.world;

import com.google.gson.JsonObject;
import me.ryandw11.octree.Octree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kakara.core.Kakara;
import org.kakara.core.Utils;
import org.kakara.core.exceptions.WorldLoadException;
import org.kakara.core.game.Block;
import org.kakara.core.game.ItemStack;
import org.kakara.core.world.*;
import org.kakara.game.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ClientWorld implements World {
    private final File worldFolder;
    private final Octree<Chunk> loadedChunks;
    private final Set<ChunkLocation> loadedChunkLocations = new HashSet<>();
    private final UUID worldID;
    private final String name;
    private final ChunkGenerator chunkGenerator;
    private final int seed;
    private final Random random;
    private final Server server;
    private Location worldSpawn;

    public ClientWorld(@NotNull File worldFolder, @NotNull Server server) throws WorldLoadException {
        this.worldFolder = worldFolder;
        this.server = server;
        loadedChunks = new Octree<>(-30000000, -100, -30000000, 30000000, 10000, 30000000);

        try {
            JsonObject object = getSettings(new File(worldFolder, "world.json"));
            chunkGenerator = Kakara.getWorldGenerationManager().getGenerator(object.get("generator").getAsString());
            if (chunkGenerator == null) {
                throw new WorldLoadException("Unable to locate ChunkGenerator: " + object.get("generator").getAsString());
            }
            seed = object.get("seed").getAsInt();
            random = new Random(seed);
            name = object.get("name").getAsString();
            worldID = UUID.fromString(object.get("id").getAsString());
            worldSpawn = Utils.getGson().fromJson(object.get("location"), Location.class);
        } catch (WorldLoadException e) {
            throw e;
        } catch (Exception e) {
            throw new WorldLoadException(e);
        }
    }

    private JsonObject getSettings(File file) throws FileNotFoundException {
        return Utils.getGson().fromJson(new FileReader(file), JsonObject.class);
    }

    @Override
    public @NotNull Chunk[] getChunks() {
        return getLoadedChunks();
    }

    @Override
    public @NotNull UUID getUUID() {
        return worldID;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull GameBlock getBlockAt(int x, int y, int z) {
        return null;
    }

    @Override
    public @NotNull GameBlock getBlockAt(Location location) {
        return null;
    }

    @Override
    public @NotNull GameBlock setBlock(@NotNull ItemStack itemStack, @NotNull Location location) {
        return null;
    }

    @Override
    public @NotNull GameBlock setBlock(@Nullable Block block, @NotNull Location location) {
        return null;
    }

    @Override
    public @NotNull Location getWorldSpawn() {
        return worldSpawn;
    }

    @Override
    public void setWorldSpawn(@NotNull Location location) {
        this.worldSpawn = location;
    }

    @Override
    public CompletableFuture<Chunk> getChunkAt(ChunkLocation location) {
        CompletableFuture<Chunk> completableFuture = new CompletableFuture<>();
        server.getExecutorService().submit(() -> {

            if (loadedChunks.find(location.getX(), location.getY(), location.getZ())) {
                completableFuture.complete(loadedChunks.get(location.getX(), location.getY(), location.getZ()));
                return;
            }


            /*try {
                Chunk chunk1 = clientChunkWriter.getChunk(location);
                if (chunk1 != null) {
                    loadChunk(chunk1);
                    completableFuture.complete(chunk1);
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                completableFuture.completeExceptionally(e);
            }*/
            ChunkBase base = new ChunkBase(location, new ArrayList<>(), null);
            base = chunkGenerator.generateChunk(seed, random, base);
            Chunk chunk = new ClientChunk(base);
            loadChunk(chunk);
            completableFuture.complete(chunk);
        });

        return completableFuture;
    }

    @Override
    public void unloadChunk(Chunk chunk) {
        loadedChunks.remove(chunk.getLocation().getX(), chunk.getLocation().getY(), chunk.getLocation().getZ());
        loadedChunkLocations.remove(chunk.getLocation());
    }

    @Override
    public void unloadChunks(List<Chunk> chunk) {
        for (Chunk chunk1 : chunk) {
            loadedChunks.remove(chunk1.getLocation().getX(), chunk1.getLocation().getY(), chunk1.getLocation().getZ());
            loadedChunkLocations.remove(chunk1.getLocation());
        }

    }

    @Override
    public void loadChunk(@NotNull Chunk chunk) {
        if (isChunkLoaded(chunk.getLocation())) {
            return;
        }
        loadedChunkLocations.add(chunk.getLocation());
        loadedChunks.insert(chunk.getLocation().getX(), chunk.getLocation().getY(), chunk.getLocation().getZ(), chunk);
    }

    @Override
    public boolean isChunkLoaded(@NotNull ChunkLocation location) {
        return loadedChunks.find(location.getX(), location.getY(), location.getZ());
    }


    public List<Chunk> getLoadedChunksList() {
        List<Chunk> chunks = new ArrayList<>();
        for (ChunkLocation location : loadedChunkLocations) {
            chunks.add(loadedChunks.get(location.getX(), location.getY(), location.getZ()));
        }

        return chunks;
    }

    @Override
    public @NotNull Chunk[] getLoadedChunks() {
        return getLoadedChunksList().toArray(Chunk[]::new);
    }
}
