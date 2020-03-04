package org.kakara.engine;

import org.kakara.engine.collision.CollisionManager;
import org.kakara.engine.events.EventManager;
import org.kakara.engine.gui.Window;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.item.ItemHandler;
import org.kakara.engine.lighting.LightHandler;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.SceneManager;
import org.kakara.engine.sound.SoundManager;

/**
 * Handles the game information.
 */
public class GameHandler {

    private ItemHandler itemHandler;
    private Camera camera;
    private MouseInput mouseInput;
    private KeyInput keyInput;
    private EventManager eventManager;
    private CollisionManager collisionManager;
    private LightHandler lightHandler;
    private SceneManager sceneManager;
    private SoundManager soundManager;
    private static GameHandler gameHandler;
    private GameEngine gameEngine;
    private ResourceManager resourceManager;

    public GameHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        this.itemHandler = new ItemHandler();
        this.camera = new Camera();
        this.mouseInput = new MouseInput(this);
        this.keyInput = new KeyInput(gameEngine);
        this.eventManager = new EventManager(this);
        this.collisionManager = new CollisionManager(this);
        this.lightHandler = new LightHandler();
        this.sceneManager = new SceneManager(this);
        soundManager = new SoundManager();
        GameHandler.gameHandler = this;
        resourceManager = new ResourceManager();
    }

    /**
     * Handles the initialization of this class.
     */
    protected void init() {
        mouseInput.init(gameEngine.getWindow());
        keyInput.init();

    }

    /**
     * Updates anything needed
     */
    protected void update() {
        mouseInput.update();
    }

    /**
     * Get the object handler.
     *
     * @return The object handler.
     */
    public ItemHandler getItemHandler() {
        return itemHandler;
    }

    /**
     * Get the main camera.
     *
     * @return The main camera
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Get the mouse inputs
     *
     * @return The mouse inputs.
     */
    public MouseInput getMouseInput() {
        return mouseInput;
    }

    /**
     * Get the key inputs.
     *
     * @return The key inputs.
     */
    public KeyInput getKeyInput() {
        return keyInput;
    }

    /**
     * Get the current window.
     *
     * @return The current window.
     */
    public Window getWindow() {
        return gameEngine.getWindow();
    }

    /**
     * Get the EventManager
     *
     * @return The EventManager
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    public CollisionManager getCollisionManager() {
        return collisionManager;
    }

    public LightHandler getLightHandler() {
        return lightHandler;
    }

    public static GameHandler getInstance() {
        return gameHandler;
    }


    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }


    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}
