package org.kakara.engine.test;

import org.kakara.engine.GameEngine;

public class Main {

    public static void main(String[] args){
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel","debug");
        KakaraTest kt = new KakaraTest();

        GameEngine gameEng = new GameEngine("Kakara Engine :: Test", 600, 480, true, kt);
        gameEng.run();
    }
}
