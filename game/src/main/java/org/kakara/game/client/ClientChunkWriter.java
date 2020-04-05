package org.kakara.game.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.kakara.core.serializers.gson.GsonSerializerRegistar;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.GameBlock;
import org.kakara.core.world.Location;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientChunkWriter {
    private ClientWorld world;
    private File chunkFolder;
    private String locFormat = "_%s_%s_%s_";
    private Gson gson;

    public ClientChunkWriter(ClientWorld world) {
        this.world = world;
        chunkFolder = new File(world.getWorldFolder(), "chunks");
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

    private File getChunkFile(ChunkLocation location) {
        int x = (int) Math.floor(location.getX() / 64);
        int y = (int) Math.floor(location.getY() / 64);
        int z = (int) Math.floor(location.getZ() / 64);
        File file = new File(chunkFolder, String.format("_%s_%s_%s_.json", x, y, z));
        return file;

    }

    public ClientChunk getChunk(ChunkLocation chunkLocation) throws IOException {
        File chunkFile = getChunkFile(chunkLocation);
        if (!chunkFile.exists()) {
            return null;
        }
        JsonObject jo;
        FileReader fileReader = new FileReader(chunkFile);
        jo = gson.fromJson(fileReader, JsonObject.class);
        String value = getLocFormat(chunkLocation);
        if (!jo.has(value)) {
            return null;
        }
        JsonObject chunkValue = jo.getAsJsonObject(value);
        List<GameBlock> gameBlocks = new ArrayList<>();
        for (JsonElement element : chunkValue.get("blocks").getAsJsonArray()) {
            gameBlocks.add(gson.fromJson(element, GameBlock.class));
        }
        return new ClientChunk(chunkLocation, gameBlocks);
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

    public void saveChunks(List<Chunk> chunks) throws IOException {
        Map<ChunkLocation, List<Chunk>> sortedChunks = sortChunk(chunks);
        for (Map.Entry<ChunkLocation, List<Chunk>> entry : sortedChunks.entrySet()) {
            File chunkFile = getChunkFile(entry.getKey());
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
            for (Chunk chunk : entry.getValue()) {
                String value = getLocFormat(chunk.getLocation());
                if (jo.has(value)) {
                    jo.remove(value);
                }
                JsonObject chunkValue = new JsonObject();
                chunkValue.add("location", gson.toJsonTree(chunk.getLocation()));
                chunkValue.add("blocks", gson.toJsonTree(chunk.getGameBlocks()));
                jo.add(value, chunkValue);
                //TODO save Entities
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(chunkFile));
            bufferedWriter.write(gson.toJson(jo));
            bufferedWriter.close();

        }
    }

    private Map<ChunkLocation, List<Chunk>> sortChunk(List<Chunk> chunk) {
        Map<ChunkLocation, List<Chunk>> sorted = new HashMap<>();
        for (Chunk chunk1 : chunk) {
            ChunkLocation groupLocation = getGroupLocation(chunk1.getLocation());
            List<Chunk> list = sorted.getOrDefault(groupLocation, new ArrayList<>());
            list.add(chunk1);
            sorted.put(groupLocation, list);
        }
        return sorted;
    }

    private ChunkLocation getGroupLocation(ChunkLocation location) {
        int x = (int) Math.floor(location.getX() / 64);
        int y = (int) Math.floor(location.getY() / 64);
        int z = (int) Math.floor(location.getZ() / 64);
        return new ChunkLocation(x, y, z, location.getWorld());
    }

    private String getLocFormat(ChunkLocation l) {
        return String.format(locFormat, l.getX(), l.getY(), l.getZ());
    }

    private String getLocationFileName(Location location) {
        return String.format("_%s_%s_%s_.json", location.getX(), location.getY(), location.getZ());
    }
}
