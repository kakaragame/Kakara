package org.kakara.client.scenes;

import org.kakara.client.KakaraGame;
import org.kakara.engine.GameHandler;
import org.kakara.engine.scene.AbstractGameScene;

public class MainGameScene extends AbstractGameScene {
    private KakaraGame kakaraGame;

    public MainGameScene(GameHandler gameHandler, KakaraGame kakaraGame) {
        super(gameHandler);
        this.kakaraGame = kakaraGame;
        setCurserStatus(false);
    }

    @Override
    public void update() {

    }
}