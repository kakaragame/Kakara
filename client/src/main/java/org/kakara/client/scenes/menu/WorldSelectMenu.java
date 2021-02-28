package org.kakara.client.scenes.menu;

import org.kakara.client.KakaraGame;
import org.kakara.engine.GameHandler;
import org.kakara.engine.gameitems.Texture;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.AbstractMenuScene;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.canvases.ComponentCanvas;
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
import org.kakara.engine.window.WindowIcon;

public class WorldSelectMenu extends AbstractMenuScene {
    private final KakaraGame kakaraGame;

    public WorldSelectMenu(GameHandler gameHandler, KakaraGame kakaraGame) {
        super(gameHandler);
        this.kakaraGame = kakaraGame;
    }

    @Override
    public void work() {

    }

    @Override
    public void loadGraphics(GameHandler handler) throws Exception {
        gameHandler.getWindow().setIcon(new WindowIcon(gameHandler.getResourceManager().getResource("icon.png")));

        ResourceManager resourceManager = gameHandler.getResourceManager();
        setBackground(new Texture(resourceManager.getResource("kakara_background.png"), this));

        Font pixelFont = new Font("PixelFont", resourceManager.getResource("PressStart2P-Regular.ttf"), this);
        Font roboto = new Font("Roboto", resourceManager.getResource("Roboto-Regular.ttf"), this);


        ComponentCanvas componentCanvas = new ComponentCanvas(this);
        {
            Rectangle backButton = new Rectangle(new Vector2(135, 600), new Vector2(300, 60), new RGBA(255, 255, 255, 0.5f));
            //backButton.addConstraint(new HorizontalCenterConstraint());
            Text backButtonText = new Text("Back", roboto);
            backButtonText.setSize(40);
            backButtonText.setLineWidth(300);
            backButtonText.setTextAlign(TextAlign.CENTER | TextAlign.MIDDLE);
            backButtonText.addConstraint(new VerticalCenterConstraint());
            backButtonText.addConstraint(new HorizontalCenterConstraint());
            backButton.add(backButtonText);

            backButton.addUActionEvent(UIHoverEnterEvent.class, (UIHoverEnterEvent) vector2 -> backButton.setColor(new RGBA(204, 202, 202, 0.5f)));
            backButton.addUActionEvent(UIHoverLeaveEvent.class, (UIHoverLeaveEvent) vector2 -> backButton.setColor(new RGBA(255, 255, 255, 0.5f)));
            backButton.addUActionEvent(UIClickEvent.class, (UIClickEvent) (vector2, mouseClickType) -> goBack(backButton));

            componentCanvas.add(backButton);
        }
        {
            Rectangle createWorld = new Rectangle(new Vector2(600, 600), new Vector2(300, 60), new RGBA(255, 255, 255, 0.5f));
            Text backButtonText = new Text("Create New World", roboto);
            backButtonText.setSize(40);
            backButtonText.setLineWidth(300);
            backButtonText.setTextAlign(TextAlign.CENTER | TextAlign.MIDDLE);
            backButtonText.addConstraint(new VerticalCenterConstraint());
            backButtonText.addConstraint(new HorizontalCenterConstraint());
            createWorld.add(backButtonText);

            createWorld.addUActionEvent(UIHoverEnterEvent.class, (UIHoverEnterEvent) vector2 -> createWorld.setColor(new RGBA(204, 202, 202, 0.5f)));
            createWorld.addUActionEvent(UIHoverLeaveEvent.class, (UIHoverLeaveEvent) vector2 -> createWorld.setColor(new RGBA(255, 255, 255, 0.5f)));
            createWorld.addUActionEvent(UIClickEvent.class, (UIClickEvent) (vector2, mouseClickType) -> createNewWorld(createWorld));

            componentCanvas.add(createWorld);
        }
        add(componentCanvas);

    }

    private void createNewWorld(Rectangle createWorld) {
        gameHandler.getSceneManager().setScene(new CreateWorldScene(gameHandler, kakaraGame));
    }

    private void goBack(Rectangle backButton) {
        try {
            Scene scene = kakaraGame.firstScene(gameHandler);
            gameHandler.getSceneManager().setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void update(float i) {
    }
}
