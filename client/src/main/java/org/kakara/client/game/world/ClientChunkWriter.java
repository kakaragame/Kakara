package org.kakara.client.game.world;

import me.ryandw11.ods.Compression;
import me.ryandw11.ods.ObjectDataStructure;
import me.ryandw11.ods.tags.ObjectTag;
import org.kakara.core.Kakara;
import org.kakara.core.exceptions.SaveLoadException;
import org.kakara.core.utils.CoreFileUtils;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.game.world.ChunkWriter;
import org.msgpack.core.MessageInsufficientBufferException;

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
        File chunkFile = getChunkFile(chunkLocation);
        if (!chunkFile.exists()) return new NullChunk(chunkLocation);

        ObjectDataStructure ods = new ObjectDataStructure(chunkFile, Compression.GZIP);
        ObjectTag objectTag = ods.getObject(chunkLocation.getX() + "-" + chunkLocation.getY() + "-" + chunkLocation.getZ());

        ChunkTag chunkTag;
        if(objectTag instanceof  ChunkTag)
            chunkTag = (ChunkTag) objectTag;
        else
            return new NullChunk(chunkLocation);

        if(chunkTag == null)
            return new NullChunk(chunkLocation);
        return chunkTag.getChunk();
    }

    @Override
    public List<Chunk> getChunksByLocation(List<ChunkLocation> locations) {
        List<Chunk> output = new ArrayList<>();
        for (ChunkLocation location : locations) {
            output.add(getChunkByLocation(location));
        }
        return output;
    }

    @Override
    public void writeChunk(Chunk chunk) {
        System.out.println("Hello");
        File chunkFile = getChunkFile(chunk.getLocation());
        ObjectDataStructure ods = new ObjectDataStructure(chunkFile, Compression.GZIP);
        if(ods.find(getChunkKey(chunk.getLocation())))
            ods.delete(getChunkKey(chunk.getLocation()));
        ods.append(new ChunkTag(chunk));
    }

    @Override
    public void writeChunks(List<Chunk> chunks) {
        //TODO this will impact speed. Improve this later.
        for(Chunk chunk : chunks){
            writeChunk(chunk);
        }
    }

    private String getChunkKey(ChunkLocation loc){
        return loc.getX() + "-" + loc.getY() + "-" + loc.getZ();
    }
}