package org.kakara.client.game;

import org.kakara.core.client.Save;
import org.kakara.core.client.SaveSettings;
import org.kakara.core.client.SaveSettingsBuilder;
import org.kakara.core.exceptions.SaveLoadException;
import org.kakara.core.exceptions.WorldLoadException;
import org.kakara.core.modinstance.ModInstance;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SaveCreator {
    private String name;
    private Set<ModInstance> mods = new HashSet<>();
    private Set<WorldCreator> worldSet = new HashSet<>();

    public SaveCreator setName(String name) {
        this.name = name;
        return this;
    }

    public SaveCreator add(ModInstance modInstance) {
        mods.add(modInstance);
        return this;
    }

    //TODO make this actually take more info about the world
    public SaveCreator add(WorldCreator world) {
        worldSet.add(world);
        return this;
    }

    public Save createSave() throws SaveLoadException {
        File saveFolder = new File(name);
            saveFolder.mkdirs();

        File saveFile = new File(saveFolder, "save.json");
        try {
            saveFile.createNewFile();
        } catch (IOException e) {
            throw new SaveLoadException(e);
        }
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