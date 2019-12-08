package test;

import org.kakara.engine.GameEngine;

public class Main {

    public static void main(String[] args){
        GameEngine gameEng = new GameEngine("The more you know", 600, 480, true);
        gameEng.run();
    }
}
