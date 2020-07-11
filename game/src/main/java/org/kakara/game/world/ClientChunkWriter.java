package org.kakara.game.world;

import me.ryandw11.ods.Compression;
import me.ryandw11.ods.ObjectDataStructure;
import me.ryandw11.ods.Tag;
import me.ryandw11.ods.exception.ODSException;
import me.ryandw11.ods.tags.ObjectTag;
import org.kakara.core.Kakara;
import org.kakara.core.serializers.ods.ChunkContentTag;
import org.kakara.core.world.ChunkContent;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.ChunkWriter;
import org.kakara.core.world.exceptions.ChunkLoadException;
import org.kakara.core.world.exceptions.ChunkWriteException;
import org.kakara.game.world.io.ChunkIOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ClientChunkWriter implements ChunkWriter {
    private final GameWorld world;
    private final File chunkFolder;

    public ClientChunkWriter(GameWorld world) {
        this.world = world;
        chunkFolder = new File(world.getWorldFolder(), "chunks");
        if (!chunkFolder.exists()) chunkFolder.mkdir();
    }

    private File getChunkFile(ChunkLocation location) {
        int x = (int) Math.floor(location.getX() / 64D);
        int y = (int) Math.floor(location.getY() / 64D);
        int z = (int) Math.floor(location.getZ() / 64D);

        return new File(chunkFolder, String.format("_%s_%s_%s_.kchunk", x, y, z));
    }

    private File getChunkFileFromChunkFileLocation(ChunkLocation location) {
        int x = location.getX();
        int y = location.getY();
        int z = location.getZ();

        return new File(chunkFolder, String.format("_%s_%s_%s_.kchunk", x, y, z));
    }

    @Override
    public ChunkContent getChunkByLocation(ChunkLocation chunkLocation) throws ChunkLoadException {
        File chunkFile = getChunkFile(chunkLocation);
        if (!chunkFile.exists()) return new ChunkContent(chunkLocation);

        ObjectDataStructure ods = new ObjectDataStructure(chunkFile, Compression.GZIP);
        ObjectTag objectTag = null;
        try {
            objectTag = ods.get(chunkLocation.getX() + "-" + chunkLocation.getY() + "-" + chunkLocation.getZ());
        } catch (ODSException e) {
            throw new ChunkLoadException(chunkLocation, chunkFile, e);
        }
        ChunkContentTag chunkContentTag;
        if (objectTag instanceof ChunkContentTag)
            chunkContentTag = (ChunkContentTag) objectTag;
        else
            return new ChunkContent(chunkLocation);

        if (chunkContentTag == null)
            return new ChunkContent(chunkLocation);
        return chunkContentTag.getChunk();
    }

    @Override
    public List<ChunkContent> getChunksByLocation(List<ChunkLocation> locations) throws ChunkLoadException {
        List<ChunkContent> output = new ArrayList<>();
        for (Map.Entry<ChunkLocation, Collection<ChunkLocation>> chunkLocationCollectionEntry : ChunkIOUtils.sort(locations).asMap().entrySet()) {
            ChunkLocation chunkLocation = chunkLocationCollectionEntry.getKey();
            File chunkFile = getChunkFileFromChunkFileLocation(chunkLocation);
            if (!chunkFile.exists()) {
                chunkLocationCollectionEntry.getValue().forEach(chunkLocation1 -> {
                    output.add(new ChunkContent(chunkLocation));
                });
                continue;
            }

            ObjectDataStructure ods = new ObjectDataStructure(chunkFile, Compression.GZIP);
            for (ChunkLocation chunkLocation1 : chunkLocationCollectionEntry.getValue()) {
                ObjectTag objectTag = null;
                try {
                    objectTag = ods.get(getTagName(chunkLocation1));
                } catch (ODSException e) {
                    throw new ChunkLoadException(chunkLocation1, chunkFile, e);
                }
                ChunkContentTag chunkContentTag = null;
                if (objectTag instanceof ChunkContentTag) {
                    chunkContentTag = (ChunkContentTag) objectTag;
                } else {
                    output.add(new ChunkContent(chunkLocation));
                    continue;
                }
                output.add(chunkContentTag.getChunk());
            }
        }
        return output;
    }

    public String getTagName(ChunkLocation chunkLocation) {
        return String.format("%d-%d-%d", chunkLocation.getX(), chunkLocation.getY(), chunkLocation.getZ());
    }

    @Override
    public void writeChunk(ChunkContent chunk) throws ChunkWriteException {
        File chunkFile = getChunkFile(chunk.getLocation());

        ObjectDataStructure ods = new ObjectDataStructure(chunkFile, Compression.GZIP);
        if (!chunkFile.exists()) {
            try {
                chunkFile.createNewFile();
            } catch (IOException e) {
                throw new ChunkWriteException(chunk.getLocation(), chunkFile, e);
            }
            try {
                ods.save(List.of(new ChunkContentTag(chunk)));
            } catch (Exception e) {
                throw new ChunkWriteException(chunk.getLocation(), chunkFile, e);
            }
        } else {
            try {
                ods.set(getTagName(chunk.getLocation()), new ChunkContentTag(chunk));
            } catch (Exception e) {
                throw new ChunkWriteException(chunk.getLocation(), chunkFile, e);
            }
        }
    }

    @Override
    public void writeChunks(List<ChunkContent> chunks) throws ChunkWriteException {
        for (Map.Entry<ChunkLocation, Collection<ChunkContent>> entry : ChunkIOUtils.sortByChunk(chunks).asMap().entrySet()) {
            File chunkFile = getChunkFile(entry.getKey());
            ObjectDataStructure ods = new ObjectDataStructure(chunkFile, Compression.GZIP);
            if (!chunkFile.exists()) {
                try {
                    //NEW File
                    chunkFile.createNewFile();
                    List<Tag<?>> tags = new ArrayList<>();
                    for (ChunkContent chunk : entry.getValue()) {
                        tags.add(new ChunkContentTag(chunk));
                    }
                    ods.save(tags);
                } catch (IOException e) {
                    throw new ChunkWriteException(entry.getKey(), chunkFile, e);
                }
            } else {
                for (ChunkContent chunk : entry.getValue()) {
                    try {
                        ods.set(getTagName(chunk.getLocation()), new ChunkContentTag(chunk));
                    } catch (ODSException e) {
                        throw new ChunkWriteException(chunk.getLocation(), chunkFile, e);
                    }
                }
            }
        }
    }

    private String getChunkKey(ChunkLocation loc) {
        return loc.getX() + "-" + loc.getY() + "-" + loc.getZ();
    }
}