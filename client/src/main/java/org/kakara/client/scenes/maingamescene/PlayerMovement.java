package org.kakara.client.scenes.maingamescene;

import org.kakara.client.ClientServerController;
import org.kakara.client.local.game.player.ClientPlayer;
import org.kakara.client.scenes.canvases.PauseMenuCanvas;
import org.kakara.core.common.world.Location;
import org.kakara.engine.Camera;
import org.kakara.engine.gameitems.MeshGameItem;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.physics.collision.Collidable;
import org.kakara.engine.renderobjects.RenderBlock;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerMovement {
    boolean playerInJump = false;
    float lastYPos = 0;
    private final MainGameScene mainGameScene;

    public PlayerMovement(MainGameScene mainGameScene) {
        this.mainGameScene = mainGameScene;
    }

    protected void playerMovement() {
        if (mainGameScene.chatComponent != null) {
            if (mainGameScene.chatComponent.isFocused()) return;
        }
        if (PauseMenuCanvas.getInstance(mainGameScene.kakaraGame, mainGameScene).isActivated()) return;
        ClientPlayer player = (ClientPlayer) mainGameScene.getServer().getPlayerEntity();

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
            MouseInput mi = mainGameScene.kakaraGame.getGameHandler().getMouseInput();

            Location location;
            location = new Location(player.getLocation().getNullableWorld(), item.getPosition().x, item.getPosition().y, item.getPosition().z, player.getLocation().getPitch(), player.getLocation().getYaw());
            location = location.add(new Location(0, 0, 0, (float) mi.getDeltaPosition().y(), (float) mi.getDeltaPosition().x()));
            ((ClientServerController) mainGameScene.getServer().getServerController()).playerMove(location);
            //TODO change ordering. In this current method on a server will result in large amounts of lag.
            Location l = player.getLocation();
            mainGameScene.getCamera().setPosition((float) l.getX(), (float) l.getY() + 1, (float) l.getZ());
            mainGameScene.getCamera().setRotation(l.getPitch(), l.getYaw(), 0);
            // Handle the block selector.
            try {
                Collidable objectFound = mainGameScene.selectGameItems(20, uuid);
                if (objectFound instanceof RenderBlock) {
                    RenderBlock block = (RenderBlock) objectFound;
                    // This does not mutate the Vector3.
                    mainGameScene.blockSelector.setPosition(block.getPosition().add(block.getParentChunk().getPosition()));
                } else {
                    mainGameScene.blockSelector.setPosition((float) l.getX(), -10, (float) l.getZ());
                }
            } catch (NullPointerException ignored) {
            }
        }));
    }
}
