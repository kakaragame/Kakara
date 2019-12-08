package org.kakara.engine;

import org.kakara.engine.gui.Window;
import org.kakara.engine.utils.Time;

/**
 * Handles the primary function of the game.
 */
public class GameEngine implements Runnable{
    public final int TARGET_FPS = 75;
    public final int TARGET_UPS = 30;

    private final Window window;
    private final Time time;

    public GameEngine(String windowTitle, int width, int height, boolean vSync){
        this.window = new Window(windowTitle, width, height, true, vSync);
        time = new Time();
    }

    /**
     * The main game loop.
     */
    @Override
    public void run() {
        init();
        gameLoop();

    }

    protected void init(){
        window.init();
        time.init();
    }

    protected void gameLoop(){
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f/TARGET_UPS;

        boolean running = true;
        while(running && !window.windowShouldClose()){
            elapsedTime = time.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while(accumulator >= interval){
                update(interval);
                accumulator -= interval;
            }

            render();

            if(!window.isvSync()){
                sync();
            }
        }
    }

    private void sync(){
        float loopSlot = 1f / TARGET_FPS;
        double endTime = time.getLastLoopTime() + loopSlot;
        while(time.getTime() < endTime){
            try{
                Thread.sleep(1);
            }catch (InterruptedException ie){

            }
        }
    }

    protected void input(){

    }

    protected void update(float interval){

    }

    protected void render(){
        window.update();
    }
    protected void cleanup(){

    }
}
