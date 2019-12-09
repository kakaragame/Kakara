package org.kakara.engine;

/**
 * Test interface which will be used as the main game class.
 */
public interface IGame {
    void start(GameHandler gameHandler) throws Exception;
    void update();
}
