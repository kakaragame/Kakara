package org.kakara.engine.test;

import org.kakara.engine.GameHandler;
import org.kakara.engine.Game;
import org.kakara.engine.collision.BoxCollider;
import org.kakara.engine.collision.ObjectBoxCollider;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.OnKeyPressEvent;
import org.kakara.engine.events.event.OnMouseClickEvent;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.item.Material;
import org.kakara.engine.item.Texture;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.item.Mesh;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.utils.Utils;

import java.io.InputStream;

import static org.lwjgl.glfw.GLFW.*;

public class KakaraTest implements Game {

    private GameHandler gInst;

    private MainGameScene gameScene;

    @Override
    public void start(GameHandler handler) throws Exception {
        gInst = handler;

        gInst.getEventManager().registerHandler(this);
        // Added engine API for the cursor GLFW method.
        gameScene = new MainGameScene(handler);
        gInst.getSceneManager().setScene(gameScene);


        // Sets the default camera position and rotation.
        gInst.getCamera().setPosition(5, 5, 0);
        gInst.getCamera().setRotation(45, 270, 0);

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
        Vector3 currentPos = gameScene.getPlayer().getPosition();
        if(ki.isKeyPressed(GLFW_KEY_UP)){
            gameScene.getPlayer().setPosition(currentPos.x + 0.1f, currentPos.y, currentPos.z);
        }
        if(ki.isKeyPressed(GLFW_KEY_DOWN)){
            gameScene.getPlayer().setPosition(currentPos.x - 0.1f, currentPos.y, currentPos.z);
        }
        if(ki.isKeyPressed(GLFW_KEY_LEFT)){
            gameScene.getPlayer().setPosition(currentPos.x, currentPos.y, currentPos.z + 0.1f);
        }
        if(ki.isKeyPressed(GLFW_KEY_RIGHT)){
            gameScene.getPlayer().setPosition(currentPos.x, currentPos.y, currentPos.z - 0.1f);
        }
        if(ki.isKeyPressed(GLFW_KEY_N)){
            gameScene.getPlayer().setPosition(currentPos.x, currentPos.y + 0.1f, currentPos.z);
        }
        if(ki.isKeyPressed(GLFW_KEY_M)){
            gameScene.getPlayer().setPosition(currentPos.x, currentPos.y - 0.1f, currentPos.z);
        }

        MouseInput mi = gInst.getMouseInput();
        gInst.getCamera().moveRotation((float) (mi.getDeltaPosition().y), (float) mi.getDeltaPosition().x, 0);
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


        Vector3 currentPos = gameScene.getPlayer().getPosition();
        if(evt.isKeyPressed(GLFW_KEY_UP)){
            gameScene.getPlayer().setPosition(currentPos.x + 0.1f, currentPos.y, currentPos.z);
        }
        if(evt.isKeyPressed(GLFW_KEY_DOWN)){
            gameScene.getPlayer().setPosition(currentPos.x - 0.1f, currentPos.y, currentPos.z);
        }
        if(evt.isKeyPressed(GLFW_KEY_LEFT)){
            gameScene.getPlayer().setPosition(currentPos.x, currentPos.y, currentPos.z + 0.1f);
        }
        if(evt.isKeyPressed(GLFW_KEY_RIGHT)){
            gameScene.getPlayer().setPosition(currentPos.x, currentPos.y, currentPos.z - 0.1f);
        }
        if(evt.isKeyPressed(GLFW_KEY_N)){
            gameScene.getPlayer().setPosition(currentPos.x, currentPos.y + 0.1f, currentPos.z);
        }
        if(evt.isKeyPressed(GLFW_KEY_M)){
            gameScene.getPlayer().setPosition(currentPos.x, currentPos.y - 0.1f, currentPos.z);
        }

    }
}
