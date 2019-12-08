package org.kakara.client;

import org.kakara.engine.GameEngine;

public class Main {
    public static void main(String[] args) {
        GameEngine gameEng = new GameEngine("Kakara", 600, 480, true);
        gameEng.run();
    }
}
