package org.kakara.client.scenes;

import org.kakara.client.KakaraGame;
import org.kakara.engine.GameHandler;
import org.kakara.engine.scene.AbstractGameScene;

public class MainMenuScene extends AbstractGameScene {
    private KakaraGame kakaraGame;

    public MainMenuScene(GameHandler gameHandler, KakaraGame kakaraGame) {
        super(gameHandler);

        this.kakaraGame = kakaraGame;
    }

    @Override
    public void update() {

    }
}
