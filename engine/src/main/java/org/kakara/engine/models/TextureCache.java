package org.kakara.engine.models;

import org.kakara.engine.item.Texture;
import org.kakara.engine.resources.Resource;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.Scene;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TextureCache {
    private static TextureCache instance;

    private Map<String, Texture> texturesMap;
    private ResourceManager resourceManager;

    private TextureCache(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        texturesMap = new HashMap<>();
    }

    public static synchronized TextureCache getInstance(ResourceManager resourceManager) {
        if (instance == null) {
            instance = new TextureCache(resourceManager);
        }
        return instance;
    }

    public Texture getTexture(String path, Scene currentScene) throws MalformedURLException {
        Texture texture = texturesMap.get(path);
        if (texture == null) {
            Resource resource = resourceManager.getResource(path);
            texture = new Texture(resource, currentScene);

            texturesMap.put(path, texture);
        }
        return texture;
    }

    /**
     * Remove unused textures from the scene.
     * @param scene
     */
    public void cleanup(Scene scene){
        Iterator<Map.Entry<String, Texture>> it = texturesMap.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<String, Texture> text = it.next();
            if(text.getValue().getCurrentScene() == scene){
                text.getValue().cleanup();
                it.remove();
            }
        }
    }
}
