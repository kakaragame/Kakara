package org.kakara.client;

import com.google.gson.JsonArray;
import org.kakara.client.game.ClientSave;
import org.kakara.client.game.WorldCreator;
import org.kakara.core.client.Save;
import org.kakara.core.client.SaveSettings;
import org.kakara.core.client.SaveSettingsBuilder;
import org.kakara.core.exceptions.SaveLoadException;
import org.kakara.core.exceptions.WorldLoadException;
import org.kakara.core.modinstance.ModInstance;
import org.kakara.core.world.World;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SaveCreator {
    private String name;
    private Set<ModInstance> mods = new HashSet<>();
    private Set<WorldCreator> worldSet = new HashSet<>();

    public void setName(String name) {
        this.name = name;
    }

    public boolean add(ModInstance modInstance) {
        return mods.add(modInstance);
    }

    //TODO make this actually take more info about the world
    public boolean add(WorldCreator world) {
        return worldSet.add(world);
    }

    public Save createSave() throws SaveLoadException {
        File saveFolder = new File(name);
        try {
            saveFolder.createNewFile();
        } catch (IOException e) {
            throw new SaveLoadException(e);
        }
        File saveFile = new File(saveFolder, "save.json");
        Set<String> worlds = new HashSet<>();
        UUID defaultWorld = null;
        for (WorldCreator worldCreator : worldSet) {
            //TODO do this better
            if (defaultWorld == null) defaultWorld = worldCreator.getUuid();
            try {
                worlds.add(worldCreator.build(saveFolder));
            } catch (WorldLoadException e) {
                throw new SaveLoadException(e);
            }
        }
        SaveSettingsBuilder builder = new SaveSettingsBuilder();
        builder.setName(name);
        builder.setModInstances(mods);
        builder.setWorlds(worlds);
        builder.setDefaultWorld(defaultWorld);
        SaveSettings settings = builder.createSaveSettings();
        ClientSave.SAVE_SETTINGS_PARSER.toFile(saveFile, settings);
        return new ClientSave(saveFolder);
    }
}
