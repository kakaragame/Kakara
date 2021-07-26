package org.kakara.client.local.game;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.kakara.core.common.ControllerKey;
import org.kakara.core.common.Utils;
import org.kakara.core.common.exceptions.WorldLoadException;
import org.kakara.core.common.world.Location;
import org.kakara.engine.ui.components.shapes.Rectangle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * The WorldCreator is a builder to create a world in the file system.
 *
 * <p>Used by default at MainMenuScene#singlePlayerClick.</p>
 */
public class WorldCreator {
    private String worldName;
    private UUID uuid = UUID.randomUUID();
    private ControllerKey generator;
    private int seed;

    /**
     * Get the name of the world.
     *
     * @return The name of the world.
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Set the name of the world.
     *
     * @param worldName The name of the world.
     * @return The current instance of World Creator.
     */
    public WorldCreator setWorldName(String worldName) {
        this.worldName = worldName;
        return this;
    }

    /**
     * Get the UUID of the world.
     *
     * <p>A random UUID is provided by default.</p>
     *
     * @return The UUID of the world.
     */
    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Set the UUID of the world.
     *
     * @param uuid The UUID to set.
     * @return The current instance of WorldCreator.
     */
    public WorldCreator setUuid(@NotNull UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * Get the generator to be used by the world.
     *
     * @return The generator to be used by the world. (The ControllerKey of it).
     */
    public ControllerKey getGenerator() {
        return generator;
    }

    /**
     * Set the generator to be used by the world.
     *
     * @param generator The controller key of the generator to be used.
     * @return The current instance of WorldCreator.
     */
    public WorldCreator setGenerator(ControllerKey generator) {
        this.generator = generator;
        return this;
    }

    /**
     * Get the current seed of the world.
     *
     * @return The current seed of the world.
     */
    public int getSeed() {
        return seed;
    }

    /**
     * Set the seed of the world.
     *
     * @param seed The seed of the world.
     * @return The current instance of WorldCreator.
     */
    public WorldCreator setSeed(int seed) {
        this.seed = seed;
        return this;
    }

    /**
     * Create the world in the file system and save the world properties in the world.json file.
     *
     * <p>If the world already exists, then nothing is done.</p>
     *
     * @param parentFolder The parent folder of the worlds.
     * @return The name of the world.
     * @throws WorldLoadException If an exception occurs while building the world in the file system.
     */
    public String build(File parentFolder) throws WorldLoadException {
        File worldFolder = new File(parentFolder, worldName);
        worldFolder.mkdir();
        File worldConfig = new File(worldFolder, "world.json");

        // If the world config already exists, do nothing.
        if(worldConfig.exists()) return worldName;

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
