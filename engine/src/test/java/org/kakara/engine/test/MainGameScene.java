package org.kakara.engine.test;

import org.joml.Vector3f;
import org.kakara.engine.GameHandler;
import org.kakara.engine.collision.BoxCollider;
import org.kakara.engine.collision.ObjectBoxCollider;
import org.kakara.engine.engine.CubeData;
import org.kakara.engine.item.*;
import org.kakara.engine.lighting.PointLight;
import org.kakara.engine.lighting.SpotLight;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.utils.Utils;

import java.io.InputStream;

public class MainGameScene extends AbstractGameScene {
    private GameItem player;
    private PointLight light;
    private GameItem lightIndication;

    public MainGameScene(GameHandler gameHandler) throws Exception {
        super(gameHandler);
        setCurserStatus(false);
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
        InputStream io = Texture.class.getResourceAsStream("/oop.png");
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
        gameHandler.getLightHandler().addPointLight(light);
        gameHandler.getLightHandler().addPointLight(new PointLight().setPosition(0, 3, 0).setDiffuse(0.1f, 0, 0).setSpecular(0.5f, 0, 0));
        gameHandler.getLightHandler().addPointLight(new PointLight().setPosition(3, 3, 3).setDiffuse(0f, 0.3f, 0).setSpecular(0, 0, 0.7f));
        gameHandler.getLightHandler().addSpotLight(new SpotLight(gameHandler.getCamera().getPosition(), new Vector3(0, 0, 1)));
        gameHandler.getItemHandler().addItem(lightIndication);
        // Allows you to see the light.
        gameHandler.getLightHandler().getDirectionalLight().setDirection(0, 1, 0);

    }

    public GameItem getPlayer() {
        return player;
    }
}
