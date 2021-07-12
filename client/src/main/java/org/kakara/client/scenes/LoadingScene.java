package org.kakara.client.scenes;


import org.kakara.client.scenes.uicomponenets.loadingbar.LoadingBar;
import org.kakara.core.common.LoadableObject;
import org.kakara.core.common.Status;
import org.kakara.engine.GameHandler;
import org.kakara.engine.scene.AbstractMenuScene;
import org.kakara.engine.ui.canvases.ComponentCanvas;
import org.kakara.engine.ui.components.text.Text;
import org.kakara.engine.ui.constraints.ComponentSide;
import org.kakara.engine.ui.constraints.GeneralConstraint;
import org.kakara.engine.ui.constraints.HorizontalCenterConstraint;
import org.kakara.engine.ui.constraints.VerticalCenterConstraint;
import org.kakara.engine.ui.font.Font;

/**
 * The loading scene for the game.
 */
public class LoadingScene extends AbstractMenuScene {
    private final LoadableObject loadable;
    private final Status targetStatus;
    private final Runnable onCompletion;

    private LoadingBar loadingBar;

    /**
     * Construct the LoadingScene.
     *
     * @param handler      The instance of the GameHandler.
     * @param loadable     A loadable object that this loading scene relies on.
     *                     <p>A loadable object is an object that has a load/unload status and a percent attached. See {@link LoadableObject}.</p>
     * @param targetStatus The target status.
     * @param onCompletion The Runnable to run when the loading is complete.
     */
    public LoadingScene(GameHandler handler, LoadableObject loadable, Status targetStatus, Runnable onCompletion) {
        super(handler);
        this.loadable = loadable;
        this.targetStatus = targetStatus;
        this.onCompletion = onCompletion;
    }

    @Override
    public void work() {

    }

    @Override
    public void loadGraphics(GameHandler gameHandler) throws Exception {
        Font roboto = new Font("Roboto", gameHandler.getResourceManager().getResource("Roboto-Regular.ttf"), this);
        Text text = new Text("Loading please wait...", roboto);
        text.setLineWidth(300);
        text.addConstraint(new VerticalCenterConstraint());
        text.addConstraint(new HorizontalCenterConstraint());

        // Create a loading bar for the user to see that status.
        loadingBar = new LoadingBar(roboto);
        loadingBar.addConstraint(new HorizontalCenterConstraint());
        loadingBar.addConstraint(new GeneralConstraint(ComponentSide.TOP, text, ComponentSide.BOTTOM, 20));

        ComponentCanvas componentCanvas = new ComponentCanvas(this);
        componentCanvas.add(text);
        componentCanvas.add(loadingBar);

        add(componentCanvas);
    }

    @Override
    public void update(float v) {
        System.out.println("getStatus() = " + loadable.getStatus());
        loadingBar.setPercent(loadable.getPercent() / 100f);
        System.out.println("getPercent() = " + loadable.getPercent());
        if (loadable.getStatus() == targetStatus) {
            onCompletion.run();
        }
    }
}
