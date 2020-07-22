package org.kakara.client.scenes.maingamescene;

import org.kakara.core.Kakara;
import org.kakara.core.NameKey;
import org.kakara.engine.GameEngine;
import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.item.MeshGameItem;
import org.kakara.engine.item.mesh.Mesh;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.engine.physics.collision.BoxCollider;
import org.kakara.engine.resources.Resource;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class SceneUtils {
    private MainGameScene gameScene;

    public SceneUtils(MainGameScene gameScene) {
        this.gameScene = gameScene;
    }

    protected Optional<GameItem> getItemByID(UUID uuid) {
        AtomicReference<GameItem> gameItemA = new AtomicReference<>();
        gameScene.getItemHandler().getNonInstancedMeshMap().forEach((mesh, gameItems) -> gameItems.stream().filter(gameItem -> gameItem.getId().equals(uuid)).findFirst().ifPresent(gameItemA::set));
        return Optional.ofNullable(gameItemA.get());
    }

    protected UUID createPlayerObject() {
        Mesh[] mainPlayer = null;
        try {
            mainPlayer = StaticModelLoader.load(gameScene.getResourceManager().getResource("player/steve.obj"), "/player", gameScene, gameScene.getResourceManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        MeshGameItem object = new MeshGameItem(mainPlayer);
        object.setVisible(false);
        object.setPosition((float) gameScene.server.getPlayerEntity().getLocation().getX(), (float) gameScene.server.getPlayerEntity().getLocation().getY(), (float) gameScene.server.getPlayerEntity().getLocation().getZ());
        object.setCollider(new BoxCollider(new Vector3(0, 0, 0), new Vector3(0.99f, 1.99f, 0.99f)));
/*        object.getCollider().addOnTriggerEnter(trig -> {
            if(trig instanceof MeshGameItem){
                MeshGameItem item = (MeshGameItem) trig;
                if(item.getTag().equals("pickupable")){
                    NameKey key = (NameKey) item.getData().get(0);
                    gameScene.getHotBar().getContentInventory().addItemStackForPickup(Kakara.createItemStack(Kakara.getItemManager().getItem(key).get()));
                    item.setTag("TO BE REMOVED");
                    gameScene.remove(item);
                    gameScene.addQueueRunnable(() -> {
                        gameScene.getHotBar().renderItems();
                    });
                }
            }
        });*/
        gameScene.add(object);
        return object.getId();
    }
}
