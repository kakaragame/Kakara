package org.kakara.game.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.kakara.core.client.Save;
import org.kakara.core.client.SaveSettings;
import org.kakara.core.player.Player;
import org.kakara.core.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameSave implements Save {
    private File saveFolder;
    private SaveSettings saveSettings;

    public GameSave(File saveFolder) throws SaveLoadException {
        this.saveFolder = saveFolder;
        this.saveSettings = new ClientSaveSettings(new File(saveFolder, "config.json"));
    }


    @Override
    public List<World> getWorlds() {
        File worldsJson = new File(saveFolder, "worlds.json");
        JsonObject jsonObject = GsonUtils.loadFile(worldsJson);
        List<World> worlds = new ArrayList<>();
        for (JsonElement element : jsonObject.getAsJsonArray("worlds")) {
            World world = new ClientWorld(element);

            worlds.add(world);
        }
        return worlds;
    }

    @Override
    public Player getPlayerLocation(UUID uuid) {
        //TODO locate the player and load that boy!
        return null;
    }

    @Override
    public SaveSettings getSettings() {
        return saveSettings;
    }
}
