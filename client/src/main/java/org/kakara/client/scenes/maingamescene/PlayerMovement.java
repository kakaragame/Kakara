package org.kakara.client.scenes.maingamescene;

import org.kakara.client.ClientServerController;
import org.kakara.client.local.game.player.ClientPlayer;
import org.kakara.client.scenes.canvases.PauseMenuCanvas;
import org.kakara.core.common.world.Location;
import org.kakara.engine.Camera;
import org.kakara.engine.gameitems.GameItem;

import org.kakara.engine.input.key.KeyInput;
import org.kakara.engine.input.mouse.MouseInput;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.physics.collision.ColliderComponent;
import org.kakara.engine.physics.collision.PhysicsComponent;
import org.kakara.engine.physics.collision.VoxelCollider;
import org.kakara.engine.voxels.Voxel;
import org.kakara.engine.voxels.VoxelTexture;

import java.util.Optional;
import java.util.UUID;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerMovement {
    boolean playerInJump = false;
    float lastYPos = 0;
    private final MainGameScene mainGameScene;
    private GameItem item;
    private PhysicsComponent physicsComponent;

    public PlayerMovement(MainGameScene mainGameScene) {
        this.mainGameScene = mainGameScene;
    }

    protected void playerMovement() {
        if (mainGameScene.chatComponent != null) {
            if (mainGameScene.chatComponent.isFocused()) return;
        }
        if (PauseMenuCanvas.getInstance(mainGameScene.kakaraGame, mainGameScene).isActivated()) return;


        ClientPlayer player = (ClientPlayer) mainGameScene.getServer().getPlayerEntity();

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
        KeyInput ki = mainGameScene.kakaraGame.getGameHandler().getKeyInput();
        if (ki.isKeyPressed(GLFW_KEY_W)) {
            physicsComponent.setVelocityByCamera(new Vector3(0, physicsComponent.getVelocity().y, -7), gameCamera);
        }
        if (ki.isKeyPressed(GLFW_KEY_S)) {
            physicsComponent.setVelocityByCamera(new Vector3(0, physicsComponent.getVelocity().y, 7), gameCamera);
        }
        if (ki.isKeyPressed(GLFW_KEY_A)) {
            physicsComponent.setVelocityByCamera(new Vector3(-7, physicsComponent.getVelocity().y, 0), gameCamera);
        }
        if (ki.isKeyPressed(GLFW_KEY_D)) {
            physicsComponent.setVelocityByCamera(new Vector3(7, physicsComponent.getVelocity().y, 0), gameCamera);
        }
        if (ki.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            item.transform.movePositionByCamera(0, -0.3f, 0, gameCamera);
        }
        if (ki.isKeyPressed(GLFW_KEY_SPACE) && !playerInJump) {
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

        if (ki.isKeyPressed(GLFW_KEY_G))
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

}
