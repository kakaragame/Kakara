package org.kakara.client.local.game;

import com.google.gson.JsonObject;
import org.kakara.core.common.ControllerKey;
import org.kakara.core.common.Utils;
import org.kakara.core.common.exceptions.WorldLoadException;
import org.kakara.core.common.world.Location;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class WorldCreator {
    private String worldName;
    private UUID uuid = UUID.randomUUID();
    private ControllerKey generator;
    private int seed;

    public String getWorldName() {
        return worldName;
    }

    public WorldCreator setWorldName(String worldName) {
        this.worldName = worldName;
        return this;
    }

    public UUID getUuid() {
        return uuid;
    }

    public WorldCreator setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public ControllerKey getGenerator() {
        return generator;
    }

    public WorldCreator setGenerator(ControllerKey generator) {
        this.generator = generator;
        return this;
    }

    public int getSeed() {
        return seed;
    }

    public WorldCreator setSeed(int seed) {
        this.seed = seed;
        return this;
    }

    public String build(File parentFolder) throws WorldLoadException {
        File worldFolder = new File(parentFolder, worldName);
        worldFolder.mkdir();
        File worldConfig = new File(worldFolder, "world.json");
        try {
            worldConfig.createNewFile();
        } catch (IOException e) {
            throw new WorldLoadException(e);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("seed", seed);
        jsonObject.addProperty("name", worldName);
        jsonObject.addProperty("generator", generator.toString());
        jsonObject.addProperty("id", uuid.toString());
        //TODO IDK do something
        jsonObject.add("location", Utils.getGson().toJsonTree(new Location(0, 0, 0)));
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(worldConfig));
            bufferedWriter.write(Utils.getGson().toJson(jsonObject));
            bufferedWriter.close();
        } catch (IOException e) {
            throw new WorldLoadException(e);
        }
        return worldName;
    }
}
