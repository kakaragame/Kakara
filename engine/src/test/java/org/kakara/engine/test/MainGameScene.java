package org.kakara.engine.test;

import org.kakara.engine.collision.BoxCollider;
import org.kakara.engine.collision.ObjectBoxCollider;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.item.Material;
import org.kakara.engine.item.Mesh;
import org.kakara.engine.item.Texture;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.utils.Utils;

import java.io.InputStream;
import java.net.URISyntaxException;

public class MainGameScene extends AbstractGameScene {
    public MainGameScene() throws Exception {
        setMouseStatus(false);
        Mesh[] mainPlayer = StaticModelLoader.load(Utils.getFileFromResource(Main.class.getResource("/player/steve.obj")), Utils.getFileFromResource(Main.class.getResource("/player/")));
        GameItem object = new GameItem(mainPlayer);
        object.setPosition(4, 100f, 4).setScale(0.3f).setCollider(new BoxCollider(new Vector3(0, 0, 0), new Vector3(1, 1.5f, 1), true));
        object.getCollider().setUseGravity(true).setTrigger(false);
        ((BoxCollider) object.getCollider()).setOffset(new Vector3(0, 0.7f, 0));
        addItem(object);

        //Load Blocks

        Mesh mesh = new Mesh(Block.BLOCK_POS, Block.TEXT_CORDS, new float[0], Block.INDICES);
        InputStream io = Texture.class.getResourceAsStream("/grassblock.png");
        Texture grass = Utils.inputStreamToTexture(io);
        mesh.setMaterial(new Material(grass));
        GameItem gi = new GameItem(mesh);
        addItem(gi);
        gi.setPosition(0, 0, -5);


        for (int x = 5; x > -6; x--) {
            for (int z = 5; z > -6; z--) {
                GameItem gis = gi.clone(false);
                gis.setPosition(x, 0, z);
                gis.setCollider(new ObjectBoxCollider(false, true));
                getItemHandler().addItem(gis);
            }
        }

    }

}
