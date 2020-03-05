package org.kakara.client;

import org.kakara.client.scenes.MainMenuScene;
import org.kakara.core.KakaraCore;
import org.kakara.core.KakaraCoreBuilder;
import org.kakara.core.game.GameSettings;
import org.kakara.core.mod.game.GameModManager;
import org.kakara.engine.Game;
import org.kakara.engine.GameHandler;
import org.kakara.game.item.GameItemManager;
import org.kakara.game.mod.KakaraMod;
import org.kakara.game.resources.GameResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class KakaraGame implements Game {
    private KakaraCore kakaraCore;
    private GameHandler gameHandler;
    private Client client;
    public static final Logger LOGGER = LoggerFactory.getLogger(KakaraGame.class);

    public KakaraGame(GameSettings gameSettings) {
        client = new Client(this, gameSettings);
        //Load Core
        loadKakaraCore();
        if (gameSettings.isTestMode()) {
            File file = new File("test" + File.separator + "mods");
            file.mkdirs();
        }
        //Set Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::exit));
    }

    private void loadKakaraCore() {
        LOGGER.info("Loading Core");
        KakaraCoreBuilder kakaraCoreBuilder = new KakaraCoreBuilder()
                .setResourceManager(new GameResourceManager())
                .setModManager(new GameModManager(new KakaraMod()))
                .setGameInstance(client)
                .setItemManager(new GameItemManager());

        kakaraCore = kakaraCoreBuilder.createKakaraCore();

        try {
            kakaraCore.load();
        } catch (IOException e) {
            LOGGER.error("Unable to load core!", e);
        }
    }

    @Override
    public void start(GameHandler gameHandler) throws Exception {
        this.gameHandler = gameHandler;
        //Load MainMenuScene
       MainMenuScene mainMenuScene = new MainMenuScene(gameHandler, this);
        gameHandler.getSceneManager().setScene(mainMenuScene);
        loadMusicManager();
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
}
