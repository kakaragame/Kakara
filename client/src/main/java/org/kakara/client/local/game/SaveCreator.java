package org.kakara.client.local.game;


import org.kakara.core.client.client.Save;
import org.kakara.core.client.client.SaveSettings;
import org.kakara.core.client.client.SaveSettingsBuilder;
import org.kakara.core.common.exceptions.SaveLoadException;
import org.kakara.core.common.exceptions.WorldLoadException;
import org.kakara.core.common.modinstance.ModInstance;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * This is the builder for save data.
 *
 * <p>Used by default in the MainMenuScene#singlePlayerClick().</p>
 */
public class SaveCreator {
    private String name;
    private final Set<ModInstance> mods = new HashSet<>();
    private final Set<WorldCreator> worldSet = new HashSet<>();

    /**
     * Set the name of the save data.
     *
     * @param name The name of the save data.
     * @return The current instance of the SaveCreator.
     */
    public SaveCreator setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Add a ModInstance to the SaveCreator.
     *
     * @param modInstance The mod instance to add.
     * @return The current instance of the SaveCreator.
     */
    public SaveCreator add(ModInstance modInstance) {
        mods.add(modInstance);
        return this;
    }

    /**
     * Add a WorldCreator to the SaveCreator.
     *
     * @param world The World Creator to add.
     * @return The current instance of the save creator.
     */
    //TODO make this actually take more info about the world
    public SaveCreator add(WorldCreator world) {
        worldSet.add(world);
        return this;
    }

    /**
     * Create the save within the file system itself. ({@link ClientSave})
     *
     * <p>This will build all provided worlds.</p>
     *
     * <p>Note: If the Save already exists, the existing save is returned.</p>
     *
     * @return The build Save class.
     * @throws SaveLoadException If an error occurs while creating the save.
     */
    public Save createSave() throws SaveLoadException {
        File saveFolder = new File(name);
        saveFolder.mkdirs();

        File saveFile = new File(saveFolder, "save.json");

        // If the save file exists, don't create a default one.
        if (saveFile.exists()) return new ClientSave(saveFolder);

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
