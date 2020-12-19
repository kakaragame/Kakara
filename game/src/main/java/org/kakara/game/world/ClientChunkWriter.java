package org.kakara.game.world;

import me.ryandw11.ods.ObjectDataStructure;
import me.ryandw11.ods.compression.Compressor;
import me.ryandw11.ods.compression.GZIPCompression;
import me.ryandw11.ods.compression.ZLIBCompression;
import me.ryandw11.ods.exception.ODSException;
import me.ryandw11.ods.tags.ObjectTag;
import me.ryandw11.odscp.zstd.ZSTDCompression;
import org.kakara.core.common.world.ChunkContent;
import org.kakara.core.common.world.ChunkLocation;
import org.kakara.core.common.world.ChunkWriter;
import org.kakara.core.common.world.exceptions.ChunkLoadException;
import org.kakara.core.common.world.exceptions.ChunkWriteException;
import org.kakara.core.server.serializers.ods.ChunkContentTag;
import org.kakara.game.GameUtils;
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

    private Compressor getCompressor() {
        return new ZSTDCompression();
    }

    private File getChunkFile(ChunkLocation location) {
        return getChunkFileFromChunkFileLocation(GameUtils.getChunkFileLocation(location));
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

        ObjectDataStructure ods = new ObjectDataStructure(chunkFile,getCompressor());
        ObjectTag objectTag = null;
        try {
            objectTag = ods.get(chunkLocation.getX() + "-" + chunkLocation.getY() + "-" + chunkLocation.getZ());
        } catch (ODSException e) {
            throw new ChunkLoadException(chunkLocation, chunkFile, e);
        }

        if (objectTag == null)
            return new ChunkContent(chunkLocation);
        return ChunkContentTag.getChunk(objectTag);
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

            ObjectDataStructure ods = new ObjectDataStructure(chunkFile,getCompressor());
            for (ChunkLocation chunkLocation1 : chunkLocationCollectionEntry.getValue()) {
                ObjectTag objectTag = null;
                try {
                    objectTag = ods.get(getTagName(chunkLocation1));
                } catch (ODSException e) {
                    throw new ChunkLoadException(chunkLocation1, chunkFile, e);
                }
                if (objectTag == null) {
                    output.add(new ChunkContent(chunkLocation1));
                    continue;
                }
                ChunkContent chunkContent = ChunkContentTag.getChunk(objectTag);
                output.add(chunkContent);
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

        ObjectDataStructure ods = new ObjectDataStructure(chunkFile, getCompressor());
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
            File chunkFile = getChunkFileFromChunkFileLocation(entry.getKey());
            ObjectDataStructure ods = new ObjectDataStructure(chunkFile, getCompressor());
            if (!chunkFile.exists()) {
                try {
                    //NEW File
                    chunkFile.createNewFile();
                    List<ChunkContentTag> tags = new ArrayList<>();
                    for (ChunkContent chunk : entry.getValue()) {
                        if (chunk.isNullChunk()) continue;
                        tags.add(new ChunkContentTag(chunk));
                    }
                    ods.save(tags);
                } catch (Exception e) {
                    throw new ChunkWriteException(entry.getKey(), chunkFile, e);
                }
            } else {

                for (ChunkContent chunk : entry.getValue()) {
                    if (chunk.isNullChunk()) continue;
                    try {
                        ods.set(getTagName(chunk.getLocation()), new ChunkContentTag(chunk));
                    } catch (ODSException e) {
                        e.getIOException().printStackTrace();
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