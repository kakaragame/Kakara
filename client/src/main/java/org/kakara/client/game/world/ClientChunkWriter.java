package org.kakara.client.game.world;

import org.kakara.core.Kakara;
import org.kakara.core.exceptions.SaveLoadException;
import org.kakara.core.utils.CoreFileUtils;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.game.world.ChunkWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientChunkWriter implements ChunkWriter {
    private final ClientWorld world;
    private final File chunkFolder;

    public ClientChunkWriter(ClientWorld world) {
        this.world = world;
        chunkFolder = new File(world.getWorldFolder(), "chunks");
        if (!chunkFolder.exists()) chunkFolder.mkdir();
    }

    private File getChunkFile(ChunkLocation location) {
        int x = (int) Math.floor(location.getX() / 64D);
        int y = (int) Math.floor(location.getY() / 64D);
        int z = (int) Math.floor(location.getZ() / 64D);

        return new File(chunkFolder, String.format("_%s_%s_%s_.json", x, y, z));
    }

    @Override
    public Chunk getChunkByLocation(ChunkLocation chunkLocation) {
        try {
            File chunkFile = getChunkFile(chunkLocation);
            if (!chunkFile.exists()) return null;
            Path file = chunkFile.toPath();
            byte[] lines = Files.readAllBytes(file);

            List<Chunk> chunks = ChunkFileSerializer.INSTANCE.deserialize(lines);
            for (Chunk chunk : chunks) {
                if (chunk.getLocation() == chunkLocation) return chunk;
            }
        } catch (IOException e) {
            Kakara.LOGGER.error("Something bad happened with getting a chunk.", e);
        }

        return null;
    }

    @Override
    public List<Chunk> getChunksByLocoation(List<ChunkLocation> locations) {
        Map<Path, List<ChunkLocation>> sortedLocations = new HashMap<>();
        for (ChunkLocation location : locations) {
            File chunkFile = getChunkFile(location);
            if (!chunkFile.exists()) continue;
            Path path = chunkFile.toPath();
            List<ChunkLocation> current = sortedLocations.containsKey(path) ? sortedLocations.get(path) : new ArrayList<>();
            current.add(location);

            sortedLocations.put(path, current);
        }

        List<Chunk> chunks = new ArrayList<>();
        sortedLocations.forEach((file, chunkLocations) -> {
            try {
                byte[] lines = Files.readAllBytes(file);

                List<Chunk> currentChunks = ChunkFileSerializer.INSTANCE.deserialize(lines);
                for (Chunk chunk : currentChunks) {
                    if (chunkLocations.contains(chunk.getLocation())) {
                        chunks.add(chunk);
                    }
                }
            } catch (IOException e) {
                Kakara.LOGGER.error("Something bad happened with getting a chunk.", e);
            }
        });

        return chunks;
    }

    @Override
    public void writeChunk(Chunk chunk) {
        try {
            File chunkFile = getChunkFile(chunk.getLocation());
            if (chunkFile.exists()) {
                try {
                    CoreFileUtils.backup(chunkFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    chunkFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Path file = chunkFolder.toPath();
            byte[] lines = Files.readAllBytes(file);

            List<Chunk> chunks = ChunkFileSerializer.INSTANCE.deserialize(lines);
            chunks.remove(chunk);
            chunks.add(chunk);

            FileOutputStream stream = new FileOutputStream(file.toFile());
            stream.write(ChunkFileSerializer.INSTANCE.serialize(chunks));
            stream.close();
        } catch (IOException e) {
            Kakara.LOGGER.error("Something bad happened with getting a chunk.");
        }
    }

    @Override
    public void writeChunks(List<Chunk> chunks) {
        Map<Path, List<Chunk>> sortedChunks = new HashMap<>();
        for (Chunk chunk : chunks) {
            File chunkFile = getChunkFile(chunk.getLocation());
            if (chunkFile.exists()) {
                try {
                    CoreFileUtils.backup(chunkFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    chunkFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Path path = chunkFile.toPath();
            List<Chunk> current = sortedChunks.containsKey(path) ? sortedChunks.get(path) : new ArrayList<>();
            current.add(chunk);

            sortedChunks.put(path, current);
        }

        sortedChunks.forEach((file, currentChunks) -> {
            try {
                byte[] lines = Files.readAllBytes(file);
                List<Chunk> fileChunks = ChunkFileSerializer.INSTANCE.deserialize(lines);

                for (Chunk currentChunk : currentChunks) {
                    fileChunks.remove(currentChunk);
                    fileChunks.add(currentChunk);
                }

                FileOutputStream stream = new FileOutputStream(file.toFile());
                stream.write(ChunkFileSerializer.INSTANCE.serialize(chunks));
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}