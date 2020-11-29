package org.kakara.client.game;

import org.jetbrains.annotations.NotNull;
import org.kakara.client.KakaraGame;
import org.kakara.client.game.world.ClientWorld;
import org.kakara.client.utils.ModUtils;

import org.kakara.core.client.client.Save;
import org.kakara.core.client.client.SaveSettings;
import org.kakara.core.client.client.SaveSettingsParser;
import org.kakara.core.client.client.parsers.JsonSaveSettingParser;
import org.kakara.core.common.exceptions.SaveLoadException;
import org.kakara.core.common.exceptions.WorldLoadException;
import org.kakara.core.common.modinstance.ModInstance;
import org.kakara.core.common.world.World;
import org.kakara.game.Server;

import java.io.File;
import java.util.*;

import static org.kakara.client.KakaraGame.LOGGER;

public class ClientSave implements Save {
    //TODO change to service provider
    public static final SaveSettingsParser SAVE_SETTINGS_PARSER = new JsonSaveSettingParser();
    @NotNull
    private final Set<World> worlds = new HashSet<>();
    @NotNull
    private final SaveSettings saveSettings;
    @NotNull
    private final File saveFolder;
    @NotNull
    private final UUID defaultWorldID;
    private Server server;

    public ClientSave(@NotNull File saveFolder) throws SaveLoadException {
        this.saveFolder = saveFolder;
        saveSettings = SAVE_SETTINGS_PARSER.fromFile(new File(saveFolder, "save.json"));
        defaultWorldID = saveSettings.getDefaultWorld();
    }

    @Override
    public void prepareWorlds() throws WorldLoadException {
        for (String worldName : saveSettings.getWorlds()) {
            LOGGER.info("Loading " + worldName);
            ClientWorld world = new ClientWorld(new File(saveFolder, worldName), server);

            worlds.add(world);
        }
    }

    public void save() {

    }

    @Override
    public Set<World> getWorlds() {
        return worlds;
    }

    @Override
    public World getDefaultWorld() {
        return worlds.stream().filter(world -> world.getUUID().equals(defaultWorldID)).findFirst().orElseThrow(RuntimeException::new);
    }

    @Override
    public SaveSettings getSettings() {
        return saveSettings;
    }

    @Override
    public File getSaveFolder() {
        return saveFolder;
    }

    @Override
    public List<File> getModsToLoad() {
        List<File> mods = new ArrayList<>();
        for (ModInstance modInstance : saveSettings.getModInstances()) {
            var mod = ModUtils.prepareModInstance(modInstance);
            if (!mod.getModFile().exists()) try {
                throw new SaveLoadException("Unable to locate mod file");
            } catch (SaveLoadException e) {
                KakaraGame.LOGGER.error("Unable to locate ModFile", e);
            }
            mods.add(mod.getModFile());
        }
        return mods;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void removeWorld(World world) {
        worlds.remove(world);
    }
}
