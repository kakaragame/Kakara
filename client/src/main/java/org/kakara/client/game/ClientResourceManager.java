package org.kakara.client.game;

import org.kakara.client.KakaraGame;
import org.kakara.client.MoreUtils;
import org.kakara.core.mod.Mod;
import org.kakara.core.resources.Resource;
import org.kakara.core.resources.ResourceType;
import org.kakara.engine.GameHandler;
import org.kakara.engine.gameitems.mesh.Mesh;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.game.resources.GameResourceManager;

public class ClientResourceManager extends GameResourceManager {
    public Mesh[] getModel(String resourceLocation, String textureLocation, Mod mod) {
        Resource resource = getResource(resourceLocation, ResourceType.MODEL, mod);
        Resource textureResource = getTexture(textureLocation, mod).get().get();
        try {
            return StaticModelLoader.load(MoreUtils.coreResourceToEngineResource(resource, KakaraGame.getInstance()), textureResource.getFile().getParentFile().getAbsolutePath(), GameHandler.getInstance().getSceneManager().getCurrentScene(), GameHandler.getInstance().getResourceManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
