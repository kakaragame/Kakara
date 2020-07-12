package org.kakara.client.scenes.maingamescene;

import org.kakara.client.MoreUtils;
import org.kakara.client.game.player.ClientPlayer;
import org.kakara.core.world.Location;
import org.kakara.engine.Camera;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.item.MeshGameItem;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.physics.collision.Collidable;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerMovement {
    private MainGameScene mainGameScene;

    public PlayerMovement(MainGameScene mainGameScene) {
        this.mainGameScene = mainGameScene;
    }

    boolean playerInJump = false;
    float lastYPos = 0;

    protected void playerMovement() {

        if (mainGameScene.chatComponent.isFocused()) return;

        ClientPlayer player = (ClientPlayer) mainGameScene.server.getPlayerEntity();

        player.getGameItemID().ifPresent(uuid -> mainGameScene.sceneUtils.getItemByID(uuid).ifPresent((gameItem) -> {
            MeshGameItem item = (MeshGameItem) gameItem;
            item.setVelocityX(0);
            item.setVelocityZ(0);

            Camera gameCamera = mainGameScene.getCamera();
            KeyInput ki = mainGameScene.kakaraGame.getGameHandler().getKeyInput();
            if (ki.isKeyPressed(GLFW_KEY_W)) {
                item.setVelocityByCamera(new Vector3(0, item.getVelocity().y, -7), gameCamera);
            }
            if (ki.isKeyPressed(GLFW_KEY_S)) {
                item.setVelocityByCamera(new Vector3(0, item.getVelocity().y, 7), gameCamera);
            }
            if (ki.isKeyPressed(GLFW_KEY_A)) {
                item.setVelocityByCamera(new Vector3(-7, item.getVelocity().y, 0), gameCamera);
            }
            if (ki.isKeyPressed(GLFW_KEY_D)) {
                item.setVelocityByCamera(new Vector3(7, item.getVelocity().y, 0), gameCamera);
            }
            if (ki.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
                item.movePositionByCamera(0, -0.3f, 0, gameCamera);
            }
            if (ki.isKeyPressed(GLFW_KEY_SPACE) && !playerInJump) {
                playerInJump = true;
                lastYPos = item.getPosition().y;
                item.setVelocityY(4);
            }
            if (playerInJump) {
                item.movePositionByCamera(0, 0.3F, 0, gameCamera);
                if (item.getPosition().y > lastYPos + 3) {
                    playerInJump = false;
                    item.setVelocityY(-9.18f);
                }
            }

            if (ki.isKeyPressed(GLFW_KEY_G))
                item.setVelocityY(-9.18f);
            Location location = player.getLocation();
            location.setX(item.getPosition().x);
            location.setY(item.getPosition().y);
            location.setZ(item.getPosition().z);
            player.setLocation(location);
            //I NEED HELP!
            MouseInput mi = mainGameScene.kakaraGame.getGameHandler().getMouseInput();
            player.moveLocation((float) mi.getDeltaPosition().y(), (float) mi.getDeltaPosition().x());
            mainGameScene.getCamera().setPosition(MoreUtils.locationToVector3(location).add(0, 1, 0));
            Location l = player.getLocation();
            mainGameScene.getCamera().setRotation(new Vector3(l.getPitch(), l.getYaw(), 0));

            // Handle the block selector.
            mainGameScene.blockSelector.setPosition(item.getPosition().x, -10, item.getPosition().z);
            try {
                Collidable objectFound = mainGameScene.selectGameItems(20, uuid);
                if (objectFound != null)
                    mainGameScene.blockSelector.setPosition(objectFound.getColPosition());
            } catch (NullPointerException ignored) {
            }
        }));
    }
}
