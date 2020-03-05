package org.kakara.client;

import org.apache.commons.cli.CommandLine;
import org.kakara.client.scenes.MainMenuScene;
import org.kakara.core.KakaraCore;
import org.kakara.core.KakaraCoreBuilder;
import org.kakara.core.mod.game.GameModManager;
import org.kakara.engine.Game;
import org.kakara.engine.GameHandler;
import org.kakara.game.item.GameItemManager;
import org.kakara.game.mod.KakaraMod;
import org.kakara.game.resources.GameResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class KakaraGame implements Game {
    private KakaraCore kakaraCore;
    private GameHandler gameHandler;
    private Client client;
    public static final Logger LOGGER = LoggerFactory.getLogger(KakaraGame.class);
    private MainMenuScene mainMenuScene;

    public KakaraGame(CommandLine parse) {
        //Load Core
        loadKakaraCore();

    }

    private void loadKakaraCore() {
        LOGGER.info("Loading Core");
        KakaraCoreBuilder kakaraCoreBuilder = new KakaraCoreBuilder()
                .setResourceManager(new GameResourceManager())
                .setModManager(new GameModManager(new KakaraMod()))
                .setGameInstance(new Client(this))
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
        mainMenuScene = new MainMenuScene(gameHandler, this);
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

    @Override
    public void exit() {
        gameHandler.exit();
    }
}
