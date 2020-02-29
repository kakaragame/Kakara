package org.kakara.game.resources;

import org.apache.commons.io.FileUtils;
import org.kakara.core.GameType;
import org.kakara.core.KakaraCore;
import org.kakara.core.mod.Mod;
import org.kakara.core.resources.Resource;
import org.kakara.core.resources.ResourceManager;
import org.kakara.core.resources.ResourceType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class GameResourceManager implements ResourceManager {
    private KakaraCore kakaraCore;
    private File resourceDirectory;

    @Override
    public void registerResource(String path, ResourceType resourceType, Mod mod) {
        if (kakaraCore.getGameType() == GameType.SERVER) return;
        if (mod.getClass().getResource(path) == null) return;

        InputStream is = mod.getClass().getResourceAsStream(path);
        File dir = getModDir(mod, resourceType);
        dir.mkdirs();

        File localFile = new File(dir, correctPath(path));
        if (localFile.exists()) return;
        localFile.getParentFile().mkdirs();

        try {
            FileUtils.copyInputStreamToFile(is, localFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String correctPath(String s) {
        return s.replace("/", File.separator);
    }

    public File getModDir(Mod mod) {
        return new File(resourceDirectory, mod.getName().toLowerCase());
    }

    public File getModDir(Mod mod, ResourceType resourceType) {
        return new File(resourceDirectory, mod.getName().toLowerCase() + File.separator + resourceType.name().toString());
    }

    @Override
    public Resource getResource(String path, ResourceType resourceType, Mod mod) {
        if (kakaraCore.getGameType() == GameType.SERVER) return null;
        File file = new File(getModDir(mod, resourceType), correctPath(path));
        if (!file.exists()) return null;
        return new Resource(file);
    }

    @Override
    public void load(KakaraCore kakaraCore) {
        this.kakaraCore = kakaraCore;
        resourceDirectory = new File(kakaraCore.getWorkingDirectory(), "resources");
    }
}
