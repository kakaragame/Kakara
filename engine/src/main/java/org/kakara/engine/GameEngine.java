package org.kakara.engine;

import org.kakara.engine.collision.Collidable;
import org.kakara.engine.gui.Window;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.render.Renderer;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.scene.AbstractMenuScene;
import org.kakara.engine.utils.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Handles the primary function of the game.
 */
public class GameEngine implements Runnable {
    public final int TARGET_FPS = 75;
    public final int TARGET_UPS = 30;
    //WE will change this to the games logger in the impl.
    public static Logger LOGGER = LoggerFactory.getLogger(GameEngine.class);
    private final Window window;
    private final Time time;

    private final Game game;
    private Renderer renderer;
    private final GameHandler gameHandler;
    protected boolean running = true;

    private Queue<Runnable> mainThreadQueue = new LinkedList<>();

    public GameEngine(String windowTitle, int width, int height, boolean vSync, Game game) {
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
        try {
            init();
            gameLoop();
        } finally {
            cleanup();
        }

    }

    protected void init() {
        window.init();
        try {
            renderer.init();
            time.init();
            game.start(gameHandler);
            gameHandler.init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        while (running && !window.windowShouldClose()) {
            elapsedTime = time.getElapsedTime();
            Time.deltaTime = elapsedTime;
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if (!window.isvSync()) {
                sync();
            }
        }
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = time.getLastLoopTime() + loopSlot;
        while (time.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    protected void input() {
    }

    protected void update(float interval) {
        gameHandler.update();
        gameHandler.getSceneManager().getCurrentScene().update(interval);
        game.update();
        collide();
    }

    protected void render() {
        gameHandler.getSceneManager().renderCurrentScene();
        window.update();
        if(!mainThreadQueue.isEmpty()){
            mainThreadQueue.poll().run();
        }
    }

    protected void cleanup() {
        if(gameHandler.getSceneManager().getCurrentScene() instanceof AbstractMenuScene) return;
        if(gameHandler.getSceneManager().getCurrentScene() == null) return;
        renderer.cleanup();
        if(gameHandler.getSceneManager().getCurrentScene()== null) return;
        gameHandler.getSceneManager().getCurrentScene().getItemHandler().cleanup();
    }

    protected void collide() {
        for (Collidable gi : gameHandler.getCollisionManager().getCollidngItems(null)) {
            gi.getCollider().update();
        }
    }

    public Window getWindow() {
        return window;
    }

    public final Renderer getRenderer() {
        return renderer;
    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }

    public void resetRender() throws Exception {
        renderer = new Renderer();
        renderer.init();
    }

    public void addQueueItem(Runnable run){
        mainThreadQueue.add(run);
    }


}
