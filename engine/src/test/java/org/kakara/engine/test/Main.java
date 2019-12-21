package org.kakara.engine.test;

import org.kakara.engine.GameEngine;
import org.kakara.engine.math.KMath;
import org.kakara.engine.math.Vector3;

public class Main {

    public static void main(String[] args){
//        System.out.println(new Vector3(1, 1, 1).greaterThan(new Vector3(1, 1, 1)));

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel","debug");
        KakaraTest kt = new KakaraTest();

        GameEngine gameEng = new GameEngine("Kakara Engine :: Test", 600, 480, true, kt);
        gameEng.run();
    }
}
