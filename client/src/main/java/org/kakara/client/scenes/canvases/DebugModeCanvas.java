package org.kakara.client.scenes.canvases;

import org.kakara.client.KakaraGame;
import org.kakara.client.scenes.maingamescene.MainGameScene;
import org.kakara.core.common.world.ChunkLocation;
import org.kakara.core.common.world.Location;
import org.kakara.engine.GameEngine;
import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.ui.components.text.Text;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.ui.font.TextAlign;
import org.kakara.engine.utils.Time;
import org.kakara.game.GameUtils;


public class DebugModeCanvas extends ActivateableCanvas {
    private static DebugModeCanvas instance;
    private final KakaraGame kakaraGame;
    private final Text fps;
    private final Text location;
    private final Text chunkLocation;
    private final Text numberOfChunksLoaded;
    private final Text engineVersion;
    private final String locationFormat = "X: %1$s Y: %2$s Z: %3$s";
    private final MainGameScene gameScene;

    private DebugModeCanvas(KakaraGame kakaraGame, MainGameScene scene) {
        super(scene);
        gameScene = scene;
        this.kakaraGame = kakaraGame;

        ResourceManager resourceManager = GameHandler.getInstance().getResourceManager();

        Font roboto = new Font("Roboto-Regular", resourceManager.getResource("Roboto-Regular.ttf"), scene);


        fps = new Text("60", roboto);
        fps.position = new Vector2(0, 25);
        fps.setTextAlign(TextAlign.CENTER);
        fps.setLineWidth(100);

        location = new Text("X: 0 Y: 0 Z: 0", roboto);
        location.position = new Vector2(0, 75);
        location.setTextAlign(TextAlign.CENTER);
        location.setLineWidth(500);

        chunkLocation = new Text("X: 0 Y: 0 Z: 0", roboto);
        chunkLocation.position = new Vector2(0, 124);
        chunkLocation.setTextAlign(TextAlign.CENTER);
        chunkLocation.setLineWidth(500);

        numberOfChunksLoaded = new Text("0", roboto);
        numberOfChunksLoaded.position = new Vector2(0, 150);
        numberOfChunksLoaded.setTextAlign(TextAlign.CENTER);
        numberOfChunksLoaded.setLineWidth(500);
        engineVersion = new Text(GameEngine.getEngineVersion(), roboto);
        engineVersion.position = new Vector2(0, 200);
        engineVersion.setTextAlign(TextAlign.CENTER);
        engineVersion.setLineWidth(500);


    }


    public static DebugModeCanvas getInstance(KakaraGame kakaraGame, MainGameScene scene) {
        if (instance == null) {
            instance = new DebugModeCanvas(kakaraGame, scene);
        }
        return instance;
    }

    public void update() {
        if (!isActivated()) return;
        Vector3 v = gameScene.getCamera().getPosition();
        location.setText(String.format(locationFormat, v.x, v.y, v.z));
        ChunkLocation l = GameUtils.getChunkLocation(new Location(v.x, v.y, v.z));
        chunkLocation.setText(String.format(locationFormat, l.getX(), l.getY(), l.getZ()));
        fps.setText("FPS: " + Math.round(1 / Time.getDeltaTime()));

        //numberOfChunksLoaded.setText("NOC: "+ gameScene.getServer().getPlayerEntity().getLocation().getWorld().getLoadedChunks().length);
    }

    public void remove() {
        removeComponent(fps);
        removeComponent(location);
        removeComponent(chunkLocation);
        removeComponent(numberOfChunksLoaded);
        removeComponent(engineVersion);
    }

    @Override
    public void close() {
        instance = null;
    }

    public void add() {
        add(fps);
        add(location);
        add(chunkLocation);
        add(numberOfChunksLoaded);
        add(engineVersion);
    }
}
