package org.kakara.client.scenes.maingamescene;

import com.google.common.cache.CacheLoader;
import org.kakara.client.scenes.CachingHashMap;
import org.kakara.engine.renderobjects.RenderTexture;
import org.kakara.game.resources.GameResourceManager;

import java.util.Map;

public class RenderResourceManager {
    private MainGameScene gameScene;
    private Map<String, RenderTexture> renderTextureCache;

    public RenderResourceManager(MainGameScene gameScene) {
        this.gameScene = gameScene;
        renderTextureCache = new CachingHashMap<>(new CacheLoader<>() {
            @Override
            public RenderTexture load(String s) throws Exception {
                return getResource(GameResourceManager.correctPath(s));
            }
        });
    }

    private RenderTexture getResource(String texture) {
        return gameScene.getTextureAtlas().getTextures().stream().filter(renderTexture -> GameResourceManager.correctPath(renderTexture.getResource().getOriginalPath()).equals(GameResourceManager.correctPath(texture))).findFirst().orElseThrow(() -> new RuntimeException("Unable to find " + GameResourceManager.correctPath(texture)));
    }

    public RenderTexture get(String localPath) {
        return renderTextureCache.get(localPath);
    }
}
