package org.kakara.client;

import org.kakara.client.game.GameEngineInventoryController;
import org.kakara.client.scenes.MainMenuScene;
import org.kakara.core.GameInstance;
import org.kakara.core.Kakara;

import org.kakara.core.game.GameSettings;
import org.kakara.core.gui.EngineInventoryRenderer;
import org.kakara.core.gui.InventoryUtils;
import org.kakara.core.gui.bnbi.BasicNineBoxedInventory;
import org.kakara.engine.Game;
import org.kakara.engine.GameHandler;
import org.kakara.engine.scene.Scene;
import org.kakara.game.mod.KakaraMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class KakaraGame implements Game {
    private GameInstance kakaraCore;
    private GameHandler gameHandler;
    private Client client;
    public static final Logger LOGGER = LoggerFactory.getLogger(KakaraGame.class);
    private static KakaraGame kakaraGame;

    public KakaraGame(GameSettings gameSettings) {
        kakaraGame = this;
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
        //TODO
        //EngineInventoryRenderer renderer = new EngineInventoryRenderer(Kakara.getResourceManager().getTexture("inventories/bnbi.png", KakaraMod.getInstance()).get(), GameInventoryUtils.getItemPositions());
       // renderer.setEngineController(new GameEngineInventoryController());
        //BasicNineBoxedInventory.setRenderer(renderer);
    }

    @Override
    public void start(GameHandler gameHandler) throws Exception {
        LOGGER.info("Starting Client");
        this.gameHandler = gameHandler;
        //Load MainMenuScene
        loadMusicManager();
    }

    @Override
    public Scene firstScene(GameHandler gameHandler) throws Exception {
        return new MainMenuScene(gameHandler, this);
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

    public static KakaraGame getInstance() {
        return kakaraGame;
    }

    public int getTPS() {
        return 20;
    }

}
