package org.kakara.client;

import me.kingtux.other.TheCodeOfAMadMan;
import org.kakara.client.join.JoinDetails;
import org.kakara.client.join.LocalJoin;
import org.kakara.client.local.game.GameEngineInventoryController;
import org.kakara.client.scenes.LoadingScene;
import org.kakara.client.scenes.MainMenuScene;
import org.kakara.client.scenes.maingamescene.MainGameScene;
import org.kakara.core.common.*;
import org.kakara.core.common.engine.EngineCore;
import org.kakara.core.common.game.GameSettings;
import org.kakara.core.common.gui.EngineInventoryRenderer;
import org.kakara.core.common.gui.InventoryProperties;
import org.kakara.core.common.mod.ModManager;
import org.kakara.core.common.mod.environment.EnvironmentModManager;
import org.kakara.core.common.mod.game.GameModManager;
import org.kakara.core.common.resources.ResourceManager;
import org.kakara.core.common.resources.Texture;
import org.kakara.engine.Game;
import org.kakara.engine.GameHandler;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.utils.Utils;
import org.kakara.game.ServerLoadException;
import org.kakara.game.mod.KakaraEnvMod;
import org.kakara.game.mod.KakaraMod;
import org.kakara.game.resources.GameResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.jar.JarFile;

public class KakaraGame implements Game, EnvironmentInstance {
    public static final Logger LOGGER = LoggerFactory.getLogger(KakaraGame.class);
    private static KakaraGame kakaraGame;
    private GameInstance kakaraCore;
    private GameHandler gameHandler;
    private Client client;
    private final GameSettings settings;
    private final EnvironmentModManager modManager;
    private final File workingDirectory;
    private final ResourceManager resourceManager;

    public KakaraGame(GameSettings gameSettings) {
        kakaraGame = this;
        this.settings = gameSettings;
        if (gameSettings.isTestMode()) System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");

        if (gameSettings.isTestMode()) {
            File file = new File("test" + File.separator + "mods");
            file.mkdirs();
        }
        workingDirectory = Utils.getCurrentDirectory();
        modManager = new EnvironmentModManager(new KakaraEnvMod(this));
        modManager.load(this);
        resourceManager = new GameResourceManager();
        resourceManager.load(new File(getWorkingDirectory(), "client" + File.separator + "resources"));
        Kakara.setEnvironmentInstance(this);


        //Set Shutdown hook
        //Runtime.getRuntime().addShutdownHook(new Thread(this::exit));
    }

    public LoadingScene join(JoinDetails joinDetails) throws ServerLoadException {
        if (joinDetails.getEnvType() == JoinDetails.JoinType.LOCAL) {
            LocalJoin join = (LocalJoin) joinDetails;
            client = new LocalClient(join, this, settings);
            loadKakaraCore();
            client.setup();
            LoadingScene loadingScene = new LoadingScene(gameHandler, join.getSave().getDefaultWorld(), Status.LOADED, () -> {
                MainGameScene gameScene = new MainGameScene(gameHandler, client, kakaraGame);
                gameHandler.getSceneManager().setScene(gameScene);
            });
            return loadingScene;
        } else {
            //client = new ServerClient(joinDetails, this, settings);
            loadKakaraCore();

            //TODO Create a server software and add code to join it. (This wont take that long) :)
            return null;
        }
    }

    public static KakaraGame getInstance() {
        return kakaraGame;
    }

    private void loadKakaraCore() {
        LOGGER.info("Loading Core");
        Kakara.setGameInstance(client);
        client.getResourceManager().load(new File(getWorkingDirectory(), "game"));
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
            //setupInventory(27);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    private void setupInventory(int size) {
        Optional<Texture> texture = Kakara.getGameInstance().getResourceManager().getTexture("inventories/bnbi_" + size + ".png", KakaraMod.getInstance());
        if (texture.isEmpty()) {
            LOGGER.warn(String.format("unable to load size: %d Inventory", size));
            return;
        }
        GameEngineInventoryController gameEngineInventoryController = new GameEngineInventoryController();
        EngineCore.setEngineController(gameEngineInventoryController);
        switch (size) {
            case 9:
                EngineInventoryRenderer render9 = new EngineInventoryRenderer(texture.get(), GameInventoryUtils.getItemPositions(size), new InventoryProperties(2));
                //Size9BoxedInventory.setRenderer(render9);
            case 27:
                EngineInventoryRenderer renderer27 = new EngineInventoryRenderer(texture.get(), GameInventoryUtils.getItemPositions(size), new InventoryProperties());
                //Size27BoxedInventory.setRenderer(renderer27);
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

    public int getTPS() {
        return 20;
    }

    public GameSettings getSettings() {
        return settings;
    }

    @Override
    public ModManager getModManager() {
        return modManager;
    }

    @Override
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public EnvType getType() {
        return EnvType.CLIENT;
    }

    @Override
    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}
