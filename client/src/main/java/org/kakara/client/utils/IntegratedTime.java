package org.kakara.client.utils;

/**
 * This class handles the time for the {@link org.kakara.client.game.IntegratedServer} class.
 */
public class IntegratedTime{
    private double lastLoopTime;

    private static float deltaTime;

    public void init(){
        lastLoopTime = getTime();
    }

    /**
     * Get the current delta time.
     * @return The delta time.
     */
    public double getTime(){
        return System.nanoTime() / 1000_000_000.0;
    }

    /**
     * the change in time between frames. (In milliseconds).
     * @return The time between frames (delta time).
     */
    public float getElapsedTime(){
        double time = getTime();
        float elapsedTime = (float) (time - lastLoopTime);
        lastLoopTime = time;
        IntegratedTime.deltaTime = elapsedTime;
        return elapsedTime;
    }

    public double getLastLoopTime(){
        return lastLoopTime;
    }

    /**
     * The time in between frames. (In milliseconds).
     * @return The time between frames. (In milliseconds).
     */
    public static float getDeltaTime(){
        return IntegratedTime.deltaTime;
    }
}
