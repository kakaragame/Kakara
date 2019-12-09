package org.kakara.engine;

import org.kakara.engine.gui.Window;
import org.kakara.engine.objects.GameObject;
import org.kakara.engine.render.Renderer;
import org.kakara.engine.utils.Time;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Handles the primary function of the game.
 */
public class GameEngine implements Runnable{
    public final int TARGET_FPS = 75;
    public final int TARGET_UPS = 30;

    private final Window window;
    private final Time time;

    private final IGame game;
    private final Renderer renderer;
    private final GameHandler gameHandler;

    public GameEngine(String windowTitle, int width, int height, boolean vSync, IGame game){
        this.window = new Window(windowTitle, width, height, true, vSync);
        time = new Time();
        this.game = game;
        this.renderer = new Renderer();
        this.gameHandler = new GameHandler(this);

    }

    /**
     * The main game loop.
     */
    @Override
    public void run() {
        try{
            init();
            gameLoop();
        }finally{
            cleanup();
        }

    }

    protected void init(){
        window.init();
        try {
            renderer.init(window);
            time.init();
            game.start(gameHandler);
            gameHandler.init();
        }catch(Exception ex){
            ex.printStackTrace();
        }

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
        if(window.isKeyPressed(GLFW_KEY_UP)){

        }
    }

    protected void update(float interval){
        gameHandler.update();
        game.update();
    }

    protected void render(){
        renderer.render(window, gameHandler.getObjectHandler().getObjectList(), gameHandler.getCamera());
        window.update();
    }
    protected void cleanup(){
        renderer.cleanup();
        for(GameObject gameObject : gameHandler.getObjectHandler().getObjectList()){
            gameObject.cleanup();
        }
    }

    public Window getWindow(){return window;}
    public GameHandler getGameHandler(){return gameHandler;}
}
