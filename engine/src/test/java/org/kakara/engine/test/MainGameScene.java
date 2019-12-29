package org.kakara.engine.test;

import org.joml.Vector3f;
import org.kakara.engine.GameHandler;
import org.kakara.engine.collision.BoxCollider;
import org.kakara.engine.collision.ObjectBoxCollider;
import org.kakara.engine.engine.CubeData;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.item.*;
import org.kakara.engine.lighting.PointLight;
import org.kakara.engine.lighting.SpotLight;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.utils.Utils;

import java.io.InputStream;

import static org.lwjgl.glfw.GLFW.*;

public class MainGameScene extends AbstractGameScene {
    private GameItem player;
    private GameHandler handler;
    private PointLight light;
    private GameItem lightIndication;

    public MainGameScene(GameHandler gameHandler) throws Exception {
        super(gameHandler);
        setCurserStatus(false);
        gameHandler.getCamera().setPosition(0, 3, 0);
        Mesh[] mainPlayer = StaticModelLoader.load(Utils.getFileFromResource(Main.class.getResource("/player/steve.obj")), Utils.getFileFromResource(Main.class.getResource("/player/")));
        MeshGameItem object = new MeshGameItem(mainPlayer);
        object.setPosition(4, 100f, 4);
        object.setScale(0.3f);
        object.setCollider(new BoxCollider(new Vector3(0, 0, 0), new Vector3(1, 1.5f, 1), true));
        object.getCollider().setUseGravity(true).setTrigger(false);
        ((BoxCollider) object.getCollider()).setOffset(new Vector3(0, 0.7f, 0));
        addItem(object);
        player = object;
        //Load Blocks

        Mesh mesh = new Mesh(CubeData.vertex, CubeData.texture, CubeData.normal, CubeData.indices);
        InputStream io = Texture.class.getResourceAsStream("/example_texture.png");
        Texture grass = Utils.inputStreamToTexture(io);
        mesh.setMaterial(new Material(grass));
        MeshGameItem gi = new MeshGameItem(mesh);
        addItem(gi);
        gi.setPosition(0, 0, -5);


        for (int x = 5; x > -12; x--) {
            for (int z = 5; z > -12; z--) {
                MeshGameItem gis = (MeshGameItem) gi.clone(false);
                gis.setPosition(x, 0, z);
                gis.setCollider(new ObjectBoxCollider(false, true));
                getItemHandler().addItem(gis);
            }
        }

        light = new PointLight(new Vector3f(0, 2, 0));
        lightIndication = new MeshGameItem(mesh).setScale(0.3f).setPosition(0, 2, 0);
        add(light);
        this.add(new PointLight().setPosition(0, 3, 0).setDiffuse(0.1f, 0, 0).setSpecular(0.5f, 0, 0));
        this.add(new PointLight().setPosition(3, 3, 3).setDiffuse(0f, 0.3f, 0).setSpecular(0, 0, 0.7f));
        this.add(new SpotLight(gameHandler.getCamera().getPosition(), new Vector3(0, 0, 1)));
        this.add(lightIndication);
        // Allows you to see the light.
        this.getLightHandler().getDirectionalLight().setDirection(0, 1, 0);
        this.handler = gameHandler;
    }

    public GameItem getPlayer() {
        return player;
    }

    @Override
    public void update() {
        KeyInput ki = handler.getKeyInput();

        if (ki.isKeyPressed(GLFW_KEY_W)) {
            handler.getCamera().movePosition(0, 0, -1);
        }
        if (ki.isKeyPressed(GLFW_KEY_S)) {
            handler.getCamera().movePosition(0, 0, 1);
        }
        if (ki.isKeyPressed(GLFW_KEY_A)) {
            handler.getCamera().movePosition(-1, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_D)) {
            handler.getCamera().movePosition(1, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_SPACE)) {
            handler.getCamera().movePosition(0, 1, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            handler.getCamera().movePosition(0, -1, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_ESCAPE)) {
            System.exit(1);
        }

        Vector3 currentPos = player.getPosition();
        if(ki.isKeyPressed(GLFW_KEY_UP)){
            player.translateBy(0.1f, 0, 0);
        }
        if(ki.isKeyPressed(GLFW_KEY_DOWN)){
            player.setPosition(currentPos.x - 0.1f, currentPos.y, currentPos.z);
        }
        if(ki.isKeyPressed(GLFW_KEY_LEFT)){
            player.setPosition(currentPos.x, currentPos.y, currentPos.z + 0.1f);
        }
        if(ki.isKeyPressed(GLFW_KEY_RIGHT)){
            player.setPosition(currentPos.x, currentPos.y, currentPos.z - 0.1f);
        }

        if(ki.isKeyPressed(GLFW_KEY_I)){
            lightIndication.translateBy(0, 0, 1);
        }
        if(ki.isKeyPressed(GLFW_KEY_K)){
            lightIndication.translateBy(0, 0, -1);
        }
        if(ki.isKeyPressed(GLFW_KEY_J)){
            lightIndication.translateBy(-1, 0, 0);
        }
        if(ki.isKeyPressed(GLFW_KEY_L)){
            lightIndication.translateBy(1, 0, 0);
        }

        light.setPosition(lightIndication.getPosition());


        getLightHandler().getSpotLight(0).setPosition(handler.getCamera().getPosition());

        MouseInput mi = handler.getMouseInput();
        handler.getCamera().moveRotation((float) (mi.getDeltaPosition().y), (float) mi.getDeltaPosition().x, 0);
    }
}
