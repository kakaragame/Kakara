package org.kakara.client.game.world;

import me.ryandw11.ods.Compression;
import me.ryandw11.ods.ObjectDataStructure;
import me.ryandw11.ods.Tag;
import me.ryandw11.ods.exception.ODSException;
import me.ryandw11.ods.tags.ObjectTag;
import org.kakara.client.KakaraGame;
import org.kakara.client.game.world.io.ChunkIOUtils;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkContent;
import org.kakara.core.world.ChunkLocation;
import org.kakara.game.world.ChunkWriter;

import java.io.File;
import java.util.*;

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

        return new File(chunkFolder, String.format("_%s_%s_%s_.kchunk", x, y, z));
    }

    private File getChunkFileFromChunkFileLocation(ChunkLocation location) {
        int x = location.getX();
        int y = location.getY();
        int z = location.getZ();

        return new File(chunkFolder, String.format("_%s_%s_%s_.kchunk", x, y, z));
    }

    @Override
    public ChunkContent getChunkByLocation(ChunkLocation chunkLocation) {
        File chunkFile = getChunkFile(chunkLocation);
        if (!chunkFile.exists()) return new ChunkContent(chunkLocation);

        ObjectDataStructure ods = new ObjectDataStructure(chunkFile, Compression.GZIP);
        ObjectTag objectTag = null;
        try {
            objectTag = ods.get(chunkLocation.getX() + "-" + chunkLocation.getY() + "-" + chunkLocation.getZ());
        } catch (ODSException e) {
            KakaraGame.LOGGER.error("Unable to get chunk: " + chunkLocation.toString(), e);
            //TODO Cancel World Load
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
    public List<ChunkContent> getChunksByLocation(List<ChunkLocation> locations) {
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
                    objectTag = ods.get(chunkLocation.getX() + "-" + chunkLocation.getY() + "-" + chunkLocation.getZ());
                } catch (ODSException e) {
                    KakaraGame.LOGGER.error("Unable to get chunk: " + chunkLocation.toString(), e);
                    //TODO Cancel World Load
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

    @Override
    public void writeChunk(ChunkContent chunk) {
        File chunkFile = getChunkFile(chunk.getLocation());
        ObjectDataStructure ods = new ObjectDataStructure(chunkFile, Compression.GZIP);
        if (ods.find(getChunkKey(chunk.getLocation())))
            ods.delete(getChunkKey(chunk.getLocation()));
        ods.append(new ChunkContentTag(chunk));
    }

    @Override
    public void writeChunks(List<ChunkContent> chunks) {
        for (Map.Entry<ChunkLocation, Collection<ChunkContent>> entry : ChunkIOUtils.sortByChunk(chunks).asMap().entrySet()) {
            File chunkFile = getChunkFile(entry.getKey());
            ObjectDataStructure ods = new ObjectDataStructure(chunkFile, Compression.GZIP);
            for (ChunkContent chunk : entry.getValue()) {
                if (ods.find(getChunkKey(chunk.getLocation())))
                    ods.delete(getChunkKey(chunk.getLocation()));
            }
            List<Tag<?>> chunkTags = new ArrayList<>();
            entry.getValue().forEach(chunk -> chunkTags.add(new ChunkContentTag(chunk)));
            ods.appendAll(chunkTags);
        }
    }

    private String getChunkKey(ChunkLocation loc) {
        return loc.getX() + "-" + loc.getY() + "-" + loc.getZ();
    }
}