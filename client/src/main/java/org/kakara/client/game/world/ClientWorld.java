package org.kakara.client.game.world;

import com.google.gson.JsonObject;
import me.ryandw11.octree.Octree;
import me.ryandw11.octree.PointExistsException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kakara.client.Client;
import org.kakara.client.KakaraGame;
import org.kakara.client.game.world.io.ChunkIO;
import org.kakara.client.game.world.io.GroupedChunkIO;
import org.kakara.core.Kakara;
import org.kakara.core.Utils;
import org.kakara.core.exceptions.WorldLoadException;
import org.kakara.core.game.Block;
import org.kakara.core.game.ItemStack;
import org.kakara.core.world.*;
import org.kakara.game.GameUtils;
import org.kakara.game.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

public class ClientWorld implements World {
    private final File worldFolder;
    private final Octree<Chunk> loadedChunks;
    private final Octree<Boolean> chunksBeingActivlyLoaded;
    private final List<ChunkLocation> loadedChunkLocations = new CopyOnWriteArrayList<>();
    private final UUID worldID;
    private final String name;
    private final WorldGenerator chunkGenerator;
    private final int seed;
    private final Random random;
    private final Server server;
    private Location worldSpawn;
    private ChunkIO chunkIO = null;

    public ClientWorld(@NotNull File worldFolder, @NotNull Server server) throws WorldLoadException {
        this.worldFolder = worldFolder;
        this.server = server;
        loadedChunks = new Octree<>(-10000000, -100, -10000000, 10000000, 10000, 10000000);
        chunksBeingActivlyLoaded = new Octree<>(-10000000, -100, -10000000, 10000000, 10000, 10000000);
        //TODO replace null with instance of ChunkWriter
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
            this.chunkIO = new GroupedChunkIO(this, new ClientChunkWriter(this));
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
    @NotNull
    public Optional<GameBlock> getBlockAt(Location location) {
        Chunk chunk = getChunkNow(GameUtils.getChunkLocation(location));
        if (chunk instanceof ClientChunk) {
            return ((ClientChunk) chunk).getGameBlock(location).or(() -> Optional.of(new GameBlock(location, Kakara.createItemStack(Kakara.getItemManager().getItem("kakara:air").get()))));
        }
        return Optional.empty();
    }

    @Override
    @NotNull
    public Optional<GameBlock> setBlock(@NotNull ItemStack itemStack, @NotNull Location location) {
        Chunk chunk = getChunkNow(GameUtils.getChunkLocation(location));
        if (chunk instanceof ClientChunk) {
            GameBlock gameBlock = new GameBlock(location, itemStack);
            ((ClientChunk) chunk).setGameBlock(gameBlock);
        }
        return Optional.empty();
    }

    @Override
    public @NotNull Optional<GameBlock> getBlockAt(int x, int y, int z) {
        return getBlockAt(new Location(this, x, y, z));
    }


    @Override
    public @NotNull Optional<GameBlock> setBlock(@Nullable Block block, @NotNull Location location) {
        return setBlock(Kakara.createItemStack(block), location);
    }

    @Override
    public @NotNull Location getWorldSpawn() {
        return worldSpawn;
    }

    @Override
    public void setWorldSpawn(@NotNull Location location) {
        this.worldSpawn = location;
    }

    public Chunk getChunkNow(ChunkLocation location) {

        try {
            Chunk chunk = loadedChunks.get(location.getX(), location.getY(), location.getZ());
            if (chunk != null) {
                return chunk;
            }
        } catch (Exception e) {
        }
        chunksBeingActivlyLoaded.insert(location.getX(), location.getY(), location.getZ(), true);
        if (chunkIO != null) {
            try {
                List<Chunk> chunks = chunkIO.get(Collections.singletonList(location)).get();
                if (!chunks.isEmpty()) {
                    chunks.forEach(this::loadChunk);
                    return chunks.get(0);
                }
            } catch (InterruptedException | ExecutionException e) {
                KakaraGame.LOGGER.warn("ChunkIO failure", e);
            }
        } else {
            KakaraGame.LOGGER.warn("No ChunkIO found");
        }
        ChunkBase base = null;
        try {
            base = chunkGenerator.generateChunk(seed, random, this, location.getX(), location.getY(), location.getZ());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Chunk chunk = new ClientChunk(base);
        loadChunk(chunk);
        return chunk;
    }

    public Chunk getChunkNow(int x, int y, int z) {

        try {
            Chunk chunk = loadedChunks.get(x, y, z);
            if (chunk != null) {
                return chunk;
            }
        } catch (Exception e) {
        }
        chunksBeingActivlyLoaded.insert(x, y, z, true);
        if (chunkIO != null) {
            try {
                List<Chunk> chunks = chunkIO.get(Collections.singletonList(new ChunkLocation(x, y, z))).get();
                if (!chunks.isEmpty()) {
                    chunks.forEach(this::loadChunk);
                    return chunks.get(0);
                }
            } catch (InterruptedException | ExecutionException e) {
                KakaraGame.LOGGER.warn("ChunkIO failure", e);
            }
        } else {
            KakaraGame.LOGGER.warn("No ChunkIO found");
        }
        ChunkBase base = null;
        try {
            base = chunkGenerator.generateChunk(seed, random, this, x, y, z);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Chunk chunk = new ClientChunk(base);
        loadChunk(chunk);
        return chunk;
    }

    @Override
    public CompletableFuture<Chunk> getChunkAt(ChunkLocation location) {
        CompletableFuture<Chunk> completableFuture = new CompletableFuture<>();
        server.getExecutorService().submit(() -> {
            completableFuture.complete(getChunkNow(location));
        });

        return completableFuture;
    }

    @Override
    public @NotNull CompletableFuture<Chunk> getChunkAt(int x, int y, int z) {
        CompletableFuture<Chunk> completableFuture = new CompletableFuture<>();
        server.getExecutorService().submit(() -> {
            completableFuture.complete(getChunkNow(x, y, z));
        });

        return completableFuture;
    }


    @Override
    public void unloadChunk(Chunk chunk) {
        loadedChunkLocations.remove(chunk.getLocation());
        loadedChunks.remove(chunk.getLocation().getX(), chunk.getLocation().getY(), chunk.getLocation().getZ());
        if (chunkIO != null) chunkIO.write(Collections.singletonList(chunk));
    }

    @Override
    public void unloadChunks(List<Chunk> chunk) {
        for (Chunk chunk1 : chunk) {
            loadedChunkLocations.remove(chunk1.getLocation());
            loadedChunks.remove(chunk1.getLocation().getX(), chunk1.getLocation().getY(), chunk1.getLocation().getZ());
        }
        if (chunkIO != null) chunkIO.write(chunk);

    }

    @Override
    public void loadChunk(@NotNull Chunk chunk) {
        try {
            loadedChunks.insert(chunk.getLocation().getX(), chunk.getLocation().getY(), chunk.getLocation().getZ(), chunk);
            loadedChunkLocations.add(chunk.getLocation());
            chunksBeingActivlyLoaded.remove(chunk.getLocation().getX(), chunk.getLocation().getY(), chunk.getLocation().getZ());
        } catch (PointExistsException ignored) {

        }
    }

    public void loadChunkForRendering(int x, int y, int z) {
        if (isChunkLoaded(x, y, z)) {
            return;
        }
        if (chunksBeingActivlyLoaded.find(x, y, z)) {
            return;
        }
        getChunkAt(x, y, z);
    }

    @Override
    public boolean isChunkLoaded(@NotNull ChunkLocation location) {
        return loadedChunks.find(location.getX(), location.getY(), location.getZ());
    }

    @Override
    public boolean isChunkLoaded(int x, int y, int z) {
        return loadedChunks.find(x, y, z);
    }

    public List<Chunk> getLoadedChunksList() {
        List<Chunk> chunks = new ArrayList<>();
        for (ChunkLocation location : loadedChunkLocations) {
            //if (loadedChunks.find(location.getX(), location.getY(), location.getZ())) {
            try {
                Chunk chunk = loadedChunks.get(location.getX(), location.getY(), location.getZ());
                if (chunk == null) continue;
                chunks.add(chunk);
            } catch (PointExistsException | NullPointerException ignored) {
            }
            //}
        }

        return chunks;
    }

    @Override
    public @NotNull Chunk[] getLoadedChunks() {
        return getLoadedChunksList().toArray(Chunk[]::new);
    }

    public File getWorldFolder() {
        return worldFolder;
    }

    public boolean isLoaded() {
        return true;
    }

    public void saveChunks(List<Chunk> chunksToSave) {
        if (chunkIO != null) chunkIO.write(chunksToSave);
    }

    public void saveChunks() {
        saveChunks(getLoadedChunksList());
    }
}
