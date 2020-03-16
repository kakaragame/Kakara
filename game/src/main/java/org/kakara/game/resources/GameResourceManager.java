package org.kakara.game.resources;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kakara.core.GameInstance;
import org.kakara.core.Kakara;
import org.kakara.core.mod.Mod;
import org.kakara.core.resources.Resource;
import org.kakara.core.resources.ResourceManager;
import org.kakara.core.resources.ResourceType;
import org.kakara.core.resources.TextureResolution;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.MissingResourceException;

public class GameResourceManager implements ResourceManager {
    private GameInstance kakaraCore;
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
            InputStream io = mod.getClass().getResourceAsStream(path);
            if (io == null) {
                throw new MissingResourceException("Unable to locate: " + path, mod.getName(), path);
            }
            IOUtils.copy(io, new FileWriter(file), Charset.defaultCharset());
        } catch (IOException e) {
            Kakara.LOGGER.error("Unable to copy file to local", e);
        }
    }


    @Override
    public void registerTexture(String s, TextureResolution i, Mod mod) {
        File directory = new File(getModDir(mod), "texture" + File.separator + String.valueOf(i.getResolution()));
        directory.mkdirs();
        String path = BASE_PATH + "texture/" + i.getResolution() + "/" + s;
        File file = new File(directory, correctPath(s));
        if (file.exists()) return;
        file.getParentFile().mkdirs();

        try {
            InputStream io = mod.getClass().getResourceAsStream(path);
            if (io == null) {
                throw new MissingResourceException("Unable to locate: " + path, mod.getName(), path);
            }
            Files.copy(io, file.toPath());
        } catch (IOException e) {
            Kakara.LOGGER.error("Unable to copy file to local", e);
        }
    }

    @Override
    public Resource getResource(String s, ResourceType resourceType, Mod mod) {
        File resoureFile = new File(getModDir(mod, resourceType), correctPath(s));
        if (!resoureFile.exists()) {
            //TODO decide what to do
        }
        return new Resource(resoureFile, s);
    }


    @Override
    public Resource getTexture(String s, TextureResolution i, Mod mod) {
        String path = mod.getName().toLowerCase() + "/texture/" + i.getResolution() + "/" + s;
        File directory = new File(resourceDirectory, correctPath(path));
        if (!directory.exists()) {
            if (i == TextureResolution._4) {
                return getTexture(s, TextureResolution._1024, mod);
            }
            return getTexture(s, TextureResolution.get(i.getResolution() / 2), mod);
        }
        return new Resource(directory, path);
    }

    @Override
    public void load(GameInstance kakaraCore) {
        this.kakaraCore = kakaraCore;
        resourceDirectory = new File(kakaraCore.getWorkingDirectory(), "resources");
        resourceDirectory.mkdirs();
    }

    //Helper Methods
    public String correctPath(String s) {
        return s.replace("/", File.separator);
    }

    public File getModDir(Mod mod) {
        return new File(resourceDirectory,  mod.getName().toLowerCase());
    }

    public File getModDir(Mod mod, ResourceType resourceType) {
        return new File(getModDir(mod), resourceType.name());
    }
}
