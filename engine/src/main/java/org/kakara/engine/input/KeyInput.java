package org.kakara.engine.input;

import org.kakara.engine.GameEngine;
import org.kakara.engine.GameHandler;
import org.kakara.engine.events.event.OnKeyPressEvent;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Handles Key input.
 */
public class KeyInput {

    private GameEngine engine;
    public KeyInput(GameEngine engine){
        this.engine = engine;
    }

    public void init(){
        glfwSetKeyCallback(engine.getWindow().getWindowHandler(), (window, key, scancode, action, mods)->{
            if(action != GLFW_PRESS) return;
            engine.getGameHandler().getEventManager().fireHandler(new OnKeyPressEvent(key));
        });
    }

    /**
     * If a key is currently pressed.
     * @param keycode The keycode value.
     * @return If it is pressed.
     */
    public boolean isKeyPressed(int keycode){
        return engine.getWindow().isKeyPressed(keycode);
    }
}
