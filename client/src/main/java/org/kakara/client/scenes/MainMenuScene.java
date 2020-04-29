package org.kakara.client.scenes;

import org.kakara.client.KakaraGame;
import org.kakara.client.SaveCreator;
import org.kakara.client.game.IntegratedServer;
import org.kakara.client.game.WorldCreator;
import org.kakara.core.NameKey;
import org.kakara.core.client.Save;
import org.kakara.core.modinstance.ModInstance;
import org.kakara.core.modinstance.ModInstanceType;
import org.kakara.engine.GameHandler;
import org.kakara.engine.input.MouseClickType;
import org.kakara.engine.item.Texture;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.scene.AbstractMenuScene;
import org.kakara.engine.ui.RGBA;
import org.kakara.engine.ui.components.Rectangle;
import org.kakara.engine.ui.components.Text;
import org.kakara.engine.ui.events.HUDClickEvent;
import org.kakara.engine.ui.events.HUDHoverEnterEvent;
import org.kakara.engine.ui.events.HUDHoverLeaveEvent;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.ui.text.TextAlign;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
    public void loadGraphics(GameHandler handler) {
        setBackground(loadBackgroundTexture());
        ComponentCanvas componentCanvas = new ComponentCanvas(this);
        Texture texture = loadTextTexture();
        // Make some more text for the title screen.
//        Text title = new Text("Kakara", kakaraGame.getFont());
//        title.setSize(200);
//        title.setLineWidth(500);
//        title.setPosition(gameHandler.getWindow().getWidth() / 2 - 250, 200);


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
                        if (file.exists()) file.delete();
                        SaveCreator saveCreator = new SaveCreator().setName("testsave");
                        for (File file1 : getModsToLoad()) {
                            saveCreator.add(new ModInstance(file1.getName(), "", "1.0", null, ModInstanceType.FILE, file1));
                        }
                        saveCreator.add(new WorldCreator().setWorldName("test").setGenerator(new NameKey("KVanilla:default")));
                        IntegratedServer integratedServer = new IntegratedServer(saveCreator.createSave(), UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"));
                        MainGameScene gameScene = new MainGameScene(gameHandler, integratedServer, kakaraGame);
                        gameHandler.getSceneManager().setScene(gameScene);
                    } catch (Exception ex) {
                        setCurserStatus(true);
                        KakaraGame.LOGGER.error("unable to start game", ex);
                        // gameHandler.getSceneManager().setScene();
                    }
                }
            }, HUDClickEvent.class);
//            Text txt = new Text("Play Test!", kakaraGame.getFont());
//            txt.setPosition(0, playButton.scale.y / 2);
//            txt.setTextAlign(TextAlign.CENTER);
//            txt.setLineWidth(playButton.scale.x);
//            playButton.add(txt);
//            componentCanvas.add(playButton);
        }
//        componentCanvas.add(title);
        add(componentCanvas);
    }

    public List<File> getModsToLoad() {
        File dir = new File("test" + File.separator + "mods");

        return Arrays.asList(dir.listFiles((dir1, filename) -> filename.endsWith(".jar")));
    }

    @Override
    public void update(float i) {
    }
}
