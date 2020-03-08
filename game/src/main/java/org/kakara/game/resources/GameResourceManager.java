package org.kakara.game.resources;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kakara.core.GameType;
import org.kakara.core.Kakara;
import org.kakara.core.KakaraCore;
import org.kakara.core.mod.Mod;
import org.kakara.core.resources.Resource;
import org.kakara.core.resources.ResourceManager;
import org.kakara.core.resources.ResourceType;
import org.kakara.core.resources.TextureResolution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class GameResourceManager implements ResourceManager {
    private KakaraCore kakaraCore;
    private File resourceDirectory;
    private static final String BASE_PATH = "/resources/";

    public GameResourceManager() {
    }

    @Override
    public void registerResource(String s, ResourceType resourceType, Mod mod) {
        File resourceDirectory = getModDir(mod, resourceType);
        resourceDirectory.mkdirs();
        String path = BASE_PATH + resourceType.name() + s;
        File file = new File(resourceDirectory, correctPath(s));
        if (file.exists()) return;
        file.getParentFile().mkdirs();
        try {
            IOUtils.copy(Mod.class.getResourceAsStream(path), new FileWriter(file), Charset.defaultCharset());
        } catch (IOException e) {
            KakaraCore.LOGGER.error("Unable to copy file to local", e);
        }
    }


    @Override
    public void registerTexture(String s, TextureResolution i, Mod mod) {
        File directory = new File(getModDir(mod), String.valueOf(i.getResolution()));
        directory.mkdirs();
        String path = BASE_PATH + i + s;
        File file = new File(resourceDirectory, correctPath(s));
        if (file.exists()) return;
        file.getParentFile().mkdirs();
        try {
            IOUtils.copy(Mod.class.getResourceAsStream(path), new FileWriter(file), Charset.defaultCharset());
        } catch (IOException e) {
            KakaraCore.LOGGER.error("Unable to copy file to local", e);
        }
    }

    @Override
    public Resource getResource(String s, ResourceType resourceType, Mod mod) {
        File resoureFile = new File(getModDir(mod, resourceType), correctPath(s));
        if (!resoureFile.exists()) {
            //TODO decide what to do
        }
        return new Resource(resoureFile);
    }


    @Override
    public Resource getTexture(String s, TextureResolution i, Mod mod) {
        File directory = new File(new File(getModDir(mod), String.valueOf(i.getResolution())), s);
        if (!directory.exists()) {
            if (i == TextureResolution._4) {
                return getTexture(s, TextureResolution._1024, mod);
            }
            return getTexture(s, TextureResolution.get(i.getResolution() / 2), mod);
        }
        return new Resource(directory);
    }

    @Override
    public void load(KakaraCore kakaraCore) {
        this.kakaraCore = kakaraCore;
        resourceDirectory.mkdirs();
    }

    //Helper Methods
    public String correctPath(String s) {
        return s.replace("/", File.separator);
    }

    public File getModDir(Mod mod) {
        return new File(resourceDirectory, mod.getName().toLowerCase());
    }

    public File getModDir(Mod mod, ResourceType resourceType) {
        return new File(getModDir(mod), resourceType.name());
    }
}
