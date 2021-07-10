package org.kakara.client.scenes.maingamescene;

import org.kakara.core.common.Kakara;
import org.kakara.engine.components.MeshRenderer;
import org.kakara.engine.gameitems.GameItem;
import org.kakara.engine.gameitems.mesh.Mesh;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.engine.physics.collision.BoxCollider;
import org.kakara.engine.physics.collision.PhysicsComponent;

import java.util.Optional;
import java.util.UUID;

public class SceneUtils {
    private MainGameScene gameScene;

    public SceneUtils(MainGameScene gameScene) {
        this.gameScene = gameScene;
    }

    protected Optional<GameItem> getItemByID(UUID uuid) {
        return gameScene.getItemHandler().getItemWithId(uuid);
    }


    protected UUID createPlayerObject() {
        Mesh[] mainPlayer = null;
        try {
            mainPlayer = StaticModelLoader.load(gameScene.getResourceManager().getResource("player/steve.obj"), "/player", gameScene, gameScene.getResourceManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        GameItem object = new GameItem(mainPlayer);
        object.getComponent(MeshRenderer.class).setVisible(false);
        object.transform.setPosition((float) gameScene.getServer().getPlayerEntity().getLocation().getX(), (float) gameScene.getServer().getPlayerEntity().getLocation().getY(), (float) gameScene.getServer().getPlayerEntity().getLocation().getZ());
        PhysicsComponent physicsComponent = object.addComponent(PhysicsComponent.class);
        BoxCollider boxCollider = object.addComponent(BoxCollider.class);
        boxCollider.setPoint1(new Vector3(0, 0, 0));
        boxCollider.setPoint2(new Vector3(0.99f, 1.99f, 0.99f));
        boxCollider.setPredicate(collidable -> {
            gameScene.movement.resetJump();

            if (collidable.getGameItem() == null) return false; //How?
            if (collidable.getGameItem().getTag() == null) return false;
            if (collidable.getGameItem().getTag().equals("pickupable")) {
                return true;
            }

            return false;
        });
        gameScene.add(object);
        Kakara.LOGGER.debug("Player created with the UUID of "+ object.getUUID().toString());
        return object.getUUID();
    }

}
