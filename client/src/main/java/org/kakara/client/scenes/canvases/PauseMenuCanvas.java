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
import org.kakara.engine.ui.events.HUDClickEvent;
import org.kakara.engine.ui.events.HUDHoverEnterEvent;
import org.kakara.engine.ui.events.HUDHoverLeaveEvent;
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

        resume = new Rectangle(new Vector2(0, 270), new Vector2(300, 60), new RGBA(255, 255, 255, 0.5f));
        resume.addConstraint(new HorizontalCenterConstraint());

        resume.addConstraint(new HorizontalCenterConstraint());
        Text resumeGameText = new Text("Resume Game", roboto);
        resumeGameText.setSize(40);
        resumeGameText.setLineWidth(300);
        resumeGameText.setTextAlign(TextAlign.CENTER | TextAlign.MIDDLE);
        resumeGameText.addConstraint(new VerticalCenterConstraint());
        resumeGameText.addConstraint(new HorizontalCenterConstraint());
        resume.add(resumeGameText);
        resume.addUActionEvent((HUDHoverEnterEvent) vector2 -> resume.setColor(new RGBA(204, 202, 202, 0.5f)), HUDHoverEnterEvent.class);
        resume.addUActionEvent((HUDHoverLeaveEvent) vector2 -> resume.setColor(new RGBA(255, 255, 255, 0.5f)), HUDHoverLeaveEvent.class);
        resume.addUActionEvent((HUDClickEvent) (vector2, mouseClickType) -> switchStatus(), HUDClickEvent.class);

        //Quit Button
        quit = new Rectangle(new Vector2(0, 370), new Vector2(300, 60), new RGBA(255, 255, 255, 0.5f));
        quit.addConstraint(new HorizontalCenterConstraint());

        quit.addConstraint(new HorizontalCenterConstraint());
        Text quitGameText = new Text("Quit Game", roboto);
        quitGameText.setSize(40);
        quitGameText.setLineWidth(300);
        quitGameText.setTextAlign(TextAlign.CENTER | TextAlign.MIDDLE);
        quitGameText.addConstraint(new VerticalCenterConstraint());
        quitGameText.addConstraint(new HorizontalCenterConstraint());

        quit.addUActionEvent((HUDHoverEnterEvent) vector2 -> quit.setColor(new RGBA(204, 202, 202, 0.5f)), HUDHoverEnterEvent.class);
        quit.addUActionEvent((HUDHoverLeaveEvent) vector2 -> quit.setColor(new RGBA(255, 255, 255, 0.5f)), HUDHoverLeaveEvent.class);
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
add(resume);
        scene.setCurserStatus(true);
    }

    @Override
    void remove() {
        removeComponent(quit);
        removeComponent(resume);
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
