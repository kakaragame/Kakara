package org.kakara.client.scenes.canvases;

import org.kakara.client.KakaraGame;
import org.kakara.client.scenes.LoadingScene;
import org.kakara.client.scenes.maingamescene.MainGameScene;
import org.kakara.core.common.Status;
import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.components.shapes.Rectangle;
import org.kakara.engine.ui.components.text.Text;
import org.kakara.engine.ui.constraints.HorizontalCenterConstraint;
import org.kakara.engine.ui.constraints.VerticalCenterConstraint;
import org.kakara.engine.ui.events.UIClickEvent;
import org.kakara.engine.ui.events.UIHoverEnterEvent;
import org.kakara.engine.ui.events.UIHoverLeaveEvent;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.ui.font.TextAlign;
import org.kakara.engine.utils.RGBA;


public class PauseMenuCanvas extends ActivateableCanvas {
    private static PauseMenuCanvas instance;
    private final Rectangle quit;
    private final Rectangle resume;
    private final Scene scene;

    private PauseMenuCanvas(Scene scene) {
        super(scene);
        this.scene = scene;
        Font roboto = new Font("Roboto", GameHandler.getInstance().getResourceManager().getResource("Roboto-Regular.ttf"), scene);
        setTag("pausemenu_canvas");
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
        resume.addUActionEvent(UIHoverEnterEvent.class, (UIHoverEnterEvent) vector2 -> resume.setColor(new RGBA(204, 202, 202, 0.5f)));
        resume.addUActionEvent(UIHoverLeaveEvent.class, (UIHoverLeaveEvent) vector2 -> resume.setColor(new RGBA(255, 255, 255, 0.5f)));
        resume.addUActionEvent(UIClickEvent.class, (UIClickEvent) (vector2, mouseClickType) -> {
            if (!isActivated()) return;
            switchStatus();
        });

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

        quit.addUActionEvent(UIHoverEnterEvent.class, (UIHoverEnterEvent) vector2 -> quit.setColor(new RGBA(204, 202, 202, 0.5f)));
        quit.addUActionEvent(UIHoverLeaveEvent.class, (UIHoverLeaveEvent) vector2 -> quit.setColor(new RGBA(255, 255, 255, 0.5f)));
        quit.addUActionEvent(UIClickEvent.class, (UIClickEvent) (vector2, mouseClickType) -> {
            if (!isActivated()) return;
            MainGameScene mainGameScene = (MainGameScene) scene;
            LoadingScene loadingScene = new LoadingScene(GameHandler.getInstance(), mainGameScene.getServer(), Status.UNLOADED, new Runnable() {
                @Override
                public void run() {
                    try {
                        GameHandler.getInstance().getSceneManager().setScene(KakaraGame.getInstance().firstScene(GameHandler.getInstance()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            GameHandler.getInstance().getSceneManager().setScene(loadingScene);
            mainGameScene.close();

            mainGameScene.getServer().close();
        });
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
        scene.setCursorStatus(true);
    }

    @Override
    void remove() {
        removeComponent(quit);
        removeComponent(resume);
        scene.setCursorStatus(false);

    }

    @Override
    public void update() {

    }

    @Override
    public void close() {
        instance = null;
    }
}
