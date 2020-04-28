package org.kakara.client.game;

import org.kakara.core.client.Save;
import org.kakara.core.client.SaveSettings;
import org.kakara.core.world.World;

import java.io.File;
import java.util.List;
import java.util.Set;

public class ClientSave implements Save {
    private Set<World> worlds;

    @Override
    public void prepareWorlds() {

    }

    public void save() {

    }

    @Override
    public Set<World> getWorlds() {
        return worlds;
    }

    @Override
    public World getDefaultWorld() {
        return null;
    }

    @Override
    public SaveSettings getSettings() {
        return null;
    }

    @Override
    public File getSaveFolder() {
        return null;
    }

    @Override
    public List<File> getModsToLoad() {
        return null;
    }
}
