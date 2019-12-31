package org.kakara.engine.test;

import org.kakara.engine.GameHandler;
import org.kakara.engine.Game;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.OnKeyPressEvent;
import org.kakara.engine.events.event.OnMouseClickEvent;

import static org.lwjgl.glfw.GLFW.*;

public class KakaraTest implements Game {

    private GameHandler gInst;
    private MainGameScene gameScene;

    @Override
    public void start(GameHandler handler) throws Exception {
        gInst = handler;
        gameScene = new MainGameScene(handler);
        gInst.getSceneManager().setScene(gameScene);
    }


    @Override
    public void update() {

    }

    @EventHandler
    public void onMouseClick(OnMouseClickEvent evt) {
        System.out.println("clicked1");
    }

    @EventHandler
    public void onKeyEvent(OnKeyPressEvent evt) {
        if (evt.isKeyPressed(GLFW_KEY_TAB)) {
            //Engine API replaced GLFW methods.
            gInst.getWindow().setCursorVisibility(!gInst.getWindow().isCursorVisable());
            gInst.getMouseInput().setCursorPosition(gInst.getWindow().getWidth() / 2, gInst.getWindow().getHeight() / 2);
        }
    }
}
