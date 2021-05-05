package org.kakara.client.scenes.canvases;

import org.kakara.client.KakaraGame;
import org.kakara.client.local.game.player.ClientPlayer;
import org.kakara.client.scenes.maingamescene.MainGameScene;
import org.kakara.core.common.world.GameBlock;
import org.kakara.core.common.world.Location;
import org.kakara.engine.GameEngine;
import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.physics.collision.ColliderComponent;
import org.kakara.engine.physics.collision.VoxelCollider;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.ui.components.text.Text;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.ui.font.TextAlign;
import org.kakara.engine.utils.Time;
import org.kakara.engine.voxels.Voxel;
import org.kakara.engine.voxels.VoxelChunk;

import java.util.Optional;
import java.util.UUID;


public class DebugModeCanvas extends ActivateableCanvas {
    private static DebugModeCanvas instance;
    private final KakaraGame kakaraGame;
    private final Text fps;
    private final Text location;
    private final Text engineVersion;
    private final Text kakaraVersion;
    private final Text lookingAt;
    private final String locationFormat = "X: %1$s Y: %2$s Z: %3$s";
    private final MainGameScene gameScene;
    private static final String posFormat = "%.2f";

    private UUID playerID;

    private DebugModeCanvas(KakaraGame kakaraGame, MainGameScene scene) {
        super(scene);
        gameScene = scene;
        this.kakaraGame = kakaraGame;
        setTag("debugmode_canvas");

        ResourceManager resourceManager = GameHandler.getInstance().getResourceManager();

        Font roboto = new Font("Roboto-Regular", resourceManager.getResource("Roboto-Regular.ttf"), scene);


        fps = new Text("60", roboto);
        fps.position = new Vector2(0, 25);
        fps.setTextAlign(TextAlign.LEFT);
        fps.setLineWidth(300);

        location = new Text("X: 0 Y: 0 Z: 0", roboto);
        location.position = new Vector2(0, 50);
        location.setTextAlign(TextAlign.LEFT);
        location.setLineWidth(500);

        engineVersion = new Text("Engine: " + GameEngine.getEngineVersion(), roboto);
        engineVersion.position = new Vector2(0, 75);
        engineVersion.setTextAlign(TextAlign.LEFT);
        engineVersion.setLineWidth(400);

        kakaraVersion = new Text("Kakara: " + KakaraGame.getGameVersion().getProperty("version"), roboto);
        kakaraVersion.position = new Vector2(0, 100);
        kakaraVersion.setTextAlign(TextAlign.LEFT);
        kakaraVersion.setLineWidth(400);

        lookingAt = new Text("Looking At: {placeholder}", roboto);
        lookingAt.setPosition(0, 125);
        lookingAt.setTextAlign(TextAlign.LEFT);
        lookingAt.setLineWidth(400);
    }


    public static DebugModeCanvas getInstance(KakaraGame kakaraGame, MainGameScene scene) {
        if (instance == null) {
            instance = new DebugModeCanvas(kakaraGame, scene);
        }
        return instance;
    }

    public void update() {
        if (!isActivated()) return;

        if(playerID == null){
            playerID = ((ClientPlayer) gameScene.getServer().getPlayerEntity()).getGameItemID()
                    .orElseThrow(() -> new IllegalStateException("No Player Entity Found"));
        }

        Vector3 v = gameScene.getCamera().getPosition();
        location.setText(String.format(locationFormat, String.format(posFormat, v.x), String.format(posFormat, v.y), String.format(posFormat, v.z)));
        fps.setText("FPS: " + Math.round(1 / Time.getDeltaTime()));
        ColliderComponent selectedItem = gameScene.selectGameItems(20, playerID);
        if (selectedItem instanceof VoxelCollider) {
            ClientPlayer player = (ClientPlayer) gameScene.getServer().getPlayerEntity();
            Voxel rb = ((VoxelCollider) selectedItem).getVoxel();
            VoxelChunk parentChunk = rb.getParentChunk();
            Location location = new Location(player.getLocation().getNullableWorld(),
                    parentChunk.transform.getPosition().x + rb.getPosition().x,
                    parentChunk.transform.getPosition().y + rb.getPosition().y,
                    parentChunk.transform.getPosition().z + rb.getPosition().z);
            Optional<GameBlock> blockAt = gameScene.getServer().getPlayerEntity().getLocation().getNullableWorld().getBlockAt(location);
            if(blockAt.isPresent())
                lookingAt.setText("Looking At: " + blockAt.get().getItemStack().getName());
            else
                lookingAt.setText("Looking At: {Unknown Voxel Block}");
        }
        else if(selectedItem != null){
            lookingAt.setText("Looking At: " + selectedItem.getGameItem().getTag());
        }else{
            lookingAt.setText("Looking At: (Nothing)");
        }
    }

    public void remove() {
        removeComponent(fps);
        removeComponent(location);
        removeComponent(engineVersion);
        removeComponent(kakaraVersion);
        removeComponent(lookingAt);
    }

    @Override
    public void close() {
        instance = null;
    }

    public void add() {
        add(fps);
        add(location);
        add(engineVersion);
        add(kakaraVersion);
        add(lookingAt);
    }
}
