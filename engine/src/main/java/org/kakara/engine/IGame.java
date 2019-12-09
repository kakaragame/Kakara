package org.kakara.engine;

/**
 * Test interface which will be used as the main game class.
 */
public interface IGame {
    void start(GameEngine gameEngine) throws Exception;
    void update();
}
