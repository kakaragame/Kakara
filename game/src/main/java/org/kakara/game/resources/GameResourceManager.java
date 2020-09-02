package org.kakara.game.resources;

import org.kakara.core.GameInstance;
import org.kakara.core.Kakara;
import org.kakara.core.mod.Mod;
import org.kakara.core.resources.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class GameResourceManager implements ResourceManager {
    protected static final String BASE_PATH = "/resources/";
    protected GameInstance kakaraCore;
    protected File resourceDirectory;
    protected Map<Integer, Texture> textures = new HashMap<>();

    public GameResourceManager() {
    }

    //Helper Methods
    public static String correctPath(String s) {
        return s.replace("/", File.separator);
    }

    @Override
    public Set<Texture> getAllTextures() {
        return new HashSet<>(textures.values());
    }

    @Override
    public void registerResource(String s, ResourceType resourceType, Mod mod) {
        File resourceDirectory = getModDir(mod, resourceType);
        resourceDirectory.mkdirs();
        String path = BASE_PATH + resourceType.name().toLowerCase() + File.separator + s;
        File file = new File(resourceDirectory, correctPath(s));
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
    public void registerTexture(String s, TextureResolution i, Mod mod) {
        if (!s.endsWith(".png")) return;
        File directory = new File(getModDir(mod), "texture" + File.separator + String.valueOf(i.getResolution()));
        directory.mkdirs();
        String path = BASE_PATH + "texture/" + i.getResolution() + "/" + s;
        File file = new File(directory, correctPath(s));

        Texture texture = getTexture(s, mod).orElseGet(() -> {
            Texture texture1 = new Texture(s, mod);
            for (String s1 : getPropertiesForResource(s, mod)) {
                System.out.println(s1 + ":  " + s);
                texture1.addProperty(s1);
            }
            textures.put(texture1.hashCode(), texture1);
            return texture1;
        });
        texture.addResolution(i, new Resource(file, mod.getName().toLowerCase() + "/texture/" + i.getResolution() + "/" + s));
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

    private Set<String> getPropertiesForResource(String s, Mod mod) {
        Set<String> set = new HashSet<>();

        if (mod.getClass().getResource("/resources/resource.rules") == null) {
            return set;
        }
        Properties properties = new Properties();
        try {
            properties.load(mod.getClass().getResourceAsStream("/resources/resource.rules"));
        } catch (IOException e) {
            e.printStackTrace();
            return set;
        }
        if (!properties.containsKey(s)) return set;

        System.out.println("Working");
        String string = properties.getProperty(s);
        System.out.println(string);
        if (string.contains(","))
            Collections.addAll(set, string.split(","));
        else
            set.add(string);
        return set;
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
        if (getTexture(s, mod).isPresent()) {
            return getTexture(s, mod).get().getByResolution(i);
        }
        return null;
    }

    @Override
    public Optional<Texture> getTexture(String path, Mod mod) {
        return Optional.ofNullable(textures.get(Objects.hash(path, mod)));
    }

    public Resource getTexture(String s, TextureResolution i, Mod mod, ResourceCheckList resourceCheckList) {

        String path = mod.getName().toLowerCase() + "/texture/" + i.getResolution() + "/" + s;
        File directory = new File(resourceDirectory, correctPath(path));
        if (!directory.exists()) {
            resourceCheckList.checked(i);
            if (resourceCheckList.hasAllBeenChecked()) return null;
            if (i == TextureResolution._4) {
                return getTexture(s, TextureResolution._2048, mod, resourceCheckList);
            }
            return getTexture(s, TextureResolution.get(i.getResolution() / 2), mod, resourceCheckList);
        }
        return new Resource(directory, path);
    }

    @Override
    public void load(GameInstance kakaraCore) {
        this.kakaraCore = kakaraCore;
        resourceDirectory = new File(kakaraCore.getWorkingDirectory(), "resources");
        resourceDirectory.mkdirs();
    }

    public File getModDir(Mod mod) {
        return new File(resourceDirectory, mod.getName().toLowerCase());
    }

    public File getModDir(Mod mod, ResourceType resourceType) {
        return new File(getModDir(mod), resourceType.name().toLowerCase());
    }
}
