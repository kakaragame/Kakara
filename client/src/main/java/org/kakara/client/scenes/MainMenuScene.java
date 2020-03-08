package org.kakara.client.scenes;

import org.kakara.client.KakaraGame;
import org.kakara.engine.GameHandler;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseClickType;
import org.kakara.engine.item.Texture;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.ui.RGBA;
import org.kakara.engine.ui.components.Panel;
import org.kakara.engine.ui.components.Rectangle;
import org.kakara.engine.ui.components.Sprite;
import org.kakara.engine.ui.components.Text;
import org.kakara.engine.ui.events.HUDClickEvent;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.ui.text.Font;
import org.kakara.engine.ui.text.TextAlign;
import org.kakara.engine.utils.Time;

import java.net.MalformedURLException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

public class MainMenuScene extends AbstractGameScene {
    private KakaraGame kakaraGame;

    public MainMenuScene(GameHandler gameHandler, KakaraGame kakaraGame) {
        super(gameHandler);

        this.kakaraGame = kakaraGame;
        getHUD().addFont(kakaraGame.getFont());
        ComponentCanvas componentCanvas = new ComponentCanvas(this);
        Panel menu = new Panel();
        Texture texture = loadTextTexture();
        if (kakaraGame.getClient().getGameSettings().isTestMode()) {
            Sprite testMode = new Sprite(texture, new Vector2(0, 0), new Vector2(texture.getWidth(), texture.getHeight()));
            testMode.setPosition(50, 50);
            Text text = new Text("Test Mode " + kakaraGame.getKakaraCore().getModManager().getLoadedMods().get(0).getName(), kakaraGame.getFont());
            text.setSize(40);
            text.setTextAlign(TextAlign.CENTER);
            text.setLineWidth(testMode.scale.x);
            text.setPosition(testMode.scale.x / 2 - text.getLineWidth(), testMode.scale.y / 2);

            menu.add(testMode);
            testMode.add(text);
            testMode.addUActionEvent(new HUDClickEvent() {
                @Override
                public void OnHUDClick(Vector2 location, MouseClickType clickType) {
                    if (!testMode.isVisible()) return;
                    try {
                        MainGameScene mgs = new MainGameScene(gameHandler, kakaraGame);
                        gameHandler.getSceneManager().setScene(mgs);
                    } catch (Exception ex) {
                        KakaraGame.LOGGER.error("Unable to load test environment", ex);
                        System.exit(1);
                    }
                }
            }, HUDClickEvent.class);
        }
        componentCanvas.add(menu);
        getHUD().addItem(componentCanvas);
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
    public void update() {

    }
}
