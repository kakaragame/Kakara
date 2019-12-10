package org.kakara.engine.test;

import org.kakara.engine.GameHandler;
import org.kakara.engine.Game;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.OnKeyPressEvent;
import org.kakara.engine.events.event.OnMouseClickEvent;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.item.Mesh;
import org.kakara.engine.utils.Utils;

import java.io.File;

import static org.lwjgl.glfw.GLFW.*;

public class KakaraTest implements Game {

    private GameHandler gInst;
    private boolean isCursorShowing;
    @Override
    public void start(GameHandler handler) throws Exception {
        gInst = handler;
        Mesh[] houseMesh = StaticModelLoader.load(Utils.getFileFromResource(Main.class.getResource("/player/steve.obj")), Utils.getFileFromResource(Main.class.getResource("/player/")));
        System.out.println("houseMesh = " + houseMesh.length);
        GameItem object = new GameItem(houseMesh);
        object.setPosition(0,5,0);

        gInst.getItemHandler().addObject(object);
        gInst.getEventManager().registerHandler(this);
        glfwSetInputMode(gInst.getWindow().getWindowHandler(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        isCursorShowing = false;
        gInst.getCamera().setPosition(0, 5, 0);
    }

    @Override
    public void update() {

        KeyInput ki = gInst.getKeyInput();

        if (ki.isKeyPressed(GLFW_KEY_W)) {
            gInst.getCamera().movePosition(0, 0, -1);
        }
        if (ki.isKeyPressed(GLFW_KEY_S)) {
            gInst.getCamera().movePosition(0, 0, 1);
        }
        if (ki.isKeyPressed(GLFW_KEY_A)) {
            gInst.getCamera().movePosition(-1, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_D)) {
            gInst.getCamera().movePosition(1, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_SPACE)) {
            gInst.getCamera().movePosition(0, 1, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            gInst.getCamera().movePosition(0, -1, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_ESCAPE)) {
            System.exit(1);
        }

        MouseInput mi = gInst.getMouseInput();
        // Gimmicky camera movements.
//        System.out.println(mi.getDeltaPosition());
        gInst.getCamera().moveRotation((float) (mi.getDeltaPosition().y), (float) mi.getDeltaPosition().x, 0);
//        gInst.getCamera().setRotation(-(float) (mi.getPosition().y - gInst.getWindow().getHeight()/2) * 0.3f, (float) mi.getPosition().x * 0.5f, 0);
    }

    @EventHandler
    public void onMouseClick(OnMouseClickEvent evt) {
        System.out.println("clicked1");
    }

    @EventHandler
    public void onKeyEvent(OnKeyPressEvent evt) {
        if (evt.isKeyPressed(GLFW_KEY_TAB)) {
            if (isCursorShowing) {
                glfwSetInputMode(gInst.getWindow().getWindowHandler(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                isCursorShowing = false;
            } else {
                glfwSetInputMode(gInst.getWindow().getWindowHandler(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                isCursorShowing = true;
            }
        }
    }
}
