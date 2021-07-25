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

/**
 * A utility class for the MainGameScene.
 */
public class SceneUtils {
    private final MainGameScene gameScene;

    /**
     * Construct the SceneUtils class.
     *
     * @param gameScene The instance to the MainGameScene.
     */
    public SceneUtils(MainGameScene gameScene) {
        this.gameScene = gameScene;
    }

    /**
     * Get a GameItem by a certain UUID.
     *
     * @param uuid The UUID of the GameItem to obtain.
     * @return The GameItem.
     */
    protected Optional<GameItem> getItemByID(UUID uuid) {
        return gameScene.getItemHandler().getItemWithId(uuid);
    }

    /**
     * Create the player object.
     *
     * <p>The player GameItem is added to the game scene.</p>
     *
     * @return The UUID of the player GameItem.
     */
    protected UUID createPlayerObject() {
        Mesh[] mainPlayer = null;
        // Load the player mesh.
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
        // Make it slightly smaller than a full block for easier movement.
        boxCollider.setPoint2(new Vector3(0.99f, 1.99f, 0.99f));
        // Prevent collision with pickup-able items.
        boxCollider.setPredicate(collidable -> {
            if (collidable.getGameItem() == null) return false; //How?
            if (collidable.getGameItem().getTag() == null) return false;
            if (collidable.getGameItem().getTag().equals("pickupable")) {
                return true;
            }
            return false;
        });

        PlayerMovement playerMovement = object.addComponent(PlayerMovement.class);
        playerMovement.setMainGameScene(this.gameScene);

        // Add the object to the main scene.
        gameScene.add(object);
        Kakara.LOGGER.debug("Player created with the UUID of " + object.getUUID().toString());
        return object.getUUID();
    }
}
