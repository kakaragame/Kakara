package org.kakara.client.scenes.canvases;

import org.kakara.client.KakaraGame;
import org.kakara.client.scenes.maingamescene.MainGameScene;
import org.kakara.engine.GameEngine;
import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.ui.components.text.Text;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.ui.font.TextAlign;
import org.kakara.engine.utils.Time;


public class DebugModeCanvas extends ActivateableCanvas {
    private static DebugModeCanvas instance;
    private final KakaraGame kakaraGame;
    private final Text fps;
    private final Text location;
    private final Text engineVersion;
    private final Text kakaraVersion;
    private final String locationFormat = "X: %1$s Y: %2$s Z: %3$s";
    private final MainGameScene gameScene;
    private static final String posFormat = "%.2f";

    private DebugModeCanvas(KakaraGame kakaraGame, MainGameScene scene) {
        super(scene);
        gameScene = scene;
        this.kakaraGame = kakaraGame;

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
        engineVersion.setLineWidth(300);

        kakaraVersion = new Text("Kakara: " + KakaraGame.getGameVersion().getProperty("version"), roboto);
        kakaraVersion.position = new Vector2(0, 100);
        kakaraVersion.setTextAlign(TextAlign.LEFT);
        kakaraVersion.setLineWidth(300);


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
        location.setText(String.format(locationFormat, String.format(posFormat, v.x), String.format(posFormat, v.y), String.format(posFormat, v.z)));
        fps.setText("FPS: " + Math.round(1 / Time.getDeltaTime()));

    }

    public void remove() {
        removeComponent(fps);
        removeComponent(location);
        removeComponent(engineVersion);
        removeComponent(kakaraVersion);
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
    }
}
