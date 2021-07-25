package org.kakara.client.scenes.maingamescene;

import org.kakara.client.ClientServerController;
import org.kakara.client.local.game.player.ClientPlayer;
import org.kakara.client.scenes.canvases.PauseMenuCanvas;
import org.kakara.core.common.world.Location;
import org.kakara.engine.Camera;
import org.kakara.engine.components.Component;
import org.kakara.engine.gameitems.GameItem;
import org.kakara.engine.input.Input;
import org.kakara.engine.input.key.KeyCode;
import org.kakara.engine.input.mouse.MouseInput;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.physics.collision.ColliderComponent;
import org.kakara.engine.physics.collision.PhysicsComponent;
import org.kakara.engine.physics.collision.VoxelCollider;
import org.kakara.engine.voxels.Voxel;

import java.util.Optional;
import java.util.UUID;

/**
 * A component that handles everything to do with PlayerMovement, including
 * updating the Camera.
 * <p>
 * This is added to the player in the {@link SceneUtils#createPlayerObject()} method.
 */
public class PlayerMovement extends Component {
    boolean playerInJump = false;
    float lastYPos = 0;
    private MainGameScene mainGameScene;
    private GameItem item;
    private PhysicsComponent physicsComponent;

    /**
     * Set the Main Game Scene for this component.
     *
     * <p>This is done by {@link SceneUtils#createPlayerObject()}.</p>
     *
     * @param mainGameScene The main game scene.
     */
    public void setMainGameScene(MainGameScene mainGameScene) {
        this.mainGameScene = mainGameScene;
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        if (mainGameScene == null) return;

        ClientPlayer player = (ClientPlayer) mainGameScene.getServer().getPlayerEntity();

        if (mainGameScene.chatComponent != null) {
            if (mainGameScene.chatComponent.isFocused()) {
                // Update the player position with gravity and stuff.
                updatePlayerPositionOnly(player);
                return;
            }
        }
        if (PauseMenuCanvas.getInstance(mainGameScene).isActivated()) return;


        Optional<UUID> gameItemID = player.getGameItemID();
        if (item == null) {
            if (gameItemID.isEmpty()) return;
            Optional<GameItem> itemByID = mainGameScene.sceneUtils.getItemByID(gameItemID.get());
            if (itemByID.isEmpty()) return;
            item = itemByID.get();
            physicsComponent = item.getComponent(PhysicsComponent.class);
        }

        physicsComponent.setVelocityX(0);
        physicsComponent.setVelocityZ(0);

        Camera gameCamera = mainGameScene.getCamera();
        if (Input.isKeyDown(KeyCode.W)) {
            physicsComponent.setVelocityByCamera(new Vector3(0, physicsComponent.getVelocity().y, -7), gameCamera);
        }
        if (Input.isKeyDown(KeyCode.S)) {
            physicsComponent.setVelocityByCamera(new Vector3(0, physicsComponent.getVelocity().y, 7), gameCamera);
        }
        if (Input.isKeyDown(KeyCode.A)) {
            physicsComponent.setVelocityByCamera(new Vector3(-7, physicsComponent.getVelocity().y, 0), gameCamera);
        }
        if (Input.isKeyDown(KeyCode.D)) {
            physicsComponent.setVelocityByCamera(new Vector3(7, physicsComponent.getVelocity().y, 0), gameCamera);
        }
        if (Input.isKeyDown(KeyCode.LEFT_SHIFT)) {
            item.transform.movePositionByCamera(0, -0.3f, 0, gameCamera);
        }
        if (Input.isKeyDown(KeyCode.SPACE) && !playerInJump) {
            playerInJump = true;
            lastYPos = item.transform.getPosition().y;
            physicsComponent.setVelocityY(4);
        }

        if (playerInJump) {
            physicsComponent.getTransform().movePositionByCamera(0, 0.3F, 0, gameCamera);
            if (physicsComponent.getTransform().getPosition().y > lastYPos + 3) {
                playerInJump = false;
                physicsComponent.setVelocityY(-9.18f);
            }
        }

        if (Input.isKeyDown(KeyCode.G))
            physicsComponent.setVelocityY(-9.18f);
        MouseInput mi = mainGameScene.kakaraGame.getGameHandler().getMouseInput();

        Location location;
        location = new Location(player.getLocation().getNullableWorld(), item.transform.getPosition().x, item.transform.getPosition().y, item.transform.getPosition().z, player.getLocation().getPitch(), player.getLocation().getYaw());
        location = location.add(new Location(0, 0, 0, (float) mi.getDeltaPosition().y(), (float) mi.getDeltaPosition().x()));
        ((ClientServerController) mainGameScene.getServer().getServerController()).playerMove(location);
        //TODO change ordering. In this current method on a server will result in large amounts of lag.
        Location l = player.getLocation();
        mainGameScene.getCamera().setPosition((float) l.getX(), (float) l.getY() + 1, (float) l.getZ());
        mainGameScene.getCamera().setRotation(l.getPitch(), l.getYaw(), 0);
        // Handle the block selector.
        try {
            ColliderComponent objectFound = mainGameScene.selectGameItems(20, gameItemID.get());
            if (mainGameScene.blockSelector != null) {

                if (objectFound instanceof VoxelCollider) {
                    VoxelCollider objectFound1 = (VoxelCollider) objectFound;
                    Voxel block = objectFound1.getVoxel();
                    // This does not mutate the Vector3.
                    mainGameScene.blockSelector.transform.setPosition(block.getPosition().add(block.getParentChunk().getTransform().getPosition()));
                } else {
                    mainGameScene.blockSelector.transform.setPosition((float) l.getX(), -10, (float) l.getZ());
                }
            }
        } catch (NullPointerException ignored) {
        }
    }

    /**
     * This serves to only update the position of the player. (In reality it is updating the position of the camera.)
     *
     * <p>This is done when the chat is open but the player still needs to fall due to gravity.</p>
     *
     * @param player The Client Player.
     */
    private void updatePlayerPositionOnly(ClientPlayer player) {
        Camera gameCamera = mainGameScene.getCamera();
        if (playerInJump) {
            physicsComponent.getTransform().movePositionByCamera(0, 0.3F, 0, gameCamera);
            if (physicsComponent.getTransform().getPosition().y > lastYPos + 3) {
                playerInJump = false;
                physicsComponent.setVelocityY(-9.18f);
            }
        }

        Location location;
        location = new Location(player.getLocation().getNullableWorld(), item.transform.getPosition().x, item.transform.getPosition().y, item.transform.getPosition().z, player.getLocation().getPitch(), player.getLocation().getYaw());
        ((ClientServerController) mainGameScene.getServer().getServerController()).playerMove(location);
        Location l = player.getLocation();
        mainGameScene.getCamera().setPosition((float) l.getX(), (float) l.getY() + 1, (float) l.getZ());
    }
}
