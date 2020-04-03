package org.kakara.client.scenes.canvases;

import org.kakara.client.KakaraGame;
import org.kakara.core.world.Location;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.components.Text;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.ui.text.TextAlign;
import org.kakara.engine.utils.Time;
import org.kakara.game.GameUtils;


public class DebugModeCanvas extends ComponentCanvas {
    private static DebugModeCanvas instance;
    private KakaraGame kakaraGame;
    private Text fps;
    private Text location;
    private Text chunkLocation;
    private String locationFormat = "X: %1$s Y: %2$s Z: %3$s";

    private DebugModeCanvas(KakaraGame kakaraGame, Scene scene) {
        super(scene);
        this.kakaraGame = kakaraGame;

        fps = new Text("60", kakaraGame.getFont());
        fps.position = new Vector2(0, 25);
        fps.setTextAlign(TextAlign.CENTER);
        fps.setLineWidth(100);

        location = new Text("X: 0 Y: 0 Z: 0", kakaraGame.getFont());
        location.position = new Vector2(0, 75);
        location.setTextAlign(TextAlign.CENTER);
        location.setLineWidth(500);

        chunkLocation = new Text("X: 0 Y: 0 Z: 0", kakaraGame.getFont());
        chunkLocation.position = new Vector2(0, 124);
        chunkLocation.setTextAlign(TextAlign.CENTER);
        chunkLocation.setLineWidth(500);


    }

    public static DebugModeCanvas getInstance(KakaraGame kakaraGame, Scene scene) {
        if (instance == null) {
            instance = new DebugModeCanvas(kakaraGame, scene);
        }
        return instance;
    }

    public void update() {
        Vector3 v = kakaraGame.getGameHandler().getCamera().getPosition();
        location.setText(String.format(locationFormat, v.x, v.y, v.z));
        Location l = GameUtils.getChunkLocation(new Location(v.x, v.y, v.z));
        chunkLocation.setText(String.format(locationFormat, l.getX(), l.getY(), l.getZ()));
        fps.setText("FPS: " + Math.round(1 / Time.deltaTime));
    }

    public void remove() {
        removeComponent(fps);
        removeComponent(location);
        removeComponent(chunkLocation);
    }

    public void add() {
        add(fps);
        add(location);
        add(chunkLocation);
    }
}
