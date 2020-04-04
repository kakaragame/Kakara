package org.kakara.game.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.kakara.core.Utils;
import org.kakara.core.serializers.gson.GsonSerializerRegistar;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.Location;
import org.kakara.core.world.World;

import java.io.*;
import java.nio.file.Files;

public class ClientChunkWriter {
    private ClientWorld world;
    private File chunkFolder;
    private String locFormat = "_%s_%s_%s_";
    private Gson gson;

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
        GsonBuilder js = new GsonBuilder();
        GsonSerializerRegistar.registerSerializers(js);
        js.setPrettyPrinting();
        gson = js.create();
    }

    private File getChunkFile(Location location) {
        int x = (int) Math.floor(location.getX() / 64);
        int y = (int) Math.floor(location.getY() / 64);
        int z = (int) Math.floor(location.getZ() / 64);
        File file = new File(chunkFolder, String.format("_%s_%s_%s_.json", x, y, z));
        return file;

    }

    public void saveChunk(Chunk chunk) throws IOException {
        File chunkFile = getChunkFile(chunk.getLocation());
        JsonObject jo;
        if (chunkFile.exists()) {
            jo = gson.fromJson(new FileReader(chunkFile), JsonObject.class);
            File backup = new File(chunkFile.getAbsolutePath() + ".bak");
            if (backup.exists()) backup.delete();
            Files.copy(chunkFile.toPath(), backup.toPath());
            chunkFile.delete();
        } else {
            jo = new JsonObject();
        }
        chunkFile.createNewFile();
        String value = getLocFormat(chunk.getLocation());
        if (jo.has(value)) {
            jo.remove(value);
        }
        JsonObject chunkValue = new JsonObject();
        chunkValue.add("location", gson.toJsonTree(chunk.getLocation()));
        chunkValue.add("blocks", gson.toJsonTree(chunk.getGameBlocks()));
        jo.add(value, chunkValue);
        //TODO save Entities
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(chunkFile));
        bufferedWriter.write(gson.toJson(jo));
        bufferedWriter.close();
    }

    private String getLocFormat(Location l) {
        return String.format(locFormat, ((int) l.getX()), ((int) l.getY()), ((int) l.getZ()));
    }

    private String getLocationFileName(Location location) {
        return String.format("_%s_%s_%s_.json", location.getX(), location.getY(), location.getZ());
    }
}
