package org.kakara.game.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.kakara.core.Kakara;
import org.kakara.core.Utils;
import org.kakara.core.client.Save;
import org.kakara.core.client.SaveSettings;
import org.kakara.core.exceptions.WorldLoadException;
import org.kakara.core.player.OfflinePlayer;
import org.kakara.core.player.Player;
import org.kakara.core.world.World;
import org.kakara.game.Server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TestSave implements Save {
    private final File saveFolder;
    private SaveSettings saveSettings;
    private Server server;

    public TestSave(File saveFolder) throws SaveLoadException {
        this.saveFolder = saveFolder;
        this.saveSettings = new SaveSettings("test", new ArrayList<>());
    }

    private void createDefaultWorld() {
        File worldsJson = new File(saveFolder, "worlds.json");
        if (worldsJson.exists()) return;
        try {
            worldsJson.createNewFile();
            JsonObject jsonObject = new JsonObject();
            JsonArray ja = new JsonArray();
            ja.add("world");
            jsonObject.add("worlds", ja);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(worldsJson));
            bufferedWriter.write(Utils.getGson().toJson(jsonObject));
            bufferedWriter.close();

            File file = new File(saveFolder, "world");
            file.mkdir();
            File worldSettigns = new File(file, "world.json");
            worldSettigns.createNewFile();
            JsonObject newWorldSett = new JsonObject();
            newWorldSett.addProperty("generator", Kakara.getWorldGenerationManager().getChunkGenerators().get(0).getNameKey().toString());
            newWorldSett.addProperty("name", "world");
            newWorldSett.addProperty("uuid", UUID.randomUUID().toString());
            newWorldSett.addProperty("seed", 51235132);
            BufferedWriter worldWriter = new BufferedWriter(new FileWriter(worldSettigns));
            worldWriter.write(Utils.getGson().toJson(newWorldSett));
            worldWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void prepareWorlds() {
        createDefaultWorld();
    }

    @Override
    public List<World> getWorlds() {
        File worldsJson = new File(saveFolder, "worlds.json");

        JsonObject jsonObject = GsonUtils.loadFile(worldsJson);
        List<World> worlds = new ArrayList<>();
        for (JsonElement element : jsonObject.getAsJsonArray("worlds")) {
            World world;
            try {
                world = new ClientWorld(element, this, server);
            } catch (WorldLoadException e) {
                Kakara.LOGGER.error("Unable to load world ", e);
                continue;
            }
            worlds.add(world);
        }
        return worlds;
    }

    @Override
    public World getDefaultWorld() {
        return getWorld("world");
    }

    private World getWorld(String world) {
        return getWorlds().stream().filter(world1 -> world1.getName().equalsIgnoreCase(world)).findFirst().get();
    }


    @Override
    public SaveSettings getSettings() {
        return saveSettings;
    }

    public File getSaveFolder() {
        return saveFolder;
    }

    @Override
    public List<File> getModsToLoad() {
        File dir = new File("test" + File.separator + "mods");

        return Arrays.asList(dir.listFiles((dir1, filename) -> filename.endsWith(".jar")));
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
