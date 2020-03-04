package org.kakara.engine.models;

import org.kakara.engine.item.Texture;
import org.kakara.engine.resources.Resource;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.events.UActionEvent;

import java.util.HashMap;
import java.util.Iterator;
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

    public Texture getTexture(String path, Scene currentScene) throws Exception {
        Texture texture = texturesMap.get(path);
        if (texture == null) {
            Resource resource = resourceManager.getResource(path);
            System.out.println(resource.toString());
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
