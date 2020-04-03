package org.kakara.game.client;

import com.google.gson.JsonObject;
import org.kakara.core.Utils;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.Location;
import org.kakara.core.world.World;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class ClientChunkWriter {
    private ClientWorld world;
    private File chunkFolder;

    public ClientChunkWriter(ClientWorld world) {
        this.world = world;
        chunkFolder = new File(world.getFile(), "chunks");
        if (!chunkFolder.exists()) chunkFolder.mkdir();
    }

    @Deprecated
    /**
     * Literally only exists for testing.
     */
    public ClientChunkWriter(File world) {
        this.world = null;
        System.out.println("DONT USE THIS MORON!");
        chunkFolder = new File(world, "chunks");
        if (!chunkFolder.exists()) chunkFolder.mkdir();
    }

    public void saveChunk(Chunk chunk) throws IOException {
        File chunkFile = new File(chunkFolder, getLocationFileName(chunk.getLocation()));
        if (chunkFile.exists()) {
            File backup = new File(chunkFile.getAbsolutePath() + ".bak");
            if (backup.exists()) backup.delete();
            Files.copy(chunkFile.toPath(), backup.toPath());
            chunkFile.delete();
        }
        chunkFile.createNewFile();
        JsonObject jo = new JsonObject();
        jo.add("location", Utils.getGson().toJsonTree(chunk.getLocation()));
        jo.add("blocks", Utils.getGson().toJsonTree(chunk.getGameBlocks()));
        //TODO save Entities
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(chunkFile));
        bufferedWriter.write(Utils.getGson().toJson(jo));
        bufferedWriter.close();
    }

    private String getLocationFileName(Location location) {
        return String.format("_%s_%s_%s_.json", location.getX(), location.getY(), location.getZ());
    }
}
