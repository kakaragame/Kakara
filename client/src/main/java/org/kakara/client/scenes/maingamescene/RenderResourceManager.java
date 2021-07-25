package org.kakara.client.scenes.maingamescene;

import com.google.common.cache.CacheLoader;
import org.jetbrains.annotations.NotNull;
import org.kakara.client.scenes.CachingHashMap;
import org.kakara.engine.voxels.VoxelTexture;
import org.kakara.game.resources.GameResourceManager;

import java.util.Map;

/**
 * The ResourceManager for content used by the Engine.
 * <p>
 * For example, this stored VoxelTextures which are used by the engine and needs to be used to
 * render textures to the VoxelChunk or UI.
 * <p>
 * Obtain this through {@link MainGameScene#getRenderResourceManager()}.
 */
public class RenderResourceManager {
    private final MainGameScene gameScene;
    private final Map<String, VoxelTexture> renderTextureCache;

    /**
     * Construct the ResourceManager.
     *
     * @param gameScene The main game scene.
     */
    public RenderResourceManager(MainGameScene gameScene) {
        this.gameScene = gameScene;
        renderTextureCache = new CachingHashMap<>(new CacheLoader<>() {
            @Override
            public VoxelTexture load(@NotNull String s) {
                return getResource(GameResourceManager.correctPath(s));
            }
        });
    }

    /**
     * Get a resource via a String texture.
     *
     * @param texture The texture to get.
     * @return The VoxelTexture.
     */
    private VoxelTexture getResource(String texture) {
        return gameScene.getTextureAtlas().getTextures().stream().filter(renderTexture -> GameResourceManager.correctPath(renderTexture.getResource().getOriginalPath()).equals(GameResourceManager.correctPath(texture))).findFirst().orElseThrow(() -> new RuntimeException("Unable to find " + GameResourceManager.correctPath(texture)));
    }

    /**
     * Get the VoxelTexture based upon the local path.
     *
     * @param localPath The local path.
     * @return The VoxelTexture.
     */
    public VoxelTexture get(String localPath) {
        return renderTextureCache.get(localPath);
    }
}
