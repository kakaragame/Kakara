package org.kakara.engine.models;

import org.kakara.engine.item.Texture;
import org.kakara.engine.resources.ResourceManager;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {
    private static TextureCache INSTANCE;

    private Map<String, Texture> texturesMap;
    private ResourceManager resourceManager;

    private TextureCache(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        texturesMap = new HashMap<>();
    }

    public static synchronized TextureCache getInstance(ResourceManager resourceManager) {
        if (INSTANCE == null) {
            INSTANCE = new TextureCache(resourceManager);
        }
        return INSTANCE;
    }

    public Texture getTexture(String path) throws Exception {
        Texture texture = texturesMap.get(path);
        if (texture == null) {
            texture = new Texture(resourceManager.getResource(path));
            texturesMap.put(path, texture);
        }
        return texture;
    }
}
