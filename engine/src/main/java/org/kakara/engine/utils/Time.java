package org.kakara.engine.utils;

public class Time {

    private double lastLoopTime;

    public static float deltaTime;

    public void init(){
        lastLoopTime = getTime();
    }

    public double getTime(){
        return System.nanoTime() / 1000_000_000.0;
    }

    public float getElapsedTime(){
        double time = getTime();
        float elapsedTime = (float) (time - lastLoopTime);
        lastLoopTime = time;
        return elapsedTime;
    }

    public double getLastLoopTime(){
        return lastLoopTime;
    }
}
