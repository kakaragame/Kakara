package org.kakara.client;

import me.kingtux.other.TheCodeOfAMadMan;
import org.kakara.client.discord.DiscordManager;
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
import org.kakara.core.common.mod.ModRules;
import org.kakara.core.common.mod.environment.EnvironmentModManager;
import org.kakara.core.common.mod.game.GameModManager;
import org.kakara.core.common.resources.ResourceManager;
import org.kakara.core.common.resources.Texture;
import org.kakara.core.common.service.ServiceManager;
import org.kakara.core.common.settings.SettingRegistry;
import org.kakara.core.common.settings.simple.SimpleSettingManager;
import org.kakara.engine.Game;
import org.kakara.engine.GameEngine;
import org.kakara.engine.GameHandler;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.utils.Utils;
import org.kakara.game.GameServiceManager;
import org.kakara.game.ServerLoadException;
import org.kakara.game.mod.KakaraEnvMod;
import org.kakara.game.mod.KakaraMod;
import org.kakara.game.resources.GameResourceManager;
import org.kakara.game.server.gui.bnbi.Size9BoxedInventory;
import org.kakara.game.settings.JsonSettingController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Properties;
import java.util.jar.JarFile;

/**
 * The main class for the actual Game.
 *
 * <p>This acts as the primary class for the Engine to interact with.</p>
 *
 * <p>This also acts as an EnvironmentInstance for the Core.</p>
 */
public class KakaraGame implements Game, EnvironmentInstance {
    /**
     * The primary client logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(KakaraGame.class);

    private static KakaraGame kakaraGame;
    private GameInstance kakaraCore;
    private GameHandler gameHandler;
    private Client client;
    private final GameSettings settings;
    private final EnvironmentModManager modManager;
    private final File workingDirectory;
    private final ResourceManager resourceManager;
    private final ServiceManager serviceManager;
    private final SettingRegistry settingManager;
    private final DiscordManager discordManager;
    private static final Properties gameVersion = new Properties();

    static {
        // Get the version of the Game Engine.
        try {
            if (GameEngine.class.getResource("/kakara/version.properties") != null)
                gameVersion.load(GameEngine.class.getResourceAsStream("/kakara/version.properties"));
        } catch (IOException e) {
            LOGGER.warn("Unable to load kakaraproperties", e);
        }
    }

    /**
     * Construct the KakaraGame class.
     *
     * @param gameSettings The game settings to construct KakaraGame with.
     */
    public KakaraGame(GameSettings gameSettings) {
        kakaraGame = this;
        this.settings = gameSettings;
        if (gameSettings.isTestMode()) System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");

        // If in testing mode, create a mods folder in the local test directory.
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
        serviceManager = new GameServiceManager();
        registerDefaultServices();

        // Create the settings directory.
        File settings = new File(getWorkingDirectory(), "settings");
        settings.mkdir();
        settingManager = new SimpleSettingManager(settings);
        ((SimpleSettingManager) settingManager).init(ModRules.ModTarget.ENVIRONMENT);

        //Set Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::exit));
        discordManager = new DiscordManager(this);
    }

    /**
     * Join a Game with the specified JoinDetails.
     *
     * <p>This is class in the {@link MainMenuScene} by default.</p>
     *
     * @param joinDetails The Join Details.
     * @return The LoadingScene that is created while the game is loading.
     * @throws ServerLoadException If a load error occurs.
     */
    public LoadingScene join(JoinDetails joinDetails) throws ServerLoadException {
        if (joinDetails.getEnvType() == JoinDetails.JoinType.LOCAL) {
            LocalJoin join = (LocalJoin) joinDetails;
            // Create the client.
            client = new LocalClient(join, this, settings);
            loadKakaraCore();
            client.setup();
            // Create and return the loading scene.
            return new LoadingScene(gameHandler, join.getSave().getDefaultWorld(), Status.LOADED, () -> {
                MainGameScene gameScene = new MainGameScene(gameHandler, client, kakaraGame);
                gameHandler.getSceneManager().setScene(gameScene);
            });
        } else {
            //client = new ServerClient(joinDetails, this, settings);
            loadKakaraCore();

            //TODO Create a server software and add code to join it. (This wont take that long) :)
            return null;
        }
    }

    /**
     * Get the instance of KakaraGame.
     *
     * <p>Note: This will be null if the game is not running. Only one instance of KakaraGame
     * can exist at a time.</p>
     *
     * @return The current instance of KakaraGame.
     */
    public static KakaraGame getInstance() {
        return kakaraGame;
    }

    /**
     * Register the default services.
     */
    private void registerDefaultServices() {
        serviceManager.registerService(new JsonSettingController());
    }

    /**
     * Load the Kakara Core.
     */
    private void loadKakaraCore() {
        LOGGER.info("Loading Core");
        Kakara.setGameInstance(client);
        client.getResourceManager().load(new File(getWorkingDirectory(), ""));
        client.getWorldGenerationRegistry().load(client);
        client.getItemRegistry().load(client);
        client.getEventManager().load(client);
        client.getModManager().load(client);
        ((SimpleSettingManager) client.getGameSettingRegistry()).init(ModRules.ModTarget.GAME);

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

    /**
     * Setup the inventory.
     *
     * @param size The size of the inventory to setup.
     */
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
                Size9BoxedInventory.setRenderer(render9);
            case 27:
                EngineInventoryRenderer renderer27 = new EngineInventoryRenderer(texture.get(), GameInventoryUtils.getItemPositions(size), new InventoryProperties());
                //Size27BoxedInventory.setRenderer(renderer27);
        }
    }

    @Override
    public void start(GameHandler gameHandler) throws Exception {
        // When the game actually starts.
        LOGGER.info("Starting Client");
        this.gameHandler = gameHandler;
        //Load MainMenuScene
        loadMusicManager();
        discordManager.start();
    }

    @Override
    public Scene firstScene(GameHandler gameHandler) throws Exception {
        // The first scene to load when the engine starts up.
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

    /**
     * Get the GameInstance from the Kakra Core.
     *
     * @return The GameInstance.
     */
    public GameInstance getKakaraCore() {
        return kakaraCore;
    }

    /**
     * Get the instance of the GameHandler for the Engine.
     *
     * @return The instance of GameHandler.
     */
    public GameHandler getGameHandler() {
        return gameHandler;
    }

    /**
     * Get the client of the user.
     *
     * <p>This can be a {@link LocalClient} or ...</p>
     *
     * @return The client of the user.
     */
    public Client getClient() {
        return client;
    }

    @Override
    public void exit() {
        KakaraGame.LOGGER.info("Terminating Game.");
        //TODO bring back mod unloading
        //kakaraCore.getModManager().unloadMods(kakaraCore.getModManager().getLoadedMods());
        discordManager.setRunning(false);
        // Close the server.
        if (client != null) {
            client.getServer().close();
        }
    }

    /**
     * Get the current TPS.
     *
     * @return The current TPS (always 20).
     */
    public int getTPS() {
        return 20;
    }

    /**
     * Get the settings of the game.
     *
     * @return The settings of the game.
     */
    public GameSettings getSettings() {
        return settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModManager getModManager() {
        return modManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnvType getType() {
        return Kakara.getGameInstance() == null ? EnvType.CLIENT : Kakara.getGameInstance().getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SettingRegistry getEnvironmentSettingsRegistry() {
        return settingManager;
    }

    /**
     * Create a Controller Key with the controller name of KAKARA.
     *
     * @param key The key.
     * @return The Controller Key.
     */
    @Override
    public ControllerKey createSystemKey(String key) {
        return ControllerKey.create("KAKARA", key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    /**
     * Get the version of the game.
     *
     * @return The version of the game.
     */
    public static Properties getGameVersion() {
        return gameVersion;
    }
}
