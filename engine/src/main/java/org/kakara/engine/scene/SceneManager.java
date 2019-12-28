package org.kakara.engine.scene;

import org.kakara.engine.GameHandler;

public class SceneManager {
    private Scene currentScene;
    private GameHandler handler;

    public SceneManager(GameHandler gameHandler) {
        this.handler = gameHandler;
    }

    public void setScene(Scene scene) {
        currentScene = scene;
    }

    public void renderCurrentScene() {
        currentScene.render();
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}
