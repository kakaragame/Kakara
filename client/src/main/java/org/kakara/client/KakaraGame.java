package org.kakara.client;

import org.kakara.client.scenes.MainMenuScene;
import org.kakara.core.Kakara;
import org.kakara.core.KakaraCore;
import org.kakara.core.KakaraCoreBuilder;
import org.kakara.core.game.GameSettings;
import org.kakara.core.mod.game.GameModManager;
import org.kakara.engine.Game;
import org.kakara.engine.GameHandler;
import org.kakara.engine.ui.text.Font;
import org.kakara.game.item.GameItemManager;
import org.kakara.game.mod.KakaraMod;
import org.kakara.game.resources.GameResourceManager;
import org.kakara.game.world.GameWorldGenerationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class KakaraGame implements Game {
    private KakaraCore kakaraCore;
    private GameHandler gameHandler;
    private Client client;
    public static final Logger LOGGER = LoggerFactory.getLogger(KakaraGame.class);
    private Font font;

    public KakaraGame(GameSettings gameSettings) {
        if (gameSettings.isTestMode()) System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");

        client = new Client(this, gameSettings);
        //Load Core
        loadKakaraCore();
        if (gameSettings.isTestMode()) {
            File file = new File("test" + File.separator + "mods");
            file.mkdirs();
        }


        //Set Shutdown hook
        //Runtime.getRuntime().addShutdownHook(new Thread(this::exit));
    }

    private void loadKakaraCore() {
        LOGGER.info("Loading Core");
        KakaraCoreBuilder kakaraCoreBuilder = new KakaraCoreBuilder()
                .setResourceManager(new GameResourceManager())
                .setModManager(new GameModManager(new KakaraMod()))
                .setGameInstance(client)
                .setItemManager(new GameItemManager()).
                        setWorldGenerationManager(new GameWorldGenerationManager());

        kakaraCore = kakaraCoreBuilder.createKakaraCore();
//KakaraCore.LOGGER = LOGGER;
        try {
            kakaraCore.load();
        } catch (IOException e) {
            LOGGER.error("Unable to load core!", e);
            System.exit(2);
        }
        Kakara.setKakaraCore(kakaraCore);
    }

    @Override
    public void start(GameHandler gameHandler) throws Exception {
        LOGGER.info("Starting Client");
        this.gameHandler = gameHandler;
        //Load MainMenuScene
        loadMusicManager();
        loadFont();
        MainMenuScene mainMenuScene = new MainMenuScene(gameHandler, this);
        gameHandler.getSceneManager().setScene(mainMenuScene);
    }

    private void loadFont() {
        try {
            font = new Font("Roboto-Regular", getGameHandler().getResourceManager().getResource("Roboto-Regular.ttf"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    private void loadMusicManager() {
        try {
            gameHandler.getSoundManager().init();
        } catch (Exception e) {
            LOGGER.error("Unable to load SoundManager", e);
        }

    }

    @Override
    public void update() {

    }


    public KakaraCore getKakaraCore() {
        return kakaraCore;
    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void exit() {

        kakaraCore.getModManager().unloadMods(kakaraCore.getModManager().getLoadedMods());
        gameHandler.exit();
    }

    public Font getFont() {
        return font;
    }
}
