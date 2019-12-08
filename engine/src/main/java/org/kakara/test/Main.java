package org.kakara.test;

import org.kakara.engine.GameEngine;

public class Main {

    public static void main(String[] args){
        KakaraTest kt = new KakaraTest();
        GameEngine gameEng = new GameEngine("The more you know", 600, 480, true, kt);
        gameEng.run();
    }
}
