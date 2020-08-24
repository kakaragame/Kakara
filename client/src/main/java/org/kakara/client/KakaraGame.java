package org.kakara.client;

import me.kingtux.other.TheCodeOfAMadMan;
import org.apache.commons.lang3.StringUtils;
import org.kakara.client.game.GameEngineInventoryController;
import org.kakara.client.scenes.DebugScene;
import org.kakara.client.scenes.MainMenuScene;
import org.kakara.core.GameInstance;
import org.kakara.core.Kakara;

import org.kakara.core.game.GameSettings;
import org.kakara.core.gui.EngineInventoryRenderer;
import org.kakara.core.gui.bnbi.Size27BoxedInventory;
import org.kakara.core.gui.bnbi.Size9BoxedInventory;
import org.kakara.core.mod.Mod;
import org.kakara.core.mod.game.GameModManager;
import org.kakara.core.resources.ResourceType;
import org.kakara.core.resources.Texture;
import org.kakara.core.resources.TextureResolution;
import org.kakara.engine.Game;
import org.kakara.engine.GameHandler;
import org.kakara.engine.scene.Scene;
import org.kakara.game.mod.KakaraMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

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
        try {
            //Loading Local Resources
            File file = TheCodeOfAMadMan.getJarFromClass(KakaraGame.class);
            if (file.isDirectory()) {
                throw new IllegalStateException("Kakara must be jar");
            }
            GameModManager.loadResources(KakaraMod.getInstance(), new JarFile(file));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        //TODO Load Inventory
        try {
            setupInventory(9);
            setupInventory(27);
        }catch (IllegalStateException e){
            e.printStackTrace();
        }

    }


    private void setupInventory(int size) {
        Optional<Texture> texture = Kakara.getResourceManager().getTexture("inventories/bnbi_" + size + ".png", KakaraMod.getInstance());
        if (texture.isEmpty()) {
            LOGGER.warn(String.format("unable to load size: %d Inventory", size));
            return;
        }
        EngineInventoryRenderer renderer = new EngineInventoryRenderer(texture.get(), GameInventoryUtils.getItemPositions());
        renderer.setEngineController(new GameEngineInventoryController());
        switch (size) {
            case 9:
                Size9BoxedInventory.setRenderer(renderer);
            case 27:
                Size27BoxedInventory.setRenderer(renderer);
        }
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
