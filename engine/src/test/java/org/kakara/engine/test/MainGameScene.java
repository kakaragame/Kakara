package org.kakara.engine.test;

import org.kakara.engine.GameHandler;
import org.kakara.engine.collision.BoxCollider;
import org.kakara.engine.collision.ObjectBoxCollider;
import org.kakara.engine.item.*;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.utils.Utils;

import java.io.InputStream;

public class MainGameScene extends AbstractGameScene {
    private GameItem player;

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

        Mesh mesh = new Mesh(Block.BLOCK_POS, Block.TEXT_CORDS, new float[0], Block.INDICES);
        InputStream io = Texture.class.getResourceAsStream("/grassblock.png");
        Texture grass = Utils.inputStreamToTexture(io);
        mesh.setMaterial(new Material(grass));
        MeshGameItem gi = new MeshGameItem(mesh);
        addItem(gi);
        gi.setPosition(0, 0, -5);


        for (int x = 5; x > -6; x--) {
            for (int z = 5; z > -6; z--) {
                MeshGameItem gis = (MeshGameItem) gi.clone(false);
                gis.setPosition(x, 0, z);
                gis.setCollider(new ObjectBoxCollider(false, true));
                getItemHandler().addItem(gis);
            }
        }

    }

    public GameItem getPlayer() {
        return player;
    }
}
