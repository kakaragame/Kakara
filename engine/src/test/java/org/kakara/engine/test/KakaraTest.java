package org.kakara.engine.test;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.kakara.engine.GameHandler;
import org.kakara.engine.Game;
import org.kakara.engine.collision.BoxCollider;
import org.kakara.engine.collision.ObjectBoxCollider;
import org.kakara.engine.engine.CubeData;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.OnKeyPressEvent;
import org.kakara.engine.events.event.OnMouseClickEvent;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.item.*;
import org.kakara.engine.lighting.PointLight;
import org.kakara.engine.lighting.SpotLight;
import org.kakara.engine.math.KMath;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.engine.utils.Utils;

import java.io.InputStream;

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

    private GameItem gi1;

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


        gInst.getLightHandler().getSpotLight(0).setPosition(gInst.getCamera().getPosition());

        MouseInput mi = gInst.getMouseInput();
        gInst.getCamera().moveRotation((float) (mi.getDeltaPosition().y), (float) mi.getDeltaPosition().x, 0);
    }

    public void input() {
        KeyInput ki = gInst.getKeyInput();

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
