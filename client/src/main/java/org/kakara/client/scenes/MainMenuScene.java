package org.kakara.client.scenes;

import org.kakara.client.KakaraGame;
import org.kakara.client.join.LocalJoin;
import org.kakara.client.local.game.SaveCreator;
import org.kakara.client.local.game.WorldCreator;
import org.kakara.core.common.ControllerKey;
import org.kakara.core.common.modinstance.ModInstance;
import org.kakara.core.common.modinstance.ModInstanceType;
import org.kakara.engine.GameHandler;
import org.kakara.engine.gameitems.Texture;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.AbstractMenuScene;
import org.kakara.engine.ui.components.Sprite;
import org.kakara.engine.ui.components.shapes.Rectangle;
import org.kakara.engine.ui.components.text.Text;
import org.kakara.engine.ui.constraints.GridConstraint;
import org.kakara.engine.ui.constraints.HorizontalCenterConstraint;
import org.kakara.engine.ui.constraints.VerticalCenterConstraint;
import org.kakara.engine.ui.events.UIClickEvent;
import org.kakara.engine.ui.events.UIHoverEnterEvent;
import org.kakara.engine.ui.events.UIHoverLeaveEvent;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.ui.font.TextAlign;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.utils.RGBA;
import org.kakara.engine.window.WindowIcon;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainMenuScene extends AbstractMenuScene {
    private final KakaraGame kakaraGame;

    public MainMenuScene(GameHandler gameHandler, KakaraGame kakaraGame) {
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

        /*
            Create the title and version number.
         */
        Text title = new Text("Kakara", pixelFont);
        title.setSize(100);
        title.setLineWidth(100 * 6 + 5);
        title.setColor(new RGBA(255, 255, 255, 1));
        title.setPosition(0, 250);
        title.addConstraint(new HorizontalCenterConstraint());
        componentCanvas.add(title);

        //TODO Get actual version number.
        Text versionNumber = new Text("0.0.00", pixelFont);
        versionNumber.setSize(19);
        versionNumber.setLineWidth(19 * 6 + 5);
        versionNumber.setColor(new RGBA(255, 255, 255, 1));
        versionNumber.setPosition(title.getPosition().x, 300);
        versionNumber.addConstraint(new GridConstraint(12, 30, 3, 11));
        componentCanvas.add(versionNumber);

        /*
            An error that is displayed if the game is not in testing mode.
         */

        if (!kakaraGame.getSettings().isTestMode()) {
            Rectangle singlePlayer = new Rectangle(new Vector2(0, 370), new Vector2(500, 60), new RGBA(255, 255, 255, 0.5f));
            singlePlayer.addConstraint(new HorizontalCenterConstraint());
            Text singlePlayerText = new Text("Error: Kakara is not in test mode", roboto);
            singlePlayerText.setSize(40);
            singlePlayerText.setLineWidth(500);
            singlePlayerText.setTextAlign(TextAlign.CENTER | TextAlign.MIDDLE);
            singlePlayerText.addConstraint(new VerticalCenterConstraint());
            singlePlayerText.addConstraint(new HorizontalCenterConstraint());
            singlePlayer.add(singlePlayerText);
            componentCanvas.add(singlePlayer);
            add(componentCanvas);
            return;
        }

        /*
         * The single player button.
         */
        Rectangle singlePlayer = new Rectangle(new Vector2(0, 370), new Vector2(300, 60), new RGBA(255, 255, 255, 0.5f));
        singlePlayer.addConstraint(new HorizontalCenterConstraint());
        Text singlePlayerText = new Text("SINGLEPLAYER", roboto);
        singlePlayerText.setSize(40);
        singlePlayerText.setLineWidth(300);
        singlePlayerText.setTextAlign(TextAlign.CENTER | TextAlign.MIDDLE);
        singlePlayerText.addConstraint(new VerticalCenterConstraint());
        singlePlayerText.addConstraint(new HorizontalCenterConstraint());
        singlePlayer.add(singlePlayerText);

        singlePlayer.addUActionEvent((UIHoverEnterEvent) vector2 -> singlePlayer.setColor(new RGBA(204, 202, 202, 0.5f)), UIHoverEnterEvent.class);
        singlePlayer.addUActionEvent((UIHoverLeaveEvent) vector2 -> singlePlayer.setColor(new RGBA(255, 255, 255, 0.5f)), UIHoverLeaveEvent.class);
        singlePlayer.addUActionEvent((UIClickEvent) (vector2, mouseClickType) -> singlePlayerClick(singlePlayer), UIClickEvent.class);

        componentCanvas.add(singlePlayer);


        /*
         * The multi player button.
         */
        Rectangle multiPlayer = new Rectangle(new Vector2(0, 500), new Vector2(300, 60), new RGBA(255, 255, 255, 0.5f));
        multiPlayer.addConstraint(new HorizontalCenterConstraint());
        Text multiPlayerText = new Text("MULTIPLAYER", roboto);
        multiPlayerText.setSize(40);
        multiPlayerText.setLineWidth(300);
        multiPlayerText.setTextAlign(TextAlign.CENTER | TextAlign.MIDDLE);
        multiPlayerText.addConstraint(new VerticalCenterConstraint());
        multiPlayerText.addConstraint(new HorizontalCenterConstraint());
        multiPlayer.add(multiPlayerText);

        multiPlayer.addUActionEvent((UIHoverEnterEvent) vector2 -> multiPlayer.setColor(new RGBA(204, 202, 202, 0.5f)), UIHoverEnterEvent.class);
        multiPlayer.addUActionEvent((UIHoverLeaveEvent) vector2 -> multiPlayer.setColor(new RGBA(255, 255, 255, 0.5f)), UIHoverLeaveEvent.class);
        multiPlayer.addUActionEvent((UIClickEvent) (vector2, mouseClickType) -> KakaraGame.LOGGER.warn("Coming Soon TM"), UIClickEvent.class);

        componentCanvas.add(multiPlayer);

        /*
            The settings gear.
         */
        Texture settings = new Texture(resourceManager.getResource("kakara_settings_gear.png"), this);
        Sprite settingsGear = new Sprite(settings, new Vector2(1030, 670), new Vector2(60, 60));
        settingsGear.addUActionEvent((UIClickEvent) (vector2, mouseClickType) -> KakaraGame.LOGGER.warn("Coming Soon TM"), UIClickEvent.class);
        Rectangle settingsHolder = new Rectangle(new Vector2(1040, 680), new Vector2(60, 60), new RGBA(255, 255, 255, 0));
        settingsHolder.addUActionEvent((UIHoverEnterEvent) vector2 -> settingsHolder.getColor().a = 0.3f, UIHoverEnterEvent.class);
        settingsHolder.addUActionEvent((UIHoverLeaveEvent) vector2 -> settingsHolder.getColor().a = 0, UIHoverLeaveEvent.class);
        componentCanvas.add(settingsGear);
        componentCanvas.add(settingsHolder);

        // Add the component canvas to to hud.
        add(componentCanvas);
    }

    /**
     * When the single player button is clicked.
     *
     * @param playButton The button that was clicked.
     */
    private void singlePlayerClick(Rectangle playButton) {
        if (!playButton.isVisible()) return;
        try {
            File file = new File("testsave");
            if (file.exists()) file.delete();
            SaveCreator saveCreator = new SaveCreator().setName("testsave");
            for (File file1 : getModsToLoad()) {
                saveCreator.add(new ModInstance(file1.getName(), "", "1.0", null, ModInstanceType.FILE, file1));
            }
            saveCreator.add(new WorldCreator().setWorldName("test").setGenerator(new ControllerKey("KVANILLA:DEFAULT")));

            gameHandler.getSceneManager().setScene(kakaraGame.join(new LocalJoin(saveCreator.createSave(),UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5") )));

        } catch (Exception ex) {
            setCursorStatus(true);
            KakaraGame.LOGGER.error("unable to start game", ex);
            // gameHandler.getSceneManager().setScene();
        }
    }

    public List<File> getModsToLoad() {
        File dir = new File("test" + File.separator + "mods");

        return Arrays.asList(dir.listFiles((dir1, filename) -> filename.endsWith(".jar")));
    }

    @Override
    public void update(float i) {
    }
}
