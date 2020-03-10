package org.kakara.engine.scene;

import org.kakara.engine.GameHandler;
import org.kakara.engine.models.TextureCache;

public class SceneManager {
    private Scene currentScene;
    private GameHandler handler;

    public SceneManager(GameHandler gameHandler) {
        this.handler = gameHandler;
    }

    public void setScene(Scene scene) {
        if(currentScene != null)
            this.cleanupScenes();
        scene.work();
        scene.unload();
        try {
            handler.getGameEngine().resetRender();
        } catch (Exception e) {
            e.printStackTrace();
        }
        scene.loadGraphics();
        currentScene = scene;
    }

    public void renderCurrentScene() {
        currentScene.render();
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * Cleanup the scene and clear the memory that way it is ready for the next scene to be loaded.
     */
    public void cleanupScenes(){
        handler.getEventManager().cleanup();
        currentScene.getHUD().cleanup();
        TextureCache.getInstance(handler.getResourceManager()).cleanup(currentScene);
        if(getCurrentScene() instanceof AbstractMenuScene) return;
        currentScene.getItemHandler().cleanup();
    }
}
