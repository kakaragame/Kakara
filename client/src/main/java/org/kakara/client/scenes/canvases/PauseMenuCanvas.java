package org.kakara.client.scenes.canvases;

import org.kakara.client.KakaraGame;
import org.kakara.core.GameInstance;
import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.RGBA;
import org.kakara.engine.ui.components.shapes.Rectangle;
import org.kakara.engine.ui.components.text.Text;
import org.kakara.engine.ui.constraints.HorizontalCenterConstraint;
import org.kakara.engine.ui.constraints.VerticalCenterConstraint;
import org.kakara.engine.ui.text.Font;
import org.kakara.engine.ui.text.TextAlign;

public class PauseMenuCanvas extends ActivateableCanvas {
    private static PauseMenuCanvas instance;
    private Rectangle quit;
    private Rectangle resume;
    private Scene scene;

    private PauseMenuCanvas(Scene scene) {
        super(scene);
        this.scene = scene;
        Font roboto = new Font("Roboto", GameHandler.getInstance().getResourceManager().getResource("Roboto-Regular.ttf"), scene);

        quit = new Rectangle(new Vector2(0, 370), new Vector2(300, 60), new RGBA(255, 255, 255, 0.5f));
        quit.addConstraint(new HorizontalCenterConstraint());

        quit.addConstraint(new HorizontalCenterConstraint());
        Text quitGameText = new Text("Quit Game", roboto);
        quitGameText.setSize(40);
        quitGameText.setLineWidth(300);
        quitGameText.setTextAlign(TextAlign.CENTER | TextAlign.MIDDLE);
        quitGameText.addConstraint(new VerticalCenterConstraint());
        quitGameText.addConstraint(new HorizontalCenterConstraint());
        quit.add(quitGameText);
    }

    public static PauseMenuCanvas getInstance(KakaraGame kakaraGame, Scene scene) {
        if (instance == null) {
            instance = new PauseMenuCanvas(scene);
        }
        return instance;
    }

    @Override
    void add() {
        add(quit);

        scene.setCurserStatus(true);
    }

    @Override
    void remove() {
        removeComponent(quit);
        scene.setCurserStatus(false);

    }

    @Override
    public void update() {

    }

    @Override
    void close() {
        instance = null;
    }
}
