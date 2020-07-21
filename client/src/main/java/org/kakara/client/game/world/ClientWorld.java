package org.kakara.client.game.world;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kakara.client.Client;

import org.kakara.core.Kakara;
import org.kakara.core.Status;
import org.kakara.core.Utils;
import org.kakara.core.exceptions.WorldLoadException;
import org.kakara.core.game.Block;
import org.kakara.core.game.ItemStack;
import org.kakara.core.world.*;
import org.kakara.game.GameUtils;
import org.kakara.game.Server;
import org.kakara.game.world.ClientChunkWriter;
import org.kakara.game.world.GameWorld;
import org.kakara.game.world.io.ChunkIO;
import org.kakara.game.world.io.GroupedChunkIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ClientWorld extends GameWorld {
    private final File worldFolder;
    //private final ChunkSet chunkSet;
    private final Map<ChunkLocation, Chunk> chunkMap = new ConcurrentHashMap<>();
    private final UUID worldID;
    private final String name;
    private final WorldGenerator chunkGenerator;
    private final int seed;
    private final Random random;
    private final Server server;
    private Location worldSpawn;
    private ChunkIO chunkIO = null;
    private Status status = Status.LOADING;

    public ClientWorld(@NotNull File worldFolder, @NotNull Server server) throws WorldLoadException {
        this.worldFolder = worldFolder;
        this.server = server;
        //chunkSet = new ChunkSet(-10000000, -100, -10000000, 10000000, 10000, 10000000);
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
    public @NotNull Set<Chunk> getChunks() {
        return new HashSet<>(chunkMap.values());
    }

    public @NotNull Collection<Chunk> getChunksNow() {
        return chunkMap.values();
    }

    public Collection<Chunk> getValue() {
        return chunkMap.values();
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
        Chunk chunk = getChunkAt(GameUtils.getChunkLocation(location));
        if (chunk.getStatus() != Status.LOADED) {
            throw new RuntimeException("TBH I am not sure what I want to do with this yet");
        }
        if (chunk instanceof ClientChunk) {
            return ((ClientChunk) chunk).getGameBlock(location);
        }
        return Optional.empty();
    }

    @Override
    @NotNull
    public Optional<GameBlock> setBlock(@NotNull ItemStack itemStack, @NotNull Location location) {
        Chunk chunk = getChunkAt(GameUtils.getChunkLocation(location));
        if (chunk.getStatus() != Status.LOADED) {
            throw new RuntimeException("TBH I am not sure what I want to do with this yet, " + chunk.getStatus());
        }
        if (chunk instanceof ClientChunk) {
            GameBlock gameBlock = new GameBlock(location, itemStack);
            ((ClientChunk) chunk).setGameBlock(gameBlock);
        }
        return Optional.empty();
    }

    public void placeBlock(@NotNull ItemStack itemStack, @NotNull Location location) {
        Chunk chunk = getChunkAt(GameUtils.getChunkLocation(location));
        if (chunk.getStatus() != Status.LOADED) {
            throw new RuntimeException("TBH I am not sure what I want to do with this yet, " + chunk.getStatus());
        }
        if (chunk instanceof ClientChunk) {
            GameBlock gameBlock = new GameBlock(location, itemStack);
            ((ClientChunk) chunk).placeBlock(gameBlock);
        }
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


    @Override
    public @NotNull Chunk getChunkAt(ChunkLocation location) {
        location.setWorld(this);
        Chunk chunkFound = chunkMap.get(location);
        if (chunkFound != null) {
            return chunkFound;
        }
        ClientChunk chunk = new ClientChunk(location);
        chunkMap.put(location, chunk);
        server.getExecutorService().submit(() -> {
            loadChunk(chunk);
        });
        return chunk;
    }

    private void loadChunk(ClientChunk chunk) {
        if (chunk.getStatus() != Status.UNLOADED) {
            return;
        }
        chunk.setStatus(Status.LOADING);
        chunkIO.get(List.of(chunk.getLocation())).thenAccept(chunkContents -> {
            server.getExecutorService().submit(() -> {
                try {
                    if (chunkContents.size() == 1 && !chunkContents.get(0).isNullChunk()) {
                        ((ClientChunk) chunkMap.get(chunk.getLocation())).load(chunkContents.get(0));
                    } else {
                        ChunkBase base = null;
                        try {
                            base = chunkGenerator.generateChunk(seed, random, this, chunk.getLocation().getX(), chunk.getLocation().getY(), chunk.getLocation().getZ());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        ((ClientChunk) chunkMap.get(chunk.getLocation())).load(new ChunkContent(base.getGameBlocks(), chunk.getLocation()));
                        System.out.println("LOADED");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    @Override
    public void unloadChunk(@NotNull Chunk chunk) {
        unloadChunks(List.of(chunk));
    }

    @Override
    public void unloadChunks(@NotNull List<Chunk> chunks) {
        List<ChunkContent> contents = new ArrayList<>();
        for (Chunk chunk : chunks) {
            ClientChunk cChunk = (ClientChunk) chunk;
            cChunk.setStatus(Status.UNLOADING);
            contents.add(cChunk.getContents());
            chunkMap.remove(chunk.getLocation());
        }
        chunkIO.write(contents);
    }


    public File getWorldFolder() {
        return worldFolder;
    }

    public void close() {
        status = Status.UNLOADING;
        unloadChunks(new ArrayList<>(getChunksNow()));
        chunkIO.close();
        status = Status.UNLOADED;
    }

    @Override
    public void errorClose() {
        chunkIO.interrupt();
        status = Status.UNLOADED;
        List<ChunkContent> contents = new ArrayList<>();
        for (Chunk chunk : getChunks()) {
            ClientChunk cChunk = (ClientChunk) chunk;
            contents.add(cChunk.getContents());
        }
        chunkIO.write(contents);
        server.errorClose(new WorldLoadException());

    }

    public boolean isLoaded() {
        return status == Status.LOADED;
    }

    public void saveChunks() {
        saveChunks(new ArrayList<>(chunkMap.values()));
    }

    public void saveChunks(List<Chunk> chunksToSave) {
        List<ChunkContent> contents = new ArrayList<>();

        for (Chunk chunk : chunksToSave) {
            ClientChunk cChunk = (ClientChunk) chunk;
            contents.add(cChunk.getContents());
        }
        chunkIO.write(contents);
    }

    public void initLoad(Set<ChunkLocation> chunkLocations) {
        chunkLocations.forEach(this::getChunkAt);
        while (true) {
            int loadedChunks = 1;
            for (Chunk chunk : getChunks()) {
                if (chunk.getStatus() == Status.LOADED) {
                    loadedChunks = loadedChunks + 1;
                }
            }
            if (chunkLocations.size() <= loadedChunks) {
                break;
            }
        }

        status = Status.LOADED;
    }

    @Override
    public Status getStatus() {
        return status;
    }
}
