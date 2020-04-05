package org.kakara.client.scenes;

import org.kakara.client.KakaraGame;
import org.kakara.core.client.Save;
import org.kakara.engine.GameHandler;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseClickType;
import org.kakara.engine.item.Texture;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.scene.AbstractMenuScene;
import org.kakara.engine.ui.RGBA;
import org.kakara.engine.ui.components.Panel;
import org.kakara.engine.ui.components.Rectangle;
import org.kakara.engine.ui.components.Sprite;
import org.kakara.engine.ui.components.Text;
import org.kakara.engine.ui.events.HUDClickEvent;
import org.kakara.engine.ui.events.HUDHoverEnterEvent;
import org.kakara.engine.ui.events.HUDHoverLeaveEvent;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.ui.text.Font;
import org.kakara.engine.ui.text.TextAlign;
import org.kakara.engine.utils.Time;
import org.kakara.game.IntergratedServer;
import org.kakara.game.client.TestSave;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

public class MainMenuScene extends AbstractMenuScene {
    private KakaraGame kakaraGame;

    public MainMenuScene(GameHandler gameHandler, KakaraGame kakaraGame) {
        super(gameHandler);
        this.kakaraGame = kakaraGame;
    }

    private Texture loadBackgroundTexture() {
        try {
            return new Texture(kakaraGame.getGameHandler().getResourceManager().getResource("background.png"), this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Texture loadTextTexture() {
        try {
            return new Texture(kakaraGame.getGameHandler().getResourceManager().getResource("text.png"), this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void work() {

    }

    @Override
    public void loadGraphics() {
        setBackground(loadBackgroundTexture());
        getHUD().addFont(kakaraGame.getFont());
        ComponentCanvas componentCanvas = new ComponentCanvas(this);
        Texture texture = loadTextTexture();
        // Make some more text for the title screen.
        Text title = new Text("Kakara", kakaraGame.getFont());
        title.setSize(200);
        title.setLineWidth(500);
        title.setPosition(gameHandler.getWindow().getWidth() / 2 - 250, 200);


        if (kakaraGame.getClient().getGameSettings().isTestMode()) {
            // Create the play button from a rectangle.
            Rectangle playButton = new Rectangle(new Vector2(gameHandler.getWindow().getWidth() / 2 - 100, gameHandler.getWindow().getHeight() - 300),
                    new Vector2(100, 100));
            playButton.setColor(new RGBA(0, 150, 150, 1));
            // Setup the events for the button.
            playButton.addUActionEvent(new HUDHoverEnterEvent() {
                @Override
                public void OnHudHoverEnter(Vector2 location) {
                    playButton.setColor(new RGBA(0, 150, 200, 1));
                }
            }, HUDHoverEnterEvent.class);
            playButton.addUActionEvent(new HUDHoverLeaveEvent() {
                @Override
                public void OnHudHoverLeave(Vector2 location) {
                    playButton.setColor(new RGBA(0, 150, 150, 1));
                }
            }, HUDHoverLeaveEvent.class);
            playButton.addUActionEvent(new HUDClickEvent() {
                @Override
                public void OnHUDClick(Vector2 location, MouseClickType clickType) {
                    if (!playButton.isVisible()) return;
                    try {
                        File file = new File("testsave");
                        if (!file.exists()) file.mkdirs();
                        Save save = new TestSave(file);
                        IntergratedServer intergratedServer = new IntergratedServer(null, save, null);

                        MainGameScene mgs = new MainGameScene(gameHandler, intergratedServer, kakaraGame);
                        gameHandler.getSceneManager().setScene(mgs);

                    } catch (Exception ex) {
                        KakaraGame.LOGGER.error("unable to start game", ex);
                        // gameHandler.getSceneManager().setScene();
                    }
                }
            }, HUDClickEvent.class);
            Text txt = new Text("Play Test!", kakaraGame.getFont());
            txt.setPosition(0, playButton.scale.y / 2);
            txt.setTextAlign(TextAlign.CENTER);
            txt.setLineWidth(playButton.scale.x);
            playButton.add(txt);
            componentCanvas.add(playButton);
        }
        componentCanvas.add(title);
        add(componentCanvas);
    }

    @Override
    public void update(float i) {
    }
}
