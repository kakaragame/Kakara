package org.kakara.game.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.kakara.core.Kakara;
import org.kakara.core.client.Save;
import org.kakara.core.client.SaveSettings;
import org.kakara.core.exceptions.WorldLoadException;
import org.kakara.core.player.OfflinePlayer;
import org.kakara.core.player.Player;
import org.kakara.core.world.World;
import org.kakara.game.Server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestSave implements Save {
    private final File saveFolder;
    private SaveSettings saveSettings;
    private File playersFolder;
    private Server server;

    public TestSave(File saveFolder, Server server) throws SaveLoadException {
        this.saveFolder = saveFolder;
        this.saveSettings = new SaveSettings("test", new ArrayList<>());
        playersFolder = new File(saveFolder, "players");
        this.server = server;
        if (!playersFolder.exists()) {
            playersFolder.mkdir();
        }
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

    public OfflinePlayer getOfflinePlayer(UUID uuid) {
        File players = new File(playersFolder, uuid.toString() + ".json");
        if (!players.exists()) {
            createNewPlayer(uuid);
        }
        return null;
    }

    private void createNewPlayer(UUID uuid) {
        //IDK
    }

    @Override
    public SaveSettings getSettings() {
        return saveSettings;
    }

    public File getSaveFolder() {
        return saveFolder;
    }
}
