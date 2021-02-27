package org.kakara.client;


import org.kakara.client.join.LocalJoin;
import org.kakara.client.local.game.ClientResourceManager;
import org.kakara.client.local.game.IntegratedServer;
import org.kakara.client.local.game.commands.ClientCommandManager;
import org.kakara.core.common.EnvType;
import org.kakara.core.common.Serverable;
import org.kakara.core.common.calculator.Calculator;
import org.kakara.core.common.calculator.CalculatorRegistry;
import org.kakara.core.common.command.CommandManager;
import org.kakara.core.common.events.EventManager;
import org.kakara.core.common.events.game.GameEventManager;
import org.kakara.core.common.exceptions.NoServerVersionAvailableException;
import org.kakara.core.common.game.GameSettings;
import org.kakara.core.common.game.Item;
import org.kakara.core.common.game.ItemRegistry;
import org.kakara.core.common.gui.container.ContainerUtils;
import org.kakara.core.common.mod.ModManager;
import org.kakara.core.common.mod.game.GameModManager;
import org.kakara.core.common.resources.ResourceManager;
import org.kakara.core.common.service.ServiceManager;
import org.kakara.core.common.settings.SettingRegistry;
import org.kakara.core.common.settings.simple.SimpleSettingManager;
import org.kakara.core.common.world.WorldGenerationRegistry;
import org.kakara.core.server.ServerGameInstance;
import org.kakara.core.server.game.ServerItemStack;
import org.kakara.core.server.world.WorldManager;
import org.kakara.game.GameServiceManager;
import org.kakara.game.ServerLoadException;
import org.kakara.game.calculator.GameCalculatorRegistry;
import org.kakara.game.item.GameItemRegistry;
import org.kakara.game.items.GameItemStack;
import org.kakara.game.mod.KakaraMod;
import org.kakara.game.server.gui.ServerContainerUtils;
import org.kakara.game.settings.JsonSettingController;
import org.kakara.game.world.GameWorldGenerationRegistry;

import java.io.File;

public class LocalClient extends Client implements ServerGameInstance {
    private final GameSettings settings;
    private final KakaraGame kakaraGame;
    private final ItemRegistry itemManager;
    private final ResourceManager resourceManager;
    private final ModManager modManager;
    private final File workingDirectory;
    private final WorldGenerationRegistry worldGenerationManager;
    private final EventManager eventManager;
    private final CommandManager commandManager;
    private WorldManager worldManager;
    private final ServiceManager serviceManager;
    private final SettingRegistry settingManager;
    private final LocalJoin localJoin;
    private final ContainerUtils containerUtils;
    private CalculatorRegistry calculatorRegistry;

    public LocalClient(LocalJoin joinDetails, KakaraGame kakaraGame, GameSettings gameSettings) {
        this.settings = gameSettings;
        this.kakaraGame = kakaraGame;
        workingDirectory = joinDetails.getWorkingDirectory();
        itemManager = new GameItemRegistry();
        modManager = new GameModManager(new KakaraMod(this));
        resourceManager = new ClientResourceManager();
        eventManager = new GameEventManager();
        commandManager = new ClientCommandManager();
        worldGenerationManager = new GameWorldGenerationRegistry();
        containerUtils = new ServerContainerUtils();
        serviceManager = new GameServiceManager();
        calculatorRegistry = new GameCalculatorRegistry();
        registerDefaultServices();
        File settings = new File(workingDirectory, "settings");
        settings.mkdir();
        settingManager = new SimpleSettingManager(settings);
        this.localJoin = joinDetails;
    }

    private void registerDefaultServices() {
        serviceManager.registerService(new JsonSettingController());
    }

    public void setup() throws ServerLoadException {
        if (settings.isTestMode()) {
            server = new IntegratedServer(localJoin.getSave(), localJoin.getSelf(), null);
            ((IntegratedServer) server).start();
        } else {
            //TODO implement world joining
        }

    }

    @Override
    public GameSettings getGameSettings() {
        return settings;
    }

    @Override
    public ServerItemStack createItemStack(Item item) {
        return new GameItemStack(1, item);
    }

    @Override
    public ResourceManager getResourceManager() {
        return resourceManager;
    }


    @Override
    public ItemRegistry getItemRegistry() {
        return itemManager;
    }


    @Override
    public WorldManager getWorldManager() {
        return worldManager;
    }

    public void setWorldManager(WorldManager worldManager) {
        this.worldManager = worldManager;
    }


    @Override
    public ModManager getModManager() {
        return modManager;
    }

    @Override
    public ContainerUtils getContainerUtils() {
        return containerUtils;
    }

    @Override
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    @Override
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public WorldGenerationRegistry getWorldGenerationRegistry() {
        return worldGenerationManager;
    }

    @Override
    public boolean isIntegratedServer() {
        return true;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }


    @Override
    public EnvType getType() {
        return EnvType.SERVER;
    }

    @Override
    public CalculatorRegistry getCalculatorRegistry() {
        return calculatorRegistry;
    }

    public SettingRegistry getGameSettingRegistry() {
        return settingManager;
    }


    @Override
    public boolean isServerVersionAvailable() {
        return false;
    }

    @Override
    public <T extends Serverable> T getServerVersion() {
        return null;
    }

    @Override
    public void requiresServerVersion() throws NoServerVersionAvailableException {

    }
}
