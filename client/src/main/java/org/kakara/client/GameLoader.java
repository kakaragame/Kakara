package org.kakara.client;

import org.kakara.core.client.client.ClientSettings;
import org.kakara.engine.GameEngine;

public class GameLoader {
    public static void load(ClientSettings clientSettings) {
        KakaraGame kakaraGame;
        try {
            kakaraGame = new KakaraGame(clientSettings);
        } catch (Exception e) {
            KakaraGame.LOGGER.error("Unable to load Kakara", e);
            return;
        }
        // TODO add in a ability to select vSync mode.
        GameEngine gameEngine = new GameEngine("Kakara", 1080, 720, false, kakaraGame);
        gameEngine.run();
    }
}
