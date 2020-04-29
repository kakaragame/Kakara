package org.kakara.client;

import org.kakara.client.scenes.MainMenuScene;
import org.kakara.client.scenes.TestUIScene;
import org.kakara.core.GameInstance;
import org.kakara.core.Kakara;

import org.kakara.core.game.GameSettings;
import org.kakara.core.mod.game.GameModManager;
import org.kakara.core.serializers.messagepack.MPSerializerRegistrar;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KakaraGame implements Game {
    private GameInstance kakaraCore;
    private GameHandler gameHandler;
    private Client client;
    public static final Logger LOGGER = LoggerFactory.getLogger(KakaraGame.class);

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
        Kakara.setGameInstance(client);
        client.getResourceManager().load(client);
        client.getWorldGenerationManager().load(client);
        client.getItemManager().load(client);
        client.getEventManager().load(client);
        client.getModManager().load(client);
        MPSerializerRegistrar.load();
}

    @Override
    public void start(GameHandler gameHandler) throws Exception {
        LOGGER.info("Starting Client");
        this.gameHandler = gameHandler;
        //Load MainMenuScene
        loadMusicManager();
//        MainMenuScene mainMenuScene = new MainMenuScene(gameHandler, this);
        gameHandler.getSceneManager().setScene(new TestUIScene(gameHandler));
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


    public GameInstance getKakaraCore() {
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

        //TODO bring back mod unloading
        //kakaraCore.getModManager().unloadMods(kakaraCore.getModManager().getLoadedMods());
        gameHandler.exit();
    }

    public int getTPS() {
        return 20;
    }

}
