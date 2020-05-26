package org.kakara.client.game.world;

import com.google.gson.JsonObject;
import me.ryandw11.octree.Octree;
import me.ryandw11.octree.PointExistsException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kakara.client.Client;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientWorld implements World {
    private final File worldFolder;
    private final Octree<Chunk> loadedChunks;
    private final List<ChunkLocation> loadedChunkLocations = new CopyOnWriteArrayList<>();
    private final UUID worldID;
    private final String name;
    private final WorldGenerator chunkGenerator;
    private final int seed;
    private final Random random;
    private final Server server;
    private Location worldSpawn;

    public ClientWorld(@NotNull File worldFolder, @NotNull Server server) throws WorldLoadException {
        this.worldFolder = worldFolder;
        this.server = server;
        loadedChunks = new Octree<>(-10000000, -100, -10000000, 10000000, 10000, 10000000);

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

    @Override
    public CompletableFuture<Chunk> getChunkAt(ChunkLocation location) {
        CompletableFuture<Chunk> completableFuture = new CompletableFuture<>();
        server.getExecutorService().submit(() -> {
            completableFuture.complete(getChunkNow(location));
        });

        return completableFuture;
    }

    @Override
    public void unloadChunk(Chunk chunk) {
        loadedChunkLocations.remove(chunk.getLocation());
        loadedChunks.remove(chunk.getLocation().getX(), chunk.getLocation().getY(), chunk.getLocation().getZ());
    }

    @Override
    public void unloadChunks(List<Chunk> chunk) {
        for (Chunk chunk1 : chunk) {
            loadedChunkLocations.remove(chunk1.getLocation());
            loadedChunks.remove(chunk1.getLocation().getX(), chunk1.getLocation().getY(), chunk1.getLocation().getZ());
        }

    }

    @Override
    public void loadChunk(@NotNull Chunk chunk) {
        try {
            loadedChunks.insert(chunk.getLocation().getX(), chunk.getLocation().getY(), chunk.getLocation().getZ(), chunk);
            loadedChunkLocations.add(chunk.getLocation());
        } catch (PointExistsException ignored) {

        }
    }

    @Override
    public boolean isChunkLoaded(@NotNull ChunkLocation location) {
        return loadedChunks.find(location.getX(), location.getY(), location.getZ());
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
}
